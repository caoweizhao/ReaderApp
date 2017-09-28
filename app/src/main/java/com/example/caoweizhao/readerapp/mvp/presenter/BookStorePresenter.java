package com.example.caoweizhao.readerapp.mvp.presenter;

import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.mvp.model.BookStoreModel;
import com.example.caoweizhao.readerapp.mvp.model.IBookStoreModel;
import com.example.caoweizhao.readerapp.mvp.view.IBookStoreView;

import java.util.List;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class BookStorePresenter implements IBookStorePresenter {

    private IBookStoreModel mModel;
    private IBookStoreView mView;

    public BookStorePresenter(IBookStoreView view) {
        onAttach(view);
    }

    @Override
    public void onAttach(IBookStoreView view) {
        mView = view;
        mModel = new BookStoreModel(this);
    }

    @Override
    public void onDetach() {
        mView = null;
    }

    public void getBookStore() {
        mView.showLoading();
        mModel.getBookStore();
    }

    @Override
    public void onBookStoreFetched(List<Book> resultList) {
        mView.dismissLoading();
        mView.updateBookShelf(resultList);
    }

    @Override
    public void onErrorHappened(String errorMsg) {
        mView.dismissLoading();
        mView.showMsg(errorMsg);
    }
}
