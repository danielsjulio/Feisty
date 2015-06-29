package com.feisty.ui;

import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feisty.R;
import com.feisty.sync.Contacts;
import com.feisty.utils.Logger;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class RecyclerViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final Logger LOG = Logger.create();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    String [] mProjection = {
            Contacts.Video.COLUMN_NAME_VIDEO_ID,
            Contacts.Video.COLUMN_NAME_TITLE,
            Contacts.Video.COLUMN_NAME_SHORT_DESCRIPTION,
            Contacts.Video.COLUMN_NAME_IMAGE_URL,
            Contacts.Video.COLUMN_NAME_PUBLISHED};

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
        getLoaderManager().initLoader(0, null, this);

//        API.getYoutubeService(getActivity()).getVideos(getString(R.string.youtube_channel_id), new Callback<VideoList>() {
//            @Override
//            public void success(VideoList videoList, Response response) {
//                mAdapter = new RecyclerViewMaterialAdapter(new VideoFeedRecyclerViewAdapter(getActivity(), videoList));
//                mRecyclerView.setAdapter(mAdapter);
//                MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//
//            }
//        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        return new CursorLoader(getActivity(),  // Context
                Contacts.Video.CONTENT_URI, // URI
                mProjection,                // Projection
                null,                           // Selection
                null,                           // Selection args
                Contacts.Video.COLUMN_NAME_PUBLISHED + " desc"); // Sort
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        LOG.d("Got here");
        mAdapter = new RecyclerViewMaterialAdapter(new VideoFeedRecyclerViewAdapter(getActivity(), data));
        mRecyclerView.setAdapter(mAdapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}