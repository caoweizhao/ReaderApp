package com.example.caoweizhao.readerapp.mvp.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

import com.example.caoweizhao.readerapp.DownloadBookTask;
import com.example.caoweizhao.readerapp.MyApplication;
import com.example.caoweizhao.readerapp.mvp.model.API.ReaderService;
import com.example.caoweizhao.readerapp.mvp.presenter.IReaderPresenter;
import com.example.caoweizhao.readerapp.util.FileHelper;
import com.example.caoweizhao.readerapp.util.RetrofitUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class ReaderModel extends BaseModel<IReaderPresenter> implements IReaderModel {

    private ReaderService mService;
    private long mFileLength;
    private DownloadBookTask mDownLoadTask;
    private final DownloadReceiver mDownloadReceiver;

    public ReaderModel(IReaderPresenter presenter) {
        super(presenter);
        mService = RetrofitUtil.getRetrofit()
                .create(ReaderService.class);
        mDownloadReceiver = new DownloadReceiver();
        MyApplication.getmContext().registerReceiver(mDownloadReceiver, new IntentFilter("com.caoweizhao.readerapp.download_done"));
    }

    @Override
    public void getBook(final String url) {
        mService.getBookSize(url)
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
                mDisposables.add(d);
            }

            @Override
            public void onNext(Long value) {
                Log.d("ReaderModel", "accept:" + value);
                mFileLength = value;
                if (mFileLength >= 50 * 1024 * 1024) {
                    //启动下载

                    File file = new File(FileHelper.getBooksDir(), url);
                    if (file.exists()) {
                        try {
                            InputStream is = new FileInputStream(file);
                            mPresenter.loadBookDone(is);
                            return;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("ReaderModel", "onNext:" + "启动下载");
                        mDownLoadTask = new DownloadBookTask(url, mFileLength);
                        mDownLoadTask.download();
                    }


                } else {
                    mService.getBook(url)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Observer<ResponseBody>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    mDisposables.add(d);
                                }

                                @Override
                                public void onNext(ResponseBody value) {
                                    getBookDone(value.byteStream());
                                }

                                @Override
                                public void onError(Throwable e) {
                                    mPresenter.onErrorHappened(e.getMessage());
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                }
            }

            @Override
            public void onError(Throwable e) {
                mPresenter.onErrorHappened(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });

    }

    @Override
    public void getBookDone(InputStream is) {
        mPresenter.loadBookDone(is);
    }

    @Override
    public void stopTasks() {
        super.stopTasks();

        if (mDownLoadTask != null) {
            mDownLoadTask.cancel();
        }
        MyApplication.getmContext().unregisterReceiver(mDownloadReceiver);
    }

    class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("DownloadReceiver", "onReceive");
            String fileName = (String) intent.getSerializableExtra("data");
            if (fileName != null) {
                File dir = Environment.getExternalStorageDirectory();
                File file = new File(dir, "/ReaderApp/Books/" + fileName);
                if (file.exists()) {
                    try {
                        getBookDone(new FileInputStream(file));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}