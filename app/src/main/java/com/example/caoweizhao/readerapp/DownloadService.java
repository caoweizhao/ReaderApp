package com.example.caoweizhao.readerapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.caoweizhao.readerapp.API.ReaderService;
import com.example.caoweizhao.readerapp.activity.DownloadActivity;
import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.util.FileHelper;
import com.example.caoweizhao.readerapp.util.NotificationUtil;
import com.example.caoweizhao.readerapp.util.RetrofitUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class DownloadService extends Service {

    DownloadBinder mBinder = new DownloadBinder();
    List<DownloadBookTask> mDownloadBookTasks = new ArrayList<>();
    ReaderService mService;

    @Override
    public void onCreate() {
        Log.d("DownloadService", "onCreate");
        super.onCreate();
        List<DownloadBookTask> tasks = DataSupport.findAll(DownloadBookTask.class);
        if (tasks != null) {
            mDownloadBookTasks = tasks;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("DownloadService", "onStartCommand");
        if (intent != null) {
            //新增下载任务
            Book book = intent.getParcelableExtra("book");
            if (book != null) {
                Log.d("DownloadService", "新增下载任务");
                for (DownloadBookTask task : mDownloadBookTasks
                        ) {
                    if (task.getUrl().equals(book.getUrl())) {
                        //已存在下载任务
                        Toast.makeText(this, "已存在下载任务！", Toast.LENGTH_SHORT).show();
                        return Service.START_STICKY;
                    }
                }
                Toast.makeText(this, "已添加到任务队列！", Toast.LENGTH_SHORT).show();
                download((Book) intent.getParcelableExtra("book"));
            }
            //更新进度
            else if (intent.getAction().equals(Constant.ACTION_UPDTATE)) {
                String url = intent.getStringExtra("url");
                double percent = intent.getDoubleExtra("percent", 0);
                Log.d("DownloadService", "url:" + url + "--percent:" + percent);
                for (DownloadBookTask task : mDownloadBookTasks
                        ) {
                    if (task.getUrl().equals(url)) {
                        DecimalFormat df = new DecimalFormat("#####0.00");
                        String p = df.format(percent);//返回的是String类型的数据
                        task.setPercent(Double.parseDouble(p));
                        if (Double.parseDouble(p) == 100) {

                            Intent intent1 = new Intent(DownloadService.this, DownloadActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
                            Notification notification = new Notification.Builder(this)
                                    .setContentTitle(task.getFileName())
                                    .setContentText("下载完成！")
                                    .setContentIntent(pendingIntent)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setAutoCancel(true)
                                    .build();
                            NotificationUtil.getInstance(this).showNotification(task.getTaskId(), notification);
                            File tempFile = new File(FileHelper.getBooksDir(), "temp" + task.getUrl());
                            if (tempFile.exists()) {
                                tempFile.delete();
                            }
                        }
                        break;
                    }
                }
            }
        }
        return Service.START_STICKY;
    }

    private void download(final Book book) {
        mService = RetrofitUtil.getRetrofit().create(ReaderService.class);
        mService.getBookSize(book.getUrl())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .map(new Function<Response<String>, Long>() {
                    @Override
                    public Long apply(Response<String> response) throws Exception {
                        return Long.parseLong(response.headers().get("size"));
                    }
                }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Long value) {
                //启动下载
                Log.d("BookDetailActivity", "onNext:" + value);
                DownloadBookTask task = new DownloadBookTask(book.getUrl(), value, Constant.TASKID.getAndIncrement());
                task.setFileName(book.getName());
                task.setPercent(0);
                task.setUrl(book.getUrl());
                task.save();
                task.download();
                mDownloadBookTasks.add(task);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.d("DownloadService", "onDestroy");
        DataSupport.saveAll(mDownloadBookTasks);
        super.onDestroy();
    }

    public class DownloadBinder extends Binder {
        public List<DownloadBookTask> getTasks() {
            return mDownloadBookTasks;
        }

        public void pause(int position) {
            DownloadBookTask mTask = mDownloadBookTasks.get(position);
            if (mTask != null) {
                mTask.pause();
            }
        }

        public void pauseAll() {
            for (DownloadBookTask task : mDownloadBookTasks
                    ) {
                task.pause();
            }
            Log.d("DownloadBinder", "pauseAll");
            saveProgress();
        }

        public void cancel(int position) {
            DownloadBookTask mTask = mDownloadBookTasks.get(position);
            if (mTask != null) {
                mTask.cancel();
                mTask.delete();
                int i = DataSupport.deleteAll(DownloadBookTask.class, "mUrl=?", mTask.getUrl());
                Log.d("DownloadBinder", "Delete:" + i);
            }
            mDownloadBookTasks.remove(position);
        }

        public void cancelAll() {
            for (DownloadBookTask task : mDownloadBookTasks
                    ) {
                task.cancel();
                mDownloadBookTasks.remove(task);
            }
        }

        public void resume(int position) {
            DownloadBookTask mTask = mDownloadBookTasks.get(position);
            if (mTask != null) {
                mTask.resume();
            }
        }

        public void saveProgress() {
            DataSupport.saveAll(mDownloadBookTasks);
        }
    }
}
