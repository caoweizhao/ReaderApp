package com.example.caoweizhao.readerapp.mvp.presenter;

import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.mvp.model.BookStoreModel;
import com.example.caoweizhao.readerapp.mvp.model.IBookStoreModel;
import com.example.caoweizhao.readerapp.mvp.view.IBookStoreSubView;

import java.util.List;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class BookStorePresenter implements IBookStorePresenter {

    private IBookStoreModel mModel;
    private IBookStoreSubView mView;

    public BookStorePresenter(IBookStoreSubView view) {
        onAttach(view);
    }

    @Override
    public void onAttach(IBookStoreSubView view) {
        mView = view;
        mModel = new BookStoreModel(this);
    }

    @Override
    public void onDetach() {
        mView = null;
    }

    public void getBookStore(String category) {
        mView.showLoading();
        mModel.getBookStore(category);
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
