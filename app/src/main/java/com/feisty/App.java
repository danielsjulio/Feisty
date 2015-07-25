package com.feisty;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Gil on 03/06/15.
 */
public class App extends Application {


    //TODO: Error screens
    //TODO: Creating a new profile should be seamless
    //TODO: Notifications


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

        Fabric.with(this, new Crashlytics());
    }

    public static Context getContext() {
        return mContext;
    }
}