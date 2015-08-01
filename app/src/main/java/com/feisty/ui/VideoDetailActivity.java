package com.feisty.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feisty.R;
import com.feisty.model.youtube.CommentList;
import com.feisty.model.Video;
import com.feisty.net.API;
import com.feisty.ui.listeners.InfinityScrollListener;
import com.feisty.ui.transformation.RoundedTransformation;
import com.feisty.utils.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VideoDetailActivity extends BaseActivity implements Callback<CommentList>,
        InfinityScrollListener.ScrollResultListener {

    private static final Logger LOG = Logger.create();

    public static final String KEY_VIDEO = "VIDEO";

    @InjectView(R.id.comments_recyclerview)
    RecyclerView mCommentsRecyclerView;

    @InjectView(R.id.video_container)
    FrameLayout mContainer;

    private YouTubeVideoFragment mYouTubeVideoFragment;

    private CommentsRecyclerViewAdapter mCommentsRecyclerViewAdapter;

    private Video mVideo;

    private InfinityScrollListener mInfinityScrollListener;

    public static Intent getIntent(Context context, Video video){
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.putExtra(KEY_VIDEO, video);
        return intent;
    }

    public static void startActivity(Context context, Video video){
        context.startActivity(getIntent(context, video));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        mVideo = (Video) intent.getSerializableExtra(KEY_VIDEO);

        if (savedInstanceState == null){
            mYouTubeVideoFragment = YouTubeVideoFragment.newInstance(mVideo.id);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.video_container, mYouTubeVideoFragment, "video_frag");
            fragmentTransaction.commit();

            mCommentsRecyclerViewAdapter = new CommentsRecyclerViewAdapter();
            mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mCommentsRecyclerView.setAdapter(mCommentsRecyclerViewAdapter);
            mInfinityScrollListener = new InfinityScrollListener(this);
            mCommentsRecyclerView.addOnScrollListener(mInfinityScrollListener);
            loadMore(null);
        }
    }

    @Override
    protected void onDestroy() {
        mCommentsRecyclerView.removeOnScrollListener(mInfinityScrollListener);
        super.onDestroy();
    }

    public void doLayout(boolean isFullscreen) {
        mCommentsRecyclerView.setVisibility(isFullscreen ? View.GONE : View.VISIBLE);
        mContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                isFullscreen ? LinearLayout.LayoutParams.MATCH_PARENT : LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onBackPressed() {
        if (mYouTubeVideoFragment.onBackPressed()){
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void success(CommentList commentList, Response response) {
        mInfinityScrollListener.setNextPageToken(commentList.nextPageToken);
        mInfinityScrollListener.finishedLoad();

        int startRange = mCommentsRecyclerViewAdapter.getItemCount();
        mCommentsRecyclerViewAdapter.getComments().addAll(commentList.comments);
        mCommentsRecyclerViewAdapter.notifyItemRangeInserted(startRange, commentList.comments.size());
    }

    //TODO: Handle the error properly
    @Override
    public void failure(RetrofitError error) {
        mInfinityScrollListener.finishedLoad();
        LOG.d(error.getMessage());
        Toast.makeText(VideoDetailActivity.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadMore(String nextPageToken) {
        if(nextPageToken != null) {
            API.getYoutubeService(this).getComments(mVideo.id, nextPageToken, this);
        } else {
            API.getYoutubeService(this).getComments(mVideo.id, this);
        }
    }

    class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER_VIEW_TYPE = 0;
        private static final int COMMENT_VIEW_TYPE = 1;

        List<CommentList.Comment> comments = new ArrayList<>();

        public List<CommentList.Comment> getComments() {
            return comments;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType){
                case HEADER_VIEW_TYPE:
                    View headerContainer = LayoutInflater.from(VideoDetailActivity.this)
                            .inflate(R.layout.cell_header_video_detail, null);
                    return new HeaderViewHolder(headerContainer);
                case COMMENT_VIEW_TYPE:
                    View commentContainer = LayoutInflater.from(VideoDetailActivity.this)
                            .inflate(R.layout.cell_comment_video_detail, null);
                    return new CommentViewHolder(commentContainer);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case HEADER_VIEW_TYPE:
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                    headerViewHolder.mNameView.setText(mVideo.title);
                    headerViewHolder.mDescriptionView.setText(Html.fromHtml(mVideo.description.replaceAll("(\\A|\\s)((http|https|ftp|mailto):\\S+)(\\s|\\z|\\n)",
                            "$1<a href=\"$2\">$2</a>$4").replace("\n","<br />")));
                    headerViewHolder.mDescriptionView.setMovementMethod(LinkMovementMethod.getInstance());


                    break;
                case COMMENT_VIEW_TYPE:
                    CommentList.Comment comment = comments.get(position - 1);
                    CommentViewHolder commentViewHolder = bindCommentBox(holder, comment.snippet.topLevelComment);
                    if(comment.replies != null) {
                        for (CommentList.Comment.Snippet.TopLevelComment reply : comment.replies.comments) {
                            RecyclerView.ViewHolder view = onCreateViewHolder(null, COMMENT_VIEW_TYPE);
                            view = bindCommentBox(view, reply);
                            commentViewHolder.mRepliesContainer.addView(view.itemView);
                        }
                    }
                    break;
            }
        }

        /**
         * Binds the comment to the view. Used for the replies alsod
         * @param holder to be bound
         * @param comment
         * @return the view holder
         */
        private CommentViewHolder bindCommentBox(RecyclerView.ViewHolder holder, CommentList.Comment.Snippet.TopLevelComment comment){
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            commentViewHolder.mTextView.setText(Html.fromHtml(comment.snippet.textDisplay.trim()).toString());
            commentViewHolder.mUsernameView.setText(comment.snippet.authorDisplayName);
            Picasso.with(VideoDetailActivity.this)
                    .load(comment.snippet.authorProfileImageUrl)
                    .transform(new RoundedTransformation(1000, 0))
                    .into(commentViewHolder.mThumbnailView);
            return commentViewHolder;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return HEADER_VIEW_TYPE;
            return COMMENT_VIEW_TYPE;
        }

        @Override
        public int getItemCount() {
            return comments.size() + 1;//Comments + video header
        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder {

            @InjectView(R.id.header_video_name)
            TextView mNameView;

            @InjectView(R.id.header_video_description)
            TextView mDescriptionView;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }
        }

        public class CommentViewHolder extends RecyclerView.ViewHolder {

            @InjectView(R.id.comment_thumbnail)
            ImageView mThumbnailView;

            @InjectView(R.id.comment_username)
            TextView mUsernameView;

            @InjectView(R.id.comment_text)
            TextView mTextView;

            @InjectView(R.id.reply_comments)
            LinearLayout mRepliesContainer;

            public CommentViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }
        }
    }
}
