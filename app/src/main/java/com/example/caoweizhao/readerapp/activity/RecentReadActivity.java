package com.example.caoweizhao.readerapp.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.adapter.RecentReadAdapter;
import com.example.caoweizhao.readerapp.base.BaseActivity;
import com.example.caoweizhao.readerapp.bean.RecentReadBook;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by caoweizhao on 2018-1-13.
 */

public class RecentReadActivity extends BaseActivity {

    @BindView(R.id.recent_reading_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recent_reading_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    List<RecentReadBook> mRecentReadBooks;
    RecentReadAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recent_read;
    }

    @Override
    protected void initData() {
        super.initData();
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
        mRecentReadBooks = new ArrayList<>();
        mAdapter = new RecentReadAdapter(this,mRecentReadBooks);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_view);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRecentReadBooks();
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                RecentReadBook recentReadBook = mRecentReadBooks.get(position);
                /*Intent intent = new Intent(CollectionActivity.this, BookDetailActivity.class);
                intent.putExtra("data",book);
                startActivity(intent);*/
            }
        });
    }

    public void getRecentReadBooks() {
        List<RecentReadBook> recentReadBooks = DataSupport.order("time desc").find(RecentReadBook.class,true);
        if (recentReadBooks.size() > 0) {
            mRecentReadBooks = recentReadBooks;
            mAdapter.setNewData(recentReadBooks);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSwipeRefreshLayout.setRefreshing(true);
        getRecentReadBooks();
    }
}
