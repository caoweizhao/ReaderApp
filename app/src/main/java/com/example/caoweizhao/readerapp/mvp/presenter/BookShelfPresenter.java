package com.example.caoweizhao.readerapp.mvp.presenter;

import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.mvp.model.BookShelfModel;
import com.example.caoweizhao.readerapp.mvp.model.IBookShelfModel;
import com.example.caoweizhao.readerapp.mvp.view.IBookShelfView;

import java.util.List;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class BookShelfPresenter implements IBookShelfPresenter {

    private IBookShelfModel mModel;
    private IBookShelfView mView;

    public BookShelfPresenter(IBookShelfView view) {
        onAttach(view);
    }

    @Override
    public void onAttach(IBookShelfView view) {
        mView = view;
        mModel = new BookShelfModel(this);
    }

    @Override
    public void onDetach() {
        mView = null;
    }

    public void getBookShelf() {
        mView.showLoading();
        mModel.getBookShelf();
    }

    @Override
    public void onBookShelfFetched(List<Book> resultList) {
        mView.dismissLoading();
        mView.updateBookShelf(resultList);
    }

    @Override
    public void onErrorHappened(String errorMsg) {
        mView.dismissLoading();
        mView.showMsg(errorMsg);
    }
}
