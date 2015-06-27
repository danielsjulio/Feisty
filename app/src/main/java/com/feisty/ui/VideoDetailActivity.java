package com.feisty.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feisty.R;
import com.feisty.model.CommentList;
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

public class VideoDetailActivity extends ActionBarActivity implements Callback<CommentList>,
        InfinityScrollListener.ScrollResultListener {

    private static final Logger LOG = Logger.create();

    public static final String KEY_VIDEO_ID = "VIDEO_ID";
    public static final String KEY_VIDEO_NAME = "VIDEO_NAME";
    public static final String KEY_VIDEO_DESCRIPTION = "VIDEO_DESCRIPTION";

    @InjectView(R.id.comments_recyclerview)
    RecyclerView mCommentsRecyclerView;

    @InjectView(R.id.video_container)
    FrameLayout mContainer;

    private VideoFragment mVideoFragment;

    private CommentsRecyclerViewAdapter mCommentsRecyclerViewAdapter;

    private String mVideoId, mVideoName, mVideoDescription;

    private InfinityScrollListener mInfinityScrollListener;

    public static void startActivity(Context context, String id, String name, String description){
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.putExtra(KEY_VIDEO_ID, id);
        intent.putExtra(KEY_VIDEO_NAME, name);
        intent.putExtra(KEY_VIDEO_DESCRIPTION, description);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        mVideoId = intent.getStringExtra(KEY_VIDEO_ID);
        mVideoName = intent.getStringExtra(KEY_VIDEO_NAME);
        mVideoDescription = intent.getStringExtra(KEY_VIDEO_DESCRIPTION);

        if (savedInstanceState == null){
            mVideoFragment = VideoFragment.newInstance(mVideoId);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.video_container, mVideoFragment, "video_frag");
            fragmentTransaction.commit();

            mCommentsRecyclerViewAdapter = new CommentsRecyclerViewAdapter();
            mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mCommentsRecyclerView.setAdapter(mCommentsRecyclerViewAdapter);
            mInfinityScrollListener = new InfinityScrollListener(this);
            mCommentsRecyclerView.setOnScrollListener(mInfinityScrollListener);
            loadMore(null);
        }
    }

    public void doLayout(boolean isFullscreen) {
        mCommentsRecyclerView.setVisibility(isFullscreen ? View.GONE : View.VISIBLE);
        mContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                isFullscreen ? LinearLayout.LayoutParams.MATCH_PARENT : LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onBackPressed() {
        if (mVideoFragment.onBackPressed()){
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void success(CommentList commentList, Response response) {
        int startRange = mCommentsRecyclerViewAdapter.getItemCount();
        mCommentsRecyclerViewAdapter.getComments().addAll(commentList.comments);
        mCommentsRecyclerViewAdapter.notifyItemRangeInserted(startRange, commentList.comments.size());
        mInfinityScrollListener.setNextPageToken(commentList.nextPageToken);
    }

    @Override
    public void failure(RetrofitError error) {
        LOG.d(error.getMessage());
        Toast.makeText(VideoDetailActivity.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadMore(String nextPageToken) {
        API.getYoutubeService(this).getComments(mVideoId, this);
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
            switch (holder.getItemViewType()){
                case HEADER_VIEW_TYPE:
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                    headerViewHolder.mNameView.setText(mVideoName);
                    headerViewHolder.mDescriptionView.setText(Html.fromHtml(mVideoDescription));
                    break;
                case COMMENT_VIEW_TYPE:
                    CommentList.Comment comment = comments.get(position - 1);
                    CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
                    commentViewHolder.mTextView.setText(Html.fromHtml(comment.snippet.topLevelComment.snippet.textDisplay.trim()).toString());
                    commentViewHolder.mUsernameView.setText(comment.snippet.topLevelComment.snippet.authorDisplayName);
                    Picasso.with(VideoDetailActivity.this)
                            .load(comment.snippet.topLevelComment.snippet.authorProfileImageUrl)
                            .transform(new RoundedTransformation(1000, 0))
                            .into(commentViewHolder.mThumbnailView);
                    break;
            }

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

            public CommentViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }
        }
    }
}
