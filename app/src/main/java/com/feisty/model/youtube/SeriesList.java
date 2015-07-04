package com.feisty.model.youtube;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by Gil on 02/07/15.
 */
public class SeriesList extends RestContainer {

    @SerializedName("items")
    public List<Series> series;
    public ContentDetails contentDetails;

    public class Series {

        public String id;
        public String kind;
        public Snippet snippet;

        public class Snippet {
            public Date publishedAt;
            public String title;
            public String description;
            public Thumbnails thumbnails;
        }
    }

    public class ContentDetails {
        public int itemCount;
    }
}
