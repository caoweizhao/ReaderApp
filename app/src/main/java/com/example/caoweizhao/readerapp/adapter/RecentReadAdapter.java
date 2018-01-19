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
import com.example.caoweizhao.readerapp.bean.RecentReadBook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by caoweizhao on 2018-1-13.
 */

public class RecentReadAdapter extends BaseQuickAdapter<RecentReadBook, BaseViewHolder> {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Context mContext;

    public RecentReadAdapter(Context context,@Nullable List<RecentReadBook> data) {
        super(R.layout.recent_reading_item, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, RecentReadBook item) {
        Book book = item.getBook();
        ImageView imageView = (ImageView) helper.itemView.findViewById(R.id.recent_reading_item_img);
        helper.setText(R.id.recent_reading_item_name, book == null ? "" : book.getName())
                .setText(R.id.recent_reading_item_progress, "阅读至："+item.getPercent() + "%")
                .setText(R.id.recent_reading_item_time, sdf.format(new Date(item.getTime())));
        String url = Constant.BASE_URL + "book/images/" + book.getImg_url();
        Glide.with(mContext)
                .load(url)
                .into(imageView);
    }
}
