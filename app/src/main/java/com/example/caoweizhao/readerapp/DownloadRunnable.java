package com.example.caoweizhao.readerapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.caoweizhao.readerapp.util.FileHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by caoweizhao on 2017-9-25.
 */

public class DownloadRunnable implements Runnable {

    private long mBegin;
    private long mEnd;
    private String mFileName;
    private volatile boolean isCancelled;

    private Observable<ResponseBody> mObservable;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private int mTaskId;

    public DownloadRunnable(int taskId, Observable<ResponseBody> observable, String fileName, long begin, long end) {
        mObservable = observable;
        mFileName = fileName;
        mBegin = begin;
        mEnd = end;
        mTaskId = taskId;
    }

    long mLast = 0;

    @Override
    public void run() {
        mObservable.subscribe(new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody value) {
                InputStream is = value.byteStream();
                File file = FileHelper.getBooksDir();
                File target = new File(file, mFileName);
                RandomAccessFile randomAccessFile;
                int len;
                byte[] buff = new byte[1024];
                try {

                    mNotificationManager = (NotificationManager) MyApplication.getmContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotification = new Notification.Builder(MyApplication.getmContext())
                            .setContentTitle("下载进度")
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setAutoCancel(false)
                            .setPriority(mTaskId)
                            .setProgress(100, (int) ((mBegin * 1.0f / mEnd) * 100), false)
                            .build();
                    randomAccessFile = new RandomAccessFile(target, "rw");
                    randomAccessFile.seek(mBegin);
                    while (!isCancelled && (len = is.read(buff)) != -1) {
                        randomAccessFile.write(buff, 0, len);
                        mBegin += len;
                        if (System.currentTimeMillis() - mLast >= 1000) {
                            mLast = System.currentTimeMillis();
                            mNotification = new Notification.Builder(MyApplication.getmContext())
                                    .setContentTitle("下载进度")
                                    .setPriority(mTaskId)
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setProgress(100, (int) ((mBegin * 1.0f / mEnd) * 100), false)
                                    .build();
                            Log.d("DownloadRunnable", "mBegin:" + mBegin + "mEnd:" + mEnd + "Progress:" + (int) ((mBegin * 1.0f / mEnd) * 100));
                            mNotificationManager.notify(mTaskId, mNotification);
                        }
                    }
                    if (isCancelled) {
                        if (target.delete()) {
                            return;
                        }
                    }
                    if (mBegin == mEnd) {
                        long[] v = new long[]{
                                1000, 0, 1000
                        };
                        mNotification = new Notification.Builder(MyApplication.getmContext())
                                .setContentTitle("下载完成")
                                .setSmallIcon(R.drawable.ic_reader)
                                .setVibrate(v)
                                .build();

                        mNotificationManager.notify(mTaskId, mNotification);
                        Intent intent = new Intent("com.caoweizhao.readerapp.download_done");
                        intent.putExtra("data", mFileName);
                        MyApplication.getmContext().sendBroadcast(intent);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public synchronized void cancel() {
        isCancelled = true;
    }
}
