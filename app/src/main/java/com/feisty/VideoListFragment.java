package com.feisty;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoListFragment extends Fragment {


    @InjectView(R.id.list)
    RecyclerView mRecyclerView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment VideoListFragment.
     */
    public static VideoListFragment newInstance() {
        VideoListFragment fragment = new VideoListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public VideoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        ButterKnife.inject(this, view);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        VideoListAdapter videoListAdapter = new VideoListAdapter();
        videoListAdapter.getVideos().add(new Video());
        videoListAdapter.getVideos().add(new Video());
        videoListAdapter.getVideos().add(new Video());
        videoListAdapter.getVideos().add(new Video());
        videoListAdapter.getVideos().add(new Video());
        videoListAdapter.getVideos().add(new Video());
        videoListAdapter.getVideos().add(new Video());
        mRecyclerView.setAdapter(videoListAdapter);
        return view;

    }

    public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder>{

        ArrayList<Video> videos = new ArrayList<>();

        public ArrayList<Video> getVideos() {
            return videos;
        }

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_video, parent, false);
            return new VideoViewHolder(v);
        }

        @Override
        public void onBindViewHolder(VideoViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return videos.size();
        }

        public class VideoViewHolder extends RecyclerView.ViewHolder {

            VideoViewHolder(View itemView) {
                super(itemView);
            }
        }

    }

}
