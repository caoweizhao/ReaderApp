package com.example.caoweizhao.readerapp.mvp.model;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public abstract class BaseModel<P> implements StopableModel {
    protected P mPresenter;
    protected List<Disposable> mDisposables = new ArrayList<>();

    public BaseModel(P presenter) {
        mPresenter = presenter;
    }

    @Override
    public void stopTasks() {
        for (Disposable d : mDisposables
                ) {
            if (d != null) {
                d.dispose();
            }
        }
    }

}
