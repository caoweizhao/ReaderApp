package com.example.caoweizhao.readerapp.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.caoweizhao.readerapp.DownloadBookTask;
import com.example.caoweizhao.readerapp.DownloadService;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.adapter.DownloadAdapter;
import com.example.caoweizhao.readerapp.base.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by caoweizhao on 2017-12-27.
 */

public class DownloadActivity extends BaseActivity {

    @BindView(R.id.download_recycler_view)
    RecyclerView mRecyclerView;

    DownloadAdapter mAdapter;
    List<DownloadBookTask> mDownloadBeanList = new ArrayList<>();
    DownloadService.DownloadBinder mBinder;
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (DownloadService.DownloadBinder) service;
            mDownloadBeanList.clear();
            mDownloadBeanList.addAll(mBinder.getTasks());
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private static class MyHandler extends Handler {
        private WeakReference<DownloadActivity> activityWeakReference;

        public MyHandler(DownloadActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DownloadActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.updateProgress();
                if (activity.mDownloadBeanList.size() > 0) {
                    Log.d("MyHandler", "handleMessage:" + activity.mDownloadBeanList.get(0).getUrl() + "-" + activity.mDownloadBeanList.get(0).getPercent());
                }
                sendEmptyMessageDelayed(0, 1000);
            }
        }
    }

    private MyHandler mHandler;

    private void updateProgress() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_download;
    }

    @Override
    protected void initData() {
        super.initData();
        setToolbar(R.id.toolbar, "下载管理");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DownloadAdapter(mDownloadBeanList);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_view);
        Intent intent = new Intent(this, DownloadService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        mHandler = new MyHandler(this);
        mHandler.sendEmptyMessage(0);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.download_or_pause_download_page:
                        if (mDownloadBeanList.get(position).getDownloadState() == DownloadBookTask.STATE_DOWNLOADING) {
                            mBinder.pause(position);
                        } else {
                            mBinder.resume(position);
                        }
                        mAdapter.notifyItemChanged(position);
                        break;
                    case R.id.cancel_download_page:
                        mBinder.cancel(position);
                        mDownloadBeanList.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("DownloadActivity","onNewIntent");
    }
}
