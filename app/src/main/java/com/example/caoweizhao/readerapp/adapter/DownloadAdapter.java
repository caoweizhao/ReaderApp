package com.example.caoweizhao.readerapp.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.caoweizhao.readerapp.DownloadBookTask;
import com.example.caoweizhao.readerapp.R;

import java.util.List;

/**
 * Created by caoweizhao on 2017-12-27.
 */

public class DownloadAdapter extends BaseQuickAdapter<DownloadBookTask, BaseViewHolder> {
    public DownloadAdapter(@Nullable List<DownloadBookTask> data) {
        super(R.layout.download_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DownloadBookTask item) {
        helper.setText(R.id.file_name_download_page, item.getFileName())
                .setProgress(R.id.progress_bar_download_page, (int) item.getPercent())
                .setText(R.id.percent_download_page, (item.getDownloadState() == DownloadBookTask.STATE_COMPLETE) ? "已完成" : (item.getPercent() + "%"))
                .setText(R.id.download_or_pause_download_page, item.getDownloadState() == DownloadBookTask.STATE_DOWNLOADING ? "暂停" : "继续")
                .addOnClickListener(R.id.cancel_download_page)
                .addOnClickListener(R.id.download_or_pause_download_page);
    }
}
