package com.feisty;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Gil on 03/06/15.
 */
public class App extends Application {

    private static Context mContext;

    public App(){
        mContext = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //sets the default values for preferences
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        Fabric.with(this, new Crashlytics());

        AnalyticsTrackers.initialize(this);

//        Picasso.setSingletonInstance(new Picasso.Builder(this)
//                .indicatorsEnabled(BuildConfig.DEBUG).build());
    }

    public static Context getContext() {
        return mContext;
    }
}