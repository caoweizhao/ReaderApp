package com.example.caoweizhao.readerapp.mvp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.adapter.BookShelfAdapter;
import com.example.caoweizhao.readerapp.base.BaseFragment;
import com.example.caoweizhao.readerapp.bean.Book;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by caoweizhao on 2017-9-27.
 */

public class BookShelfFragment extends BaseFragment {
    @BindView(R.id.book_shelf_recycler_view)
    RecyclerView mRecyclerView;

    BookShelfAdapter mAdapter;
    List<Book> mBooks = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.book_shelf_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Book book = new Book();
        book.setAuthor("caoweizhao");
        mBooks.add(book);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new BookShelfAdapter(mBooks);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_view);
    }
}
