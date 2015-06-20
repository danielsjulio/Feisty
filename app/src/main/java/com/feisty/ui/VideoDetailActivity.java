package com.feisty.ui;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.feisty.R;

import butterknife.ButterKnife;

public class VideoDetailActivity extends ActionBarActivity {

    public static final String KEY_VIDEO_ID = "VIDEO_ID";

    VideoFragment mVideoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        String videoId = intent.getStringExtra(KEY_VIDEO_ID);

        mVideoFragment = VideoFragment.newInstance(videoId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
