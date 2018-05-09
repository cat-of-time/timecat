package com.yumingchuan.rsqmonthcalendar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yumingchuan.rsqmonthcalendar.R;
import com.yumingchuan.rsqmonthcalendar.bean.ScheduleToDo;

/**
 * Created by yumingchuan on 2017/7/19.
 */

public class WeekViewAdapter2 extends BaseRecyclerViewAdapter<ScheduleToDo> {

    private final Context mContext;

    public WeekViewAdapter2(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(mContext).inflate(R.layout.item_schedule_calendar, null);
    }

    @Override
    public void bindViewData(View itemView, ScheduleToDo scheduleToDo, int position) {

    }
}
