package com.feisty.ui.listeners;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Gil on 24/06/15.
 */
public class InfinityScrollListener extends RecyclerView.OnScrollListener {

    ScrollResultListener mScrollResultListener;
    boolean mLoading = false;
    int mScrollY = 0;

    @Nullable
    String mNextPageToken;

    public InfinityScrollListener(ScrollResultListener listener) {
        super();
        mScrollResultListener = listener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mScrollY += dy;
        if(!mLoading && mNextPageToken != null && mScrollY < 1000){
            mScrollResultListener.loadMore(mNextPageToken);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    public void finishedLoad(){
        setLoading(true);
    }

    public void setLoading(boolean mLoading) {
        this.mLoading = mLoading;
    }

    public void setNextPageToken(String nextPageToken) {
        this.mNextPageToken = nextPageToken;
    }

    public interface ScrollResultListener {
        void loadMore(String nextPageToken);
    }
}
