package com.example.caoweizhao.readerapp;

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

    public DownloadRunnable(Observable<ResponseBody> observable, String fileName, long begin, long end) {
        mObservable = observable;
        mFileName = fileName;
        mBegin = begin;
        mEnd = end;
    }

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
                Log.d("DownloadRunnable", file.exists() + ":");
                RandomAccessFile randomAccessFile = null;
                int len = -1;
                byte[] buff = new byte[1024];
                try {
                    randomAccessFile = new RandomAccessFile(target, "rw");
                    randomAccessFile.seek(mBegin);
                    while (!isCancelled && (len = is.read(buff)) != -1) {
                        randomAccessFile.write(buff, 0, len);
                        mBegin += len;
                    }
                    if (isCancelled) {
                        if (target.delete()) {
                            return;
                        }
                    }
                    if (mBegin == mEnd) {
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
