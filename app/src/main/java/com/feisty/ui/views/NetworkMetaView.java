package com.feisty.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.feisty.R;
import com.feisty.ui.SeriesEpisodesActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Gil on 30/07/15.
 */
public class NetworkMetaView extends FrameLayout implements Callback, View.OnClickListener {

    @InjectView(R.id.error_no_internet)
    View mErrorNoInternetView;

    @InjectView(R.id.progress)
    ProgressBar mProgress;

    @InjectView(R.id.error_server)
    View mErrorServer;

    private View mContainer;
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener;
    private boolean isInflated;

    public NetworkMetaView(Context context) {
        this(context, null);
        init();
    }

    public NetworkMetaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public NetworkMetaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.network_container, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this, getRootView());
        isInflated = true;
        setOnClickListener(this);
        if(mContainer != null){
            showProgress();
        }
    }

    public void setNetworkResultsContainer(View mContainer) {
        this.mContainer = mContainer;
        if(isInflated){
            showProgress();
        }
    }

    public void showNetworkError(){
        setVisibility(VISIBLE);
        mContainer.setVisibility(GONE);
        mProgress.setVisibility(GONE);
        mErrorServer.setVisibility(GONE);
        mErrorNoInternetView.setVisibility(VISIBLE);
    }

    public void showProgress(){
        setVisibility(VISIBLE);
        mContainer.setVisibility(GONE);
        mErrorNoInternetView.setVisibility(GONE);
        mErrorServer.setVisibility(GONE);
        mProgress.setVisibility(VISIBLE);
    }

    public void showServerError(){
        setVisibility(VISIBLE);
        mContainer.setVisibility(GONE);
        mErrorNoInternetView.setVisibility(GONE);
        mProgress.setVisibility(GONE);
        mErrorServer.setVisibility(VISIBLE);
    }

    public void showResponse(){
        mErrorNoInternetView.setVisibility(GONE);
        mProgress.setVisibility(GONE);
        mErrorServer.setVisibility(GONE);
        mContainer.setVisibility(VISIBLE);
        setVisibility(GONE);
    }

    @Override
    public void success(Object o, Response response) {
        showResponse();
    }

    @Override
    public void failure(RetrofitError error) {
        if (error.getKind() == RetrofitError.Kind.NETWORK){
            showNetworkError();
        } else if(error.getKind() == RetrofitError.Kind.HTTP){
            if(error.getResponse().getStatus() / 100 == 5) {
                showServerError();
            }
        }
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        mRefreshListener = onRefreshListener;
    }


    @Override
    public void onClick(View v) {
        if(mErrorServer.getVisibility() == VISIBLE || mErrorNoInternetView.getVisibility() == VISIBLE) {
            if (mRefreshListener != null) {
                showProgress();
                mRefreshListener.onRefresh();
            }
        }

    }
}
