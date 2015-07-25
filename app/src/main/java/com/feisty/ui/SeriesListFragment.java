package com.feisty.ui;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feisty.R;
import com.feisty.model.Playlist;
import com.feisty.model.youtube.SeriesList;
import com.feisty.net.API;
import com.feisty.ui.listeners.InfinityScrollListener;
import com.feisty.ui.transformation.PaletteTransformation;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeriesListFragment extends Fragment implements Callback<SeriesList>, InfinityScrollListener.ScrollResultListener {

    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    SeriesAdapter mSeriesAdapter;
    RecyclerViewMaterialAdapter mAdapter;

    InfinityScrollListener mInfinityScrollListener;

    public SeriesListFragment() {
        // Required empty public constructor
    }


    public static SeriesListFragment newInstance() {
        return new SeriesListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_series_list, container, false);
        ButterKnife.inject(this, view);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSeriesAdapter = new SeriesAdapter();
        mAdapter = new RecyclerViewMaterialAdapter(mSeriesAdapter);
        mRecyclerView.setAdapter(mAdapter);
        mInfinityScrollListener = new InfinityScrollListener(this);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, mInfinityScrollListener);
        loadMore(null);
        return view;
    }

    @Override
    public void success(SeriesList seriesList, Response response) {
        mInfinityScrollListener.setNextPageToken(seriesList.nextPageToken);
        mInfinityScrollListener.setLoading(false);

        int startRange = mAdapter.getItemCount();
        mSeriesAdapter.getSeries().addAll(seriesList.series);
        mAdapter.notifyItemRangeInserted(startRange, seriesList.series.size());
        mInfinityScrollListener.setNextPageToken(seriesList.nextPageToken);
    }

    @Override
    public void failure(RetrofitError error) {

    }

    @Override
    public void loadMore(String nextPageToken) {
        if(nextPageToken == null) {
            API.getYoutubeService(getActivity()).getPlaylists(getString(R.string.youtube_channel_id), this);
        } else {
            API.getYoutubeService(getActivity()).getPlaylists(getString(R.string.youtube_channel_id), nextPageToken, this);
        }
    }


    class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.ViewHolder> {

        List<SeriesList.Series> mSeries = new ArrayList<>();

        public List<SeriesList.Series> getSeries() {
            return mSeries;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.list_item_card_small_playlist, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final SeriesList.Series series = mSeries.get(position);
            holder.reset(); //resets the viewholder back to its initial state
            holder.mTitleView.setText(series.snippet.title);
            if(!series.snippet.description.isEmpty()) {
                holder.mDescriptionView.setText(series.snippet.description);
                holder.mDescriptionView.setVisibility(View.VISIBLE);
            }
            holder.mEpisodeCount.setText(String.valueOf(series.contentDetails.itemCount));
            Picasso.with(getActivity()).load(series.snippet.thumbnails.high.url)
//                    .transform(PaletteTransformation.instance())
                    .into(holder.mThumbnailView/*, new PaletteTransformation.PaletteCallback(holder.mThumbnailView) {
                        @Override
                        public void onSuccess(Palette palette) {
//                            animateBackgroundColor(holder.mCardView, getResources().getColor(R.color.default_card_background),
//                                    palette.getVibrantColor(getResources().getColor(R.color.default_card_background)));
                        }

                        @Override
                        public void onError() {

                        }
                    }*/);
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeriesEpisodesActivity.startActivity(getActivity(),
                            new Playlist(series.id, series.snippet.title, series.snippet.description, series.snippet.thumbnails.high.url));
                }
            });
        }

        void animateBackgroundColor(final View view, int currentColor, int newColor){
            final ObjectAnimator backgroundColorAnimator = ObjectAnimator.ofObject(view,
                    "backgroundColor",
                    new ArgbEvaluator(),
                    currentColor,
                    newColor);
            backgroundColorAnimator.setDuration(300);
            backgroundColorAnimator.start();
            view.setTag(backgroundColorAnimator);

        }

        @Override
        public int getItemCount() {
            return mSeries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @InjectView(R.id.card_view)
            CardView mCardView;

            @InjectView(R.id.video_name)
            TextView mTitleView;

            @InjectView(R.id.playlist_description)
            TextView mDescriptionView;

            @InjectView(R.id.video_thumbnail)
            ImageView mThumbnailView;

            @InjectView(R.id.episode_count)
            TextView mEpisodeCount;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }

            public void reset(){
                mDescriptionView.setVisibility(View.GONE);

//                if(mCardView.getTag() != null){
//                    ((ObjectAnimator) mCardView.getTag()).cancel();
//                }
//                mCardView.setBackgroundColor(getResources().getColor(R.color.default_card_background));
            }
        }
    }
}
