//package com.time.cat.ui.modules.plans;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.RectF;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.haibin.calendarview.Calendar;
//import com.haibin.calendarview.CalendarLayout;
//import com.haibin.calendarview.CalendarView;
//import com.time.cat.R;
//import com.time.cat.data.database.DB;
//import com.time.cat.data.model.APImodel.User;
//import com.time.cat.data.model.DBmodel.DBTask;
//import com.time.cat.data.model.DBmodel.DBUser;
//import com.time.cat.data.network.RetrofitHelper;
//import com.time.cat.ui.activity.main.MainActivity;
//import com.time.cat.ui.base.BaseFragment;
//import com.time.cat.ui.base.mvp.presenter.FragmentPresenter;
//import com.time.cat.ui.widgets.asyncExpandableListView.AsyncExpandableListView;
//import com.time.cat.ui.widgets.asyncExpandableListView.AsyncExpandableListViewCallbacks;
//import com.time.cat.ui.widgets.asyncExpandableListView.CollectionView;
//import com.time.cat.util.override.LogUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//import rx.Subscriber;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.functions.Action1;
//import rx.schedulers.Schedulers;
//
///**
// * @author dlink
// * @date 2018/1/25
// * @discription 生物钟fragment
// */
//public class SFragment extends BaseFragment implements FragmentPresenter,
//                                                       View.OnClickListener,
//                                                       WeekDayView.MonthChangeListener,
//                                                       WeekDayView.EventClickListener,
//                                                       WeekDayView.EventLongPressListener,
//                                                       WeekDayView.EmptyViewClickListener,
//                                                       WeekDayView.EmptyViewLongPressListener,
//                                                       WeekDayView.ScrollListener{
//    //view
//    private WeekDayView mWeekView;
//    private WeekHeaderView mWeekHeaderView;
//    private TextView mTv_date;
//
//    List<WeekViewEvent> mNewEvent = new ArrayList<WeekViewEvent>();
//    private DBUser dbUser;
//
//    //<生命周期>-------------------------------------------------------------------------------------
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        View v = inflater.inflate(R.layout.fragment_temp, container, false);
//        assignViews();
//
//        //<功能归类分区方法，必须调用>-----------------------------------------------------------------
//        initData();
//        initView();
//        initEvent();
//        //</功能归类分区方法，必须调用>----------------------------------------------------------------
//
//        return v;
//    }
//    //</生命周期>------------------------------------------------------------------------------------
//
//
//    //<editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码">--------------------------------
//    TextView mTextMonthDay;
//
//    TextView mTextYear;
//
//    TextView mTextLunar;
//
//    TextView mTextCurrentDay;
//
//    CalendarLayout mCalendarLayout;
//    CalendarView mCalendarView;
//    private int mYear;
//    RelativeLayout mRelativeTool;
//    private AsyncExpandableListView<DBTask, DBTask> mAsyncExpandableListView;
//
//    @Override
//    public void initView() {//必须调用
//        mCalendarView.setOnDateSelectedListener(this);
//        mCalendarView.setOnYearChangeListener(this);
//        mYear = mCalendarView.getCurYear();
//        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
//        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
//        mTextLunar.setText("今日");
//        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
//    }
//    //</editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码">)>-----------------------------
//
//
//    //<editor-fold desc="Data数据区--存在数据获取或处理代码，但不存在事件监听代码">-----------------------------------------
//    @Override
//    public void initData() {//必须调用
//        List<Calendar> schemes = new ArrayList<>();
//        int year = mCalendarView.getCurYear();
//        int month = mCalendarView.getCurMonth();
//
//        schemes.add(getSchemeCalendar(year, month, 3, 0xFF40db25, "假"));
//        schemes.add(getSchemeCalendar(year, month, 6, 0xFFe69138, "事"));
//        schemes.add(getSchemeCalendar(year, month, 10, 0xFFdf1356, "议"));
//        schemes.add(getSchemeCalendar(year, month, 11, 0xFFedc56d, "记"));
//        schemes.add(getSchemeCalendar(year, month, 14, 0xFFedc56d, "记"));
//        schemes.add(getSchemeCalendar(year, month, 15, 0xFFaacc44, "假"));
//        schemes.add(getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记"));
//        schemes.add(getSchemeCalendar(year, month, 25, 0xFF13acf0, "假"));
//        schemes.add(getSchemeCalendar(year, month, 27, 0xFF13acf0, "多"));
//        mCalendarView.setSchemeDate(schemes);
//
//        dbUser = DB.users().getActive();
//        initExpandableListViewData();
//    }
//
//    private void initExpandableListViewData() {
//
//        inventory = new CollectionView.Inventory<>();
//        RetrofitHelper.getUserService().getUserByEmail(dbUser.getEmail()) //获取Observable对象
//                .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
//                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
//                .doOnNext(new Action1<User>() {
//                    @Override
//                    public void call(User user) {
//                        //保存用户信息到本地
//                        DB.users().updateActiveUserAndFireEvent(dbUser, user);
////                        Log.i(TAG, dbUser.toString());
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
//                .subscribe(new Subscriber<User>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        //请求失败
////                        LogUtil.e(e.toString());
////                        ToastUtil.show("更新用户信息失败");
//                    }
//
//                    @Override
//                    public void onNext(User user) {
//                        //请求成功
////                        Log.i(TAG, "更新用户信息成功 --> " + user.toString());
////                        ToastUtil.show("更新用户信息成功");
//                    }
//                });
//
//        ArrayList<String> task_urls = dbUser.getTasks();
//        if (task_urls != null && task_urls.size() > 0) {
//            AsyncTask<ArrayList<String>, Void, ArrayList<DBTask>> loadDataForHeader = new LoadDataTaskHeader(inventory);
//            loadDataForHeader.execute(task_urls);
//        } else {
//            AsyncTask<ArrayList<String>, Void, ArrayList<DBTask>> loadDataForHeader = new LoadDataTaskHeader(inventory);
//            loadDataForHeader.execute(new ArrayList<String>());
//            LogUtil.e("null task list");
//        }
//        LogUtil.e("initExpandableListViewData --> dbUser.getTasks() --> " + task_urls);
//    }
//
//    //</editor-fold desc="Data数据区--存在数据获取或处理代码，但不存在事件监听代码">----------------------------------------
//
//
//    //<editor-fold desc="Event事件区--只要存在事件监听代码就是">----------------------------------------------------------
//    @Override
//    public void initEvent() {//必须调用
//        mAsyncExpandableListView.setCallbacks(this);
//    }
//
//
//    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
//        Calendar calendar = new Calendar();
//        calendar.setYear(year);
//        calendar.setMonth(month);
//        calendar.setDay(day);
//        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
//        calendar.setScheme(text);
//        calendar.addScheme(new Calendar.Scheme());
//        calendar.addScheme(0xFF008800, "假");
//        calendar.addScheme(0xFF008800, "节");
//        return calendar;
//    }
//
//
//    @SuppressLint("SetTextI18n")
//    @Override
//    public void onDateSelected(Calendar calendar, boolean isClick) {
//        mTextLunar.setVisibility(View.VISIBLE);
//        mTextYear.setVisibility(View.VISIBLE);
//        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
//        mTextYear.setText(String.valueOf(calendar.getYear()));
//        mTextLunar.setText(calendar.getLunar());
//        mYear = calendar.getYear();
//    }
//
//
//    @Override
//    public void onYearChange(int year) {
//        mTextMonthDay.setText(String.valueOf(year));
//    }
//
//    @Override
//    public void onClick(View v) {
//
//    }
//
//
//
//    private void assignViews() {
//        mWeekView = (WeekDayView) findViewById(R.id.weekdayview);
//        mWeekHeaderView= (WeekHeaderView) findViewById(R.id.weekheaderview);
//        mTv_date =(TextView)findViewById(R.id.tv_date);
//        //init WeekView
//        mWeekView.setMonthChangeListener(this);
//        mWeekView.setEventLongPressListener(this);
//        mWeekView.setOnEventClickListener(this);
//        mWeekView.setScrollListener(this);
//        mWeekHeaderView.setDateSelectedChangeListener(new WeekHeaderView.DateSelectedChangeListener() {
//            @Override
//            public void onDateSelectedChange(Calendar oldSelectedDay, Calendar newSelectedDay) {
//                mWeekView.goToDate(newSelectedDay);
//            }
//        });
//        mWeekHeaderView.setScrollListener(new WeekHeaderView.ScrollListener() {
//            @Override
//            public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {
//                mWeekView.goToDate(mWeekHeaderView.getSelectedDay());
//            }
//        });
//        setupDateTimeInterpreter(false);
//
//    }
//
//
//    /**
//     * Set up a date time interpreter which will show short date values when in week view and long
//     * date values otherwise.
//     *
//     * @param shortDate True if the date values should be short.
//     */
//    private void setupDateTimeInterpreter(final boolean shortDate) {
//        final String[] weekLabels={"日","一","二","三","四","五","六"};
//        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
//            @Override
//            public String interpretDate(Calendar date) {
//                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
//                String weekday = weekdayNameFormat.format(date.getTime());
//                SimpleDateFormat format = new SimpleDateFormat("d", Locale.getDefault());
//                return format.format(date.getTime());
//            }
//
//            @Override
//            public String interpretTime(int hour) {
//                return String.format("%02d:00", hour);
//
//            }
//
//            @Override
//            public String interpretWeek(int date) {
//                if(date>7||date<1){
//                    return null;
//                }
//                return weekLabels[date-1];
//            }
//        });
//    }
//
//    @Override
//    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
//
//        // Populate the week view with some events.
//        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
//
//        Calendar startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth - 1);
//        startTime.set(Calendar.YEAR, newYear);
//        Calendar endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR, 1);
//        endTime.set(Calendar.MONTH, newMonth - 1);
//        WeekViewEvent event = new WeekViewEvent(1, "This is a Event!!", startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_01));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 30);
//        startTime.set(Calendar.MONTH, newMonth - 1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.set(Calendar.HOUR_OF_DAY, 4);
//        endTime.set(Calendar.MINUTE, 30);
//        endTime.set(Calendar.MONTH, newMonth - 1);
//        event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_02));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 4);
//        startTime.set(Calendar.MINUTE, 20);
//        startTime.set(Calendar.MONTH, newMonth - 1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.set(Calendar.HOUR_OF_DAY, 5);
//        endTime.set(Calendar.MINUTE, 0);
//        event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_03));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 5);
//        startTime.set(Calendar.MINUTE, 30);
//        startTime.set(Calendar.MONTH, newMonth - 1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 2);
//        endTime.set(Calendar.MONTH, newMonth - 1);
//        event = new WeekViewEvent(2, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_02));
//        events.add(event);
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 5);
//        startTime.set(Calendar.MINUTE, 30);
//        startTime.set(Calendar.MONTH, newMonth - 1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 2);
//        endTime.set(Calendar.MONTH, newMonth - 1);
//        event = new WeekViewEvent(2, "dddd", startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_01));
//        events.add(event);
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 5);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth - 1);
//        startTime.set(Calendar.YEAR, newYear);
//        startTime.add(Calendar.DATE, 1);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        endTime.set(Calendar.MONTH, newMonth - 1);
//        event = new WeekViewEvent(3, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_03));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 15);
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth - 1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(4, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_04));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 1);
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth - 1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_01));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, startTime.getActualMaximum(Calendar.DAY_OF_MONTH));
//        startTime.set(Calendar.HOUR_OF_DAY, 15);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth - 1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_02));
//        events.add(event);
//        events.addAll(mNewEvent);
//        return events;
//    }
//
//    private String getEventTitle(Calendar time) {
//        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
//    }
//
//    @Override
//    public void onEventClick(WeekViewEvent event, RectF eventRect) {
//        Toast.makeText(MainActivity.this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
//        Toast.makeText(MainActivity.this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
//    }
//
//
//    @Override
//    public void onEmptyViewClicked(Calendar time) {
//        Toast.makeText(MainActivity.this, "Empty View clicked " + time.get(Calendar.YEAR) + "/" + time.get(Calendar.MONTH) + "/" + time.get(Calendar.DAY_OF_MONTH), Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onEmptyViewLongPress(Calendar time) {
//        Toast.makeText(MainActivity.this, "Empty View long  clicked " + time.get(Calendar.YEAR) + "/" + time.get(Calendar.MONTH) + "/" + time.get(Calendar.DAY_OF_MONTH), Toast.LENGTH_LONG).show();
//
//    }
//
//    @Override
//    public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {
//
//    }
//
//    @Override
//    public void onSelectedDaeChange(Calendar selectedDate) {
//        mWeekHeaderView.setSelectedDay(selectedDate);
//        mTv_date.setText(selectedDate.get(Calendar.YEAR)+"年"+(selectedDate.get(Calendar.MONTH)+1)+"月");
//    }
//
//    //-//<AsyncExpandableListViewCallbacks>---------------------------------------------------------
//    //-//</AsyncExpandableListViewCallbacks>--------------------------------------------------------
//
//
//
//    //-//<Listener>------------------------------------------------------------------------------
//    //-//</Listener>-----------------------------------------------------------------------------
//
//    //</editor-fold desc="Event事件区--只要存在事件监听代码就是">---------------------------------------------------------
//
//
//    //<内部类>---尽量少用----------------------------------------------------------------------------
//    //</内部类>---尽量少用---------------------------------------------------------------------------
//
//}
