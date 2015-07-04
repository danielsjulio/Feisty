package com.feisty.net;

import com.feisty.model.youtube.ChannelList;
import com.feisty.model.youtube.CommentList;
import com.feisty.model.youtube.SeriesList;
import com.feisty.model.youtube.VideoList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Gil on 08/06/15.
 */
public interface YouTubeService {


    @GET("/youtube/v3/channels?part=snippet,brandingSettings,contentDetails")
    void getChannel(@Query("id") String id, Callback<ChannelList> cb);
    @GET("/youtube/v3/channels?part=snippet,brandingSettings,contentDetails")
    ChannelList getChannel(@Query("id") String id);

    @GET("/youtube/v3/search?part=snippet,id&order=date&maxResults=50")
    void getVideos(@Query("channelId") String channelId, Callback<VideoList> cb);
    @GET("/youtube/v3/search?part=snippet,id&order=date&maxResults=50")
    VideoList getVideos(@Query("channelId") String channelId);
    @GET("/youtube/v3/search?part=snippet,id&order=date&maxResults=50")
    VideoList getVideos(@Query("channelId") String channelId, @Query("pageToken") String nextPageToken);
    @GET("/youtube/v3/videos")
    void getVideo(@Query("id") String id, Callback<VideoList> cb);


    @GET("/youtube/v3/commentThreads?part=id%2Creplies%2Csnippet")
    void getComments(@Query("videoId") String videoId, @Query("pageToken") String nextPageToken, Callback<CommentList> cb);
    @GET("/youtube/v3/commentThreads?part=id%2Creplies%2Csnippet")
    void getComments(@Query("videoId") String videoId, Callback<CommentList> cb);


    @GET("/youtube/v3/playlists?part=contentDetails%2Cid%2Cplayer%2Csnippet%2Cstatus")
    void getPlaylists(@Query("channelId") String channelId, Callback<SeriesList> cb);
    @GET("/youtube/v3/playlists?part=contentDetails%2Cid%2Cplayer%2Csnippet%2Cstatus")
    void getPlaylists(@Query("channelId") String channelId, @Query("pageToken") String nextPageToken, Callback<SeriesList> cb);


    @GET("/youtube/v3/playlistItems?part=contentDetails%2Cid%2Csnippet%2Cstatus&maxResults=50")
    void getPlaylistItems(@Query("playlistId") String playlistId, Callback<VideoList> cb);
    @GET("/youtube/v3/playlistItems?part=contentDetails%2Cid%2Csnippet%2Cstatus&maxResults=50")
    void getPlaylistItems(@Query("playlistId") String playlistId, @Query("pageToken") String nextPageToken, Callback<VideoList> cb);
    @GET("/youtube/v3/playlistItems?part=contentDetails%2Cid%2Csnippet%2Cstatus&maxResults=50")
    VideoList getPlaylistItems(@Query("playlistId") String playlistId);
    @GET("/youtube/v3/playlistItems?part=contentDetails%2Cid%2Csnippet%2Cstatus&maxResults=50")
    VideoList getPlaylistItems(@Query("playlistId") String playlistId, @Query("pageToken") String nextPageToken);
}
