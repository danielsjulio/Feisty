package com.feisty.ui;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.feisty.AnalyticsTrackers;
import com.feisty.R;
import com.feisty.model.youtube.About;
import com.feisty.net.API;
import com.feisty.utils.Logger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by danieljulio on 26/07/15.
 */
public class AboutActivity extends BaseActivity implements Callback<About> {

    private static final Logger LOG = Logger.create();

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @InjectView(R.id.backdrop)
    ImageView mToolbarBackdrop;

    @InjectView(R.id.about_channel_description)
    TextView mDescription;

    @InjectView(R.id.about_subscribers_amount)
    TextView mSubsTotal;

    @InjectView(R.id.about_videos_amount)
    TextView mVideosTotal;

    @InjectView(R.id.about_total_watched)
    TextView mWatchedTotal;

    @InjectView(R.id.about_feisty)
    TextView mAboutFeisty;


    //TODO: Implement loading state
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);

        Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        t.setScreenName("AboutActivity");
        t.send(new HitBuilders.ScreenViewBuilder().build());

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mCollapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }

        API.getYoutubeService(this).getAboutUsInfo(getString(R.string.youtube_channel_id), this);
    }

    @Override
    public void success(About about, Response response) {
        Picasso.with(this).load(about.items.get(0).snippet.thumbnails.high.url).into(mToolbarBackdrop);

        mDescription.setText(about.items.get(0).snippet.description);
        mSubsTotal.setText("Total subscribers: " + about.items.get(0).statistics.subscriberCount);
        mVideosTotal.setText("Total videos uploaded: " + about.items.get(0).statistics.videoCount);
        mWatchedTotal.setText("Total video views: " + about.items.get(0).statistics.viewCount);
        mAboutFeisty.setText(Html.fromHtml(getResources().getString(R.string.about_feisty_info) + " " + getResources().getString(R.string.about_feisty_email)));
        mAboutFeisty.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //TODO: Handle failure
    @Override
    public void failure(RetrofitError error) {
        LOG.d(error.toString());
        LOG.d(error.getMessage());
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
