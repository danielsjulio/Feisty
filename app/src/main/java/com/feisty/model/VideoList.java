package com.feisty.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Gil on 13/06/15.
 */
public class VideoList extends RestContainer {

    @SerializedName("items")
    List<Video> videos;

    public List<Video> getVideos() {
        return videos;
    }

    public class Video {
        Id id;
        Snippet snippet;

        public class Id {
            String king;
            String videoId;
        }

        public class Snippet {
            //TODO: fix gson date coversion
//            String publishedAt;
            String channelId;
            String title;
            String description;
            Thumbnails thumbnails;
            String channelTittle;
        }
    }
}
