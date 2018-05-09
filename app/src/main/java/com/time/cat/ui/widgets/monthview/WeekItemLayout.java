package com.yumingchuan.rsqmonthcalendar.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yumingchuan.rsqmonthcalendar.R;
import com.yumingchuan.rsqmonthcalendar.adapter.WeekViewAdapter2;
import com.yumingchuan.rsqmonthcalendar.bean.ScheduleToDo;

import java.util.List;


/**
 * Created by yumingchuan on 2017/9/14.
 */

public class WeekItemLayout extends RelativeLayout implements View.OnClickListener {

    private RecyclerView refreshList;
    private TextView tv_createSchedule;
    private WeekViewAdapter2 adapter;

    public WeekItemLayout(Context context) {
        super(context);
        initView();
        initLister();
        initAdapter();
    }

    public WeekItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initLister();
        initAdapter();
    }

    public WeekItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_week_schedule, null);
        addView(view);
        refreshList = (RecyclerView) view.findViewById(R.id.refreshList);
        tv_createSchedule = (TextView) view.findViewById(R.id.tv_createSchedule);
    }


    private void initLister() {
        tv_createSchedule.setOnClickListener(this);
    }

    private void initAdapter() {
        adapter = new WeekViewAdapter2(getContext());
        refreshList.setLayoutManager(new LinearLayoutManager(getContext()));
        //refreshList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        //((HomeActivity) getContext()).addSchedule();
    }

    public void refreshData(List<ScheduleToDo> tempTodos) {
        tv_createSchedule.setVisibility(tempTodos.size() != 0 ? View.GONE : View.VISIBLE);
        refreshList.setVisibility(tempTodos.size() != 0 ? View.VISIBLE : View.GONE);
        //adapter.reloadData(tempTodos);
    }
}
