package com.example.caoweizhao.readerapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.caoweizhao.readerapp.API.ReaderService;
import com.example.caoweizhao.readerapp.util.FileHelper;
import com.example.caoweizhao.readerapp.util.RetrofitUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by caoweizhao on 2017-9-25.
 */

public class DownloadRunnable implements Runnable {

    private long mBegin;
    private long mDownloaded;
    private long mEnd;
    private String mFileName;
    private String mThreadId;
    private long mTotal;
    /**
     * 标志是否取消
     */
    private volatile boolean isPause = false;

    private int mTaskId;
    private DownloadDatabaseHelper mHelper;
    private ReaderService mService;
    private DownloadBookTask.DownloadListener mListener;
    private SQLiteDatabase mDb;

    CyclicBarrier mBarrier;


    public DownloadRunnable(int taskId, String fileName, String threadId, long begin, long end, long total, DownloadBookTask.DownloadListener listener) {
        mFileName = fileName;
        mBegin = begin;
        mEnd = end;
        mTaskId = taskId;
        mThreadId = threadId;
        mTotal = total;
        mListener = listener;
        mHelper = new DownloadDatabaseHelper(MyApplication.getmContext(), "DownloadThread.db", null,1);
        mService = RetrofitUtil.getRetrofit().newBuilder().build()
                .create(ReaderService.class);
    }

    @Override
    public void run() {

        //todo 读取之前的下载进度
        long downloadedLength = 0;
        mDb = mHelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = mDb.query("Thread", null, "id=?", new String[]{mThreadId}, null, null, null);
            if (cursor.moveToFirst()) {
                downloadedLength = cursor.getLong(cursor.getColumnIndex("downloadedLength"));
                //Log.d("DownloadRunnable", downloadedLength + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Log.d("DownloadRunnable", "downloadedLen:" + downloadedLength);
        mDownloaded = downloadedLength;

        mService.getBookSegment(mFileName, "bytes=" + (mBegin + mDownloaded) + "-" + mEnd)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        Log.d("DownloadRunnable", "onNext");
                        InputStream is = value.byteStream();
                        File file = FileHelper.getBooksDir();
                        File target = new File(file, mFileName);
                        RandomAccessFile randomAccessFile;
                        int len;
                        byte[] buff = new byte[2048];
                        try {
                            randomAccessFile = new RandomAccessFile(target, "rw");
                            randomAccessFile.setLength(mTotal);
                            randomAccessFile.seek(mBegin + mDownloaded);
                            while (!isPause && ((len = is.read(buff)) != -1)) {
                                randomAccessFile.write(buff, 0, len);
                                mDownloaded = mDownloaded + len;
                                //Log.d("DownloadRunnable", mDownloaded + "--" + Thread.currentThread().getName());
                                //更新进度
                                mListener.publishDownloadedLength(len);

                                //暂停下载，将下载进度保存
                                if (isPause) {
                                    Log.d("DownloadRunnable", "isPause:" + "暂停下载" + Thread.currentThread().getName());
                                    Cursor cr = mDb.query("Thread", null, "id=?", new String[]{mThreadId}, null, null, null);
                                    ContentValues values = new ContentValues();
                                    values.put("id", mThreadId);
                                    values.put("downloadedLength", mDownloaded);
                                    //如果存在记录，则更新，否则插入
                                    if (cr.moveToFirst()) {
                                        mDb.update("Thread", values, "id=?", new String[]{mThreadId});
                                    } else {
                                        mDb.insert("Thread", null, values);
                                    }
                                    try {
                                        mBarrier.await();
                                    } catch (InterruptedException | BrokenBarrierException e) {
                                        e.printStackTrace();
                                    }
                                    is.close();
                                    randomAccessFile.close();
                                    return;
                                }

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
                        Log.d("DownloadRunnable", "onComplet" + Thread.currentThread().getName());
                    }
                });
    }

    public synchronized void pause(CyclicBarrier barrier) {
        mBarrier = barrier;
        isPause = true;
    }
}
