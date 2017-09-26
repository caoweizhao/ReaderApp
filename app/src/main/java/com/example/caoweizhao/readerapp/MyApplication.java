package com.example.caoweizhao.readerapp;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

import static org.litepal.LitePalApplication.getContext;

/**
 * Created by caoweizhao on 2017-9-25.
 */

public class MyApplication extends Application {

    public static boolean DEBUG = true;

    public MyApplication(){
        LitePal.initialize(this);
    }

    public static Context getmContext() {
        return getContext();
    }
}
