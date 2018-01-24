package com.time.cat.component.activity.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldf.calendar.Utils;
import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.MonthPager;
import com.time.cat.R;
import com.time.cat.component.base.BaseFragment;
import com.time.cat.mvp.presenter.FragmentPresenter;
import com.time.cat.mvp.presenter.OnViewClickListener;
import com.time.cat.mvp.view.calendar.CustomDayView;
import com.time.cat.mvp.view.calendar.ThemeDayView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author dlink
 * @date 2018/1/24
 * @discription HomeFragment
 */
@SuppressLint("SetTextI18n")
public class HomeFragment extends BaseFragment implements FragmentPresenter, OnSelectDateListener, View.OnClickListener, OnViewClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = "HomeFragment";


    //<生命周期>-------------------------------------------------------------------------------------

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        activity.setOnViewClickListener(this);
    }

    //</生命周期>------------------------------------------------------------------------------------


    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------
    private CoordinatorLayout content;
    private MonthPager monthPager;
    private RecyclerView rvToDoList;
    private ArrayList<Calendar> currentCalendars = new ArrayList<>();
    private CalendarViewAdapter calendarAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        content = view.findViewById(R.id.content);
        monthPager = view.findViewById(R.id.calendar_view);
        //此处强行setViewHeight，毕竟你知道你的日历牌的高度
        monthPager.setViewHeight(Utils.dpi2px(context, 270));

        rvToDoList = view.findViewById(R.id.list);
        rvToDoList.setHasFixedSize(true);
        //这里用线性显示 类似于listView
        rvToDoList.setLayoutManager(new LinearLayoutManager(context));
        rvToDoList.setAdapter(new CalendarAdapter(context));

        //<功能归类分区方法，必须调用>-----------------------------------------------------------------
        initData();
        initView();
        initEvent();
        //</功能归类分区方法，必须调用>----------------------------------------------------------------

        Log.e(TAG, "OnCreated");
        return view;
    }

    private Context context;
    private CalendarDate currentDate;
    private int mCurrentPage = MonthPager.CURRENT_DAY_INDEX;
    @Override
    public void initView() {//必须调用
        initCalendarView();
        initMonthPager();
    }

    /**
     * 初始化CustomDayView，并作为CalendarViewAdapter的参数传入
     */
    private void initCalendarView() {
        CustomDayView customDayView = new CustomDayView(context, R.layout.view_calendar_custom_day);
        calendarAdapter = new CalendarViewAdapter(
                context,
                this,
                CalendarAttr.CalendarType.MONTH,
                CalendarAttr.WeekArrayType.Sunday,
                customDayView);
        calendarAdapter.setOnCalendarTypeChangedListener(new CalendarViewAdapter.OnCalendarTypeChanged() {
            @Override
            public void onCalendarTypeChanged(CalendarAttr.CalendarType type) {
                rvToDoList.scrollToPosition(0);
            }
        });
        initMarkData();
    }

    /**
     * 初始化标记数据，HashMap的形式，可自定义
     * 如果存在异步的话，在使用setMarkData之后调用 calendarAdapter.notifyDataChanged();
     */
    private void initMarkData() {
        HashMap<String, String> markData = new HashMap<>();
        markData.put("2017-8-9", "1");
        markData.put("2017-7-9", "0");
        markData.put("2017-6-9", "1");
        markData.put("2017-6-10", "0");
        calendarAdapter.setMarkData(markData);
    }

    /**
     * 初始化monthPager，MonthPager继承自ViewPager
     */
    private void initMonthPager() {
        monthPager.setAdapter(calendarAdapter);
        monthPager.setCurrentItem(MonthPager.CURRENT_DAY_INDEX);
        monthPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                position = (float) Math.sqrt(1 - Math.abs(position));
                page.setAlpha(position);
            }
        });
        monthPager.addOnPageChangeListener(new MonthPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
                currentCalendars = calendarAdapter.getPagers();
                if (currentCalendars.get(position % currentCalendars.size()) != null) {
                    CalendarDate date = currentCalendars.get(position % currentCalendars.size()).getSeedDate();
                    currentDate = date;
                    if (mDateChangeListener != null) {
                        mDateChangeListener.onDateChange(date.getYear(), date.getMonth());
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>-----------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-----------------------------------------
    @Override
    public void initData() {//必须调用
        initCurrentDate();
    }

    /**
     * 初始化currentDate
     */
    private void initCurrentDate() {
        currentDate = new CalendarDate();
        if (mDateChangeListener != null) {
            mDateChangeListener.onDateChange(currentDate.getYear(), currentDate.getMonth());
        }
    }

    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------


    //<Event事件区>---只要存在事件监听代码就是----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用
//        MainActivity.setOnViewClickListener(this);
    }


    //-//<View.OnClickListener>---------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.back_today_button:
//                refreshMonthPager();
//                break;
//            case R.id.theme_switch:
//                refreshSelectBackground();
//                break;
//            case R.id.scroll_switch:
//                if (calendarAdapter.getCalendarType() == CalendarAttr.CalendarType.WEEK) {
//                    Utils.scrollTo(content, rvToDoList, monthPager.getViewHeight(), 200);
//                    calendarAdapter.switchToMonth();
//                } else {
//                    Utils.scrollTo(content, rvToDoList, monthPager.getCellHeight(), 200);
//                    calendarAdapter.switchToWeek(monthPager.getRowIndex());
//                }
//                break;
//            case R.id.next_month:
//                monthPager.setCurrentItem(monthPager.getCurrentPosition() + 1);
//                break;
//            case R.id.last_month:
//                monthPager.setCurrentItem(monthPager.getCurrentPosition() - 1);
//                break;
        }
    }


    //-//</View.OnClickListener>---------------------------------------------------------------------


    //-//<MainActivity.OnViewClickListener>---------------------------------------------------------------------
    @Override
    public void onView1Click() {
        refreshMonthPager();
    }

    @Override
    public void onView2Click() {
        refreshSelectBackground();
    }

    private void refreshMonthPager() {
        CalendarDate today = new CalendarDate();
        calendarAdapter.notifyDataChanged(today);
        if (mDateChangeListener != null) {
            mDateChangeListener.onDateChange(today.getYear(), today.getMonth());
        }
    }

    private void refreshSelectBackground() {
        ThemeDayView themeDayView = new ThemeDayView(context, R.layout.view_calendar_custom_day_focus);
        calendarAdapter.setCustomDayRenderer(themeDayView);
        calendarAdapter.notifyDataSetChanged();
        calendarAdapter.notifyDataChanged(new CalendarDate());
    }
    //-//</MainActivity.OnViewClickListener>---------------------------------------------------------------------


    //-//<OnSelectDateListener>---------------------------------------------------------------------
    @Override
    public void onSelectDate(CalendarDate date) {
        refreshClickDate(date);
    }

    @Override
    public void onSelectOtherMonth(int offset) {
        //偏移量 -1表示刷新成上一个月数据 ， 1表示刷新成下一个月数据
        monthPager.selectOtherMonth(offset);
    }

    private void refreshClickDate(CalendarDate date) {
        currentDate = date;
        if (mDateChangeListener != null) {
            mDateChangeListener.onDateChange(date.getYear(), date.getMonth());
        }
    }
    //-//</OnSelectDateListener>--------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------
    private OnDateChangeListener mDateChangeListener;

    public void setOnDateChangeListener(OnDateChangeListener DateChangeListener) {
        mDateChangeListener = DateChangeListener;
    }

    public interface OnDateChangeListener {
        void onDateChange(int year, int month);
    }
    //</内部类>---尽量少用---------------------------------------------------------------------------

}
