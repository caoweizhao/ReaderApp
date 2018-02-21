package com.example.caoweizhao.readerapp.mvp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.adapter.BookStoreFragmentAdapter;
import com.example.caoweizhao.readerapp.base.BaseFragment;

import butterknife.BindView;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class BookStoreFragment extends BaseFragment {

   /* @BindView(R.id.book_shelf_recycler_view)
    RecyclerView mRecyclerView;*/
    /*@BindView(R.id.book_store_swiprefreshlayout)
    SwipeRefreshLayout mSwipeRefreshLayout;*/


    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    BookStoreFragmentAdapter mAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.book_store_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mAdapter = new BookStoreFragmentAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(5);
        mTabLayout.setupWithViewPager(mViewPager, true);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}

