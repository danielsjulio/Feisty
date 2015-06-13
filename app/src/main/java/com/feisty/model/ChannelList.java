package com.feisty.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Gil on 08/06/15.
 */
public class ChannelList extends RestContainer {


    @SerializedName("items")
    public List<Channel> channels;

    public class Channel {
        public String id;
        public Snippet snippet;

        @SerializedName("brandingSettings")
        public ContentDetails contentDetails;
    }

    public class Snippet {
        public String title;
        public String description;
        /*public Date publishedAt;*/
        public Thumbnails thumbnails;

    }

    public class ContentDetails {
        public Channel channel;

        @SerializedName("image")
        public Images images;

        public class Channel {
            public String title;
            public String description;
            public String profileColor;
        }

        public class Images {

            public String bannerImageUrl;
            public String bannerMobileImageUrl;
            public String bannerTabletLowImageUrl;
            public String bannerTabletImageUrl;
            public String bannerTabletHdImageUrl;
            public String bannerTabletExtraHdImageUrl;
            public String bannerMobileLowImageUrl;
            public String bannerMobileMediumHdImageUrl;
            public String bannerMobileHdImageUrl;
            public String bannerMobileExtraHdImageUrl;
            public String bannerTvImageUrl;
            public String bannerTvLowImageUrl;
            public String bannerTvMediumImageUrl;
            public String bannerTvHighImageUrl;
        }
    }

}
