package com.feisty.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.feisty.App;

/**
 * Created by Gil on 03/06/15.
 */
public class BaseActivity extends AppCompatActivity {

    protected App getApp(){
        return (App) getApplication();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);

    }
}
