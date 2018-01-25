package com.example.caoweizhao.readerapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatDelegate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class SharePreferenceMgr {

    public static final String KEY_THEME = "KEY_THEME";
    public static final int THEME_NIGHT = AppCompatDelegate.MODE_NIGHT_YES;
    public static final int THEME_DAY_TIME = AppCompatDelegate.MODE_NIGHT_NO;
    public static final int THEME_AUTO = AppCompatDelegate.MODE_NIGHT_AUTO;

    @IntDef({THEME_AUTO, THEME_DAY_TIME, THEME_NIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ThemeType {
    }

    public static void putPage(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("page", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getPage(Context context, String key) {
        return getPage(context, key, 0);
    }

    public static int getPage(Context context, String key, int valueIfNotFound) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("page", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, valueIfNotFound);
    }

    public static void putTheme(Context context, @ThemeType int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_THEME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_THEME, value);
        editor.apply();
    }

    @ThemeType
    public static int getTheme(Context context) {
        return getTheme(context, THEME_DAY_TIME);
    }

    @ThemeType
    public static int getTheme(Context context, int valueIfNotFound) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_THEME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_THEME, valueIfNotFound);
    }
}
