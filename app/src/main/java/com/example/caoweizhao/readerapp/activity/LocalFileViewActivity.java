package com.example.caoweizhao.readerapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.caoweizhao.readerapp.Constant;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.base.BaseActivity;
import com.example.caoweizhao.readerapp.bean.LocalFile;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

import butterknife.BindView;

/**
 * Created by caoweizhao on 2018-2-2.
 */

public class LocalFileViewActivity extends BaseActivity implements TbsReaderView.ReaderCallback {

    @BindView(R.id.frame_layout)
    FrameLayout mLayout;

    Toolbar mToolbar;
    TbsReaderView mTbsReaderView;
    View mDecorView;

    AlertDialog mDialog;

    LocalFile mLocalFile;
    File mFile;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_local_file_view;
    }

    private float mLastDownX;
    private float mLastDownY;

    @Override
    protected void initData() {
        super.initData();
        mDecorView = getWindow().getDecorView();
        Intent intent = getIntent();
        mLocalFile = intent.getParcelableExtra("file");
        if (mLocalFile == null ||
                ((mFile = mLocalFile.getReferenceFile()) == null)) {
            mDialog = new AlertDialog.Builder(this)
                    .setTitle("发生错误")
                    .setMessage("发生未知错误")
                    .setCancelable(false)
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("LocalFileViewActivity", "onClick");
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .create();
            mDialog.show();
        } else {
            mTbsReaderView = new TbsReaderView(this, this) {
                @Override
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    if (ev.getAction() == MotionEvent.ACTION_UP) {
                        float x = ev.getX();
                        float y = ev.getY();
                        if (Math.abs(x - mLastDownX) < 10 &&
                                Math.abs(y - mLastDownY) < 10) {
                            toggleToolbar();
                        }
                    } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                        mLastDownX = ev.getX();
                        mLastDownY = ev.getY();
                    } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                        float x2 = ev.getX();
                        float y2 = ev.getY();
                        if (Math.abs(x2 - mLastDownX) > 10 ||
                                Math.abs(y2 - mLastDownY) > 10) {
                            mToolbar.setVisibility(INVISIBLE);
                        }
                    }
                    return super.dispatchTouchEvent(ev);
                }
            };
            mToolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, mLayout,false);
            mToolbar.setVisibility(View.GONE);
            mLayout.addView(mTbsReaderView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mLayout.addView(mToolbar);
            mToolbar.setId(R.id.toolbar);
            setToolbar(R.id.toolbar);

            //displayFile();
            showFile();
        }

    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    private void displayFile() {
        QbSdk.initX5Environment(LocalFileViewActivity.this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.d("LocalFileViewActivity", "onCoreInitFinished");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                //这里被回调，并且b=true说明内核初始化并可以使用
                //如果b=false,内核会尝试安装，你可以通过下面监听接口获知
                Log.d("LocalFileViewActivity", "onViewInitFinished" + b);
                if (b) {
                    showFile();
                }
            }
        });
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                //tbs内核下载完成回调
                Log.d("LocalFileViewActivity", "onDownloadFinish" + i);
            }

            @Override
            public void onInstallFinish(int i) {
                //内核安装完成回调，
                Log.d("LocalFileViewActivity", "onInstallFinish" + i);
                showFile();
            }

            @Override
            public void onDownloadProgress(int i) {
                //下载进度监听
                Log.d("LocalFileViewActivity", "onDownloadProgress" + i);
            }
        });
    }

    private void showFile() {
        String filePath = mFile.getAbsolutePath();
        Bundle bundle = new Bundle();
        String bsReaderTemp = Constant.TAMP_FILE_DIR;
        File tmpFile = new File(bsReaderTemp);
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
        bundle.putString("filePath", filePath);
        bundle.putString("tempPath", tmpFile.getAbsolutePath());
        boolean result = mTbsReaderView.preOpen(getFileType(filePath), false);
        if (result) {
            mTbsReaderView.openFile(bundle);
        }
    }

    private void toggleToolbar() {

        Log.d("LocalFileViewActivity", "toggleToolbar");
        if (mToolbar.getVisibility() == View.VISIBLE) {
            mToolbar.setVisibility(View.GONE);
        } else {
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }

        str = paramString.substring(i + 1);
        return str;
    }

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

    @Override
    protected void onDestroy() {
        mTbsReaderView.onStop();
        super.onDestroy();
    }
}
