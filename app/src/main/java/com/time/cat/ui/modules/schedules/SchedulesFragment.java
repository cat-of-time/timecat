package com.time.cat.ui.modules.schedules;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.api.ScrollBoundaryDecider;
import com.time.cat.R;
import com.time.cat.ui.base.BaseFragment;
import com.time.cat.ui.base.mvp.presenter.FragmentPresenter;
import com.time.cat.ui.modules.main.listener.OnDateChangeListener;
import com.time.cat.ui.modules.main.listener.OnScheduleViewClickListener;
import com.time.cat.ui.modules.schedules.calendar_view.ScheduleCalendarFragment;
import com.time.cat.ui.modules.schedules.list_view.ScheduleListFragment;
import com.time.cat.util.override.LogUtil;
import com.timecat.commonjar.contentProvider.SPHelper;

import java.util.ArrayList;
import java.util.List;

import static com.time.cat.data.Constants.SCHEDULES_VIEW_TYPE;

/**
 * @author dlink
 * @date 2018/1/24
 * @discription SchedulesFragment
 */
@SuppressLint("SetTextI18n")
public class SchedulesFragment extends BaseFragment implements
                                                    FragmentPresenter,
                                                    OnScheduleViewClickListener,
                                                    CalendarView.OnDateSelectedListener,
                                                    CalendarView.OnYearChangeListener {

    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentConfig(false, true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.base_refresh_layout;
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedules, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        frameLayout = view.findViewById(R.id.fragment_container);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        initView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
    //</生命周期>------------------------------------------------------------------------------------


    //<editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码">
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    private List<Fragment> fragmentList;
    RefreshLayout mRefreshLayout;
    ScheduleCalendarFragment scheduleCalendarFragment;
    ScheduleListFragment scheduleListFragment;


    @Override
    public void initView() {//必须调用
        super.initView();
        fragmentList = new ArrayList<>();

        mRefreshLayout.setScrollBoundaryDecider(new ScrollBoundaryDecider() {
            @Override
            public boolean canRefresh(View content) {
                return onScrollBoundaryDecider != null && onScrollBoundaryDecider.canRefresh();
            }

            @Override
            public boolean canLoadMore(View content) {
                return onScrollBoundaryDecider != null && onScrollBoundaryDecider.canLoadMore();
            }
        });
    }

    private void updateViewPager() {
        if (fragmentList == null) return;
        for (Fragment f : fragmentList) {
            getChildFragmentManager().beginTransaction().remove(f).commitNow();
        }
        fragmentList.clear();
        switch(SPHelper.getInt(SCHEDULES_VIEW_TYPE, 1)) {
            case 0:
                scheduleListFragment = new ScheduleListFragment();
                fragmentList.add(scheduleListFragment);
                setOnScrollBoundaryDecider(scheduleListFragment);
                scheduleListFragment.setUserVisibleHint(true);
                getChildFragmentManager().beginTransaction().add(R.id.fragment_container, scheduleListFragment).commitNow();
                break;
            case 1:
                scheduleCalendarFragment = new ScheduleCalendarFragment();
                if (mDateChangeListener != null) {
                    scheduleCalendarFragment.setOnDateChangeListener(mDateChangeListener);
                }
                fragmentList.add(scheduleCalendarFragment);
                setOnScrollBoundaryDecider(scheduleCalendarFragment);
                scheduleCalendarFragment.setUserVisibleHint(true);
                getChildFragmentManager().beginTransaction().add(R.id.fragment_container, scheduleCalendarFragment).commitNow();
                break;
            case 2:
                break;
        }
    }
    //</editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码">)>




    //<editor-fold desc="Data数据区--存在数据获取或处理代码，但不存在事件监听代码">
    @Override
    public void initData() {//必须调用
        new Handler().postDelayed(() -> {
            if (!isPrepared()) {
                LogUtil.w("initData", "目标已被回收");
                return;
            }
            updateViewPager();
            frameLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }, 500);
    }

    public void refreshData() {

        if (frameLayout != null) {
            frameLayout.setVisibility(View.GONE);
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        if (isFragmentVisible()) {
            initData();
        } else {
            setForceLoad(true);
        }
    }
    //</editor-fold desc="Data数据区--存在数据获取或处理代码，但不存在事件监听代码">

    //<editor-fold desc="Event事件区--只要存在事件监听代码就是">
    @Override
    public void initEvent() {}

    @Override
    public void notifyDataChanged() {
        refreshData();
    }

    //-//<MainActivity.OnScheduleViewClickListener>-------------------------------------------------
    @Override
    public void onViewTodayClick() {
        if (scheduleCalendarFragment != null) {
            scheduleCalendarFragment.onViewTodayClick();
        }
    }

    @Override
    public void onViewRefreshClick() {
        refreshData();
    }

    @Override
    public void onViewExpand() {
        if (scheduleCalendarFragment != null) {
            scheduleCalendarFragment.onViewExpand();
        }
    }

    //-//</MainActivity.OnScheduleViewClickListener>------------------------------------------------

    //-//<CalendarView>---------------------------------------------------------------------
    @Override
    public void onDateSelected(Calendar calendar, boolean isClick) {
        if (scheduleCalendarFragment != null) {
            scheduleCalendarFragment.onDateSelected(calendar, isClick);
        }
    }

    @Override
    public void onYearChange(int year) {
        if (scheduleCalendarFragment != null) {
            scheduleCalendarFragment.onYearChange(year);
        }
    }
    //-//</CalendarView>--------------------------------------------------------------------

    //</editor-fold desc="Event事件区--只要存在事件监听代码就是">



    //<回调接口>-------------------------------------------------------------------------------------
    private OnScrollBoundaryDecider onScrollBoundaryDecider;

    public void setOnScrollBoundaryDecider(OnScrollBoundaryDecider onScrollBoundaryDecider) {
        this.onScrollBoundaryDecider = onScrollBoundaryDecider;
    }


    public interface OnScrollBoundaryDecider {
        boolean canRefresh();
        boolean canLoadMore();
    }

    private OnDateChangeListener mDateChangeListener;

    public void setOnDateChangeListener(OnDateChangeListener DateChangeListener) {
        mDateChangeListener = DateChangeListener;
    }
    //</回调接口>------------------------------------------------------------------------------------

}
