package com.feisty;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

/**
 * Created by Gil on 03/06/15.
 */
public class App extends Application {


    //TODO(gil): Install crashlytics
    //TODO(daniel): Store playlists offline
    //TODO(gil): Error screens
    //TODO(daniel): Change main feed to use channel data
    //TODO(gil): Creating a new profile should be seamless
    //TODO(gil): Investigate problem loading comments
    //TODO(gil: Notifications


    private static Context mContext;

    public App(){
        mContext = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    public static Context getContext() {
        return mContext;
    }
}