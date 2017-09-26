package com.example.caoweizhao.readerapp.mvp.presenter;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public interface IPresenter<V> {

    void onAttach(V view);

    void onDetach();
}
