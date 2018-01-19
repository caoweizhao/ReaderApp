package com.example.caoweizhao.readerapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.caoweizhao.readerapp.util.FileHelper;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by caoweizhao on 2017-9-25.
 */

public class DownloadBookTask extends DataSupport {
    /**
     * 暂停下载状态
     */
    public static final int STATE_PAUSE = 1;
    /**
     * 下载中状态
     */
    public static final int STATE_DOWNLOADING = 0;
    /**
     * 下载完成状态
     */
    public static final int STATE_COMPLETE = 2;

    /**
     * 文件url
     */
    @Column(unique = true, nullable = false)
    private String mUrl;
    /**
     * 目标文件大小
     */
    @Column
    private long mTargetSize;
    /**
     * 文件名
     */
    @Column
    private String mFileName;
    /**
     * 开启线程数量
     */
    @Column
    private int THREAD_COUNT = 3;
    /**
     * 下载百分比
     */
    @Column
    private double mPercent;
    /**
     * 下载状态
     */
    @Column
    private int mDownloadState;

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public double getPercent() {
        return mPercent;
    }

    public void setPercent(double percent) {
        this.mPercent = percent;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public int getDownloadState() {
        return mDownloadState;
    }

    public void setDownloadState(int downloadState) {
        this.mDownloadState = downloadState;
    }

    private DownloadListener mListener = new DownloadListener();

    /**
     * 记录已下载量
     */
    private volatile long mDownloadedLength = 0;

    /**
     * 线程池
     */
    private ExecutorService mThreadPool = Executors.newFixedThreadPool(3);
    /**
     * 下载任务Id
     */
    private int mTaskId;

    public void setTaskId(int taskId) {
        mTaskId = taskId;
    }

    public int getTaskId() {
        return mTaskId;
    }

    //java并发，等待下载线程完成暂停后，将进度保存
    private CyclicBarrier mBarrier = new CyclicBarrier(THREAD_COUNT, new Runnable() {
        @Override
        public void run() {
            saveProgress();
        }
    });

    private void saveProgress() {

        Cursor cursor = null;
        try {
            cursor = mDatabase.query("Thread", null, "id=?", new String[]{mUrl + "main"}, null, null, null);
            ContentValues values = new ContentValues(1);
            values.put("id", mUrl + "main");
            values.put("downloadedLength", mDownloadedLength);
            if (cursor.moveToFirst()) {
                mDatabase.update("Thread", values, "id=?", new String[]{mUrl + "main"});
            } else {
                mDatabase.insert("Thread", null, values);
            }
            // Log.d("DownloadBookTask", "percent:" + getPercent() + "DownloadState:" + getDownloadState());
            DownloadBookTask.this.save();
           /* List<DownloadBookTask> tasks = DataSupport.findAll(DownloadBookTask.class);
            for (DownloadBookTask t:tasks
                 ) {
                if(t.getUrl().equals(getUrl())){
                    Log.d("DownloadBookTask",t.getPercent()+"%");
                    break;
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private List<DownloadRunnable> mRunnables = new ArrayList<>(3);
    private DownloadDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public DownloadBookTask(String url, long targetSize, int taskId) {
        mUrl = url;
        mFileName = url;
        mTargetSize = targetSize;
        mTaskId = taskId;
        mDatabaseHelper = new DownloadDatabaseHelper(MyApplication.getmContext(), "DownloadThread.db", null, 1);
        mDatabase = mDatabaseHelper.getWritableDatabase();
    }

    /**
     * 开始下载
     */
    public void download() {
        setDownloadState(DownloadBookTask.STATE_DOWNLOADING);
        long begin = 0;
        long end = 0;
        long perTaskSize = mTargetSize / THREAD_COUNT;
        Log.d("DownloadBookTask", "per" + perTaskSize);
        //读取总下载进度
        Cursor cursor = null;
        try {
            cursor = mDatabase.query("Thread", null, "id=?", new String[]{mUrl + "main"}, null, null, null);
            if (cursor.moveToFirst()) {
                mDownloadedLength = cursor.getLong(cursor.getColumnIndex("downloadedLength"));
                Log.d("DownloadRunnable", mDownloadedLength + "downloadedLen");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Log.d("DownloadBookTask", mTargetSize + ":" + mDownloadedLength);
        if (mTargetSize - mDownloadedLength <= 2) {
            Log.d("DownloadBookTask", "已下载完");
            return;
        }

        mRunnables.clear();
        for (int i = 0; i < THREAD_COUNT; i++) {
            if (i != THREAD_COUNT - 1) {
                begin = perTaskSize * i;
                end = begin + perTaskSize - 1;
            } else {
                begin = perTaskSize * i;
                end = mTargetSize;
            }
            Log.d("DownloadBookTask", "downloadaa:" + mUrl);
            DownloadRunnable downloadRunnable = new DownloadRunnable(mTaskId, mUrl, mUrl + i, begin, end, mTargetSize, mListener);
            mRunnables.add(downloadRunnable);
            mThreadPool.execute(downloadRunnable);
        }
    }

    /**
     * 暂停下载,保存已下载总进度
     */
    public void pause() {
        setDownloadState(DownloadBookTask.STATE_PAUSE);
        for (DownloadRunnable r : mRunnables
                ) {
            r.pause(mBarrier);
        }
    }

    /**
     * 恢复下载
     */
    public void resume() {
        download();
    }

    /**
     * 取消下载
     */
    public void cancel() {
        pause();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                File file = FileHelper.getBooksDir();
                File bookFile = new File(file, mUrl);
                bookFile.delete();
                Log.d("DownloadBookTask", bookFile.getAbsolutePath());
                mDownloadedLength = 0;
                try {
                    Log.d("DownloadBookTask", "Target:" + mUrl);
                    int deleteCount = mDatabase.delete("Thread", "id like ?", new String[]{mUrl + "%"});
                    Log.d("DownloadBookTask", "cancel" + deleteCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
    }

    /**
     * 更新进度
     *
     * @param downloadedLength 提交的下载量
     */
    public synchronized void publishProgress(long downloadedLength) {
        mDownloadedLength += downloadedLength;
        double percent = (((double) mDownloadedLength / (double) mTargetSize) * 100);
        Intent i = new Intent(MyApplication.getmContext(), DownloadService.class);
        i.setAction(Constant.ACTION_UPDTATE);
        i.putExtra("url", mUrl);
        i.putExtra("percent", percent);
        Log.d("DownloadBookTask", "publishProgress:" + mDownloadedLength + "---" + percent + "%" + "--" + Thread.currentThread().getName());
        if (mTargetSize - mDownloadedLength <= 2) {
            i.putExtra("percent", (double) 100);
            mDownloadedLength = mTargetSize;
            setPercent(100);
            setDownloadState(DownloadBookTask.STATE_COMPLETE);
            saveProgress();
        }
        MyApplication.getmContext().startService(i);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DownloadBookTask)) {
            return false;
        }
        DownloadBookTask task = (DownloadBookTask) obj;

        return task.getUrl().equals(getUrl());
    }

    public class DownloadListener {
        void publishDownloadedLength(long downloadedLength) {
            publishProgress(downloadedLength);
        }
    }
}
