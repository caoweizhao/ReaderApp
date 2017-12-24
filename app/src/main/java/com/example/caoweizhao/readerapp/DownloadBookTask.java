package com.example.caoweizhao.readerapp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.caoweizhao.readerapp.API.ReaderService;
import com.example.caoweizhao.readerapp.util.RetrofitUtil;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by caoweizhao on 2017-9-25.
 */

public class DownloadBookTask {

    /**
     * 文件url
     */
    private String mUrl;
    /**
     * 目标文件大小
     */
    private long mTargetSize;

    private ReaderService mService;
    /**
     * 文件名
     */
    private String mFileName;

    private static final Executor THREAD_POOL_EXECUTOR;
    private SerialExecutor SERIAL_EXECUTOR;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    private int mTaskId;

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    /**
     * 每个线程下载大小
     */
    private long mPerDownloadSize = 50 * 1024 * 1024;

    {
        mService = RetrofitUtil.getRetrofit()
                .create(ReaderService.class);
    }

    public DownloadBookTask(String url, long targetSize, int taskId) {
        mUrl = url;
        mFileName = url;
        mTargetSize = targetSize;
        SERIAL_EXECUTOR = new SerialExecutor(mFileName);
        mTaskId = taskId;
    }

    public void download() {
        //所需要的线程数量
        long count = (mTargetSize % mPerDownloadSize) == 0 ?
                mTargetSize / mPerDownloadSize : (mTargetSize / mPerDownloadSize + 1);
        for (int i = 0; i < count - 1; i++) {
            long begin = mPerDownloadSize * i;
            long end = begin + mPerDownloadSize - 1;
            Observable<ResponseBody> observable = mService.getBookSegment(mUrl, "bytes=" + begin + "-" + end);
            DownloadRunnable downloadRunnable = new DownloadRunnable(mTaskId, observable, mFileName, begin, mTargetSize);
            SERIAL_EXECUTOR.execute(downloadRunnable);
        }
        long begin = mPerDownloadSize * (count - 1);
        long end = mTargetSize;
        Observable<ResponseBody> observable = mService.getBookSegment(mUrl, "bytes=" + begin + "-" + end);

        DownloadRunnable downloadRunnable = new DownloadRunnable(mTaskId, observable, mFileName, begin, mTargetSize);
        SERIAL_EXECUTOR.execute(downloadRunnable);
    }

    public void cancel() {
        SERIAL_EXECUTOR.cancel();
    }

    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;
        boolean errorHappen = false;
        String mFileName;

        public SerialExecutor(String fileName) {
            mFileName = fileName;
        }

        public synchronized void execute(final Runnable r) {
            if (errorHappen) {
                Log.d("SerialExecutor","error happen");
                return;
            }
            mTasks.offer(new MR(r));
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }

        }
        public synchronized void pause(){
            if (mActive != null) {
                ((DownloadRunnable) (((MR) mActive).mR)).cancel();
            }
        }

        public synchronized void cancel() {
            if (mActive != null) {
                ((DownloadRunnable) (((MR) mActive).mR)).cancel();
            }
            mTasks.clear();
        }

        class MR implements Runnable {

            Runnable mR;

            public MR(Runnable r) {
                mR = r;
            }

            @Override
            public void run() {
                try {
                    mR.run();
                    Log.d("MR","schedule next");
                    scheduleNext();
                } catch (Exception e) {
                    Log.d("SerialExecutor", "error");
                    errorHappen = true;
                    e.printStackTrace();
                }
            }
        }
    }
}
