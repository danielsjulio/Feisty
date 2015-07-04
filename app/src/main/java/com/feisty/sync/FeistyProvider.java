package com.feisty.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by danieljulio on 20/06/15.
 */
public class FeistyProvider extends android.content.ContentProvider {
    FeedDatabase mDatabaseHelper;

    /**
     * Content authority for this provider.
     */
    private static final String AUTHORITY = Contacts.CONTENT_AUTHORITY;

    // The constants below represent individual URI routes, as IDs. Every URI pattern recognized by
    // this ContentProvider is defined using sUriMatcher.addURI(), and associated with one of these
    // IDs.
    //
    // When a incoming URI is run through sUriMatcher, it will be tested against the defined
    // URI patterns, and the corresponding route ID will be returned.
    /**
     * URI ID for route: /videos
     */
    public static final int ROUTE_VIDEOS = 1;

    /**
     * URI ID for route: /videos/{ID}
     */
    public static final int ROUTE_VIDEOS_ID = 2;
    /**
     * URI ID for route: /playlists
     */
    public static final int ROUTE_PLAYLISTS = 3;

    /**
     * URI ID for route: /playlists/{ID}
     */
    public static final int ROUTE_PLAYLISTS_ID = 4;

    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, "videos", ROUTE_VIDEOS);
        sUriMatcher.addURI(AUTHORITY, "videos/*", ROUTE_VIDEOS_ID);
        sUriMatcher.addURI(AUTHORITY, "playlists", ROUTE_VIDEOS);
        sUriMatcher.addURI(AUTHORITY, "playlists/*", ROUTE_VIDEOS_ID);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new FeedDatabase(getContext());
        return true;
    }

    /**
     * Determine the mime type for entries returned by a given URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_VIDEOS:
                return Contacts.Video.CONTENT_TYPE;
            case ROUTE_VIDEOS_ID:
                return Contacts.Video.CONTENT_ITEM_TYPE;
            case ROUTE_PLAYLISTS:
                return Contacts.Playlist.CONTENT_TYPE;
            case ROUTE_PLAYLISTS_ID:
                return Contacts.Playlist.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Perform a database query by URI.
     *
     * <p>Currently supports returning all entries (/entries) and individual entries by ID
     * (/entries/{ID}).
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        int uriMatch = sUriMatcher.match(uri);
        String id;
        Context ctx = getContext();
        switch (uriMatch) {
            case ROUTE_VIDEOS_ID:
                // Return a single entry, by ID.
                id = uri.getLastPathSegment();
                builder.where(Contacts.Video._ID + "=?", id);
            case ROUTE_VIDEOS:
                // Return all known entries.
                builder.table(Contacts.Video.TABLE_NAME)
                        .where(selection, selectionArgs);
                Cursor c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
            case ROUTE_PLAYLISTS_ID:
                // Return a single entry, by ID.
                id = uri.getLastPathSegment();
                builder.where(Contacts.Video._ID + "=?", id);
            case ROUTE_PLAYLISTS:
                // Return all known entries.
                builder.table(Contacts.Video.TABLE_NAME)
                        .where(selection, selectionArgs);
                Cursor playlistCursor = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                assert ctx != null;
                playlistCursor.setNotificationUri(ctx.getContentResolver(), uri);
                return playlistCursor;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Insert a new entry into the database.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        assert db != null;
        final int match = sUriMatcher.match(uri);
        Uri result;
        long id;
        switch (match) {
            case ROUTE_PLAYLISTS:
                id = db.insertOrThrow(Contacts.Playlist.TABLE_NAME, null, values);
                result = Uri.parse(Contacts.Playlist.CONTENT_URI + "/" + id);
                break;
            case ROUTE_VIDEOS:
                id = db.insertOrThrow(Contacts.Video.TABLE_NAME, null, values);
                result = Uri.parse(Contacts.Video.CONTENT_URI + "/" + id);
                break;
            case ROUTE_PLAYLISTS_ID:
            case ROUTE_VIDEOS_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    /**
     * Delete an entry by database by URI.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        String id;
        switch (match) {
            case ROUTE_VIDEOS:
                count = builder.table(Contacts.Video.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_VIDEOS_ID:
                id = uri.getLastPathSegment();
                count = builder.table(Contacts.Video.TABLE_NAME)
                        .where(Contacts.Video._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_PLAYLISTS:
                count = builder.table(Contacts.Playlist.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_PLAYLISTS_ID:
                id = uri.getLastPathSegment();
                count = builder.table(Contacts.Playlist.TABLE_NAME)
                        .where(Contacts.Playlist._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * Update an etry in the database by URI.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        String id;
        switch (match) {
            case ROUTE_VIDEOS:
                count = builder.table(Contacts.Video.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_VIDEOS_ID:
                id = uri.getLastPathSegment();
                count = builder.table(Contacts.Video.TABLE_NAME)
                        .where(Contacts.Video._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_PLAYLISTS:
                count = builder.table(Contacts.Playlist.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_PLAYLISTS_ID:
                id = uri.getLastPathSegment();
                count = builder.table(Contacts.Playlist.TABLE_NAME)
                        .where(Contacts.Playlist._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * SQLite backend for @{link FeedProvider}.
     *
     * Provides access to an disk-backed, SQLite datastore which is utilized by FeedProvider. This
     * database should never be accessed by other parts of the application directly.
     */
    static class FeedDatabase extends SQLiteOpenHelper {
        /** Schema version. */
        public static final int DATABASE_VERSION = 1;
        /** Filename for SQLite file. */
        public static final String DATABASE_NAME = "video.db";

        private static final String PRIMARY_KEY = " PRIMARY KEY";
        private static final String TYPE_TEXT = " TEXT";
        private static final String TYPE_INTEGER = " INTEGER";
        private static final String COMMA_SEP = ",";

        /** SQL statement to create "video" table. */
        private static final String SQL_CREATE_VIDEO_TABLE =
                "CREATE TABLE " + Contacts.Video.TABLE_NAME + " (" +
                        Contacts.Video._ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP +
                        Contacts.Video.COLUMN_NAME_VIDEO_ID + TYPE_TEXT + COMMA_SEP +
                        Contacts.Video.COLUMN_NAME_TITLE + TYPE_TEXT + COMMA_SEP +
                        Contacts.Video.COLUMN_NAME_SHORT_DESCRIPTION + TYPE_TEXT + COMMA_SEP +
                        Contacts.Video.COLUMN_NAME_IMAGE_URL + TYPE_TEXT + COMMA_SEP +
                        Contacts.Video.COLUMN_NAME_PUBLISHED + TYPE_TEXT + ")";

        public static final String SQL_CREATE_PLAYLIST_TABLE =
                "CREATE TABLE " + Contacts.Playlist.TABLE_NAME + " (" +
                        Contacts.Playlist._ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP +
                        Contacts.Playlist.COLUMN_NAME_PLAYLIST_ID + TYPE_TEXT + COMMA_SEP +
                        Contacts.Playlist.COLUMN_NAME_TITLE + TYPE_TEXT + ")";

        private String deleteTable(String tableName) {
            return "DROP TABLE IF EXISTS " + tableName;
        }

        public FeedDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_VIDEO_TABLE);
            db.execSQL(SQL_CREATE_PLAYLIST_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(deleteTable(Contacts.Video.TABLE_NAME));
            db.execSQL(deleteTable(Contacts.Playlist.TABLE_NAME));
            onCreate(db);
        }
    }
}
