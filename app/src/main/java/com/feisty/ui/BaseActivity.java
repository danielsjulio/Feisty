package com.feisty.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;

import com.feisty.App;

/**
 * Created by Gil on 03/06/15.
 */
public class BaseActivity extends ActionBarActivity {

    protected App getApp(){
        return (App) getApplication();
    }
}
