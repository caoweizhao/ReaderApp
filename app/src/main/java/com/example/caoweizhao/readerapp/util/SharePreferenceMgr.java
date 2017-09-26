package com.example.caoweizhao.readerapp.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class SharePreferenceMgr {

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


}
