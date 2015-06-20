package com.feisty.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feisty.R;
import com.feisty.model.VideoList;
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

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    public VideoFeedRecyclerViewAdapter(Context context, VideoList video) {
        this.mVideo = video;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            default:
                return TYPE_HEADER;
        }
    }

    @Override
    public int getItemCount() {
        return mVideo.videos.size();
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

                Picasso.with(mContext)
                        .load(mVideo.videos.get(position).snippet.thumbnails.high.url)
                        .into(holder.thumbnail);
                holder.description.setText(mVideo.videos.get(position).snippet.description);
                holder.title.setText(mVideo.videos.get(position).snippet.title);

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