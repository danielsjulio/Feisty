package com.feisty;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.feisty.net.YouTubeService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.squareup.okhttp.OkHttpClient;

import java.util.Date;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by Gil on 03/06/15.
 */
public class App extends Application {

    YouTubeService mYoutubeService;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    public YouTubeService getYoutubeService(final Context context){
        if(mYoutubeService == null){
            /*HttpsTrustManager.allowAllSSL();*/
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .registerTypeAdapter(Date.class, new DateTypeAdapter())
//                    .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                    .create();

            OkHttpClient client = new OkHttpClient();
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
                        }
                    })
                    .build();

            mYoutubeService = restAdapter.create(YouTubeService.class);
        }

        return mYoutubeService;
    }
}
