package com.example.caoweizhao.readerapp.mvp.view;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.adapter.ViewPagerAdapter;
import com.example.caoweizhao.readerapp.base.BaseActivity;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by caoweizhao on 2017-9-21.
 */

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.bottom_bar)
    BottomBar mBottomBar;

    ViewPagerAdapter mAdapter;

    List<Fragment> mFragments;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        super.initData();
        mFragments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                mFragments.add(new BookShelfFragment());
            } else {
                mFragments.add(new SelfFragment());
            }
        }

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);

    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBottomBar.selectTabAtPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                mViewPager.setCurrentItem(mBottomBar.findPositionForTabWithId(tabId));
            }
        });
    }
}
