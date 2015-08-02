package com.feisty.model;

import android.database.Cursor;

import com.feisty.model.youtube.Thumbnails;
import com.feisty.model.youtube.VideoList;
import com.feisty.sync.Contacts;
import com.feisty.sync.SyncAdapter;
import com.feisty.utils.Logger;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by danieljulio on 21/06/15.
 */
public class Video implements Serializable {

    private static final Logger LOG = Logger.create();

    public String id;
    public String title;
    public String description;
    public String imageUrl;
    public Date publishedAt;

    public Video() {
    }

    public Video(String id, String title, String description, String imageUrl, Date publishedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.publishedAt = publishedAt;
    }

    /**
     * Helper methods to convert a YouTube video into Feisty Video object
     * @param videoList from youtube API
     * @return list of videos
     */
    public static List<Video> toVideos(VideoList videoList){
        return toVideos(videoList.videos);
    }

    public static List<Video> toVideos(List<VideoList.Video> videos){
        List<Video> newVideos = new ArrayList<>();
        for (VideoList.Video video : videos){
            try {
                newVideos.add(toVideo(video));
            } catch (IllegalArgumentException e){
                //Video is private therefore no details found
            }
        }
        return newVideos;
    }

    public static Video toVideo(VideoList.Video video) throws IllegalArgumentException{
        if (video.status.privacyStatus.equals("private"))
            throw new IllegalArgumentException("Video is private, no details found");
        return new Video(video.snippet.resourceId.videoId, video.snippet.title, video.snippet.description, video.snippet.thumbnails.high.url, video.snippet.publishedAt);
    }

    public static Video toVideo(Cursor cursor) throws ParseException {
        return new Video(cursor.getString(Contacts.Video.PROJECTION.COLUMN_VIDEO_ID),
                cursor.getString(Contacts.Video.PROJECTION.COLUMN_TITLE),
                cursor.getString(Contacts.Video.PROJECTION.COLUMN_SHORT_DESCRIPTION),
                cursor.getString(Contacts.Video.PROJECTION.COLUMN_IMAGE_URL),
                SyncAdapter.sDateFormatter.parse(cursor.getString(Contacts.Video.PROJECTION.COLUMN_PUBLISHED)));
    }
}
