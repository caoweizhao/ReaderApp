package com.example.caoweizhao.readerapp;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.example.caoweizhao.readerapp.bean.User;
import com.example.caoweizhao.readerapp.util.SharePreferenceMgr;

import org.litepal.LitePal;

import static org.litepal.LitePalApplication.getContext;

/**
 * Created by caoweizhao on 2017-9-25.
 */

public class MyApplication extends Application {

    public static boolean DEBUG = true;

    public MyApplication() {
        LitePal.initialize(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        int mode = SharePreferenceMgr.getTheme(this);
        setThemeMode(mode);
    }

    public static Context getmContext() {
        return getContext();
    }

    public static User mUser;

    public static void setUser(User user) {
        mUser = user;
    }

    public static User getUser() {

        User user = new User();
        user.setUser_name("caoweizhao");
        user.setUser_password("123456");
        return user;

        // TODO: 2017-12-24
       /* if(mUser == null){
            Intent intent = new Intent(getContext(), LoginActivity.class);
            getContext().startActivity(intent);
        }else{
            return mUser;
        }
        return null;*/
    }

    public void setThemeMode(@SharePreferenceMgr.ThemeType int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
        SharePreferenceMgr.putTheme(this, mode);
    }
}
