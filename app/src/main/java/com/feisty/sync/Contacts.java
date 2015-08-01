package com.feisty.sync;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.feisty.App;
import com.feisty.R;

/**
 * Created by danieljulio on 20/06/15.
 */
public class Contacts {


    private Contacts() {
    }

    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = App.getContext().getString(R.string.content_authority);

    /**
     * Base URI. (content://com.feisty[.username][.debug])
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    /**
     * Columns supported by "entries" records.
     */
    public static class Video implements BaseColumns {
        /**
         * Path component for "entry"-type resources..
         */
        private static final String PATH_VIDEOS = "videos";

        /**
         * MIME type for lists of videos.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.feisty.videos";
        /**
         * MIME type for individual videos.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.feisty.video";

        /**
         * Fully qualified URI for "video" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "video";
        /**
         * Atom ID. (Note: Not to be confused with the database primary key, which is _ID.
         */
        public static final String COLUMN_NAME_VIDEO_ID = "video_id";
        /**
         * Article title
         */
        public static final String COLUMN_NAME_TITLE = "title";
        /**
         * Article title
         */
        public static final String COLUMN_NAME_SHORT_DESCRIPTION = "short_description";
        /**
         * Article title
         */
        public static final String COLUMN_NAME_IMAGE_URL = "image_url";
        /**
         * Date article was published.
         */
        public static final String COLUMN_NAME_PUBLISHED = "published";

        public static class PROJECTION {

            // Constants representing column positions from PROJECTION.
            public static final int COLUMN_ID = 0;
            public static final int COLUMN_VIDEO_ID = 1;
            public static final int COLUMN_TITLE = 2;
            public static final int COLUMN_SHORT_DESCRIPTION = 3;
            public static final int COLUMN_IMAGE_URL = 4;
            public static final int COLUMN_PUBLISHED = 5;
            /**
             * Project used when querying content provider. Returns all known fields.
             */
            public static final String[] PROJECTION = new String[] {
                    _ID,
                    COLUMN_NAME_VIDEO_ID,
                    COLUMN_NAME_TITLE,
                    COLUMN_NAME_SHORT_DESCRIPTION,
                    COLUMN_NAME_IMAGE_URL,
                    COLUMN_NAME_PUBLISHED};
        }
    }


    public static class Playlist implements BaseColumns {
        /**
         * Path component for "entry"-type resources..
         */
        private static final String PATH_PLAYLISTS = "playlists";

        /**
         * MIME type for lists of videos.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.feisty.playlists";
        /**
         * MIME type for individual videos.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.feisty.playlist";

        /**
         * Fully qualified URI for "playlist" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYLISTS).build();

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "playlist";

        /**
         * YouTube playlist ID. (Note: Not to be confused with the database primary key, which is _ID.
         */
        public static final String COLUMN_NAME_PLAYLIST_ID = "playlist_id";

        /**
         * Title of the playlist
         */
        public static final String COLUMN_NAME_TITLE = "title";

        public static class PROJECTION {

            public static final int COLUMN_ID = 0;
            public static final int COLUMN_PLAYLIST_ID = 1;
            public static final int COLUMN_NAME = 1;

            public static final String[] FULL = new String[]{
                    _ID,
                    COLUMN_NAME_PLAYLIST_ID,
                    COLUMN_NAME_TITLE
            };
        }
    }

}
