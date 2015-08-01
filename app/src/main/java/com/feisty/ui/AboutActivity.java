package com.feisty.ui;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.feisty.R;
import com.feisty.model.youtube.About;
import com.feisty.net.API;
import com.feisty.utils.Logger;
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


    //TODO: Implement loading state
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mCollapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
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
