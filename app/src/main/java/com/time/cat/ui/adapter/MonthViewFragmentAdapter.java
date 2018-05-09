package com.yumingchuan.rsqmonthcalendar.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yumingchuan.rsqmonthcalendar.ui.MonthViewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumingchuan on 2017/6/15.
 * 无限循环viewpager adapter
 */

public class MonthViewFragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;

    public MonthViewFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {

        Fragment page;
        if (fragmentList.size() > position) {
            page = fragmentList.get(position);
            if (page != null) {
                return page;
            }
        }
        while (position >= fragmentList.size()) {
            fragmentList.add(null);
        }
        page = MonthViewFragment.newInstance(position);
        fragmentList.set(position, page);

        return page;
    }

    @Override
    public int getCount() {
        return 48;
    }

}
