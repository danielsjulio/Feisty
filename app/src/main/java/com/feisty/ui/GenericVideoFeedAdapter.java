package com.feisty.ui;

/**
 * Created by Gil on 03/07/15.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feisty.R;
import com.feisty.model.Video;
import com.feisty.utils.Logger;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class GenericVideoFeedAdapter extends RecyclerView.Adapter<GenericVideoFeedAdapter.ViewHolder> {

    private static final Logger LOG = Logger.create();

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    Context mContext;

    public GenericVideoFeedAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public abstract int getItemCount();

    public abstract Video getItem(int position);

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            default:
                return TYPE_HEADER;
        }
    }

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

                final Video video = getItem(position);
                Picasso.with(mContext)
                        .load(video.imageUrl)
                        .into(holder.thumbnail);
                holder.description.setText(video.description);
                holder.title.setText(video.title);


                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VideoDetailActivity.startActivity(mContext, video);
                    }
                });
                break;
            case TYPE_CELL:
                break;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder  {

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
        }
    }
}