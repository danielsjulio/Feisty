package com.feisty.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.feisty.BuildConfig;
import com.feisty.R;
import com.feisty.model.youtube.ChannelList;
import com.feisty.net.API;
import com.feisty.net.YouTubeService;
import com.feisty.sync.SyncUtils;
import com.feisty.ui.transformation.RoundedTransformation;
import com.feisty.utils.Logger;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends BaseActivity implements Callback<ChannelList> {

    private static final Logger LOG = Logger.create();

    @InjectView(R.id.materialViewPager)
    MaterialViewPager mViewPager;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());
        }

        SyncUtils.createSyncAccount(this);

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

        YouTubeService youTubeService = API.getYoutubeService(this);
        youTubeService.getChannel(getString(R.string.youtube_channel_id), this);
    }


    @Override
    public void success(ChannelList channelList, Response response) {
        ChannelList.Channel channel = channelList.channels.get(0);
        toolbar.setTitle(channel.snippet.title);

        mViewPager.setVisibility(View.VISIBLE);
        mViewPager.setColor(getResources().getColor(R.color.primary), 400);

        mViewPager.setImageUrl(channel.brandingSettings.images.bannerTvLowImageUrl, 400);
        ImageView thumbnail = (ImageView) mViewPager.findViewById(R.id.toolbar_logo);

        Picasso.with(this)
                .load(channel.snippet.thumbnails.high.url)
                .transform(new RoundedTransformation(1000, 0))
                .into(thumbnail);

    }

    @Override
    public void failure(RetrofitError error) {
        //TODO(gil): Handle errors properly
        LOG.d("failure" + error.getMessage());
    }

    class FragmentPagerAdapter extends FragmentStatePagerAdapter {

        public FragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return VideosListFragment.newInstance();
                case 1:
                    return SeriesListFragment.newInstance();
            }
            return null;
        }

        /*@Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

            //only if position changed
            if(position == oldPosition)
                return;
            oldPosition = position;

        }*/

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.about:
                Intent intent1 = new Intent(this, AboutActivity.class);
                startActivity(intent1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}