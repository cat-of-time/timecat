package com.yumingchuan.rsqmonthcalendar.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.yumingchuan.rsqmonthcalendar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumingchuan on 2017/9/14.
 */

public class WeekItemLayoutUtils {

    public static WeekItemLayoutUtils weekItemLayoutUtils = new WeekItemLayoutUtils();
    private List<View> list;

    public static WeekItemLayoutUtils news() {
        return weekItemLayoutUtils;
    }

    public List<View> getViews(Context context) {
        if (list == null) {
            list = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                list.add(LayoutInflater.from(context).inflate(R.layout.item_week_view, null));
            }
        }
        return list;
    }


}
