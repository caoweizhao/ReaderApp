package com.example.caoweizhao.readerapp.mvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.caoweizhao.readerapp.API.CollectionServices;
import com.example.caoweizhao.readerapp.Constant;
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
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.last_read_block)
    FrameLayout mLastReadBlock;

    BookShelfAdapter mAdapter;
    List<Book> mBooks = new ArrayList<>();

    CollectionServices mServices;
    List<Disposable> mDisposables = new ArrayList<>();

    float radius = 20;
    @BindView(R.id.book_shelf_img)
    ImageView mLastReadBookImg;
    @BindView(R.id.book_shelf_name)
    TextView mLastReadBookText;
    @BindView(R.id.bg_container)
    View mLastReadBookBG;


    @Override
    protected int getLayoutId() {
        return R.layout.book_shelf_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLastReadBookBG.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
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
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCollections();
            }
        });

        mLastReadBookBG = view.findViewById(R.id.bg_container);
        mLastReadBookImg = (ImageView) view.findViewById(R.id.book_shelf_img);

    }

    @Override
    public void onResume() {
        super.onResume();
        getCollections();
    }

    private void getCollections() {
        mSwipeRefreshLayout.setRefreshing(true);
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
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onComplete() {
                            mAdapter.notifyDataSetChanged();
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (mBooks.size() == 0) {
                                mLastReadBlock.setVisibility(View.GONE);
                            } else {
                                mLastReadBlock.setVisibility(View.VISIBLE);
                                Book book = mBooks.get(0);
                                mLastReadBookText.setText(book.getName());
                                String url = Constant.BASE_URL + "book/images/" + book.getImg_url();
                                Glide.with(getContext())
                                        .load(url)
                                        .into(mLastReadBookImg);
                            }
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
