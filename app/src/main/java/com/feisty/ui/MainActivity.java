package com.feisty.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.feisty.R;
import com.feisty.model.ChannelList;
import com.feisty.net.YouTubeService;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends BaseActivity implements Callback<ChannelList> {

    private static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.progress_bar)
    View mProgressBar;

    @InjectView(R.id.materialViewPager)
    MaterialViewPager mViewPager;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        toolbar = mViewPager.getToolbar();

        if (toolbar != null) {
            setTitle(getString(R.string.app_name));
            setSupportActionBar(toolbar);
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(false);
            }
        }

        mViewPager.getViewPager().setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()));
        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        YouTubeService youTubeService = getApp().getYoutubeService(this);
        youTubeService.getChannel(getString(R.string.youtube_channel_id), this);
    }


    @Override
    public void success(ChannelList channelList, Response response) {
        ChannelList.Channel channel = channelList.channels.get(0);
        toolbar.setTitle(channel.snippet.title);

        mProgressBar.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        mViewPager.setColor(getResources().getColor(R.color.primary), 400);

        Log.d(TAG, "Thumbnail: " + channel.contentDetails.images.bannerImageUrl);
        mViewPager.setImageUrl(channel.contentDetails.images.bannerTvLowImageUrl, 400);
        ImageView thumbnail = (ImageView) mViewPager.findViewById(R.id.toolbar_logo);
        Picasso.with(this).load(channel.snippet.thumbnails.high.url).into(thumbnail);

    }

    @Override
    public void failure(RetrofitError error) {
        //TODO(gil): Handle errors properly
        Log.d(TAG, "failure" + error.getMessage());
    }


    class FragmentPagerAdapter extends FragmentStatePagerAdapter {

        int oldPosition = -1;

        public FragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return RecyclerViewFragment.newInstance();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

            //only if position changed
            if(position == oldPosition)
                return;
            oldPosition = position;

            int color = 0;
            String imageUrl = "";
            switch (position){
                case 0:
                    imageUrl = "http://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2014/06/wallpaper_51.jpg";
                    color = getResources().getColor(R.color.primary);
                    break;
                case 1:
                    imageUrl = "https://fs01.androidpit.info/a/63/0e/android-l-wallpapers-630ea6-h900.jpg";
                    color = getResources().getColor(R.color.blue);
                    break;
            }

            final int fadeDuration = 400;
//            mViewPager.setImageUrl(imageUrl,fadeDuration);
//            mViewPager.setColor(color,fadeDuration);

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getString(R.string.tabbar_item_videos);
                case 1:
                    return getString(R.string.tabbar_item_series);
            }
            return "";
        }
    }

}