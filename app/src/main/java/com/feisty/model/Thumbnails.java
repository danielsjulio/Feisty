package com.feisty.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gil on 13/06/15.
 */
public class Thumbnails {
    @SerializedName("default")
    Thumbnail regular;
    Thumbnail medium;
    Thumbnail high;

    public Thumbnail getRegular() {
        return regular;
    }

    public Thumbnail getMedium() {
        return medium;
    }

    public Thumbnail getHigh() {
        return high;
    }

    public class Thumbnail {
        String url;

        public String getUrl() {
            return url;
        }
    }
}
