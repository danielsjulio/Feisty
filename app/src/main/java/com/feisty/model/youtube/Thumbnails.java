package com.feisty.model.youtube;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gil on 13/06/15.
 */
public class Thumbnails {
    @SerializedName("default")
    public Thumbnail regular;
    public Thumbnail medium;
    public Thumbnail high;

    public class Thumbnail {
        public String url;

    }
}
