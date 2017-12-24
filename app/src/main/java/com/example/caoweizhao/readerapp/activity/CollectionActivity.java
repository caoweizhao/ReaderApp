package com.example.caoweizhao.readerapp.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.caoweizhao.readerapp.API.CollectionServices;
import com.example.caoweizhao.readerapp.MyApplication;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.adapter.CollectionListAdapter;
import com.example.caoweizhao.readerapp.base.BaseActivity;
import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.bean.Collection;
import com.example.caoweizhao.readerapp.bean.User;
import com.example.caoweizhao.readerapp.mvp.view.BookDetailActivity;
import com.example.caoweizhao.readerapp.util.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CollectionActivity extends BaseActivity {

    @BindView(R.id.collection_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.collection_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    CollectionServices mServices;
    List<Disposable> mDisposables = new ArrayList<>();

    User mUser;

    List<Book> mCollectionsList;
    CollectionListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collection;
    }

    @Override
    protected void initData() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        ActionBar ab;
        if ((ab = getSupportActionBar()) != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCollectionsList = new ArrayList<>();
        mAdapter = new CollectionListAdapter(this, mCollectionsList);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_view);
        mServices = RetrofitUtil.getRetrofit()
                .create(CollectionServices.class);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCollections();
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Book book =  mCollectionsList.get(position);
                Intent intent = new Intent(CollectionActivity.this, BookDetailActivity.class);
                intent.putExtra("data",book);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCollections();
    }

    private void getCollections() {
        mCollectionsList.clear();
        if (mUser == null) {
            mUser = MyApplication.getUser();
        }
        if (mUser != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            mServices.queryAllByUser(mUser.getUser_name())
                    .subscribeOn(Schedulers.newThread())
                    .flatMap(new Function<List<Collection>, ObservableSource<Collection>>() {
                        @Override
                        public ObservableSource<Collection> apply(List<Collection> collections) throws Exception {
                            return io.reactivex.Observable.fromIterable(collections);
                        }
                    }).map(new Function<Collection, Book>() {

                @Override
                public Book apply(Collection collection) throws Exception {
                    return mServices.queryBookById(collection.getId()).execute().body();
                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Book>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mDisposables.add(d);
                        }

                        @Override
                        public void onNext(Book value) {
                            mCollectionsList.add(value);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("CollectionActivity", e.getMessage());
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onComplete() {
                            mAdapter.notifyDataSetChanged();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        for (Disposable d : mDisposables
                ) {
            if (d != null) {
                d.dispose();
            }
        }
        super.onDestroy();
    }
}
