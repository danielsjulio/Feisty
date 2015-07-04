package com.feisty.net;

import android.content.Context;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.feisty.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;

import java.util.Arrays;
import java.util.Date;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by danieljulio on 20/06/15.
 */
public class API {


    private static YouTubeService sYoutubeService;

    public static YouTubeService getYoutubeService(final Context context){
        if(sYoutubeService == null){
            /*HttpsTrustManager.allowAllSSL();*/
            Gson gson = new GsonBuilder()
//                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//                    .registerTypeAdapter(Date.class, new DateTypeAdapter())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//                    .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                    .create();

            OkHttpClient client = new OkHttpClient();
            client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));//to stop java.io.IOException: stream was reset: CANCEL error
            client.networkInterceptors().add(new StethoInterceptor());
//            client.interceptors().add(new AuthInterceptor(context));

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://www.googleapis.com")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setConverter(new GsonConverter(gson))
                    .setClient(new OkClient(client))
//                    .setErrorHandler(new CustomErrorHandler(context))
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addQueryParam("key", context.getString(R.string.youtube_api_key));
//                            request.addQueryParam("channelId", context.getString(R.string.youtube_channel_id));
                        }
                    })
                    .build();

            sYoutubeService = restAdapter.create(YouTubeService.class);
        }

        return sYoutubeService;
    }


}
