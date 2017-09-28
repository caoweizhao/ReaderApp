package com.example.caoweizhao.readerapp.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.caoweizhao.readerapp.Constant;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.bean.Book;

import java.util.List;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class BookStoreAdapter extends BaseQuickAdapter<Book, BaseViewHolder> {

    Fragment mFragment;

    public BookStoreAdapter(Context context, @Nullable List<Book> data) {
        super(R.layout.book_store_item, data);
    }

    public BookStoreAdapter(Fragment fragment, @Nullable List<Book> data) {
        super(R.layout.book_store_item, data);
        mFragment = fragment;
    }

    @Override
    protected void convert(BaseViewHolder helper, Book item) {
        helper.setText(R.id.item_book_name, item.getName());
        ImageView imageView = (ImageView) helper.itemView.findViewById(R.id.item_book_img);
        Log.d("BookStoreAdapter",item.getImg_url());
        String url = Constant.BASE_URL + "book/images/" + item.getImg_url();
        Log.d("BookStoreAdapter",url);
        Glide.with(mFragment)
                .load(url)
                .into(imageView);
    }
}
