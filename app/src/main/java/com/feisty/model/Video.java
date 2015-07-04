package com.feisty.model;

import com.feisty.model.youtube.VideoList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by danieljulio on 21/06/15.
 */
public class Video implements Serializable {

    public String id;
    public String title;
    public String description;
    public String imageUrl;
    public String publishedAt;

    public Video() {
    }

    public Video(String id, String title, String description, String imageUrl, String publishedAt) {
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
        List<Video> videos = new ArrayList<>();
        for (VideoList.Video video : videoList.videos){
            videos.add(toVideo(video));
        }
        return videos;
    }

    public static Video toVideo(VideoList.Video video){
        return new Video(video.snippet.resourceId.videoId, video.snippet.title, video.snippet.description, video.snippet.thumbnails.high.url, video.snippet.publishedAt);
    }
}
