package com.example.caoweizhao.readerapp.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.caoweizhao.readerapp.Constant;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.bean.RecentReadBook;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by caoweizhao on 2017-12-9.
 */

public class BookShelfAdapter extends BaseQuickAdapter<Book, BaseViewHolder> {

    public BookShelfAdapter( @Nullable List<Book> data) {
        super(R.layout.shelf_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Book item) {
        RecentReadBook recentReadBook = DataSupport.where("url=?", item.getUrl()).findFirst(RecentReadBook.class);
        float percent = 0;
        if (recentReadBook != null) {
            percent = recentReadBook.getPercent();
        }
        helper.setText(R.id.book_shelf_name, item.getName())
                .setText(R.id.book_shelf_percent, "阅读至：" + percent + "%");
        String url = Constant.BASE_URL + "book/images/" + item.getImg_url();
        ImageView imageView = (ImageView) helper.itemView.findViewById(R.id.book_shelf_img);
        Glide.with(mContext)
                .load(url)
                .into(imageView);
    }
}
