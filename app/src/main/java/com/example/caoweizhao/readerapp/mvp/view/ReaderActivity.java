package com.example.caoweizhao.readerapp.mvp.view;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.caoweizhao.readerapp.DownloadBookTask;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.base.BaseActivity;
import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.bean.RecentReadBook;
import com.example.caoweizhao.readerapp.mvp.presenter.IReaderPresenter;
import com.example.caoweizhao.readerapp.mvp.presenter.ReaderPresenter;
import com.example.caoweizhao.readerapp.util.FileHelper;
import com.example.caoweizhao.readerapp.util.SharePreferenceMgr;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class ReaderActivity extends BaseActivity implements IReaderView {

    @BindView(R.id.pdf_view)
    PDFView mPDFView;
    IReaderPresenter mPresenter;
    int mLastPage = 0;
    float mPercent = 0;
    String mUrl;
    Book mBook;
    boolean isDownloaded;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reader;
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        mUrl = intent.getStringExtra("data");
        mBook = intent.getParcelableExtra("book");
        mLastPage = SharePreferenceMgr.getPage(this, mUrl);
        Log.d("ReaderActivity", "mUrl:" + mUrl);
        File dir = FileHelper.getBooksDir();
        File file = new File(dir, mUrl);
        //文件存在，可能已下载或者正在下载
        if (file.exists()) {
                List<DownloadBookTask> downloadBookTasks = DataSupport.findAll(DownloadBookTask.class);
                for (DownloadBookTask dt : downloadBookTasks
                        ) {
                    //文件已下载
                    if (dt.getUrl().equals(mUrl) &&
                            (dt.getDownloadState() == DownloadBookTask.STATE_COMPLETE)) {
                        isDownloaded = true;
                        mPDFView.fromFile(file)
                                .defaultPage(mLastPage)
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        mLastPage = page;
                                        Log.d("ReaderActivity", "page:" + pageCount);
                                        mPercent = ((page * 1.0f) / pageCount) * 100;
                                        Log.d("ReaderActivity", "per" + mPercent);
                                    }
                                })
                                .load();
                        if (mLastPage > 0) {
                            Toast.makeText(this, "已恢复上次阅读位置", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
        }
        //文件不存在或者正在下载
        if (!isDownloaded && mUrl != null) {
            Log.d("ReaderActivity","Load From Net");
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
    }

    @Override
    public void dismissLoading() {
    }

    @Override
    public void updatePdfView(InputStream is) {
        mPDFView.fromStream(is)
                .defaultPage(mLastPage)
                .swipeHorizontal(true)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        mLastPage = page;
                        Log.d("ReaderActivity", "page:" + pageCount);

                        mPercent = ((page * 1.0f) / pageCount) * 100;
                        Log.d("ReaderActivity", "per" + mPercent);
                    }
                })
                .load();
        if (mLastPage > 0) {
            Toast.makeText(this, "已恢复上次阅读位置", Toast.LENGTH_SHORT).show();
        }
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
        storeProgress();
        super.onDestroy();
    }

    /**
     * 退出时保存阅读进度
     */
    private void storeProgress() {
        SharePreferenceMgr.putPage(this, mUrl, mLastPage);
        RecentReadBook recentReadBook = new RecentReadBook();
        recentReadBook.setPage(mLastPage);
        recentReadBook.setTime(System.currentTimeMillis());
        mBook.save();
        recentReadBook.setBook(mBook);
        recentReadBook.setPercent(mPercent);
        recentReadBook.setUrl(mBook.getUrl());
        recentReadBook.saveOrUpdate("url=?", mBook.getUrl());
    }
}
