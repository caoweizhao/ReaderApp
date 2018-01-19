package com.example.caoweizhao.readerapp.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.caoweizhao.readerapp.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by caoweizhao on 2017-9-21.
 */

public abstract class BaseActivity extends AppCompatActivity {

    Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mUnbinder = ButterKnife.bind(this);
        initData();
        initEvent();
    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected void setToolbar(@IdRes int toolbarId){
        this.setToolbar(toolbarId,"");
    }

    protected void setToolbar(@IdRes int toolbarId, String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void initData() {
    }

    protected void initEvent() {
    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroy();
    }
}
