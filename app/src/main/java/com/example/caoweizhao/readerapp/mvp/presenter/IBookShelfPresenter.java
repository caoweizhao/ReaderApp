package com.example.caoweizhao.readerapp.mvp.presenter;

import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.mvp.view.IBookShelfView;

import java.util.List;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public interface IBookShelfPresenter extends IPresenter<IBookShelfView> {

    void getBookShelf();

    void onBookShelfFetched(List<Book> resultList);

    void onErrorHappened(String errorMsg);


}
