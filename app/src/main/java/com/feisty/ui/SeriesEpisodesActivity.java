package com.feisty.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.feisty.R;
import com.feisty.model.Playlist;
import com.feisty.model.Video;
import com.feisty.model.youtube.VideoList;
import com.feisty.net.API;
import com.feisty.ui.listeners.InfinityScrollListener;
import com.feisty.ui.listeners.RetrofitResponseObserver;
import com.feisty.ui.views.NetworkMetaView;
import com.feisty.utils.Logger;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SeriesEpisodesActivity extends BaseActivity
        implements InfinityScrollListener.ScrollResultListener, Callback<VideoList>, SwipeRefreshLayout.OnRefreshListener {

    private static final Logger LOG = Logger.create();

    private static final String PLAYLIST = "playlist";

    @InjectView(R.id.series_videos)
    RecyclerView mRecyclerView;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @InjectView(R.id.backdrop)
    ImageView mToolbarBackdrop;

    @InjectView(R.id.network_meta_view)
    NetworkMetaView mNetworkMetaView;

    private Playlist mPlaylist;
    private InfinityScrollListener mInfinityScrollListener;
    private VideoFeedArrayAdapter mAdapter;
    private RetrofitResponseObserver<VideoList> mResponseObserver;

    public static void startActivity(Activity activity, ImageView imageView, Playlist playlist){
        Intent intent = new Intent(activity, SeriesEpisodesActivity.class);
        intent.putExtra(PLAYLIST, playlist);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView, "videoThumbnail").toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        mPlaylist = (Playlist) intent.getSerializableExtra(PLAYLIST);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mInfinityScrollListener = new InfinityScrollListener(this);
        mRecyclerView.addOnScrollListener(mInfinityScrollListener);
        mAdapter = new VideoFeedArrayAdapter(this);
        mRecyclerView.setAdapter(mAdapter);


        mResponseObserver = new RetrofitResponseObserver<>();
        mResponseObserver.addObserver(this);
        mResponseObserver.addObserver(mNetworkMetaView);
        mNetworkMetaView.setNetworkResultsContainer(mRecyclerView);
        mNetworkMetaView.setOnRefreshListener(this);
        loadMore(null);

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mCollapsingToolbarLayout.setTitle(mPlaylist.title);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }


        //TODO: Decide what to do here
        Picasso.with(this).load(mPlaylist.thumbnail)/*.transform(PaletteTransformation.instance())*/
                .into(mToolbarBackdrop/*, new PaletteTransformation.PaletteCallback(mToolbarBackdrop) {
                    @Override
                    public void onSuccess(Palette palette) {
                        mCollapsingToolbarLayout.setContentScrimColor(palette.getVibrantColor(getResources().getColor(R.color.default_card_background)));
                        mCollapsingToolbarLayout.setStatusBarScrimColor(palette.getVibrantColor(getResources().getColor(R.color.default_card_background)));
                    }

                    @Override
                    public void onError() {

                    }
                }*/);
        mCollapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.primary));
        mCollapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.primary_dark));

    }

    @Override
    protected void onDestroy() {
        mRecyclerView.removeOnScrollListener(mInfinityScrollListener);
        super.onDestroy();
    }

    @Override
    public void loadMore(String nextPageToken) {
        if (nextPageToken == null) {
            API.getYoutubeService(this).getPlaylistItems(mPlaylist.id, mResponseObserver);
        } else {
            API.getYoutubeService(this).getPlaylistItems(mPlaylist.id, nextPageToken, mResponseObserver);
        }
    }

    @Override
    public void success(VideoList videoList, Response response) {
        int startRange = mAdapter.getItemCount();
        mAdapter.videos.addAll(Video.toVideos(videoList));
        mAdapter.notifyItemRangeInserted(startRange, videoList.videos.size());

        mInfinityScrollListener.setNextPageToken(videoList.nextPageToken);
        mInfinityScrollListener.setLoading(false);

    }

    @Override
    public void failure(RetrofitError error) {
        LOG.e(error.getMessage());
    }

    @Override
    public void onRefresh() {
        int size = mAdapter.videos.size();
        mAdapter.videos.clear();
        mAdapter.notifyItemRangeRemoved(0, size);
        loadMore(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
