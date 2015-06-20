package com.feisty.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.feisty.R;
import com.feisty.utils.Logger;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

/**
 * Created by Gil on 06/02/15.
 */

public final class VideoFragment extends YouTubePlayerSupportFragment
        implements YouTubePlayer.OnInitializedListener {

    private static final Logger LOG = Logger.create();

    @Nullable
    private YouTubePlayer player;

    private String mVideoId;
    private boolean mFullscreen = false;
    private VideoControlsView mControlsOverlay;
    private GestureDetector mGestureDetector;

    public static VideoFragment newInstance(String videoId) {
        VideoFragment fragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(VideoDetailActivity.KEY_VIDEO_ID, videoId);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
//        String videoId = bundle.getString(VideoDetailActivity.KEY_VIDEO_ID);
        setVideoId("6uLL418S1GQ");
        initialize(getString(R.string.youtube_player_api_key), this);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(player != null) {
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.play();
                }
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            LOG.d("onDown");
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            LOG.d("Tapped at: (" + x + "," + y + ")");

            setFullScreen(true);
            /*Intent intent = new Intent(getActivity(), YouTubePlayerActivity.class);

            // Youtube video ID or Url (Required)
            intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, mVideoId);

            // Youtube player style (DEFAULT as default)
            intent.putExtra(YouTubePlayerActivity.EXTRA_PLAYER_STYLE, YouTubePlayer.PlayerStyle.DEFAULT);

            // Screen Orientation Setting (AUTO for default)
            // AUTO, AUTO_START_WITH_LANDSCAPE, ONLY_LANDSCAPE, ONLY_PORTRAIT
            intent.putExtra(YouTubePlayerActivity.EXTRA_ORIENTATION, Orientation.AUTO);

            // Show audio interface when user adjust volume (true for default)
            intent.putExtra(YouTubePlayerActivity.EXTRA_SHOW_AUDIO_UI, true);

            // If the video is not playable, use Youtube app or Internet Browser to play it
            // (true for default)
            intent.putExtra(YouTubePlayerActivity.EXTRA_HANDLE_ERROR, true);

            // Animation when closing youtubeplayeractivity (none for default)
//            intent.putExtra(YouTubePlayerActivity.EXTRA_ANIM_ENTER, R.anim.fade_in);
//            intent.putExtra(YouTubePlayerActivity.EXTRA_ANIM_EXIT, R.anim.fade_out);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/


            return true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {

        mGestureDetector = new GestureDetector(getActivity(), new GestureListener());

        FrameLayout container = new FrameLayout(getActivity()) {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                mGestureDetector.onTouchEvent(ev);
                return false;
            }
        };

        container.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        final View rootView = super.onCreateView(layoutInflater, container, bundle);
        container.addView(rootView);
        mControlsOverlay = new VideoControlsView(getActivity());
        container.addView(mControlsOverlay);
        return container;
    }

    /*private void disableChildViews(View view){
        view.setClickable(false);
        view.setFocusable(false);
        if(view instanceof ViewGroup){
            ViewGroup viewGroup = ((ViewGroup) view);
            for (int i = 0; i < viewGroup.getChildCount(); i++)
                disableChildViews(viewGroup.getChildAt(i));
        }
    }*/

    @Override
    public void onDestroy() {
        if (player != null) {
            player.release();
        }
        super.onDestroy();
    }

    public void setVideoId(String videoId) {
        this.mVideoId = videoId;
        if (player != null) {
            player.loadVideo(videoId);
        }
    }

    public void pause() {
        if (player != null) {
            player.pause();
        }
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer player, boolean restored) {
        this.player = player;
        player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
            @Override
            public void onFullscreen(boolean isFullscreen) {
                player.setPlayerStyle(isFullscreen ? YouTubePlayer.PlayerStyle.DEFAULT : YouTubePlayer.PlayerStyle.CHROMELESS);
                mFullscreen = isFullscreen;
            }
        });
        mControlsOverlay.setYoutubePlayer(player);
        player.setPlaybackEventListener(mControlsOverlay);
        player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        if (!restored && mVideoId != null) {
            player.loadVideo(mVideoId);
        }
        //TODO: Handle back button when video is fullscreen
        /*((MainActivity) getActivity()).setOnBack(new MainActivity.OnBack() {
            @Override
            public boolean allowedToGoBack() {
                if(mFullscreen && player != null){
                    setFullScreen(false);
                    return false;
                }
                return true;
            }
        });*/

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        this.player = null;
    }

    public void setFullScreen(boolean fullScreen){
        if(player != null) {
            player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
            player.setFullscreen(fullScreen);
        }
    }
}