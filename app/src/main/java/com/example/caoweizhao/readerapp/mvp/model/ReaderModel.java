package com.example.caoweizhao.readerapp.mvp.model;

import android.util.Log;

import com.example.caoweizhao.readerapp.API.ReaderService;
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

    public ReaderModel(IReaderPresenter presenter) {
        super(presenter);
        mService = RetrofitUtil.getRetrofit()
                .create(ReaderService.class);
    }

    @Override
    public void getBook(final String url) {

        Log.d("ReaderModel","getBook");
        File dir = FileHelper.getBooksDir();
        File file = new File(dir, url);
        if (file.exists()) {
            try {
                Log.d("ReaderModel", "File Exists");
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

        for (Disposable d : mDisposables
                ) {
            if (d != null) {
                d.dispose();
            }
        }
        /*if (mDownLoadTask != null) {
            mDownLoadTask.pause();
        }*/
    }
}