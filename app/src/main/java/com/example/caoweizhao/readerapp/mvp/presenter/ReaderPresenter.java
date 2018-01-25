package com.example.caoweizhao.readerapp.mvp.presenter;

import com.example.caoweizhao.readerapp.mvp.model.IReaderModel;
import com.example.caoweizhao.readerapp.mvp.model.ReaderModel;
import com.example.caoweizhao.readerapp.mvp.view.IReaderView;

import java.io.InputStream;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class ReaderPresenter implements IReaderPresenter {

    private IReaderView mView;
    private IReaderModel mModel;

    public ReaderPresenter(IReaderView view) {
        this.onAttach(view);
    }

    @Override
    public void loadBook(String url) {
        mView.showLoading();
        mModel.getBook(url);
    }

    @Override
    public void loadBookDone(InputStream is) {
        mView.updatePdfView(is);
        mView.dismissLoading();
    }

    @Override
    public void onErrorHappened(String errorMsg) {
        mView.dismissLoading();
        mView.showMsg(errorMsg);
    }

    @Override
    public void onAttach(IReaderView view) {
        mView = view;
        mModel = new ReaderModel(this);
    }

    @Override
    public void onDetach() {
        mView = null;
        mModel.stopTasks();
    }
}
