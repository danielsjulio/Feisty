package com.feisty.ui;

import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class VideosListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final Logger LOG = Logger.create();

    private RecyclerView.Adapter mAdapter;

    @InjectView(R.id.progress)
    View mProgressView;

    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private int mColumnCount;

    public static VideosListFragment newInstance() {
        return new VideosListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mColumnCount = getResources().getInteger(R.integer.column_count);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_video, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), mColumnCount);
        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.addItemDecoration(new SpacesItemDecoration(getActivity(),
//                R.dimen.cardMarginHorizontal, R.dimen.cardMarginVertical));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mProgressView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),  // Context
                Contacts.Video.CONTENT_URI, // URI
                Contacts.Video.PROJECTION.PROJECTION,// Projection
                null,                           // Selection
                null,                           // Selection args
                Contacts.Video.COLUMN_NAME_PUBLISHED + " desc"); // Sort
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new RecyclerViewMaterialAdapter(new VideoFeedCursorAdapter(getActivity(), data), mColumnCount);
        mRecyclerView.setAdapter(mAdapter);
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);

        mProgressView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}