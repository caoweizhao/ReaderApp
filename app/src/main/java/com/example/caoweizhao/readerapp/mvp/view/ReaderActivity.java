package com.example.caoweizhao.readerapp.mvp.view;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.base.BaseActivity;
import com.example.caoweizhao.readerapp.mvp.presenter.IReaderPresenter;
import com.example.caoweizhao.readerapp.mvp.presenter.ReaderPresenter;
import com.example.caoweizhao.readerapp.util.SharePreferenceMgr;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.InputStream;

import butterknife.BindView;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class ReaderActivity extends BaseActivity implements IReaderView {

    @BindView(R.id.pdf_view)
    PDFView mPDFView;
    IReaderPresenter mPresenter;
    int mLastPage = 0;
    String mUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reader;
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        mUrl = intent.getStringExtra("data");
        mLastPage = SharePreferenceMgr.getPage(this, mUrl);
        Log.d("ReaderActivity", "mUrl:" + mUrl);
        if (mUrl != null) {
            mPresenter = new ReaderPresenter(this);
            mPresenter.loadBook(mUrl);
        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();
    }

    @Override
    public void showLoading() {
        Toast.makeText(this, "Show", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismissLoading() {
        Toast.makeText(this, "Dismiss", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updatePdfView(InputStream is) {
        mPDFView.fromStream(is)
                .defaultPage(mLastPage)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        mLastPage = page;
                    }
                })
                .load();
    }

    @Override
    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDetach();
        }
        SharePreferenceMgr.putPage(this, mUrl, mLastPage);
        super.onDestroy();
    }
}
