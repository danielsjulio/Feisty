package com.feisty.model;

import com.feisty.model.youtube.VideoList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by danieljulio on 21/06/15.
 */
public class Video implements Serializable {

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
            newVideos.add(toVideo(video));
        }
        return newVideos;
    }

    public static Video toVideo(VideoList.Video video){
        return new Video(video.snippet.resourceId.videoId, video.snippet.title, video.snippet.description, video.snippet.thumbnails.high.url, video.snippet.publishedAt);
    }
}
