package com.example.caoweizhao.readerapp.mvp.presenter;

import com.example.caoweizhao.readerapp.mvp.view.IReaderView;

import java.io.InputStream;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public interface IReaderPresenter extends IPresenter<IReaderView> {

    void loadBook(String url);

    void loadBookDone(InputStream is);

    void onErrorHappened(String errorMsg);

}
