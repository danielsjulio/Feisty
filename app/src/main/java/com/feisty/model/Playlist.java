package com.feisty.model;

import java.io.Serializable;

/**
 * Created by Gil on 04/07/15.
 */
public class Playlist implements Serializable {

    public String id;
    public String title;
    String description;
    public String thumbnail;

    public Playlist(String id, String title, String description, String thumbnail) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
    }
}
