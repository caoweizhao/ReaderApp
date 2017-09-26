package com.example.caoweizhao.readerapp.mvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.adapter.BookShelfAdapter;
import com.example.caoweizhao.readerapp.base.BaseFragment;
import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.mvp.presenter.BookShelfPresenter;
import com.example.caoweizhao.readerapp.mvp.presenter.IBookShelfPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class BookShelfFragment extends BaseFragment implements IBookShelfView {

    @BindView(R.id.book_shelf_recycler_view)
    RecyclerView mRecyclerView;

    BookShelfAdapter mAdapter;
    List<Book> mBooks = new ArrayList<>();
    IBookShelfPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.book_shelf_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mAdapter = new BookShelfAdapter(this, mBooks);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_view);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Book book = mAdapter.getItem(position);
                Intent intent = new Intent(getContext(), ReaderActivity.class);
                intent.putExtra("data", book.getUrl());
                startActivity(intent);
            }
        });
        mPresenter = new BookShelfPresenter(this);
        mPresenter.getBookShelf();
    }

    @Override
    public void showLoading() {
        Toast.makeText(getActivity(), "Loading..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismissLoading() {
        Toast.makeText(getActivity(), "Dismiss Loading...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateBookShelf(List<Book> resultList) {
        mAdapter.setNewData(resultList);
    }

    @Override
    public void showMsg(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
