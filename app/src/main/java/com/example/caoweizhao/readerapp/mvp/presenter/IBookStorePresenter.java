package com.example.caoweizhao.readerapp.mvp.presenter;

import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.mvp.view.IBookStoreView;

import java.util.List;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public interface IBookStorePresenter extends IPresenter<IBookStoreView> {

    void getBookStore();

    void onBookStoreFetched(List<Book> resultList);

    void onErrorHappened(String errorMsg);


}
