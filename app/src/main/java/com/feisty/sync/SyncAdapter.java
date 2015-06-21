package com.feisty.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.feisty.R;
import com.feisty.model.VideoList;
import com.feisty.net.API;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by danieljulio on 20/06/15.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";

    /**
     * URL to fetch content from during a sync.
     *
     * <p>This points to the Android Developers Blog. (Side note: We highly recommend reading the
     * Android Developer Blog to stay up to date on the latest Android platform developments!)
     */
    private static final String FEED_URL = "http://android-developers.blogspot.com/atom.xml";

    /**
     * Network connection timeout, in milliseconds.
     */
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    /**
     * Network read timeout, in milliseconds.
     */
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    /**
     * Content resolver, for performing database operations.
     */
    private final ContentResolver mContentResolver;

    /**
     * Project used when querying content provider. Returns all known fields.
     */
    private static final String[] PROJECTION = new String[] {
            Contacts.Video._ID,
            Contacts.Video.COLUMN_NAME_VIDEO_ID,
            Contacts.Video.COLUMN_NAME_TITLE,
            Contacts.Video.COLUMN_NAME_SHORT_DESCRIPTION,
            Contacts.Video.COLUMN_NAME_IMAGE_URL,
            Contacts.Video.COLUMN_NAME_PUBLISHED};

    // Constants representing column positions from PROJECTION.
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_VIDEO_ID = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_SHORT_DESCRIPTION = 3;
    public static final int COLUMN_IMAGE_URL = 4;
    public static final int COLUMN_PUBLISHED = 5;

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Called by the Android system in response to a request to run the sync adapter. The work
     * required to read data from the network, parse it, and store it in the content provider is
     * done here. Extending AbstractThreadedSyncAdapter ensures that all methods within SyncAdapter
     * run on a background thread. For this reason, blocking I/O and other long-running tasks can be
     * run <em>in situ</em>, and you don't have to set up a separate thread for them.
     .
     *
     * <p>This is where we actually perform any work required to perform a sync.
     * {@link AbstractThreadedSyncAdapter} guarantees that this will be called on a non-UI thread,
     * so it is safe to peform blocking I/O here.
     *
     * <p>The syncResult argument allows you to pass information back to the method that triggered
     * the sync.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Beginning network synchronization");
        try {
            updateYoutubeData(syncResult);
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "Feed URL is malformed", e);
            syncResult.stats.numParseExceptions++;
            return;
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
            return;
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error parsing feed: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing feed: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        } catch (RemoteException e) {
            Log.e(TAG, "Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        } catch (OperationApplicationException e) {
            Log.e(TAG, "Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        }
        Log.i(TAG, "Network synchronization complete");
    }

    /**
     * Read XML from an input stream, storing it into the content provider.
     *
     * <p>This is where incoming data is persisted, committing the results of a sync. In order to
     * minimize (expensive) disk operations, we compare incoming data with what's already in our
     * database, and compute a merge. Only changes (insert/update/delete) will result in a database
     * write.
     *
     * <p>As an additional optimization, we use a batch operation to perform all database writes at
     * once.
     *
     * <p>Merge strategy:
     * 1. Get cursor to all items in feed<br/>
     * 2. For each item, check if it's in the incoming data.<br/>
     *    a. YES: Remove from "incoming" list. Check if data has mutated, if so, perform
     *            database UPDATE.<br/>
     *    b. NO: Schedule DELETE from database.<br/>
     * (At this point, incoming database only contains missing items.)<br/>
     * 3. For any items remaining in incoming list, ADD to database.
     */
    public void updateYoutubeData(final SyncResult syncResult)
            throws IOException, XmlPullParserException, RemoteException,
            OperationApplicationException, ParseException {

        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        final ContentResolver contentResolver = getContext().getContentResolver();

        HashMap<String, VideoList.Video> videoMap = new HashMap<>();
        String nextPageToken = null;
        do {
            VideoList videoList;
            if (nextPageToken == null) {
                videoList = API.getYoutubeService(getContext()).getVideos(getContext().getString(R.string.youtube_channel_id));
            } else {
                videoList = API.getYoutubeService(getContext()).getVideos(getContext().getString(R.string.youtube_channel_id), nextPageToken);
            }

            nextPageToken = videoList.nextPageToken;
            // Build hash table of incoming entries
            for (VideoList.Video e : videoList.videos) {
                videoMap.put(e.id.videoId, e);
            }
        } while (nextPageToken != null);

        // Get list of all items
        Log.i(TAG, "Fetching local entries for merge");
        Uri uri = Contacts.Video.CONTENT_URI; // Get all entries
        Cursor c = contentResolver.query(uri, PROJECTION, null, null, null);
        assert c != null;
        Log.i(TAG, "Found " + c.getCount() + " local entries. Computing merge solution...");

        // Find stale data
        int id;
        String videoId;
        String title;
        String description;
        String imageUrl;
        String published;
        while (c.moveToNext()) {
            syncResult.stats.numEntries++;
            id = c.getInt(COLUMN_ID);
            videoId = c.getString(COLUMN_VIDEO_ID);
            title = c.getString(COLUMN_TITLE);
            description = c.getString(COLUMN_SHORT_DESCRIPTION);
            imageUrl = c.getString(COLUMN_IMAGE_URL);
            published = c.getString(COLUMN_PUBLISHED);
            VideoList.Video match = videoMap.get(videoId);
            if (match != null) {
                // Entry exists. Remove from entry map to prevent insert later.
                videoMap.remove(videoId);
                // Check to see if the entry needs to be updated
                Uri existingUri = Contacts.Video.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();
                if ((match.snippet.title != null && !match.snippet.title.equals(title)) ||
                        (match.snippet.description != null && !match.snippet.description.equals(description)) ||
                        (match.snippet.thumbnails.high.url != null && !match.snippet.thumbnails.high.url.equals(imageUrl)) ||
                        (match.snippet.publishedAt != published)) {
                    // Update existing record
                    Log.i(TAG, "Scheduling update: " + existingUri);
                    batch.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(Contacts.Video.COLUMN_NAME_TITLE, title)
                            .withValue(Contacts.Video.COLUMN_NAME_SHORT_DESCRIPTION, description)
                            .withValue(Contacts.Video.COLUMN_NAME_IMAGE_URL, imageUrl)
                            .withValue(Contacts.Video.COLUMN_NAME_PUBLISHED, published)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No action: " + existingUri);
                }
            } else {
                // Entry doesn't exist. Remove it from the database.
                Uri deleteUri = Contacts.Video.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();
                Log.i(TAG, "Scheduling delete: " + deleteUri);
                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Add new items
        for (VideoList.Video e : videoMap.values()) {
            Log.i(TAG, "Scheduling insert: entry_id=" + e.id.videoId);
            batch.add(ContentProviderOperation.newInsert(Contacts.Video.CONTENT_URI)
                    .withValue(Contacts.Video.COLUMN_NAME_VIDEO_ID, e.id.videoId)
                    .withValue(Contacts.Video.COLUMN_NAME_TITLE, e.snippet.title)
                    .withValue(Contacts.Video.COLUMN_NAME_SHORT_DESCRIPTION, e.snippet.description)
                    .withValue(Contacts.Video.COLUMN_NAME_IMAGE_URL, e.snippet.thumbnails.high.url)
                    .withValue(Contacts.Video.COLUMN_NAME_PUBLISHED, e.snippet.publishedAt)
                    .build());
            syncResult.stats.numInserts++;
        }
        Log.i(TAG, "Merge solution ready. Applying batch update");
        mContentResolver.applyBatch(Contacts.CONTENT_AUTHORITY, batch);
        mContentResolver.notifyChange(
                Contacts.Video.CONTENT_URI, // URI where data was modified
                null,                           // No local observer
                false);                         // IMPORTANT: Do not sync to network
        // This sample doesn't support uploads, but if *your* code does, make sure you set
        // syncToNetwork=false in the line above to prevent duplicate syncs.
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets an input stream.
     */
    private InputStream downloadUrl(final URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS /* milliseconds */);
        conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
