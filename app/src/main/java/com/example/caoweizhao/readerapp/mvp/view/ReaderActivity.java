package com.example.caoweizhao.readerapp.mvp.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.caoweizhao.readerapp.DownloadBookTask;
import com.example.caoweizhao.readerapp.MyApplication;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.activity.NewNoteActivity;
import com.example.caoweizhao.readerapp.activity.NoteBookActivity;
import com.example.caoweizhao.readerapp.base.BaseActivity;
import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.bean.RecentReadBook;
import com.example.caoweizhao.readerapp.mvp.presenter.IReaderPresenter;
import com.example.caoweizhao.readerapp.mvp.presenter.ReaderPresenter;
import com.example.caoweizhao.readerapp.ocr.OCRActivity;
import com.example.caoweizhao.readerapp.util.FileHelper;
import com.example.caoweizhao.readerapp.util.FileUtil;
import com.example.caoweizhao.readerapp.util.SharePreferenceMgr;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class ReaderActivity extends BaseActivity implements IReaderView {

    @BindView(R.id.pdf_view)
    PDFView mPDFView;
    @BindView(R.id.ocr_button)
    FloatingActionButton mOcrButton;
    @BindView(R.id.loading_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    View mDecorView;


    IReaderPresenter mPresenter;
    int mLastPage = 0;
    float mPercent = 0;
    String mUrl;
    Book mBook;
    boolean isDownloaded;
    private File mTempFile;

    private Disposable mDisposable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reader;
    }

    @Override
    protected void initData() {
        super.initData();
        loading();
        setToolbar(R.id.toolbar);
        mPDFView.enableRenderDuringScale(false);
        mDecorView = getWindow().getDecorView();
        Intent intent = getIntent();
        mUrl = intent.getStringExtra("data");
        mBook = intent.getParcelableExtra("book");
        mLastPage = SharePreferenceMgr.getPage(this, mUrl);
        Log.d("ReaderActivity", "mUrl:" + mUrl);
        File dir = FileHelper.getBooksDir();
        File file = new File(dir, mUrl);
        mTempFile = new File(dir, "temp" + mUrl);
        //文件存在，可能已下载或者正在下载
        if (file.exists()) {
            List<DownloadBookTask> downloadBookTasks = DataSupport.findAll(DownloadBookTask.class);
            for (DownloadBookTask dt : downloadBookTasks
                    ) {
                //文件已下载
                if (dt.getUrl().equals(mUrl) &&
                        (dt.getDownloadState() == DownloadBookTask.STATE_COMPLETE)) {
                    isDownloaded = true;
                    PDFView.Configurator configurator = mPDFView.fromFile(file);
                    loadBook(configurator);
                    break;
                }
            }
        }

        if (mTempFile.exists()) {
            PDFView.Configurator configurator = mPDFView.fromFile(mTempFile);
            loadBook(configurator);
        } else if (!isDownloaded && mUrl != null) {
            //文件不存在或者正在下载
            Log.d("ReaderActivity", "Load From Net");
            loadBook();
        }
    }

    private void loadBook() {
        mPresenter = new ReaderPresenter(this);
        mPresenter.loadBook(mUrl);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mPDFView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFAB();
            }
        });
    }

    private void toggleFAB() {
        if (mOcrButton.getVisibility() == View.VISIBLE) {
            mOcrButton.setVisibility(View.GONE);
            mToolbar.setVisibility(View.GONE);
            // hideSystemUI();
        } else {
            mOcrButton.setVisibility(View.VISIBLE);
            mToolbar.setVisibility(View.VISIBLE);
            //showSystemUI();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reader_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_note:
                Intent intent = new Intent(ReaderActivity.this, NewNoteActivity.class);
                intent.putExtra("page", mLastPage);
                intent.putExtra("bookId", mBook.getId());
                startActivity(intent);
                break;
            case R.id.note_book:
                Intent intent2 = new Intent(ReaderActivity.this, NoteBookActivity.class);
                intent2.putExtra("bookId", mBook.getId());
                intent2.putExtra("userName", MyApplication.getUser().getUser_name());
                startActivity(intent2);
                break;
        }
        return true;
    }

    private void loading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void endLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void dismissLoading() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    public void updatePdfView(final InputStream is) {

        io.reactivex.Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) throws Exception {
                File dir = FileHelper.getBooksDir();
                File file = new File(dir, "temp" + mBook.getUrl());
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, len);
                    }
                    is.close();
                    fileOutputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.onNext(file);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(File value) {
                        PDFView.Configurator configurator = mPDFView.fromFile(value);
                        loadBook(configurator);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

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
        if (mDisposable != null) {
            mDisposable.dispose();
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

    private void loadBook(PDFView.Configurator configurator) {
        configurator
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
                .spacing(0)
                .pageFitPolicy(FitPolicy.BOTH)
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Log.d("ReaderActivity", "onError" + t.getMessage());
                        if (mTempFile != null && mTempFile.exists()) {
                            mTempFile.delete();
                            loadBook();
                        }
                    }
                })
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        if (mLastPage > 0) {
                            Toast.makeText(ReaderActivity.this, "已恢复上次阅读位置", Toast.LENGTH_SHORT).show();
                        }
                        endLoading();
                    }
                })
                .load();

    }

    public void orcTest(View view) {
        try {
            FileOutputStream fileOutputStream = null;
            fileOutputStream = new FileOutputStream(FileUtil.getSaveFile(MyApplication.getmContext()));
            mPDFView.setDrawingCacheEnabled(true);
            Bitmap bitmap = mPDFView.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
            mPDFView.setDrawingCacheEnabled(false);
            Intent intent = new Intent(ReaderActivity.this, OCRActivity.class);
            intent.putExtra("page", mLastPage);
            intent.putExtra("bookId", mBook.getId());
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}
