package com.feisty.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
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

    class Channel {
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

    class Snippet {
        String title;
        String description;
        Date publishedAt;
        Thumbnails thumbnails;

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public Date getPublishedAt() {
            return publishedAt;
        }

        public Thumbnails getThumbnails() {
            return thumbnails;
        }

        class Thumbnails {
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

            class Thumbnail {
                String url;

                public String getUrl() {
                    return url;
                }
            }
        }
    }



    class ContentDetails {
        Channel channel;

        public Channel getChannel() {
            return channel;
        }

        class Channel {
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

        class Images {
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
