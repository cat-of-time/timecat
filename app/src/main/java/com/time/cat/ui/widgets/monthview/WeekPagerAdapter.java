package com.yumingchuan.rsqmonthcalendar.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.yumingchuan.rsqmonthcalendar.bean.ScheduleToDo;
import com.yumingchuan.rsqmonthcalendar.view.WeekItemLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumingchuan on 2017/9/14.
 */

public class WeekPagerAdapter extends PagerAdapter {

    private final List<View> list;

    public WeekPagerAdapter() {
        list = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = list.get(position);
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeAllViews();
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(list.get(position));
    }

    public void reloadData(List<View> tempList) {
        list.clear();
        list.addAll(tempList);
        notifyDataSetChanged();
    }

    private WeekItemLayout getSomeView(int position) {
        if (list != null && list.size() > position) {
            return (WeekItemLayout) list.get(position);
        } else {
            return null;
        }
    }

    /**
     * 刷新对应的数据
     *
     * @param position
     * @param toDos
     */
    public void refreshItemData(int position, List<ScheduleToDo> toDos) {
        if (getSomeView(position) != null) {
            getSomeView(position).refreshData(toDos);
        }
    }

}
