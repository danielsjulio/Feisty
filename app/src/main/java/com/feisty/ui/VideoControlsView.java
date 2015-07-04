package com.feisty.ui;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.feisty.R;
import com.feisty.utils.Logger;
import com.google.android.youtube.player.YouTubePlayer;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Gil on 15/06/15.
 */
public class VideoControlsView extends FrameLayout implements
        YouTubePlayer.PlaybackEventListener, SeekBar.OnSeekBarChangeListener, YouTubePlayer.PlayerStateChangeListener {

    private static final Logger LOG = Logger.create();

    @Nullable
    YouTubePlayer mYoutubePlayer;

    @InjectView(R.id.buffering_progress_bar)
    ProgressBar mBufferingProgressBar;

    @InjectView(R.id.controls_container)
    View mControlsContainer;

    @InjectView(R.id.video_controls_play)
    ImageButton mPlayBtn;

    @InjectView(R.id.video_controls_pause)
    ImageButton mPauseBtn;

    @InjectView(R.id.video_controls_seeks_container)
    RelativeLayout mProgressSeekbar;

    @InjectView(R.id.video_controls_seekbar_label)
    TextView mSeekBarLabel;

    @InjectView(R.id.seek_bar)
    SeekBar mSeekBar;

    @InjectView(R.id.video_controls_length_label)
    TextView mLengthLabel;

    boolean isBuffering;

    public VideoControlsView(Context context) {
        super(context);
        init();
    }

    public VideoControlsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoControlsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public VideoControlsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.custom_video_overlay, this);
        ButterKnife.inject(view, this);
        mSeekBar.setOnSeekBarChangeListener(this);
        setVisibility(View.GONE);
    }

    private static String formatTime(int milliseconds){
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    public void show(){

        if(mYoutubePlayer != null){
            mSeekBarLabel.setText(formatTime(mYoutubePlayer.getCurrentTimeMillis()));
            mLengthLabel.setText(formatTime(mYoutubePlayer.getDurationMillis()));
            mSeekBar.setMax(mYoutubePlayer.getDurationMillis());
            mSeekBar.setProgress(mYoutubePlayer.getCurrentTimeMillis());
        }

//        mBufferingProgressBar.setVisibility(GONE);
        setVisibility(VISIBLE);
        mControlsContainer.setAlpha(0);
        mControlsContainer.setVisibility(VISIBLE);
        mControlsContainer.setScaleX(1.2F);
        mControlsContainer.setScaleY(1.2F);
        mControlsContainer.animate()
                .alpha(1)
                .scaleX(1F)
                .scaleY(1F)
                .setDuration(200L)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mControlsContainer.setScaleX(1F);
                        mControlsContainer.setScaleY(1F);
                        mControlsContainer.setAlpha(1);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();

        mPlayBtn.setVisibility(GONE);
        /*mPauseBtn.setVisibility(VISIBLE);
        mPauseBtn.setAlpha(0);
        mPauseBtn.setScaleX(0.8F);
        mPauseBtn.setScaleY(0.8F);
        mPauseBtn.animate()
                .setStartDelay(200L)
                .alpha(1)
                .scaleX(1.2F)
                .scaleY(1.2F)
                .setDuration(1000L)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
//                        mPauseBtn.setAlpha(0);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();*/
    }

    public void hide(){
//        mBufferingProgressBar.setVisibility(GONE);
        mControlsContainer.setVisibility(GONE);
        if(!isBuffering)
            setVisibility(GONE);

    }

    @Override
    public void onPlaying() {
        LOG.d("onPlaying");
        hide();
    }

    @Override
    public void onPaused() {
        LOG.d("onPause");
        show();
    }

    @Override
    public void onStopped() {
        LOG.d("onStopped");
        show();
    }

    @Override
    public void onBuffering(boolean b) {
        LOG.d("onBuffering(" + b + ")");
        isBuffering = b;
        setVisibility(b ? VISIBLE : GONE);
        mBufferingProgressBar.setVisibility(b ? VISIBLE : GONE);
    }

    @Override
    public void onSeekTo(int milliseconds) {
        LOG.d("onSeekTo");
        mSeekBarLabel.setText(formatTime(milliseconds));
        mSeekBar.setProgress(milliseconds);
    }

    public void setYoutubePlayer(YouTubePlayer youtubePlayer) {
        this.mYoutubePlayer = youtubePlayer;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(mYoutubePlayer != null && fromUser)
            this.mYoutubePlayer.seekToMillis(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(mYoutubePlayer != null)
            mYoutubePlayer.play();
    }

    @OnClick(R.id.fullscreen)
    public void setFullscreen(View view){
        if (mYoutubePlayer != null)
            mYoutubePlayer.setFullscreen(true);
    }

    @Override
    public void onLoading() {
        LOG.d("onLoading");
        onBuffering(true);
    }

    @Override
    public void onLoaded(String videoId) {
        LOG.d("onLoaded");
        onBuffering(false);
    }

    @Override
    public void onAdStarted() {
        LOG.d("onAdStarted");

    }

    @Override
    public void onVideoStarted() {
        LOG.d("onVideoStarted");
    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }
}
