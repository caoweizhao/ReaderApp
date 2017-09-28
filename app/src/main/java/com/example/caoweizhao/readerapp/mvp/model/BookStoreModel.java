package com.example.caoweizhao.readerapp.mvp.model;

import android.util.Log;

import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.mvp.model.API.BookStoreService;
import com.example.caoweizhao.readerapp.mvp.presenter.IBookStorePresenter;
import com.example.caoweizhao.readerapp.util.RetrofitUtil;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class BookStoreModel extends BaseModel<IBookStorePresenter> implements IBookStoreModel {

    private BookStoreService mService;

    public BookStoreModel(IBookStorePresenter presenter) {
        super(presenter);
        mService = RetrofitUtil.getRetrofit()
                .create(BookStoreService.class);
    }

    @Override
    public void getBookStore() {
        Log.d("BookStoreModel","getBookStore");
        mService.getBooks()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Book>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(List<Book> value) {
                        mPresenter.onBookStoreFetched(value);
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
