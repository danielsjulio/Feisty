package com.feisty.net;

import com.feisty.model.ChannelList;

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

}
