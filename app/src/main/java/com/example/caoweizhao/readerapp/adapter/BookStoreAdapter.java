package com.example.caoweizhao.readerapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
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

    Context mContext;

    public BookStoreAdapter(Context context, @Nullable List<Book> data) {
        super(R.layout.book_store_item, data);
        mContext = context;
    }

    public BookStoreAdapter(Fragment fragment, @Nullable List<Book> data) {
        super(R.layout.book_store_item, data);
        mFragment = fragment;
        mContext = mFragment.getContext();
    }

    @Override
    protected void convert(BaseViewHolder helper, Book item) {
        helper.setText(R.id.item_book_name, item.getName());
        ImageView imageView = (ImageView) helper.itemView.findViewById(R.id.item_book_img);
        String url = Constant.BASE_URL + "book/images/" + item.getImg_url();
        Log.d("BookStoreAdapter", url);
        Glide.with(mFragment)
                .load(url)
                .into(imageView);
        imageView.setDrawingCacheEnabled(true);
    }

    public Bitmap convertDrawable2BitmapByCanvas(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
//canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
