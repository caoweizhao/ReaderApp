package com.example.caoweizhao.readerapp.mvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.caoweizhao.readerapp.API.CollectionServices;
import com.example.caoweizhao.readerapp.MyApplication;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.adapter.BookShelfAdapter;
import com.example.caoweizhao.readerapp.base.BaseFragment;
import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.bean.Collection;
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

import static com.example.caoweizhao.readerapp.MyApplication.mUser;

/**
 * Created by caoweizhao on 2017-9-27.
 */

public class BookShelfFragment extends BaseFragment {
    @BindView(R.id.book_shelf_recycler_view)
    RecyclerView mRecyclerView;

    BookShelfAdapter mAdapter;
    List<Book> mBooks = new ArrayList<>();

    CollectionServices mServices;
    List<Disposable> mDisposables = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.book_shelf_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mServices = RetrofitUtil.getRetrofit()
                .create(CollectionServices.class);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mAdapter = new BookShelfAdapter(mBooks);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Book book = mBooks.get(position);
                Intent intent = new Intent(getContext(), BookDetailActivity.class);
                intent.putExtra("data", book);
                startActivity(intent);
            }
        });
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_view);
    }


    @Override
    public void onResume() {
        super.onResume();
        getCollections();
    }

    private void getCollections() {
        mBooks.clear();
        if (mUser == null) {
            mUser = MyApplication.getUser();
        }
        if (mUser != null) {
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
                            mBooks.add(value);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("CollectionActivity", e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        for (Disposable d : mDisposables
                ) {
            if (d != null) {
                d.dispose();
            }
        }
        super.onDestroyView();
    }

}
