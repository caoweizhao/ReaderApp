package com.example.caoweizhao.readerapp.mvp.view;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.adapter.ViewPagerAdapter;
import com.example.caoweizhao.readerapp.base.BaseActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;
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
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.title_text)
    TextView mTitle;

    ViewPagerAdapter mAdapter;

    List<Fragment> mFragments;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        super.initData();

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        SystemBarTintManager manager = new SystemBarTintManager(this);

        mFragments = new ArrayList<>();
        mFragments.add(new BookShelfFragment());
        mFragments.add(new BookStoreFragment());
        mFragments.add(new SelfFragment());

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mTitle.setText(mAdapter.getPageTitle(0));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTitle.setText(mAdapter.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
