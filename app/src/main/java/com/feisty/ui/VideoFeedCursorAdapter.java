package com.feisty.ui;

import android.content.Context;
import android.database.Cursor;

import com.feisty.model.Video;
import com.feisty.sync.Contacts;


/**
 * Created by florentchampigny on 24/04/15.
 */
public class VideoFeedCursorAdapter extends GenericVideoFeedAdapter {


    Cursor mCursor;

    public VideoFeedCursorAdapter(Context context, Cursor cursor) {
        super(context);
        this.mCursor = cursor;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor cursor) {
        if (mCursor == cursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        this.mCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    @Override
    public Video getItem(int position) {
        mCursor.moveToPosition(position);

        Video video = new Video();
        video.id = (mCursor.getString(mCursor.getColumnIndex(Contacts.Video.COLUMN_NAME_VIDEO_ID)));
        video.title = (mCursor.getString(mCursor.getColumnIndex(Contacts.Video.COLUMN_NAME_TITLE)));
        video.description = (mCursor.getString(mCursor.getColumnIndex(Contacts.Video.COLUMN_NAME_SHORT_DESCRIPTION)));
        video.imageUrl = (mCursor.getString(mCursor.getColumnIndex(Contacts.Video.COLUMN_NAME_IMAGE_URL)));
        video.publishedAt = (mCursor.getString(mCursor.getColumnIndex(Contacts.Video.COLUMN_NAME_PUBLISHED)));

        return video;
    }
}