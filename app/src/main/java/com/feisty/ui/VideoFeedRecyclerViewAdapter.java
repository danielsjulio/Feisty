package com.feisty.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feisty.R;
import com.feisty.model.Vid;
import com.feisty.model.VideoList;
import com.feisty.sync.Contacts;
import com.feisty.utils.Logger;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class VideoFeedRecyclerViewAdapter extends RecyclerView.Adapter<VideoFeedRecyclerViewAdapter.ViewHolder> {


    private static final Logger LOG = Logger.create();

    VideoList mVideo;
    Context mContext;
    Cursor mCursor;

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    public VideoFeedRecyclerViewAdapter(Context context, Cursor cursor) {
        this.mCursor = cursor;
        this.mContext = context;
    }


    ///////////////////////
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

    private Vid getItem(int position) {
        mCursor.moveToPosition(position);

        Vid vid = new Vid();
        vid.setTitle(mCursor.getString(mCursor.getColumnIndex(Contacts.Video.COLUMN_NAME_TITLE)));
        vid.setDescription(mCursor.getString(mCursor.getColumnIndex(Contacts.Video.COLUMN_NAME_SHORT_DESCRIPTION)));
        vid.setImageUrl(mCursor.getString(mCursor.getColumnIndex(Contacts.Video.COLUMN_NAME_IMAGE_URL)));
        vid.setPublishedAt(mCursor.getString(mCursor.getColumnIndex(Contacts.Video.COLUMN_NAME_PUBLISHED)));

        return vid;
        // Load data from dataCursor and return it...
    }
    ////////////////////////////////

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            default:
                return TYPE_HEADER;
        }
    }

//    @Override
//    public int getItemCount() {
//        return mVideo.videos.size();
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_big, parent, false);

                return new ViewHolder(view) {
                };
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_small, parent, false);
                return new ViewHolder(view) {
                };
            }
        }
        return null;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:

                Vid vid = getItem(position);
                Picasso.with(mContext)
                        .load(vid.getImageUrl())
                        .into(holder.thumbnail);
                holder.description.setText(vid.getDescription());
                holder.title.setText(vid.getTitle());

                break;
            case TYPE_CELL:
                break;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.card_view)
        CardView card;

        @InjectView(R.id.video_list_image)
        ImageView thumbnail;

        @InjectView(R.id.video_list_description)
        TextView description;

        @InjectView(R.id.video_list_title)
        TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            card.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), VideoDetailActivity.class);
            v.getContext().startActivity(intent);
        }
    }
}