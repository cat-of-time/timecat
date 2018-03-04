package com.time.cat.ui.fragment.schedules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ldf.calendar.Utils;
import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.MonthPager;
import com.time.cat.R;
import com.time.cat.database.DB;
import com.time.cat.mvp.model.APImodel.User;
import com.time.cat.mvp.model.DBmodel.DBTask;
import com.time.cat.mvp.model.DBmodel.DBUser;
import com.time.cat.mvp.model.Task;
import com.time.cat.mvp.presenter.FragmentPresenter;
import com.time.cat.mvp.view.SmoothCheckBox;
import com.time.cat.mvp.view.asyncExpandableListView.AsyncExpandableListView;
import com.time.cat.mvp.view.asyncExpandableListView.AsyncExpandableListViewCallbacks;
import com.time.cat.mvp.view.asyncExpandableListView.AsyncHeaderViewHolder;
import com.time.cat.mvp.view.asyncExpandableListView.CollectionView;
import com.time.cat.mvp.view.calendar.CustomDayView;
import com.time.cat.mvp.view.calendar.ThemeDayView;
import com.time.cat.network.RetrofitHelper;
import com.time.cat.theme.ThemeManager;
import com.time.cat.ui.activity.addtask.DialogActivity;
import com.time.cat.ui.activity.main.listener.OnDateChangeListener;
import com.time.cat.ui.activity.main.listener.OnScheduleViewClickListener;
import com.time.cat.ui.base.BaseFragment;
import com.time.cat.util.ModelUtil;
import com.time.cat.util.override.LogUtil;
import com.time.cat.util.override.ToastUtil;
import com.time.cat.util.string.TimeUtil;
import com.time.cat.util.view.ViewUtil;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author dlink
 * @date 2018/1/24
 * @discription SchedulesFragment
 */
@SuppressLint("SetTextI18n")
public class SchedulesFragment extends BaseFragment implements
                                                    FragmentPresenter,
                                                    OnSelectDateListener,
                                                    View.OnClickListener,
                                                    OnScheduleViewClickListener,
                                                    AsyncExpandableListViewCallbacks<DBTask, DBTask> {
    @SuppressWarnings("unused")
    private static final String TAG = "SchedulesFragment";
    private static final int[] labelColor = new int[]{
            Color.parseColor("#f44336"),
            Color.parseColor("#ff8700"),
            Color.parseColor("#2196f3"),
            Color.parseColor("#4caf50")
    };
    private CoordinatorLayout content;
    private ProgressBar progressBar;
    private MonthPager monthPager;
    private ArrayList<Calendar> currentCalendars = new ArrayList<>();
    private CalendarViewAdapter calendarAdapter;
    private ArrayList<TextView> textViewList;
    private AsyncExpandableListView<DBTask, DBTask> mAsyncExpandableListView;
    private CollectionView.Inventory<DBTask, DBTask> inventory;
    private Context context;
    private CalendarDate currentDate;
    private int mCurrentPage = MonthPager.CURRENT_DAY_INDEX;
    private boolean onBindCollectionHeaderView;
    private OnDateChangeListener mDateChangeListener;
    private DBUser dbUser;
    private Handler handler = new Handler();


    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onPause() {
        super.onPause();
//        refreshMonthPager();
//        Utils.scrollTo(content, mAsyncExpandableListView, monthPager.getCellHeight(), 200);
//        calendarAdapter.switchToWeek(monthPager.getRowIndex());
//        ThemeUtils.refreshUI(getActivity(), null);
    }

    @Override
    public void onResume() {
        super.onResume();
        //由activity通知，自己不再自动处理
//        refreshData();
//        ThemeUtils.refreshUI(getActivity(), null);
    }
    //</生命周期>------------------------------------------------------------------------------------





    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码------------------------------------
    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedules, container, false);
        context = getContext();
        content = view.findViewById(R.id.content);
        progressBar = view.findViewById(R.id.progress_bar);
        monthPager = view.findViewById(R.id.calendar_view);
        //此处强行setViewHeight，毕竟你知道你的日历牌的高度
        monthPager.setViewHeight(Utils.dpi2px(context, 270));

        textViewList = new ArrayList<>();
        TextView weekIndicator_7 = view.findViewById(R.id.weekIndicator_7);
        TextView weekIndicator_1 = view.findViewById(R.id.weekIndicator_1);
        TextView weekIndicator_2 = view.findViewById(R.id.weekIndicator_2);
        TextView weekIndicator_3 = view.findViewById(R.id.weekIndicator_3);
        TextView weekIndicator_4 = view.findViewById(R.id.weekIndicator_4);
        TextView weekIndicator_5 = view.findViewById(R.id.weekIndicator_5);
        TextView weekIndicator_6 = view.findViewById(R.id.weekIndicator_6);
        textViewList.add(weekIndicator_7);
        textViewList.add(weekIndicator_1);
        textViewList.add(weekIndicator_2);
        textViewList.add(weekIndicator_3);
        textViewList.add(weekIndicator_4);
        textViewList.add(weekIndicator_5);
        textViewList.add(weekIndicator_6);

        mAsyncExpandableListView = view.findViewById(R.id.asyncExpandableCollectionView);
        mAsyncExpandableListView.setHasFixedSize(true);

        //<功能归类分区方法，必须调用>-----------------------------------------------------------------
        initData();
        initView();
        initEvent();
        //</功能归类分区方法，必须调用>----------------------------------------------------------------

        return view;
    }

    @Override
    public void initView() {//必须调用
        initCalendarView();
        initMonthPager();
        Utils.scrollTo(content, mAsyncExpandableListView, monthPager.getCellHeight(), 200);
        calendarAdapter.switchToWeek(monthPager.getRowIndex());
    }

    /**
     * 初始化CustomDayView，并作为CalendarViewAdapter的参数传入
     */
    private void initCalendarView() {
        CustomDayView customDayView = new CustomDayView(context, R.layout.view_calendar_custom_day);
        calendarAdapter = new CalendarViewAdapter(context, this, CalendarAttr.CalendarType.WEEK, CalendarAttr.WeekArrayType.Sunday, customDayView);
        calendarAdapter.setOnCalendarTypeChangedListener(new CalendarViewAdapter.OnCalendarTypeChanged() {
            @Override
            public void onCalendarTypeChanged(CalendarAttr.CalendarType type) {
                mAsyncExpandableListView.scrollToPosition(0);
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
                    for (int i = 1; i <= 7; i++) {
                        if (i == currentDate.getDayOfWeek()) {
                            textViewList.get(i - 1).setTextColor(Color.WHITE);
                        } else {
                            textViewList.get(i - 1).setTextColor(Color.BLACK);
                        }
                    }
                    if (mDateChangeListener != null) {
                        mDateChangeListener.onDateChange(date.getYear(), date.getMonth(), date.isToday());
                    }
                } else {
                    refreshMonthPager();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                calendarAdapter.notifyDataSetChanged();
            }
        });
        monthPager.setBackgroundColor(ThemeManager.getTheme(getActivity()));
    }
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>--------------------------------






    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码--------------------------------------------
    @Override
    public void initData() {//必须调用
        // 耗时操作 比如网络请求
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isPrepared()) {
                    Log.w("initData", "目标已被回收");
                    return;
                }
                initCurrentDate();
                dbUser = DB.users().getActive();
