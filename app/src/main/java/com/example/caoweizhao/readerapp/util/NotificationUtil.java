package com.example.caoweizhao.readerapp.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

/**
 * Created by caoweizhao on 2018-1-4.
 */

public class NotificationUtil {
    private static NotificationUtil instance = null;
    private Context mContext;

    private NotificationUtil(Context context) {
        mContext = context;
    }

    public static NotificationUtil getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationUtil(context);
        }
        return instance;
    }

    public void showNotification(int id, Notification notification) {
        NotificationManager manager = getManager();
        manager.notify(id, notification);
    }

    private NotificationManager getManager() {
        return (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
