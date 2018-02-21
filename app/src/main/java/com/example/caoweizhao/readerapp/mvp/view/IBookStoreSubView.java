package com.example.caoweizhao.readerapp.mvp.view;

import com.example.caoweizhao.readerapp.bean.Book;

import java.util.List;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public interface IBookStoreSubView {

    void showLoading();

    void dismissLoading();

    void updateBookShelf(List<Book> resultList);

    void showMsg(String msg);

}
