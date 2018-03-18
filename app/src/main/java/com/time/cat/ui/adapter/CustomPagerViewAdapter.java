package com.time.cat.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.time.cat.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/16
 * @discription for MainActivity
 * @usage null
 */
public class CustomPagerViewAdapter extends FragmentPagerAdapter {
    private final List<BaseFragment> mFragments = new ArrayList<>();

    public CustomPagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(BaseFragment fragment) {
        mFragments.add(fragment);
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
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        //截断， 使fragment持久化，提高性能
    }

    public void replace(int position, BaseFragment baseFragment) {
        mFragments.set(position, baseFragment);
        notifyDataSetChanged();
    }

    public void notifyDataChanged() {
        for (int i = 0; i < getCount(); i++) {
            mFragments.get(i).notifyDataChanged();
        }
    }
}