//                LogUtil.e("active dbUser --> " + dbUser.toString());
                initExpandableListViewData();
                content.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }, 500);

    }

    /**
     * 初始化currentDate
     */
    private void initCurrentDate() {
        currentDate = new CalendarDate();
        for (int i = 1; i <= 7; i++) {
            if (i == currentDate.getDayOfWeek()) {
                textViewList.get(i - 1).setTextColor(Color.WHITE);
            } else {
                textViewList.get(i - 1).setTextColor(Color.BLACK);
            }
        }
        if (mDateChangeListener != null) {
            mDateChangeListener.onDateChange(currentDate.getYear(), currentDate.getMonth(), currentDate.isToday());
        }
    }

    private void initExpandableListViewData() {

        inventory = new CollectionView.Inventory<>();
        RetrofitHelper.getUserService().getUserByEmail(dbUser.getEmail()) //获取Observable对象
                .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        //保存用户信息到本地
                        DB.users().updateActiveUserAndFireEvent(dbUser, user);
//                        Log.i(TAG, dbUser.toString());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //请求失败
                        LogUtil.e(e.toString());
                        ToastUtil.show("更新用户信息失败");
                    }

                    @Override
                    public void onNext(User user) {
                        //请求成功
                        Log.i(TAG, "更新用户信息成功 --> " + user.toString());
                        ToastUtil.show("更新用户信息成功");
                    }
                });

        ArrayList<String> task_urls = dbUser.getTasks();
        if (task_urls != null && task_urls.size() > 0) {
            AsyncTask<ArrayList<String>, Void, ArrayList<DBTask>> loadDataForHeader = new LoadDataTaskHeader(inventory);
            loadDataForHeader.execute(task_urls);
        } else {
            AsyncTask<ArrayList<String>, Void, ArrayList<DBTask>> loadDataForHeader = new LoadDataTaskHeader(inventory);
            loadDataForHeader.execute(new ArrayList<String>());
            LogUtil.e("null task list");
        }
        LogUtil.e("initExpandableListViewData --> dbUser.getTasks() --> " + task_urls);
    }

    public void refreshData() {

        if (content != null) {
            content.setVisibility(View.GONE);
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

    private void refreshExpandableListViewData() {

        inventory = new CollectionView.Inventory<>();
        RetrofitHelper.getUserService().getUserByEmail(dbUser.getEmail()) //获取Observable对象
                .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        //保存用户信息到本地
                        DB.users().updateActiveUserAndFireEvent(dbUser, user);
//                        Log.i(TAG, dbUser.toString());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //请求失败
                        LogUtil.e(e.toString());
                        ToastUtil.show("更新用户信息失败");
                    }

                    @Override
                    public void onNext(User user) {
                        //请求成功
                        Log.i(TAG, "更新用户信息成功 --> " + user.toString());
                        ToastUtil.show("更新用户信息成功");
                    }
                });

        ArrayList<String> task_urls = dbUser.getTasks();
        if (task_urls != null && task_urls.size() > 0) {
            AsyncTask<ArrayList<String>, Void, ArrayList<DBTask>> loadDataForHeader = new LoadDataTaskHeader(inventory);
            loadDataForHeader.execute(task_urls);
        } else {
            AsyncTask<ArrayList<String>, Void, ArrayList<DBTask>> loadDataForHeader = new LoadDataTaskHeader(inventory);
            loadDataForHeader.execute(new ArrayList<>());
        }
    }

    @Override
    public void notifyDataChanged() {
        super.notifyDataChanged();
        refreshData();
        LogUtil.e("schedule fragment --> notifyDataChanged()");
    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-------------------------------------------







    //<Event事件区>---只要存在事件监听代码就是-----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用
        mAsyncExpandableListView.setCallbacks(this);
    }

    //-//<View.OnClickListener>---------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }
    //-//</View.OnClickListener>--------------------------------------------------------------------




    //-//<AsyncExpandableListViewCallbacks>---------------------------------------------------------
    @Override
    public void onStartLoadingGroup(int groupOrdinal) {
        new LoadDataTaskContent(groupOrdinal, mAsyncExpandableListView)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public AsyncHeaderViewHolder newCollectionHeaderView(Context context, int groupOrdinal, ViewGroup parent) {
        // Create a new view.
        View v = LayoutInflater.from(context).inflate(R.layout.item_todo_list, parent, false);

        return new ScheduleHeaderViewHolder(v, groupOrdinal, mAsyncExpandableListView);
    }

    @Override
    public RecyclerView.ViewHolder newCollectionItemView(Context context, int groupOrdinal, ViewGroup parent) {
        // Create a new view.
        View v = LayoutInflater.from(context).inflate(R.layout.card_todolist_async, parent, false);

        return new ScheduleItemHolder(v);
    }

    @Override
    public void bindCollectionHeaderView(Context context, AsyncHeaderViewHolder holder, int groupOrdinal, DBTask headerItem) {
        onBindCollectionHeaderView = true;
        ScheduleHeaderViewHolder scheduleHeaderViewHolder = (ScheduleHeaderViewHolder) holder;
        scheduleHeaderViewHolder.getCalendarItemCheckBox().setUncheckedStrokeColor(labelColor[headerItem.getLabel()]);
        scheduleHeaderViewHolder.getCalendarItemCheckBox().setChecked(headerItem.getIsFinish());

        scheduleHeaderViewHolder.getCalendarItemTitle().setText(headerItem.getTitle());
        Date today = new Date();
        if (headerItem.getCreated_datetime() != null && headerItem.getCreated_datetime().length() > 0) {
            Date date = TimeUtil.formatGMTDateStr(headerItem.getCreated_datetime());
            if (date != null) {
                if (headerItem.getIsFinish()) {
                    today = TimeUtil.formatGMTDateStr(headerItem.getFinished_datetime());
                }
                long during = today.getTime() - date.getTime();
                long day = during / (1000 * 60 * 60 * 24);
                if (day >= 1) {
                    scheduleHeaderViewHolder.getCalendarItemDelay().setText(day >= 1 ? getString(R.string.calendar_delay) + day + getString(R.string.calendar_day) : "");
                    if (headerItem.getIsFinish()) {
                        scheduleHeaderViewHolder.getCalendarItemDelay().setTextColor(Color.GRAY);
                    } else {
                        scheduleHeaderViewHolder.getCalendarItemDelay().setTextColor(getResources().getColor(R.color.red));
                    }
                } else {
                    scheduleHeaderViewHolder.getCalendarItemDelay().setVisibility(View.INVISIBLE);
                }
            }
        }
        onBindCollectionHeaderView = false;
    }

    @Override
    public void bindCollectionItemView(Context context, RecyclerView.ViewHolder holder, int groupOrdinal, DBTask item) {
        ScheduleItemHolder scheduleItemHolder = (ScheduleItemHolder) holder;
        scheduleItemHolder.getTextViewContent().setText(item.getContent());
        Date d;
        if (item.getBegin_datetime() != "null" && item.getEnd_datetime() != "null"
                && item.getBegin_datetime() != null && item.getEnd_datetime() != null) {
            d = TimeUtil.formatGMTDateStr(item.getBegin_datetime());
            String begin_date = TimeUtil.formatMonthDay(d);//d.getMonth() + "月" + d.getDay() + "日";
            d = TimeUtil.formatGMTDateStr(item.getEnd_datetime());
            String end_date = TimeUtil.formatMonthDay(d);//d.getMonth() + "月" + d.getDay() + "日";
            scheduleItemHolder.getScheduleTaskTv_date().setText(begin_date + "-" + end_date);
        } else if (item.getCreated_datetime() != "null" && item.getCreated_datetime() != null) {
            d = TimeUtil.formatGMTDateStr(item.getCreated_datetime());
            String created_date = TimeUtil.formatMonthDay(d);//d.getMonth() + "月" + d.getDay() + "日";
            scheduleItemHolder.getScheduleTaskTv_date().setText(created_date);
        } else {
            d = new Date();
            String today = TimeUtil.formatMonthDay(d);//d.getMonth() + "月" + d.getDay() + "日";
            scheduleItemHolder.getScheduleTaskTv_date().setText(today);
        }
        scheduleItemHolder.setLabel(item.getLabel());
        if (item.getIs_all_day()) {
            scheduleItemHolder.getScheduleTaskTv_time().setText("全天");
        } else if (item.getBegin_datetime() != "null" && item.getEnd_datetime() != "null") {
            d = TimeUtil.formatGMTDateStr(item.getBegin_datetime());
            String begin_datetime = d.getHours() + ":" + d.getMinutes();
            d = TimeUtil.formatGMTDateStr(item.getEnd_datetime());
            String end_datetime = d.getHours() + ":" + d.getMinutes();
            scheduleItemHolder.getScheduleTaskTv_time().setText(begin_datetime + "-" + end_datetime);
        }
    }
    //-//</AsyncExpandableListViewCallbacks>--------------------------------------------------------




    //-//<MainActivity.OnScheduleViewClickListener>---------------------------------------------------------
    @Override
    public void onViewTodayClick() {
        refreshMonthPager();
    }

    @Override
    public void onViewChangeMarkThemeClick() {
        refreshSelectBackground();
    }

    @Override
    public void onViewRefreshClick() {
        refreshData();
    }

    private void refreshMonthPager() {
        CalendarDate today = new CalendarDate();
        if (calendarAdapter != null) {
            calendarAdapter.notifyDataChanged(today);
        }
        for (int i = 1; i <= 7; i++) {
            if (i == today.getDayOfWeek()) {
                textViewList.get(i - 1).setTextColor(Color.WHITE);
            } else {
                textViewList.get(i - 1).setTextColor(Color.BLACK);
            }
        }
        if (mDateChangeListener != null) {
            mDateChangeListener.onDateChange(today.getYear(), today.getMonth(), today.isToday());
        }
        if (monthPager != null) {
            monthPager.setBackgroundColor(ThemeManager.getTheme(getActivity()));
        }
    }

    private void refreshSelectBackground() {
        ThemeDayView themeDayView = new ThemeDayView(context, R.layout.view_calendar_custom_day_focus);
        calendarAdapter.setCustomDayRenderer(themeDayView);
        calendarAdapter.notifyDataSetChanged();
        calendarAdapter.notifyDataChanged(new CalendarDate());
    }
    //-//</MainActivity.OnScheduleViewClickListener>--------------------------------------------------------




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
    //-//</OnSelectDateListener>--------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是----------------------------------------------------------






    //<回调接口>-------------------------------------------------------------------------------------
    private void refreshClickDate(CalendarDate date) {
        currentDate = date;
        for (int i = 1; i <= 7; i++) {
            if (i == currentDate.getDayOfWeek()) {
                textViewList.get(i - 1).setTextColor(Color.WHITE);
            } else {
                textViewList.get(i - 1).setTextColor(Color.BLACK);
            }
        }
        if (mDateChangeListener != null) {
            mDateChangeListener.onDateChange(date.getYear(), date.getMonth(), date.isToday());
        }
        refreshExpandableListViewData();
    }

    public void setOnDateChangeListener(OnDateChangeListener DateChangeListener) {
        mDateChangeListener = DateChangeListener;
    }
    //</回调接口>------------------------------------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------
    private class LoadDataTaskHeader extends AsyncTask<ArrayList<String>, Void, ArrayList<DBTask>> {

        private CollectionView.Inventory<DBTask, DBTask> inventory = null;

        public LoadDataTaskHeader(CollectionView.Inventory<DBTask, DBTask> inventory) {
            this.inventory = inventory;
        }

        @SafeVarargs
        @Override
        protected final ArrayList<DBTask> doInBackground(ArrayList<String>... params) {
            ArrayList<DBTask> tasks;
            if (params.length <= 0) {
                return null;
            }
            final int[] count = {params[0].size()};
            for (int i = 0; i < params[0].size(); i++) {
                RetrofitHelper.getTaskService().getTaskByUrl(params[0].get(i))
                        .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                        .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                        .doOnNext(new Action1<Task>() {
                            @Override
                            public void call(Task task) {
                                DB.schedules().safeSaveTask(task);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                        .subscribe(new Subscriber<Task>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                //请求失败
//                                ToastUtil.show("数据同步到云端时出现错误");
                                LogUtil.e("count[0] == "+count[0] + " --> 失败 --> " + e.toString());
                                count[0] -= 1;
                            }

                            @Override
                            public void onNext(Task task) {
                                //请求成功
                                count[0] -= 1;
//                                ToastUtil.show("成功获取任务");
                                Log.i(TAG, "请求成功 --> count[0] == " + count[0] + " --> " + task.toString());
                            }
                        });
                Log.w(TAG, "fetching task " + i);
            }
//            Log.w(TAG, "waiting -->");
//            int retryTimes = 5; //重试次数
//            while (count[0] != 0 && retryTimes != 0) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Log.w(TAG, "retryTimes -->" + retryTimes);
//                retryTimes--;
//                // 循环结束条件:上面的网络请求线程全部完成(count[0] == 0) 或 超过重试次数网络请求还没全部完成(retryTimes == 0)
//            }
//            Log.w(TAG, "returning -->");
            List<DBTask> taskList = DB.schedules().findAll();
            tasks = sort(DBTaskFilter(taskList));
            return tasks;
        }

        @Override
        protected void onPostExecute(ArrayList<DBTask> tasks) {
            if (inventory != null && tasks != null) {
                for (int i = 0; i < tasks.size(); i++) {
                    CollectionView.InventoryGroup<DBTask, DBTask> group_i = inventory.newGroup(i); // groupOrdinal is the smallest, displayed first
                    group_i.setHeaderItem(tasks.get(i));
                }
                mAsyncExpandableListView.updateInventory(inventory);
            } else {
                mAsyncExpandableListView.updateInventory(new CollectionView.Inventory<>());
            }
        }

        private ArrayList<DBTask> DBTaskFilter(List<DBTask> taskArrayList) {
            ArrayList<DBTask> tasks = new ArrayList<>();
            if (taskArrayList == null || taskArrayList.size() <= 0) {
                return tasks;
            }
            // 需要显示的
            // 首先是active user的
            // 今天刚刚finished的
            // 顺延的
            // begin_datetime < today <= end_datetime
            DBUser dbUser = DB.users().getActive();

            Date today = new Date();
            if (currentDate != null) {
                today = TimeUtil.transferCalendarDate(currentDate);
            }
            for (DBTask task : taskArrayList) {
                if (!task.getOwner().equals((ModelUtil.getOwnerUrl(dbUser)))) {
                    continue;
                }
                boolean hasAddedTask = false;
                // 把今天刚刚完成的任务(getIsFinish()==true)添加到显示List并标记
                if (task.getIsFinish()) {
                    Date finished_datetime = TimeUtil.formatGMTDateStr(task.getFinished_datetime());
                    if (finished_datetime != null) {
                        if (finished_datetime.getDay() == today.getDay()
                                && finished_datetime.getMonth() == today.getMonth()
                                && finished_datetime.getYear() == today.getYear()) {
                            tasks.add(task);
                            Log.i(TAG, "add task, because task is finished today");
                        }
                    }
                    hasAddedTask = true; // 只要是完成了的，之后都不再判断
                }
                LogUtil.e(task.getIs_all_day() + task.toString());
                if (!task.getIs_all_day() && !hasAddedTask
                        && task.getBegin_datetime() != null
                        && task.getEnd_datetime() != null) {
                    Date begin_datetime = TimeUtil.formatGMTDateStr(task.getBegin_datetime());
                    Date end_datetime = TimeUtil.formatGMTDateStr(task.getEnd_datetime());
                    if (begin_datetime != null && end_datetime != null) {
                        if (TimeUtil.isDateEarlier(begin_datetime, today) && TimeUtil.isDateEarlier(today, end_datetime)) {
                            tasks.add(task);
                            hasAddedTask = true;
                            Log.i(TAG, "add task, because begin <= today <= end");
                        }
                    }
                }
                // 把顺延的添加到显示List并标记
                if (task.getCreated_datetime() != null || task.getCreated_datetime() != "null") {
                    Date created_datetime = TimeUtil.formatGMTDateStr(task.getCreated_datetime());
                    if (!hasAddedTask && created_datetime != null) {
                        long during = today.getTime() - created_datetime.getTime();
                        if (TimeUtil.isDateEarlier(created_datetime, today)) {
                            if (task.getIs_all_day()) {
                                tasks.add(task);
                                Log.i(TAG, "add task, because of delay");
                            }
                        }
                    }
                }
            }

            return tasks;
        }

        private ArrayList<DBTask> sort(ArrayList<DBTask> taskArrayList) {
            ArrayList<DBTask> sortedDBTaskList = new ArrayList<>();
            if (taskArrayList == null || taskArrayList.size() <= 0) {
                return sortedDBTaskList;
            }
            ArrayList<DBTask> label_0_DBTaskList = new ArrayList<>();
            ArrayList<DBTask> label_1_DBTaskList = new ArrayList<>();
            ArrayList<DBTask> label_2_DBTaskList = new ArrayList<>();
            ArrayList<DBTask> label_3_DBTaskList = new ArrayList<>();
            ArrayList<DBTask> finished_DBTaskList = new ArrayList<>();

            for (DBTask dbTask : taskArrayList) {
                if (dbTask.getIsFinish()) {
                    finished_DBTaskList.add(dbTask);
                    continue;
                }
                switch (dbTask.getLabel()) {
                    case DBTask.LABEL_IMPORTANT_URGENT:
                        label_0_DBTaskList.add(dbTask);
                        break;
                    case DBTask.LABEL_IMPORTANT_NOT_URGENT:
                        label_1_DBTaskList.add(dbTask);
                        break;
                    case DBTask.LABEL_NOT_IMPORTANT_URGENT:
                        label_2_DBTaskList.add(dbTask);
                        break;
                    case DBTask.LABEL_NOT_IMPORTANT_NOT_URGENT:
                        label_3_DBTaskList.add(dbTask);
                        break;
                }
            }
            mergeSort2List(label_0_DBTaskList, sortedDBTaskList);
            mergeSort2List(label_1_DBTaskList, sortedDBTaskList);
            mergeSort2List(label_2_DBTaskList, sortedDBTaskList);
            mergeSort2List(label_3_DBTaskList, sortedDBTaskList);
            mergeSort2List(finished_DBTaskList, sortedDBTaskList);

            return sortedDBTaskList;
        }

        private void reverse(ArrayList<DBTask> arr, int i, int j) {
            while(i < j) {
                DBTask temp = arr.get(i);
                arr.set(i++, arr.get(j));
                arr.set(j--, temp);
            }
        }

        // swap [bias, bias+headSize) and [bias+headSize, bias+headSize+endSize)
        private void swapAdjacentBlocks(ArrayList<DBTask> arr, int bias, int oneSize, int anotherSize) {
            reverse(arr, bias, bias + oneSize - 1);
            reverse(arr, bias + oneSize, bias + oneSize + anotherSize - 1);
            reverse(arr, bias, bias + oneSize + anotherSize - 1);
        }

        private void inplaceMerge(ArrayList<DBTask> arr, int l, int mid, int r) {
            int i = l;     // 指示左侧有序串
            int j = mid + 1; // 指示右侧有序串
            while(i < j && j <= r) { //原地归并结束的条件。
                while(i < j && isValid(arr, i, j)) {
                    i++;
                }
                int index = j;
                while(j <= r && isValid(arr, j, i)) {
                    j++;
                }
                swapAdjacentBlocks(arr, i, index-i, j-index);
                i += (j-index);
            }
        }

        private boolean isValid(ArrayList<DBTask> arr, int i, int j) {
            Date date_i = TimeUtil.formatGMTDateStr(arr.get(i).getCreated_datetime());
            Date date_j = TimeUtil.formatGMTDateStr(arr.get(j).getCreated_datetime());
            return (date_i != null ? date_i.getTime() : 0) <= (date_j != null ? date_j.getTime() : 0);
        }

        private void mergeSort(ArrayList<DBTask> arr, int l, int r) {
            if(l < r) {
                int mid = (l + r) / 2;
                mergeSort(arr, l, mid);
                mergeSort(arr, mid + 1, r);
                inplaceMerge(arr, l, mid, r);
            }
        }

        private void mergeSort2List(ArrayList<DBTask> taskArrayList, ArrayList<DBTask> result) {
            if (taskArrayList == null || taskArrayList.size() <= 0) {
                return;
            }
            mergeSort(taskArrayList, 0, taskArrayList.size()-1);
            result.addAll(taskArrayList);
        }

    }

    private class LoadDataTaskContent extends AsyncTask<Void, Void, List<DBTask>> {

        private final int mGroupOrdinal;
        private WeakReference<AsyncExpandableListView<DBTask, DBTask>> listviewRef = null;

        public LoadDataTaskContent(int groupOrdinal, AsyncExpandableListView<DBTask, DBTask> listview) {
            mGroupOrdinal = groupOrdinal;
            listviewRef = new WeakReference<>(listview);
        }

        @Override
        protected List<DBTask> doInBackground(Void... params) {
            List<DBTask> items = new ArrayList<>();
            items.add(listviewRef.get().getHeader(mGroupOrdinal));
            return items;
        }


        @Override
        protected void onPostExecute(List<DBTask> tasks) {
            if (listviewRef.get() != null && tasks != null) {
                listviewRef.get().onFinishLoadingGroup(mGroupOrdinal, tasks);
            }
        }

    }

    public class ScheduleItemHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ScheduleItemHolder";

        private final TextView tvContent;
        private final TextView schedule_task_tv_label;
        private final TextView schedule_task_tv_date;
        private final TextView schedule_task_tv_time;
        private final TextView schedule_task_tv_tag;
        String[] text_color_set = new String[]{
                "#f44336", "#ff9800", "#2196f3", "#4caf50"
        };
        String[] background_color_set = new String[]{
                "#50f44336", "#50ff9800", "#502196f3", "#504caf50"
        };
        String[] label_str_set = new String[] {
                "重要且紧急", "重要不紧急", "紧急不重要", "不重要不紧急",
        };
        public ScheduleItemHolder(View v) {
            super(v);
            tvContent = v.findViewById(R.id.schedule_task_tv_content);
            schedule_task_tv_label = v.findViewById(R.id.schedule_task_tv_label);
            schedule_task_tv_date = v.findViewById(R.id.schedule_task_tv_date);
            schedule_task_tv_time = v.findViewById(R.id.schedule_task_tv_time);
            schedule_task_tv_tag = v.findViewById(R.id.schedule_task_tv_tag);
        }

        public TextView getTextViewContent() {
            return tvContent;
        }

        public TextView getScheduleTaskTv_date() {
            return schedule_task_tv_date;
        }

        public TextView getScheduleTaskTv_label() {
            return schedule_task_tv_label;
        }

        public TextView getScheduleTaskTv_time() {
            return schedule_task_tv_time;
        }

        public TextView getScheduleTaskTv_tag() {
            return schedule_task_tv_tag;
        }

        public void setLabel(int label) {
            schedule_task_tv_label.setText(label_str_set[label]);
            schedule_task_tv_label.setTextColor(Color.parseColor(text_color_set[label]));
            schedule_task_tv_label.setBackgroundColor(Color.parseColor(background_color_set[label]));
        }
    }

    public class ScheduleHeaderViewHolder extends AsyncHeaderViewHolder implements
                                                                        AsyncExpandableListView.OnGroupStateChangeListener,
                                                                        SmoothCheckBox.OnCheckedChangeListener,
                                                                        View.OnClickListener,
                                                                        View.OnLongClickListener {

        private RelativeLayout calendar_item_ll;
        private SmoothCheckBox calendar_item_checkBox;
        private TextView calendar_item_title;
        private ProgressBar calendar_item_progressBar;
        private ImageView ivExpansionIndicator;
        private TextView calendar_item_delay;
        private RelativeLayout calendar_item_rl_content_container;
        private RelativeLayout calendar_item_rl_container;

        private int mGroupOrdinal;

        public ScheduleHeaderViewHolder(View v, int groupOrdinal, AsyncExpandableListView asyncExpandableListView) {
            super(v, groupOrdinal, asyncExpandableListView);
            mGroupOrdinal = groupOrdinal;
            calendar_item_ll = v.findViewById(R.id.calendar_item_ll);
            // 防止误点击
            calendar_item_ll.setOnClickListener(null);

            calendar_item_checkBox = v.findViewById(R.id.calendar_item_checkBox);
            calendar_item_checkBox.setOnCheckedChangeListener(this);

            calendar_item_title = v.findViewById(R.id.calendar_item_title);
            calendar_item_delay = v.findViewById(R.id.calendar_item_delay);

            calendar_item_progressBar = v.findViewById(R.id.calendar_item_progressBar);
            calendar_item_progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#1296db"), android.graphics.PorterDuff.Mode.MULTIPLY);
            ivExpansionIndicator = v.findViewById(R.id.calendar_item_ivExpansionIndicator);
            calendar_item_rl_container = v.findViewById(R.id.calendar_item_rl_container);
            calendar_item_rl_content_container = v.findViewById(R.id.calendar_item_rl_content_container);

            calendar_item_rl_container.setOnClickListener(this);
            calendar_item_rl_content_container.setOnClickListener(this);
            calendar_item_rl_content_container.setOnLongClickListener(this);
        }


        public TextView getCalendarItemTitle() {
            return calendar_item_title;
        }

        public SmoothCheckBox getCalendarItemCheckBox() {
            return calendar_item_checkBox;
        }

        public TextView getCalendarItemDelay() {
            return calendar_item_delay;
        }

        @Override
        public void onGroupStartExpending() {
            calendar_item_progressBar.setVisibility(View.VISIBLE);
            ivExpansionIndicator.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onGroupExpanded() {
            calendar_item_progressBar.setVisibility(View.GONE);
            ivExpansionIndicator.setVisibility(View.VISIBLE);
            ivExpansionIndicator.setImageResource(R.drawable.ic_arrow_up);
        }

        @Override
        public void onGroupCollapsed() {
            calendar_item_progressBar.setVisibility(View.GONE);
            ivExpansionIndicator.setVisibility(View.VISIBLE);
            ivExpansionIndicator.setImageResource(R.drawable.ic_arrow_down);

        }

        @Override
        public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
//            if (mAsyncExpandableListView.getHeader(mGroupOrdinal).getIsFinish() == isChecked) {
//                return;
//            }
            if (isChecked) {
                ViewUtil.addClearCenterLine(calendar_item_title);
                calendar_item_title.setTextColor(Color.GRAY);
                calendar_item_delay.setTextColor(Color.GRAY);
                mAsyncExpandableListView.getHeader(mGroupOrdinal).setIsFinish(true);
                if (mAsyncExpandableListView.getHeader(mGroupOrdinal).getFinished_datetime() == null) {
                    mAsyncExpandableListView.getHeader(mGroupOrdinal).setFinished_datetime(TimeUtil.formatGMTDate(new Date()));
                }
            } else {
                ViewUtil.removeLine(calendar_item_title);
                calendar_item_title.setTextColor(Color.BLACK);
                calendar_item_delay.setTextColor(getResources().getColor(R.color.red));
                mAsyncExpandableListView.getHeader(mGroupOrdinal).setIsFinish(false);
                mAsyncExpandableListView.getHeader(mGroupOrdinal).setFinished_datetime(null);
            }
            DBTask task = mAsyncExpandableListView.getHeader(mGroupOrdinal);
            Log.i(TAG, "onCheckedChanged() --> header task -->" + task.toString());
            DB.schedules().safeSaveDBTask(task);

            RetrofitHelper.getTaskService().putTaskByUrl(task.getUrl(), ModelUtil.toTask(task))
                    .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                    .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                    .doOnNext(new Action1<Task>() {
                        @Override
                        public void call(Task task) {
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                    .subscribe(new Subscriber<Task>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            //请求失败
                            ToastUtil.show("网络请求失败，已保存到本地");
                            LogUtil.e("网络请求失败 --> " + e.toString());
                        }

                        @Override
                        public void onNext(Task task) {
                            //请求成功
                            ToastUtil.show("同步成功");
                            LogUtil.e("同步成功 --> " + task.toString());
                        }
                    });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.calendar_item_rl_container:
                    mAsyncExpandableListView.onGroupClicked(mGroupOrdinal);
                    break;
                case R.id.calendar_item_rl_content_container:
                    DBTask task = mAsyncExpandableListView.getHeader(mGroupOrdinal);
                    Intent intent2DialogActivity = new Intent(context, DialogActivity.class);
                    intent2DialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2DialogActivity.putExtra(DialogActivity.TO_SAVE_STR, task.getContent());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(DialogActivity.TO_UPDATE_TASK, task);
                    intent2DialogActivity.putExtras(bundle);
                    startActivity(intent2DialogActivity);
                    ToastUtil.show("编辑任务");
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (v.getId() == R.id.calendar_item_rl_content_container) {
                // 长按显示删除按钮
                new MaterialDialog.Builder(getActivity())
                        .content("确定删除这个任务吗？")
                        .positiveText("删除")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                DBTask task = mAsyncExpandableListView.getHeader(mGroupOrdinal);
                                LogUtil.e("onLongClick() --> 确定删除 task -->" + task.toString());
                                try {
                                    DB.schedules().delete(task);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                RetrofitHelper.getTaskService().deleteTaskByUrl(task.getUrl())
                                        .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                                        .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                                        .subscribe(new Subscriber<Task>() {
                                            @Override
                                            public void onCompleted() {

                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                //请求失败
                                                ToastUtil.show("删除操作同步失败");
                                                LogUtil.e("删除操作同步失败 --> " + e.toString());
                                            }

                                            @Override
                                            public void onNext(Task task) {
                                                //请求成功
                                                ToastUtil.show("删除成功");
                                                LogUtil.e("删除成功 --> " + task.toString());
                                            }
                                        });
                                notifyDataChanged();
                            }
                        })
                        .negativeText("取消")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
            return false;
        }
    }
    //</内部类>---尽量少用---------------------------------------------------------------------------
}
