package com.jdev.module_welcome.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BaseFragmentV4StatePagerAdapter extends FragmentStatePagerAdapter {

    public BaseFragmentV4StatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    List<Fragment> mFragments = new ArrayList<>();
    List<String> mTabs = new ArrayList<>();

    public BaseFragmentV4StatePagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, List<String> tabs) {
        super(fm);
        mFragments = fragments;
        mTabs = tabs;
    }

    public BaseFragmentV4StatePagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, String[] tabs) {
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
