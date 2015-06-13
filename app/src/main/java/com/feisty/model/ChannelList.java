package com.feisty.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Gil on 08/06/15.
 */
public class ChannelList extends RestContainer {


    @SerializedName("items")
    List<Channel> channels;

    public List<Channel> getChannels() {
        return channels;
    }

    public class Channel {
        String id;
        Snippet snippet;

        @SerializedName("brandingSettings")
        ContentDetails contentDetails;

        public String getId() {
            return id;
        }

        public Snippet getSnippet() {
            return snippet;
        }

        public ContentDetails getContentDetails() {
            return contentDetails;
        }
    }

    public class Snippet {
        String title;
        String description;
        /*Date publishedAt;*/
        Thumbnails thumbnails;

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        /*public Date getPublishedAt() {
            return publishedAt;
        }*/

        public Thumbnails getThumbnails() {
            return thumbnails;
        }

    }



    public class ContentDetails {
        Channel channel;

        @SerializedName("image")
        Images images;

        public Images getImage() {
            return images;
        }

        public Channel getChannel() {
            return channel;
        }

        public class Channel {
            String title;
            String description;
            String profileColor;

            public String getTitle() {
                return title;
            }

            public String getDescription() {
                return description;
            }

            public String getProfileColor() {
                return profileColor;
            }
        }

        public class Images {

            String bannerImageUrl;
            String bannerMobileImageUrl;
            String bannerTabletLowImageUrl;
            String bannerTabletImageUrl;
            String bannerTabletHdImageUrl;
            String bannerTabletExtraHdImageUrl;
            String bannerMobileLowImageUrl;
            String bannerMobileMediumHdImageUrl;
            String bannerMobileHdImageUrl;
            String bannerMobileExtraHdImageUrl;
            String bannerTvImageUrl;
            String bannerTvLowImageUrl;
            String bannerTvMediumImageUrl;
            String bannerTvHighImageUrl;

            public String getBannerImageUrl() {
                return bannerImageUrl;
            }

            public String getBannerMobileImageUrl() {
                return bannerMobileImageUrl;
            }

            public String getBannerTabletLowImageUrl() {
                return bannerTabletLowImageUrl;
            }

            public String getBannerTabletImageUrl() {
                return bannerTabletImageUrl;
            }

            public String getBannerTabletHdImageUrl() {
                return bannerTabletHdImageUrl;
            }

            public String getBannerTabletExtraHdImageUrl() {
                return bannerTabletExtraHdImageUrl;
            }

            public String getBannerMobileLowImageUrl() {
                return bannerMobileLowImageUrl;
            }

            public String getBannerMobileMediumHdImageUrl() {
                return bannerMobileMediumHdImageUrl;
            }

            public String getBannerMobileHdImageUrl() {
                return bannerMobileHdImageUrl;
            }

            public String getBannerMobileExtraHdImageUrl() {
                return bannerMobileExtraHdImageUrl;
            }

            public String getBannerTvImageUrl() {
                return bannerTvImageUrl;
            }

            public String getBannerTvLowImageUrl() {
                return bannerTvLowImageUrl;
            }

            public String getBannerTvMediumImageUrl() {
                return bannerTvMediumImageUrl;
            }

            public String getBannerTvHighImageUrl() {
                return bannerTvHighImageUrl;
            }
        }
    }

}
