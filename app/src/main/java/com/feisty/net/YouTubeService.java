package com.feisty.net;

import com.feisty.model.ChannelList;
import com.feisty.model.VideoList;

import java.nio.channels.Channel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Gil on 08/06/15.
 */
public interface YouTubeService {


    //https://www.googleapis.com/youtube/v3/channels?part=snippet,brandingSettings,contentDetails&id=UC7IcJI8PUf5Z3zKxnZvTBog&key=AIzaSyCdtsITCb2SmSjrF9w4kMhM-aeLf1GoKwo
    @GET("/youtube/v3/channels?part=snippet,brandingSettings,contentDetails")
    void getChannel(@Query("id") String id, Callback<ChannelList> cb);

    //https://www.googleapis.com/youtube/v3/search?key=AIzaSyCdtsITCb2SmSjrF9w4kMhM-aeLf1GoKwo&channelId=UC7IcJI8PUf5Z3zKxnZvTBog
    @GET("/youtube/v3/search?part=snippet,id&order=date&maxResults=20")
    void getVideos(@Query("channelId") String channelId, Callback<VideoList> cb);

    //https://www.googleapis.com/youtube/v3/videos?key=AIzaSyCdtsITCb2SmSjrF9w4kMhM-aeLf1GoKwo&id=7V-fIGMDsmE&part=contentDetails,id,snippet,statistics
    @GET("/youtube/v3/videos")
    void getVideo(@Query("id") String id, Callback<VideoList> cb);
}
