package com.example.caoweizhao.readerapp.mvp.model;

import java.io.InputStream;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public interface IReaderModel extends StopableModel{

    void getBook(String url);

    void getBookDone(InputStream is);
}
