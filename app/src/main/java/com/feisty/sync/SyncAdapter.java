package com.feisty.sync;

import android.accounts.Account;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.TimingLogger;

import com.feisty.R;
import com.feisty.model.Video;
import com.feisty.model.youtube.ChannelList;
import com.feisty.model.youtube.VideoList;
import com.feisty.net.API;
import com.feisty.ui.VideoDetailActivity;
import com.feisty.ui.transformation.RoundedTransformation;
import com.feisty.utils.Logger;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by danieljulio on 20/06/15.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final Logger LOG = Logger.create();
    private static final String FIRST_SYNC = "first_sync";

    private final ContentResolver mContentResolver;

    private static DateFormat sDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    public static int VIDEO_NOTIFICATION_ID = 4237;

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
        LOG.i("Beginning network synchronization");
        try {
            TimingLogger timingLogger = new TimingLogger(LOG.tag, "sync tasks");

            syncChannelVideos(syncResult);
            timingLogger.addSplit("Channel Videos sync");

            syncPlaylists(syncResult);
            timingLogger.addSplit("Playlist sync");

            timingLogger.dumpToLog();
        } catch (MalformedURLException e) {
            LOG.e("Feed URL is malformed", e);
            syncResult.stats.numParseExceptions++;
            return;
        } catch (IOException e) {
            LOG.e("Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
            return;
        } catch (XmlPullParserException e) {
            LOG.e("Error parsing feed: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        } catch (ParseException e) {
            LOG.e("Error parsing feed: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        } catch (RemoteException e) {
            LOG.e("Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        } catch (OperationApplicationException e) {
            LOG.e("Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        } catch (Exception e){
            LOG.e("Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
            return;
        }
        LOG.i("Network synchronization complete");
    }

    private void syncPlaylists(SyncResult syncResult) {

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
    public void syncChannelVideos(final SyncResult syncResult)
            throws IOException, XmlPullParserException, RemoteException,
            OperationApplicationException, ParseException {

        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        final ContentResolver contentResolver = getContext().getContentResolver();

        ChannelList channelList = API.getYoutubeService(getContext()).getChannel(getContext().getString(R.string.youtube_channel_id));
        String uploadsPlaylistId = channelList.channels.get(0).contentDetails.relatedPlaylists.uploads;

        HashMap<String, VideoList.Video> videoMap = new HashMap<>();
        String nextPageToken = null;
        do {
            VideoList videoList;
            if (nextPageToken == null) {
                videoList = API.getYoutubeService(getContext()).getPlaylistItems(uploadsPlaylistId);
            } else {
                videoList = API.getYoutubeService(getContext()).getPlaylistItems(uploadsPlaylistId, nextPageToken);
            }
            nextPageToken = videoList.nextPageToken;
            // Build hash table of incoming entries
            for (VideoList.Video e : videoList.videos) {
                videoMap.put(e.snippet.resourceId.videoId, e);
            }
        } while (nextPageToken != null);

        // Get list of all items
        LOG.i("Fetching local entries for merge");
        Uri uri = Contacts.Video.CONTENT_URI; // Get all entries
        Cursor c = contentResolver.query(uri, Contacts.Video.PROJECTION.PROJECTION, null, null, null);
        assert c != null;
        LOG.i("Found " + c.getCount() + " local entries. Computing merge solution...");

        // Find stale data
        int id;
        String videoId;
        String title;
        String description;
        String imageUrl;
        Date published;
        while (c.moveToNext()) {
            syncResult.stats.numEntries++;
            id = c.getInt(Contacts.Video.PROJECTION.COLUMN_ID);
            videoId = c.getString(Contacts.Video.PROJECTION.COLUMN_VIDEO_ID);
            title = c.getString(Contacts.Video.PROJECTION.COLUMN_TITLE);
            description = c.getString(Contacts.Video.PROJECTION.COLUMN_SHORT_DESCRIPTION);
            imageUrl = c.getString(Contacts.Video.PROJECTION.COLUMN_IMAGE_URL);
            published = sDateFormatter.parse(c.getString(Contacts.Video.PROJECTION.COLUMN_PUBLISHED));
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
                        (!match.snippet.publishedAt.equals(published))) {
                    // Update existing record
                    LOG.i("Scheduling update: " + existingUri);
                    batch.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(Contacts.Video.COLUMN_NAME_TITLE, title)
                            .withValue(Contacts.Video.COLUMN_NAME_SHORT_DESCRIPTION, description)
                            .withValue(Contacts.Video.COLUMN_NAME_IMAGE_URL, imageUrl)
                            .withValue(Contacts.Video.COLUMN_NAME_PUBLISHED, sDateFormatter.format(published))
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    LOG.i("No action: " + existingUri);
                }
            } else {
                // Entry doesn't exist. Remove it from the database.
                Uri deleteUri = Contacts.Video.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();
                LOG.i("Scheduling delete: " + deleteUri);
                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();


        //Show notification if it isn't first sync
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean firstSync = preferences.getBoolean(FIRST_SYNC, true);
        boolean notify = preferences.getBoolean(getContext().getResources().getString(R.string.sp_key_video_notifications), true);

        if (firstSync) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_SYNC, false);
            editor.commit();
        } else if (!notify) {

        } else {
            triggerNotification(videoMap);
        }

        // Add new items
        for (VideoList.Video e : videoMap.values()) {
            LOG.i("Scheduling insert: entry_id=" + e.snippet.resourceId.videoId);
            batch.add(ContentProviderOperation.newInsert(Contacts.Video.CONTENT_URI)
                    .withValue(Contacts.Video.COLUMN_NAME_VIDEO_ID, e.snippet.resourceId.videoId)
                    .withValue(Contacts.Video.COLUMN_NAME_TITLE, e.snippet.title)
                    .withValue(Contacts.Video.COLUMN_NAME_SHORT_DESCRIPTION, e.snippet.description)
                    .withValue(Contacts.Video.COLUMN_NAME_IMAGE_URL, e.snippet.thumbnails.high.url)
                    .withValue(Contacts.Video.COLUMN_NAME_PUBLISHED, sDateFormatter.format(e.snippet.publishedAt))
                    .build());
            syncResult.stats.numInserts++;
        }

        LOG.i("Merge solution ready. Applying batch update");
        mContentResolver.applyBatch(Contacts.CONTENT_AUTHORITY, batch);
        mContentResolver.notifyChange(
                Contacts.Video.CONTENT_URI, // URI where data was modified
                null,                           // No local observer
                false);                         // IMPORTANT: Do not sync to network
        // This sample doesn't support uploads, but if *your* code does, make sure you set
        // syncToNetwork=false in the line above to prevent duplicate syncs.
    }

    private void triggerNotification(HashMap<String, VideoList.Video> map) {
        if (!map.isEmpty()) {

            ArrayList<Video> videos = (ArrayList<Video>) Video.toVideos(new ArrayList<>(map.values()));
            Collections.sort(videos, new Comparator<Video>() {
                @Override
                public int compare(Video lhs, Video rhs) {
                    return lhs.publishedAt.after(rhs.publishedAt) ? 1 : 0;
                }
            });

            Bitmap icon = null;
            try {
                icon = Picasso.with(getContext())
                        .load(R.mipmap.ic_launcher)
                        .resize(128, 128)
                        .centerInside()
                        .transform(new RoundedTransformation(1000, 0))
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            boolean single = videos.size() == 1;
            Video first = videos.get(0);
            String appName = getContext().getString(R.string.app_name);

            Intent intent = VideoDetailActivity.getIntent(getContext(), first);

            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(getContext())
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_new_video_notification)
                    .setAutoCancel(true)
                    .setLargeIcon(icon);

            if(single) {
                notification.setContentTitle(first.title);
                notification.setContentText("New upload from " + appName);
            } else {
                notification.setContentTitle(appName + " uploaded " + videos.size() + " new videos");
                notification.setContentText(first.title);
                String uploadCount = getContext().getResources()
                        .getQuantityString(R.plurals.notification_uploads, videos.size(), videos.size());
                notification.setContentInfo(uploadCount);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                    inboxStyle.setBigContentTitle(first.title);
                    inboxStyle.setSummaryText(appName);
                    notification.setStyle(inboxStyle);
                    for (Video video : videos) {
                        inboxStyle.addLine(video.title);
                    }
                }
            }

            NotificationManager notificationManager =
                    (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(VIDEO_NOTIFICATION_ID, notification.build());
        }
    }
}
