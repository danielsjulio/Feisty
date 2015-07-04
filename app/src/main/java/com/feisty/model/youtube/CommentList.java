package com.feisty.model.youtube;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by Gil on 22/06/15.
 */
public class CommentList extends RestContainer {

    @SerializedName("items")
    public List<Comment> comments;

    public class Comment {
        public String kind;
        public String id;
        public Snippet snippet;
        public Replies replies;

        public class Snippet {

            public String channelId;
            public String videoId;
            public TopLevelComment topLevelComment;
            public int totalReplyCount;

            public class TopLevelComment {
                public String kind;
                public String id;
                public TopLevelSnippet snippet;

                public class TopLevelSnippet {

                    /**
                     * HTML may be embebed
                     */
                    public String textDisplay;
                    public String authorDisplayName;
                    public String authorProfileImageUrl;
                    public String authorChannelUrl;
                    public String authorGoogleplusProfileUrl;
                    public String viewerRating;
                    public int likeCount;
                    public Date publishedAt; //TODO: GSON causing an error from 2009-08-24T19:58:09.000Z
                    public Date updatedAt;
                }
            }
        }

        public class Replies {

            public List<Snippet.TopLevelComment> comments;
        }
    }

}
