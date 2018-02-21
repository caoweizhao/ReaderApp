package com.example.caoweizhao.readerapp.mvp.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.adapter.BookStoreAdapter;
import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.mvp.presenter.BookStorePresenter;
import com.example.caoweizhao.readerapp.mvp.presenter.IBookStorePresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by caoweizhao on 2018-2-21.
 */

@SuppressLint("ValidFragment")
public class BookStoreSubFragment extends android.support.v4.app.Fragment implements IBookStoreSubView {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    BookStoreAdapter mAdapter;
    List<Book> mBooks = new ArrayList<>();
    IBookStorePresenter mPresenter;

    private String mCategory;
    private Unbinder mUnbinder;

    public BookStoreSubFragment(String category) {
        mCategory = category;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_store_sub_view, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new BookStoreAdapter(this, mBooks);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_view);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Book book = mAdapter.getItem(position);
                Intent intent = new Intent(getContext(), BookDetailActivity.class);
                intent.putExtra("data", book);
                startActivity(intent);
            }
        });
        mPresenter = new BookStorePresenter(this);
        mPresenter.getBookStore(mCategory);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getBookStore(mCategory);
            }
        });
    }

    @Override
    public void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void dismissLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void updateBookShelf(List<Book> resultList) {
        mAdapter.setNewData(resultList);
    }

    @Override
    public void showMsg(String msg) {

    }

    @Override
    public void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroy();
    }
}
