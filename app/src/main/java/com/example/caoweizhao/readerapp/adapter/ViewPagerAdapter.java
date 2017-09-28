package com.example.caoweizhao.readerapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoweizhao on 2017-9-21.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragments;
    private List<String> mTitles = new ArrayList<>();
    {
        mTitles.add("书架");
        mTitles.add("书城");
        mTitles.add("我的");
    }

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        mFragments = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
