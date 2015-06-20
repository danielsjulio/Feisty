package com.feisty.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feisty.R;
import com.feisty.model.VideoList;
import com.feisty.net.API;
import com.feisty.utils.Logger;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class RecyclerViewFragment extends Fragment {

    private static final Logger LOG = Logger.create();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    public static RecyclerViewFragment newInstance() {
        return new RecyclerViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        API.getYoutubeService(getActivity()).getVideos(getString(R.string.youtube_channel_id), new Callback<VideoList>() {
            @Override
            public void success(VideoList videoList, Response response) {
                mAdapter = new RecyclerViewMaterialAdapter(new VideoFeedRecyclerViewAdapter(getActivity(), videoList));
                mRecyclerView.setAdapter(mAdapter);
                MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}