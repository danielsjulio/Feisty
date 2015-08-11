package com.feisty.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.feisty.AnalyticsTrackers;
import com.feisty.BuildConfig;
import com.feisty.R;
import com.feisty.model.youtube.ChannelList;
import com.feisty.net.API;
import com.feisty.net.YouTubeService;
import com.feisty.sync.SyncUtils;
import com.feisty.ui.transformation.RoundedTransformation;
import com.feisty.utils.Logger;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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

    public static final String KEY_MORE_FEATURES_SOON = "shown_more_features_soon";

    @InjectView(R.id.materialViewPager)
    MaterialViewPager mViewPager;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        t.setScreenName("MainActivity");
        t.send(new HitBuilders.ScreenViewBuilder().build());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.getBoolean(KEY_MORE_FEATURES_SOON, false)) {
            new AlertDialog.Builder(this)
                    .setTitle("Welcome " + new String(Character.toChars(0x1F64B)))
                    .setMessage("We have just launched this new app. We understand that there is limited amount of functionality on the app but we're working on this - promise!")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            preferences.edit().putBoolean(KEY_MORE_FEATURES_SOON, true).commit();
        }
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

        if(!getResources().getBoolean(R.bool.solid_action_bar)) {
            mViewPager.setImageUrl(channel.brandingSettings.images.bannerTvLowImageUrl, 400);
        }
        ImageView thumbnail = (ImageView) mViewPager.findViewById(R.id.toolbar_logo);

        Picasso.with(this)
                .load(channel.snippet.thumbnails.high.url)
                .transform(new RoundedTransformation(1000, 0))
                .into(thumbnail);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewPager.notifyHeaderChanged();
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