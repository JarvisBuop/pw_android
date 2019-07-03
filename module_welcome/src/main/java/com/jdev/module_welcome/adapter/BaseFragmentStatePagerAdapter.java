package com.jdev.module_welcome.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BaseFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    public BaseFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    List<Fragment> mFragments = new ArrayList<>();
    List<String> mTabs = new ArrayList<>();

    public BaseFragmentStatePagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, List<String> tabs) {
        super(fm);
        mFragments = fragments;
        mTabs = tabs;
    }

    public BaseFragmentStatePagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, String[] tabs) {
        this(fm, fragments, Arrays.asList(tabs));
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
        return mTabs.get(position);
    }
}
