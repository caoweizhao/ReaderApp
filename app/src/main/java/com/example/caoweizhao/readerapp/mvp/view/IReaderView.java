package com.example.caoweizhao.readerapp.mvp.view;

import java.io.InputStream;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public interface IReaderView {

    void showLoading();

    void dismissLoading();

    void updatePdfView(InputStream is);

    void showMsg(String msg);
}
