package com.example.caoweizhao.readerapp.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.bean.LocalFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caoweizhao on 2018-2-5.
 */

public class LocalFileAdapter extends BaseQuickAdapter<LocalFile, BaseViewHolder> {

    public static Map<String, Integer> IMAGE_RESOURCE_MAP = new HashMap<>();

    {
        IMAGE_RESOURCE_MAP.put(".doc", R.drawable.ic_word);
        IMAGE_RESOURCE_MAP.put(".docx", R.drawable.ic_word);
        IMAGE_RESOURCE_MAP.put(".xlsx", R.drawable.ic_excel);
        IMAGE_RESOURCE_MAP.put(".xls", R.drawable.ic_excel);
        IMAGE_RESOURCE_MAP.put(".pdf", R.drawable.ic_pdf);
        //IMAGE_RESOURCE_MAP.put(".xml", R.drawable.ic_xml);
        IMAGE_RESOURCE_MAP.put(".txt", R.drawable.ic_txt);
        IMAGE_RESOURCE_MAP.put(".ppt", R.drawable.ic_ppt);
        /*IMAGE_RESOURCE_MAP.put(".htm", R.drawable.ic_html);
        IMAGE_RESOURCE_MAP.put(".html", R.drawable.ic_html);*/

    }

    public LocalFileAdapter(@Nullable List<LocalFile> data) {
        super(R.layout.local_file_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LocalFile item) {
        helper.setText(R.id.local_file_name, item.getName())
                .setImageResource(R.id.local_file_img, getImageResource(item.getName()));
    }

    private int getImageResource(String name) {
        String tail = null;
        try {
            tail = name.substring(name.lastIndexOf("."));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tail != null) {
            Integer res = IMAGE_RESOURCE_MAP.get(tail);
            if (res != null) {
                return res;
            }
        }
        return R.drawable.ic_txt;
    }
}
