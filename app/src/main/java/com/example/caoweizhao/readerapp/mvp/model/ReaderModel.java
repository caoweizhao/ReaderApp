package com.example.caoweizhao.readerapp.mvp.model;

import android.util.Log;

import com.example.caoweizhao.readerapp.API.ReaderService;
import com.example.caoweizhao.readerapp.DownloadBookTask;
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
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class ReaderModel extends BaseModel<IReaderPresenter> implements IReaderModel {

    private ReaderService mService;
    private long mFileLength;
    private DownloadBookTask mDownLoadTask;

    public ReaderModel(IReaderPresenter presenter) {
        super(presenter);
        mService = RetrofitUtil.getRetrofit()
                .create(ReaderService.class);
    }

    @Override
    public void getBook(final String url) {
        /*mService.getBookSize(url)
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
                        mDownLoadTask = new DownloadBookTask(url, mFileLength, Constant.TASKID.getAndIncrement());
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
        });*/

        File dir = FileHelper.getBooksDir();
        File file = new File(dir,url);
        if(file.exists()){
            try {
                Log.d("ReaderModel","File Exists");
                mPresenter.loadBookDone(new FileInputStream(file));
                return;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

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

    @Override
    public void getBookDone(InputStream is) {
        mPresenter.loadBookDone(is);
    }

    @Override
    public void stopTasks() {
        super.stopTasks();

        /*if (mDownLoadTask != null) {
            mDownLoadTask.pause();
        }*/
    }
}