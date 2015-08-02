package com.feisty.model.youtube;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by Gil on 13/06/15.
 */
public class VideoList extends RestContainer {

    @SerializedName("items")
    public List<Video> videos;

    public class Video {
        public String id;
        public Snippet snippet;
        public String kind;

        public Statistics statistics;
        public ContentDetails contentDetails;
        public Status status;

        public class Id {
            public String king;
            public String videoId;
        }

        public class Snippet {
            public Date publishedAt;
            public String channelId;
            public String title;
            public String description;
            public Thumbnails thumbnails;
            public String channelTittle;
            public ResourceId resourceId;

            public class ResourceId {
                public String kind;
                public String videoId;
            }
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

        public class Status {
            public String privacyStatus;
        }
    }
}
