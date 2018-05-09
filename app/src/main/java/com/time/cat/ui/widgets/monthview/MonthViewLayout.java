package com.yumingchuan.rsqmonthcalendar.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yumingchuan.rsqmonthcalendar.R;
import com.yumingchuan.rsqmonthcalendar.adapter.WeekPagerAdapter;
import com.yumingchuan.rsqmonthcalendar.bean.CurrentMonthInfo;
import com.yumingchuan.rsqmonthcalendar.bean.DayInfo;
import com.yumingchuan.rsqmonthcalendar.bean.DayType;
import com.yumingchuan.rsqmonthcalendar.bean.ScheduleListBean;
import com.yumingchuan.rsqmonthcalendar.listener.MyOnPageChangeListener;
import com.yumingchuan.rsqmonthcalendar.utils.LogUtils;
import com.yumingchuan.rsqmonthcalendar.utils.MonthViewCalendarUtil;
import com.yumingchuan.rsqmonthcalendar.utils.TimestampTool;
import com.yumingchuan.rsqmonthcalendar.view.ChildViewPager;
import com.yumingchuan.rsqmonthcalendar.view.MonthViewDay;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yumingchuan on 2017/7/18.
 */

public class MonthViewLayout extends RelativeLayout {

    public Handler mWeekHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1000:
                    setWeekFragmentData(currentDayInfo);
                    break;
            }
        }
    };

    LinearLayout ll_1;
    LinearLayout ll_2;
    LinearLayout ll_3;
    LinearLayout ll_4;
    LinearLayout ll_5;
    LinearLayout ll_6;

    LinearLayout ll_monthCalendarArea;

    private Calendar calendar;
    private View view;
    private List<DayInfo> listDayInfos;
    private CurrentMonthInfo currentMonthInfo;

    public DayInfo currentDayInfo;
    private boolean isNeedReAddFragment;
    ChildViewPager vp_weekSchedule;
    private WeekPagerAdapter pagerAdapter;
    private RelativeLayout titleHeight;


    public MonthViewLayout(Context context) {
        super(context);
    }

    public MonthViewLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public MonthViewLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        view = LayoutInflater.from(getContext()).inflate(R.layout.layout_month_view_fragment, null);
        addView(view);
        initView();
    }

    private void initView() {

        titleHeight = (RelativeLayout) view.findViewById(R.id.titleHeight);

        ll_1 = (LinearLayout) view.findViewById(R.id.ll_1);
        ll_2 = (LinearLayout) view.findViewById(R.id.ll_2);
        ll_3 = (LinearLayout) view.findViewById(R.id.ll_3);
        ll_4 = (LinearLayout) view.findViewById(R.id.ll_4);
        ll_5 = (LinearLayout) view.findViewById(R.id.ll_5);
        ll_6 = (LinearLayout) view.findViewById(R.id.ll_6);

        vp_weekSchedule = (ChildViewPager) view.findViewById(R.id.vp_weekSchedule);

        pagerAdapter = new WeekPagerAdapter();
        vp_weekSchedule.setAdapter(pagerAdapter);
        vp_weekSchedule.setOnPageChangeListener(onPageChangeListener);

        ll_monthCalendarArea = (LinearLayout) view.findViewById(R.id.ll_monthCalendarArea);
        listDayInfos = new ArrayList<>();//一定要new 对象不能直接引用，气死了
    }


    MyOnPageChangeListener onPageChangeListener = new MyOnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (vp_weekSchedule.getVisibility() == View.VISIBLE && currentMonthInfo.currentOpenDayOfMonth != -1 && listDayInfos != null && listDayInfos.size() == 42) {

                int tempPosition;

                if (pagerAdapter.getCount() != 7 && currentMonthInfo.currentOpenWeek < 3) {
                    tempPosition = currentMonthInfo.currentOpenWeek * 7 + 7 - pagerAdapter.getCount() + position;
                } else if (pagerAdapter.getCount() != 7 && currentMonthInfo.currentOpenWeek > 3) {
                    tempPosition = currentMonthInfo.currentOpenWeek * 7 + position;
                } else {
                    tempPosition = currentMonthInfo.currentOpenWeek * 7 + position;
                }

                currentDayInfo = listDayInfos.get(tempPosition);
                if (currentDayInfo.dayType == DayType.DAY_TYPE_NOW) {
                    currentMonthInfo.currentYMD = currentDayInfo.date;
                    renderMonthCalendarBackground();
                    toSomeDateEventBus(TimestampTool.getDateCastDate(currentMonthInfo.currentYMD, TimestampTool.FormatTypeStr.sdf_yMdp, TimestampTool.FormatTypeStr.sdf_all));
                }

                pagerAdapter.refreshItemData(position, listDayInfos.get(currentDayInfo.position).todos);
            }
        }
    };

    /**
     * 初始化当前月份的日历的信息
     */
    public void initCurrentCalendar(int currentPosition) {
        try {
            calendar = Calendar.getInstance();
            calendar.setTime(TimestampTool.sdf_all.parse(TimestampTool.getCurrentDateToWeb()));
            calendar.add(Calendar.MONTH, currentPosition - 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        listDayInfos.clear();
        listDayInfos.addAll(MonthViewCalendarUtil.getInstance(getContext()).getDayInfo(calendar));
    }


    /**
     * 初始化当前月份的信息
     */
    public void initCurrentMonthInfo() {
        currentMonthInfo = new CurrentMonthInfo();
        currentMonthInfo.weeks = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
        currentMonthInfo.currentYMD = TimestampTool.sdf_yMdp.format(calendar.getTime());

        LogUtils.i("monthAreaHeight", ll_monthCalendarArea.getWidth() + "：" + ll_monthCalendarArea.getHeight());

        ll_monthCalendarArea.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                currentMonthInfo.monthAreaWidth = ll_monthCalendarArea.getWidth();
                currentMonthInfo.monthAreaHeight = ll_monthCalendarArea.getHeight();
                currentMonthInfo.weekAreaHeight = (int) currentMonthInfo.monthAreaHeight / currentMonthInfo.weeks;

                currentMonthInfo.initY = (int) (currentMonthInfo.monthAreaHeight - currentMonthInfo.weekAreaHeight * currentMonthInfo.weeks);

                //重新调整高度(误差在6dp的范围内)
                setLayoutWidthHeight(titleHeight, currentMonthInfo.monthAreaWidth, currentMonthInfo.initY);
                setLayoutWidthHeight(ll_monthCalendarArea, currentMonthInfo.monthAreaWidth, currentMonthInfo.weekAreaHeight * currentMonthInfo.weeks);

                setLayoutWidthHeight(ll_1, currentMonthInfo.monthAreaWidth, currentMonthInfo.weekAreaHeight);
                setLayoutWidthHeight(ll_2, currentMonthInfo.monthAreaWidth, currentMonthInfo.weekAreaHeight);
                setLayoutWidthHeight(ll_3, currentMonthInfo.monthAreaWidth, currentMonthInfo.weekAreaHeight);
                setLayoutWidthHeight(ll_4, currentMonthInfo.monthAreaWidth, currentMonthInfo.weekAreaHeight);
                setLayoutWidthHeight(ll_5, currentMonthInfo.monthAreaWidth, currentMonthInfo.weekAreaHeight);
                setLayoutWidthHeight(ll_6, currentMonthInfo.monthAreaWidth, currentMonthInfo.weekAreaHeight);

                setLayoutWidthHeight(vp_weekSchedule, currentMonthInfo.monthAreaWidth, currentMonthInfo.weeks == 5 ? (currentMonthInfo.weekAreaHeight * 3) : (currentMonthInfo.weekAreaHeight * 4));

                //还原到初始化的布局
                translateView(ll_1, ll_1.getY(), (currentMonthInfo.weekAreaHeight * 0), 20);
                translateView(ll_2, ll_2.getY(), (currentMonthInfo.weekAreaHeight * 1), 20);
                translateView(ll_3, ll_3.getY(), (currentMonthInfo.weekAreaHeight * 2), 20);
                translateView(ll_4, ll_4.getY(), (currentMonthInfo.weekAreaHeight * 3), 20);
                translateView(ll_5, ll_5.getY(), (currentMonthInfo.weekAreaHeight * 4), 20);
                translateView(ll_6, ll_6.getY(), (currentMonthInfo.weekAreaHeight * 5), 20);

                // 成功调用一次后，移除 Hook 方法，防止被反复调用
                // removeGlobalOnLayoutListener() 方法在 API 16 后不再使用
                // 使用新方法 removeOnGlobalLayoutListener() 代替

                LogUtils.i("monthAreaHeight", ll_1.getY() + ":" + ll_monthCalendarArea.getY() + ":" + currentMonthInfo.initY + "：" + currentMonthInfo.monthAreaHeight + ":" + currentMonthInfo.weekAreaHeight * currentMonthInfo.weeks);

                //这个移除监听已经过时，替代的是最低版本16的removeOnGlobalLayoutListener
                ll_monthCalendarArea.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        ll_6.setVisibility(currentMonthInfo.weeks == 5 ? View.GONE : View.VISIBLE);
    }


    /**
     * @param addContent 是否添加内容
     */
    public void renderMonthCalendarData(boolean addContent) {
        for (int i = 0; i < 42; i++) {
            final ViewGroup view = (ViewGroup) ll_monthCalendarArea.getChildAt(i / 7);
            final MonthViewDay dayOfWeek = (MonthViewDay) view.getChildAt(i % 7);
            dayOfWeek.setMonthDayData(listDayInfos.get(i));
            if (addContent) {
                dayOfWeek.invalidate();
            }
            if (listDayInfos.get(i).dayType == DayType.DAY_TYPE_NOW) {
                dayOfWeek.setTag(i);
                dayOfWeek.setOnClickListener(onClickListener);
            } else {
                dayOfWeek.setTag(i);
                dayOfWeek.setOnClickListener(null);
            }
        }
    }

    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            clickSomeDate(v);
        }
    };

    private void toSomeDateEventBus(String date) {
       // EventBus.getDefault().post(new EventMessage(EventMessage.Schedule.SELECTED_SOME_DATE).setObj(date));
    }

    /**
     * 点击某一天
     *
     * @param v
     */
    public void clickSomeDate(View v) {

        if (listDayInfos == null) {
            return;
        }

        currentDayInfo = listDayInfos.get((Integer) v.getTag());

        if (listDayInfos.get(currentDayInfo.position).dayType == DayType.DAY_TYPE_NOW) {
            currentMonthInfo.currentYMD = currentDayInfo.date;

            toSomeDateEventBus(TimestampTool.getDateCastDate(currentMonthInfo.currentYMD, TimestampTool.FormatTypeStr.sdf_yMdp, TimestampTool.FormatTypeStr.sdf_all));

            renderMonthCalendarBackground();
        }

        if (listDayInfos.get(currentDayInfo.position).dayType == DayType.DAY_TYPE_NOW) {

            isNeedReAddFragment = (-1 == currentMonthInfo.currentOpenDayOfMonth) || (currentMonthInfo.currentOpenDayOfMonth / 7 != currentDayInfo.position / 7);

            boolean isNeedChange = (currentDayInfo.position / 7 != currentMonthInfo.currentOpenWeek) ||
                    ((currentDayInfo.position / 7 == currentMonthInfo.currentOpenWeek) && currentMonthInfo.currentOpenDayOfWeek == (listDayInfos.get(currentDayInfo.position).whichWeek == 1 ? (currentDayInfo.position) % 7 - (7 - currentDayInfo.daysOfWeek) : currentDayInfo.position % 7));

            if (!isNeedReAddFragment && isCanTranslate(160) && !isNeedChange) {
                startClickTime = System.currentTimeMillis();
                if (listDayInfos.get(currentDayInfo.position).dayType == DayType.DAY_TYPE_NOW) {
                    currentMonthInfo.currentOpenDayOfMonth = currentDayInfo.position;
                    mWeekHandler.sendEmptyMessage(1000);
                }
            } else {
                if (isCanTranslate(400)) {
                    startClickTime = System.currentTimeMillis();

                    if (isNeedChange) {
                        switch (currentDayInfo.position / 7) {
                            case 0:
                                objectAnimatorList.clear();
                                dealWeek(0, 0, 4, 5, 6, 7, 0, 5, 6, 7, 8, 9);
                                break;
                            case 1:
                                objectAnimatorList.clear();
                                dealWeek(1, -1, 0, 4, 5, 6, -1, 0, 5, 6, 7, 8);
                                break;
                            case 2:
                                objectAnimatorList.clear();
                                dealWeek(2, -2, -1, 0, 4, 5, -2, -1, 0, 5, 6, 7);
                                break;
                            case 3:
                                objectAnimatorList.clear();
                                dealWeek(3, -3, -2, -1, 0, 4, -3, -2, -1, 0, 5, 6);
                                break;
                            case 4:
                                objectAnimatorList.clear();
                                dealWeek(4, -3, -2, -1, 0, 1, -4, -3, -2, -1, 0, 5);
                                break;
                            case 5:
                                objectAnimatorList.clear();
                                dealWeek(5, -3, -3, -3, -3, -3, -4, -3, -2, -1, 0, 1);
                                break;
                            default:
                                startClickTime = System.currentTimeMillis();
                                break;
                        }

                    }
                    currentMonthInfo.currentOpenDayOfMonth = currentDayInfo.position;
                }
            }
        }
    }


    /**
     * 渲染月视图的背景色
     */
    public void renderMonthCalendarBackground() {

        if (ll_monthCalendarArea == null) {
            return;
        }

        for (int i = 0; i < 42; i++) {
            ViewGroup view = (ViewGroup) ll_monthCalendarArea.getChildAt(i / 7);
            MonthViewDay dayOfWeek = (MonthViewDay) view.getChildAt(i % 7);
            dayOfWeek.invalidate();
        }
    }


    List<ObjectAnimator> objectAnimatorList = new ArrayList<ObjectAnimator>();
    private long startClickTime = 0;

    /**
     * 判断是否可以移动
     *
     * @return
     */
    private boolean isCanTranslate(int time) {
        for (int i = 0; i < objectAnimatorList.size(); i++) {
            if (objectAnimatorList.get(i).isRunning() || System.currentTimeMillis() - startClickTime < time) {
                return false;
            }
        }
        return true;
    }

    /**
     * 具体的移动方案
     *
     * @param currentOpenWeek
     * @param transDistance51
     * @param transDistance52
     * @param transDistance53
     * @param transDistance54
     * @param transDistance55
     * @param transDistance61
     * @param transDistance62
     * @param transDistance63
     * @param transDistance64
     * @param transDistance65
     * @param transDistance66
     */
    private void dealWeek(final int currentOpenWeek, final int transDistance51, final int transDistance52, final int transDistance53
            , final int transDistance54, final int transDistance55, final int transDistance61, final int transDistance62, final int transDistance63
            , final int transDistance64, final int transDistance65, final int transDistance66) {

        if (currentMonthInfo.currentOpenWeek == -1 && !currentMonthInfo.isExpand) {
            //关闭到打开

            openWeek(currentMonthInfo.weeks == 5, currentOpenWeek, transDistance51, transDistance52, transDistance53, transDistance54, transDistance55,
                    transDistance61, transDistance62, transDistance63, transDistance64, transDistance65, transDistance66);


            currentMonthInfo.isExpand = true;
            currentMonthInfo.currentOpenWeek = currentOpenWeek;

            startClickTime = System.currentTimeMillis();

            postClickEvent();

        } else {
            if (currentMonthInfo.currentOpenWeek == currentOpenWeek) {
                //打开到关闭
                closeWeek(currentMonthInfo.weeks == 5);

                currentMonthInfo.isExpand = false;
                currentMonthInfo.currentOpenWeek = -1;

                startClickTime = System.currentTimeMillis();

                postClickEvent();

            } else {
                //先关闭
                closeWeek(currentMonthInfo.weeks == 5);

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onlyOpenWeek(currentOpenWeek);
                        currentMonthInfo.isExpand = true;
                        currentMonthInfo.currentOpenWeek = currentOpenWeek;
                        startClickTime = System.currentTimeMillis();

                        postClickEvent();

                    }
                }, 230);

            }
        }
    }

    private void postClickEvent() {
        postDelayed(() -> mWeekHandler.sendEmptyMessage(1000), 230);
    }


    /**
     * remove_memb_icon
     *
     * @param is5Week
     */
    private void closeWeek(boolean is5Week) {

        if (is5Week) {
            translateView(ll_1, ll_1.getY(), currentMonthInfo.weekAreaHeight * 0, transAnim);
            translateView(ll_2, ll_2.getY(), currentMonthInfo.weekAreaHeight * 1, transAnim);
            translateView(ll_3, ll_3.getY(), currentMonthInfo.weekAreaHeight * 2, transAnim);
            translateView(ll_4, ll_4.getY(), currentMonthInfo.weekAreaHeight * 3, transAnim);
            translateView(ll_5, ll_5.getY(), currentMonthInfo.weekAreaHeight * 4, transAnim);

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    vp_weekSchedule.setVisibility(View.GONE);
                }
            }, transAnim);

        } else {
            translateView(ll_1, ll_1.getY(), currentMonthInfo.weekAreaHeight * 0, transAnim);
            translateView(ll_2, ll_2.getY(), currentMonthInfo.weekAreaHeight * 1, transAnim);
            translateView(ll_3, ll_3.getY(), currentMonthInfo.weekAreaHeight * 2, transAnim);
            translateView(ll_4, ll_4.getY(), currentMonthInfo.weekAreaHeight * 3, transAnim);
            translateView(ll_5, ll_5.getY(), currentMonthInfo.weekAreaHeight * 4, transAnim);
            translateView(ll_6, ll_6.getY(), currentMonthInfo.weekAreaHeight * 5, transAnim);

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    vp_weekSchedule.setVisibility(View.GONE);
                }
            }, transAnim);

        }


    }


    /**
     * 只是简单的打开
     *
     * @param week
     */
    public void onlyOpenWeek(int week) {
        switch (week) {
            case 0:
                openWeek(currentMonthInfo.weeks == 5, week, 0, 4, 5, 6, 7, 0, 5, 6, 7, 8, 9);
                break;
            case 1:
                openWeek(currentMonthInfo.weeks == 5, week, -1, 0, 4, 5, 6, -1, 0, 5, 6, 7, 8);
                break;
            case 2:
                openWeek(currentMonthInfo.weeks == 5, week, -2, -1, 0, 4, 5, -2, -1, 0, 5, 6, 7);
                break;
            case 3:
                openWeek(currentMonthInfo.weeks == 5, week, -3, -2, -1, 0, 4, -3, -2, -1, 0, 5, 6);
                break;
            case 4:
                openWeek(currentMonthInfo.weeks == 5, week, -3, -2, -1, 0, 1, -4, -3, -2, -1, 0, 5);
                break;
            case 5:
                openWeek(currentMonthInfo.weeks == 5, week, -3, -3, -3, -3, -3, -4, -3, -2, -1, 0, 1);
                break;
        }
    }


    private int transAnim = 200;

    /**
     * 打开和关闭
     *
     * @param is5Week
     * @param currentOpenWeek
     * @param transDistance51
     * @param transDistance52
     * @param transDistance53
     * @param transDistance54
     * @param transDistance55
     * @param transDistance61
     * @param transDistance62
     * @param transDistance63
     * @param transDistance64
     * @param transDistance65
     * @param transDistance66
     */
    private void openWeek(boolean is5Week, int currentOpenWeek, int transDistance51, int transDistance52, int transDistance53
            , int transDistance54, int transDistance55, int transDistance61, int transDistance62, int transDistance63
            , int transDistance64, int transDistance65, int transDistance66) {

        if (is5Week) {
            translateView(ll_1, ll_1.getY(), (currentMonthInfo.weekAreaHeight * transDistance51), transAnim);
            translateView(ll_2, ll_2.getY(), (currentMonthInfo.weekAreaHeight * transDistance52), transAnim);
            translateView(ll_3, ll_3.getY(), (currentMonthInfo.weekAreaHeight * transDistance53), transAnim);
            translateView(ll_4, ll_4.getY(), (currentMonthInfo.weekAreaHeight * transDistance54), transAnim);
            translateView(ll_5, ll_5.getY(), (currentMonthInfo.weekAreaHeight * transDistance55), transAnim);

            vp_weekSchedule.setVisibility(View.VISIBLE);
            translateView(vp_weekSchedule, (int) vp_weekSchedule.getY(), getEditAreaTranslateHeight(currentOpenWeek), 10);
        } else {
            translateView(ll_1, ll_1.getY(), (currentMonthInfo.weekAreaHeight * transDistance61), transAnim);
            translateView(ll_2, ll_2.getY(), (currentMonthInfo.weekAreaHeight * transDistance62), transAnim);
            translateView(ll_3, ll_3.getY(), (currentMonthInfo.weekAreaHeight * transDistance63), transAnim);
            translateView(ll_4, ll_4.getY(), (currentMonthInfo.weekAreaHeight * transDistance64), transAnim);
            translateView(ll_5, ll_5.getY(), (currentMonthInfo.weekAreaHeight * transDistance65), transAnim);
            translateView(ll_6, ll_6.getY(), (currentMonthInfo.weekAreaHeight * transDistance66), transAnim);

            vp_weekSchedule.setVisibility(View.VISIBLE);
            translateView(vp_weekSchedule, (int) vp_weekSchedule.getY(), getEditAreaTranslateHeight(currentOpenWeek), 10);
        }

    }


    /**
     * 平移属性动画
     *
     * @param view
     * @param from
     * @param to
     */
    public void translateView(View view, float from, float to, int animTime) {
        ObjectAnimator translationUp = ObjectAnimator.ofFloat(view, "Y", from, to);
        translationUp.setInterpolator(new DecelerateInterpolator());
        translationUp.setDuration(animTime);
        translationUp.start();
        objectAnimatorList.add(translationUp);
    }


    private float getEditAreaTranslateHeight(int week) {
        return currentMonthInfo.weeks == 5 ? ((week + 1) == 5 ? (currentMonthInfo.weekAreaHeight * 2) : (currentMonthInfo.weekAreaHeight)) : ((week + 1) == 6 ? (currentMonthInfo.weekAreaHeight * 2) : (currentMonthInfo.weekAreaHeight));
    }

    /**
     * 动态设置布局的高和宽
     *
     * @param viewGroup
     * @param screenWidth
     * @param screenHeight
     */
    private void setLayoutWidthHeight(ViewGroup viewGroup, float screenWidth, float screenHeight) {
        ViewGroup.LayoutParams layoutParams = viewGroup.getLayoutParams(); //取控件textView当前的布局参数
        layoutParams.width = (int) screenWidth;
        layoutParams.height = (int) screenHeight;
        viewGroup.setLayoutParams(layoutParams);
    }

    private void setWeekFragmentData(final DayInfo dayInfo) {

        if (isNeedReAddFragment && currentMonthInfo.isExpand) {
            if (listDayInfos.get(dayInfo.position).daysOfWeek == 7) {
                pagerAdapter.reloadData(WeekItemLayoutUtils.news().getViews(getContext()));
            } else {
                List<View> list = new ArrayList<>();
                List<View> tempList = WeekItemLayoutUtils.news().getViews(getContext());
                for (int i = 0; i < listDayInfos.get(dayInfo.position).daysOfWeek; i++) {
                    list.add(tempList.get(i));
                }
                pagerAdapter.reloadData(list);
            }
            pagerAdapter.refreshItemData(vp_weekSchedule.getCurrentItem(), listDayInfos.get(currentDayInfo.position).todos);
        }

        if (listDayInfos.get(dayInfo.position).daysOfWeek == 7) {
            currentMonthInfo.currentOpenDayOfWeek = (dayInfo.position) % 7;
            vp_weekSchedule.setCurrentItem((dayInfo.position) % listDayInfos.get(dayInfo.position).daysOfWeek);
        } else {
            if (listDayInfos.get(dayInfo.position).whichWeek == 1) {
                currentMonthInfo.currentOpenDayOfWeek = (dayInfo.position) % 7 - (7 - dayInfo.daysOfWeek);
                vp_weekSchedule.setCurrentItem((dayInfo.position) % 7 - (7 - dayInfo.daysOfWeek));
            } else {
                currentMonthInfo.currentOpenDayOfWeek = (dayInfo.position) % 7;
                vp_weekSchedule.setCurrentItem((dayInfo.position) % 7);
            }
        }
    }

    private int getStartPosition(List<ScheduleListBean> scheduleListBeen) {
        boolean stop = false;
        int startPosition = -1;
        for (int i = 0; i < listDayInfos.size(); i++) {
            boolean isStart = TimestampTool.getDateCastDate(listDayInfos.get(i).date, TimestampTool.FormatTypeStr.sdf_yMdp, TimestampTool.FormatTypeStr.sdf_yMd).equals(scheduleListBeen.get(0).getDate());
            if (isStart && !stop) {
                startPosition = i;
                stop = !stop;
            }
        }
        return startPosition;
    }

    public void setMonthViewData(List<ScheduleListBean> scheduleListBeen) {
        int startPosition = getStartPosition(scheduleListBeen);
        if (startPosition != -1) {
            for (int i = 0; i < listDayInfos.size(); i++) {
                if (i >= startPosition && i < startPosition + scheduleListBeen.size()) {
                    listDayInfos.get(i).todos.clear();
                    listDayInfos.get(i).todos.addAll(scheduleListBeen.get(i - startPosition).getData());
                } else {
                    listDayInfos.get(i).todos.clear();
                }
                renderMonthCalendarData(true);
            }
        }
    }


    public CurrentMonthInfo getCurrentMonthInfo() {
        return currentMonthInfo;
    }

    private MonthViewDay getCurrentClickView(int position) {
        return (MonthViewDay) ((ViewGroup) ll_monthCalendarArea.getChildAt(position / 7)).getChildAt(position % 7);
    }

    public void closeSomeDate(boolean isVisibleToUser) {
        if (!isVisibleToUser && vp_weekSchedule != null && vp_weekSchedule.getVisibility() == View.VISIBLE
                && currentMonthInfo.isExpand && currentMonthInfo.currentOpenDayOfMonth != -1) {
            clickSomeDate(getCurrentClickView(currentMonthInfo.currentOpenDayOfMonth));
        }
    }


}
