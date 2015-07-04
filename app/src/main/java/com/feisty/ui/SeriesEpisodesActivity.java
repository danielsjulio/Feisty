package com.feisty.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.feisty.R;
import com.feisty.model.Playlist;
import com.feisty.model.Video;
import com.feisty.model.youtube.VideoList;
import com.feisty.net.API;
import com.feisty.ui.listeners.InfinityScrollListener;
import com.feisty.ui.transformation.PaletteTransformation;
import com.feisty.utils.Logger;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SeriesEpisodesActivity extends BaseActivity implements InfinityScrollListener.ScrollResultListener, Callback<VideoList> {

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

    private Playlist mPlaylist;

    private InfinityScrollListener mInfinityScrollListener;
    private VideoFeedArrayAdapter mAdapter;

    public static void startActivity(Context context, Playlist playlist){
        Intent intent = new Intent(context, SeriesEpisodesActivity.class);
        intent.putExtra(PLAYLIST, playlist);
        context.startActivity(intent);
    }

    //TODO: Implement loading state
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
        loadMore(null);


        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mCollapsingToolbarLayout.setTitle(mPlaylist.title);
        }

        Picasso.with(this).load(mPlaylist.thumbnail).transform(PaletteTransformation.instance())
                .into(mToolbarBackdrop, new PaletteTransformation.PaletteCallback(mToolbarBackdrop) {
                    @Override
                    public void onSuccess(Palette palette) {
                        mCollapsingToolbarLayout.setContentScrimColor(palette.getVibrantColor(getResources().getColor(R.color.default_card_background)));
                        mCollapsingToolbarLayout.setStatusBarScrimColor(palette.getVibrantColor(getResources().getColor(R.color.default_card_background)));
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        mRecyclerView.removeOnScrollListener(mInfinityScrollListener);
        super.onDestroy();
    }

    @Override
    public void loadMore(String nextPageToken) {
        if (nextPageToken == null) {
            API.getYoutubeService(this).getPlaylistItems(mPlaylist.id, this);
        } else {
            API.getYoutubeService(this).getPlaylistItems(mPlaylist.id, nextPageToken, this);
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


    //TODO: Handle failure
    @Override
    public void failure(RetrofitError error) {
        LOG.d(error.toString());
        LOG.d(error.getMessage());
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_series, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
