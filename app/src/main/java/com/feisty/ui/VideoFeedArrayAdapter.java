package com.feisty.ui;

import android.content.Context;

import com.feisty.model.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gil on 03/07/15.
 */
public class VideoFeedArrayAdapter extends GenericVideoFeedAdapter {

    public List<Video> videos = new ArrayList<>();

    public VideoFeedArrayAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    @Override
    public Video getItem(int position) {
        return videos.get(position);
    }
}
