package com.example.caoweizhao.readerapp.activity;

import android.view.MotionEvent;
import android.view.View;

import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by Administrator on 2017-8-5.
 */

public class AboutActivity extends BaseActivity {
    @BindView(R.id.about_activity_container)
    View mContainer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_layout;
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return true;
            }
        });
    }
}
