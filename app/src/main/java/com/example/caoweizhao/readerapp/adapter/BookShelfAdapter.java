package com.example.caoweizhao.readerapp.adapter;

import android.support.annotation.Nullable;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.bean.Book;

import java.util.List;

/**
 * Created by caoweizhao on 2017-12-9.
 */

public class BookShelfAdapter extends BaseQuickAdapter<Book, BaseViewHolder> {

    public BookShelfAdapter(@Nullable List<Book> data) {
        super(R.layout.shelf_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Book item) {
        Log.d("BookShelfAdapter",item.getAuthor());
    }
}
