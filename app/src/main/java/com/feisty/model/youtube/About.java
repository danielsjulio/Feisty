package com.feisty.model.youtube;

import java.util.List;

/**
 * Created by danieljulio on 01/08/15.
 */
public class About extends RestContainer {

    public List<ChannelDetails> items;

    public class ChannelDetails {

        public Snippet snippet;
        public Statistics statistics;
    }

    public class Snippet {
        public String title;
        public String description;
        /*public Date publishedAt;*/
        public Thumbnails thumbnails;

    }

    public class Statistics {

        public String viewCount;
        public String subscriberCount;
        public String videoCount;
    }
}
