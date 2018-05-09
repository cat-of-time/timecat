package com.yumingchuan.rsqmonthcalendar.ui;


import android.annotation.SuppressLint;

import com.yumingchuan.rsqmonthcalendar.R;
import com.yumingchuan.rsqmonthcalendar.base.BaseFragment;

import butterknife.BindView;

@SuppressLint("ValidFragment")
public class MonthViewFragment extends BaseFragment {

    private int currentPosition;

    @BindView(R.id.customMonthView)
    MonthViewLayout customMonthView;

    public MonthViewFragment(int position) {
        currentPosition = position;
    }


    public MonthViewFragment() {

    }

    public static MonthViewFragment newInstance(int position) {
        return new MonthViewFragment(position);
    }

    public void setCurrentPosition(int position) {
        currentPosition = position;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_month_tt;
    }

    @Override
    protected void initView() {
        super.initView();

        customMonthView.initCurrentCalendar(currentPosition);
        customMonthView.initCurrentMonthInfo();
        customMonthView.renderMonthCalendarData(false);
    }


    @Override
    public void initData() {
        super.initData();

    }

    public MonthViewLayout getCustomMonthView() {
        return customMonthView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && getCustomMonthView() != null) {
            getCustomMonthView().closeSomeDate(isVisibleToUser);
        }
    }


}
