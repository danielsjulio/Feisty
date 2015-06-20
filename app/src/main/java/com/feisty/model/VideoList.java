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
        public String kind;

        public Statistics statistics;
        public ContentDetails contentDetails;

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

        public class Statistics {

            public int viewCount;
            public int likeCount;
            public int dislikeCount;
            public int favoriteCount;
            public int commentCount;
        }

        public class ContentDetails {
            public String duration;//weird format, example: PT2H8M28S
            public String dimension;//wether it is 3d or 2d
            public String definition;//example: "hd"
            public boolean licensedContent;

        }
    }
}
