package com.example.caoweizhao.readerapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.caoweizhao.readerapp.mvp.view.BookStoreSubFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoweizhao on 2018-2-21.
 */

public class BookStoreFragmentAdapter extends FragmentPagerAdapter {

    List<String> mTitles = new ArrayList<>();

    {
        mTitles.add("Android");
        mTitles.add("Java");
        mTitles.add("算法");
        mTitles.add("设计模式");
        mTitles.add("数据结构");
        mTitles.add("Scala");
    }

    public BookStoreFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new BookStoreSubFragment(mTitles.get(position));
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
