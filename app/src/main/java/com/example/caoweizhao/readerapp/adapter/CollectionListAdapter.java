package com.example.caoweizhao.readerapp.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.caoweizhao.readerapp.Constant;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.bean.Book;

import java.util.List;

/**
 * Created by caoweizhao on 2017-12-9.
 */

public class CollectionListAdapter extends BaseQuickAdapter<Book, BaseViewHolder> {

    Context mContext;
    public CollectionListAdapter(Context context, @Nullable List<Book> data) {
        super(R.layout.collection_item, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Book item) {
        helper.setText(R.id.collection_name, item.getName())
                .setText(R.id.collection_summary, item.getSummary());
        String url = Constant.BASE_URL + "book/images/" + item.getImg_url();
        ImageView imageView = (ImageView) helper.itemView.findViewById(R.id.collection_img);
        Glide.with(mContext)
                .load(url)
                .into(imageView);
    }
}
