package com.feisty.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Gil on 13/06/15.
 */
public class VideoList extends RestContainer {

    @SerializedName("items")
    public List<Video> videos;

    public class Video {
        public Id id;
        public Snippet snippet;

        public class Id {
            public String king;
            public String videoId;
        }

        public class Snippet {
            //TODO: fix gson date coversion
//            public String publishedAt;
            public String channelId;
            public String title;
            public String description;
            public Thumbnails thumbnails;
            public String channelTittle;
        }
    }
}
