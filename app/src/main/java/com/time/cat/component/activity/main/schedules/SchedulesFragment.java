package com.time.cat.component.activity.main.schedules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import com.ldf.calendar.Utils;
import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.MonthPager;
import com.time.cat.NetworkSystem.RetrofitHelper;
import com.time.cat.R;
import com.time.cat.ThemeSystem.ThemeManager;
import com.time.cat.ThemeSystem.utils.ThemeUtils;
import com.time.cat.component.activity.addtask.DialogActivity;
import com.time.cat.component.activity.main.listener.OnDateChangeListener;
import com.time.cat.component.activity.main.listener.OnViewClickListener;
import com.time.cat.component.base.BaseFragment;
import com.time.cat.database.DB;
import com.time.cat.mvp.model.APImodel.User;
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
import com.time.cat.util.override.ToastUtil;
import com.time.cat.util.string.TimeUtil;
import com.time.cat.util.view.ViewUtil;

import java.lang.ref.WeakReference;
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
                                                    OnViewClickListener,
                                                    AsyncExpandableListViewCallbacks<Task, Task> {
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
    private AsyncExpandableListView<Task, Task> mAsyncExpandableListView;
    private CollectionView.Inventory<Task, Task> inventory;
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
        refreshMonthPager();
        Utils.scrollTo(content, mAsyncExpandableListView, monthPager.getCellHeight(), 200);
        calendarAdapter.switchToWeek(monthPager.getRowIndex());
        ThemeUtils.refreshUI(getActivity(), null);
    }

    @Override
    public void onResume() {
        super.onResume();
        ThemeUtils.refreshUI(getActivity(), null);
    }
    //</生命周期>------------------------------------------------------------------------------------





    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------
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

    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>-----------------------------






    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-----------------------------------------
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
                dbUser = DB.users().getActive(context);
                Log.e(TAG, dbUser.toString());
                initExpandableListViewData();
                content.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }, 1000);

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
        final ArrayList<String>[] task_urls = new ArrayList[]{dbUser.getTasks()};
        final boolean[] isSuccess = {false};
        RetrofitHelper.getUserService().getUserByEmail(dbUser.getEmail()) //获取Observable对象
                .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        //保存用户信息到本地
                        DB.users().updateActiveUserAndFireEvent(dbUser, user);
                        Log.i(TAG, dbUser.toString());
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
                        Log.e(TAG, e.toString());
                        ToastUtil.show("更新用户信息失败");
                    }

                    @Override
                    public void onNext(User user) {
                        //请求成功
                        task_urls[0] = user.getTasks();
                        isSuccess[0] = true;
                        Log.i(TAG, "更新用户信息成功 --> " + user.toString());
                        ToastUtil.show("更新用户信息成功");
                    }
                });
//        ArrayList<String> task_urls = dbUser.getTasks();
        if (task_urls[0] != null && task_urls[0].size() > 0) {
            AsyncTask<ArrayList<String>, Void, ArrayList<Task>> loadDataForHeader = new LoadDataTaskHeader(inventory);
            loadDataForHeader.execute(task_urls[0]);
        }
//        mAsyncExpandableListView.updateInventory(inventory);
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

    @Override
    public void notifyDataChanged() {
        super.notifyDataChanged();
        refreshData();
        Log.e(TAG, "schedule fragment --> notifyDataChanged()");
    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------







    //<Event事件区>---只要存在事件监听代码就是----------------------------------------------------------
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
    //-//</View.OnClickListener>---------------------------------------------------------------------




    //-//<AsyncExpandableListViewCallbacks>---------------------------------------------------------------------
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
    public void bindCollectionHeaderView(Context context, AsyncHeaderViewHolder holder, int groupOrdinal, Task headerItem) {
        onBindCollectionHeaderView = true;
        ScheduleHeaderViewHolder scheduleHeaderViewHolder = (ScheduleHeaderViewHolder) holder;
        scheduleHeaderViewHolder.getCalendarItemCheckBox().setUncheckedStrokeColor(labelColor[headerItem.getLabel()]);
        scheduleHeaderViewHolder.getCalendarItemCheckBox().setChecked(headerItem.getIsFinish());

        scheduleHeaderViewHolder.getCalendarItemTitle().setText(headerItem.getTitle());
        Date today = new Date();
        if (headerItem.getCreated_datetime() != "null" && headerItem.getCreated_datetime() != null && headerItem.getCreated_datetime() != "") {
            Date date = TimeUtil.formatGMTDateStr(headerItem.getCreated_datetime());
            if (date != null) {
                long during = today.getTime() - date.getTime();
                long day = during / (1000 * 60 * 60 * 24);
                if (day >= 1) {
                    scheduleHeaderViewHolder.getCalendarItemDelay().setText(day >= 1 ? getString(R.string.calendar_delay) + day + getString(R.string.calendar_day) : "");
                } else {
                    scheduleHeaderViewHolder.getCalendarItemDelay().setVisibility(View.INVISIBLE);
                }
            }
        }
        onBindCollectionHeaderView = false;
    }

    @Override
    public void bindCollectionItemView(Context context, RecyclerView.ViewHolder holder, int groupOrdinal, Task item) {
        ScheduleItemHolder scheduleItemHolder = (ScheduleItemHolder) holder;
        scheduleItemHolder.getTextViewContent().setText(item.getContent());
        Date d;
        if (item.getBegin_datetime() != "null" && item.getEnd_datetime() != "null") {
            d = TimeUtil.formatGMTDateStr(item.getBegin_datetime());
            String begin_date = d.getMonth() + "月" + d.getDay() + "日";
            d = TimeUtil.formatGMTDateStr(item.getEnd_datetime());
            String end_date = d.getMonth() + "月" + d.getDay() + "日";
            scheduleItemHolder.getScheduleTaskTv_date().setText(begin_date + "-" + end_date);
        } else if (item.getCreated_datetime() != "null") {
            d = TimeUtil.formatGMTDateStr(item.getCreated_datetime());
            String created_date = d.getMonth() + "月" + d.getDay() + "日";
            scheduleItemHolder.getScheduleTaskTv_date().setText(created_date);
        } else {
            d = new Date();
            String today = d.getMonth() + "月" + d.getDay() + "日";
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
    //-//</AsyncExpandableListViewCallbacks>---------------------------------------------------------------------




    //-//<MainActivity.OnViewClickListener>---------------------------------------------------------------------
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
    }

    public void setOnDateChangeListener(OnDateChangeListener DateChangeListener) {
        mDateChangeListener = DateChangeListener;
    }
    //</回调接口>------------------------------------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------
    private class LoadDataTaskHeader extends AsyncTask<ArrayList<String>, Void, ArrayList<Task>> {

        private CollectionView.Inventory<Task, Task> inventory = null;

        public LoadDataTaskHeader(CollectionView.Inventory<Task, Task> inventory) {
            this.inventory = inventory;
        }

        @Override
        protected ArrayList<Task> doInBackground(ArrayList<String>... params) {
            ArrayList<Task> tasks = new ArrayList<>();
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
                                // TODO 保存任务信息到本地, java.lang.RuntimeException: Error saving model
                                // TODO abort at 32 in [INSERT INTO `Schedules` (`begin_datetime` ,`content` ,`created_datetime` ,`Cycle` ,`Days` ,`Dose` ,`end_datetime` ,`finished_datetime` ,`is_all_day` ,`is_finished` ,`label` ,`owner` ,`Scanned` ...
//                                DB.schedules().saveAndFireEvent(ModelUtil.toDBTask(task));
//                                Log.e(TAG, "保存任务信息到本地" + task.toString());
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
                                ToastUtil.show("失败");
                                Log.e(TAG, "count[0] == "+count[0] + " --> 失败 --> " + e.toString());
                                count[0] -= 1;
                            }

                            @Override
                            public void onNext(Task task) {
                                //请求成功
                                tasks.add(task);
                                ToastUtil.show("成功");
                                count[0] -= 1;
                                Log.e(TAG, "请求成功 --> count[0] == " + count[0] + " --> " + task.toString());
                            }
                        });
                Log.e(TAG, "fetching task" + i);
            }
            Log.e(TAG, "waiting -->");
            int retryTimes = 5; //重试次数
            while (count[0] != 0 && retryTimes != 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "retryTimes -->" + retryTimes);
                retryTimes--;
                // 循环结束条件:上面的网络请求线程全部完成(count[0] == 0) 或 超过重试次数网络请求还没全部完成(retryTimes == 0)
            }
            Log.e(TAG, "returning");
            return tasks;
        }

        @Override
        protected void onPostExecute(ArrayList<Task> tasks) {
            if (inventory != null && tasks != null) {
                for (int i = 0; i < tasks.size(); i++) {
                    CollectionView.InventoryGroup<Task, Task> group_i = inventory.newGroup(i); // groupOrdinal is the smallest, displayed first
                    group_i.setHeaderItem(tasks.get(i));
                }
                mAsyncExpandableListView.updateInventory(inventory);
            }
        }

    }

    private class LoadDataTaskContent extends AsyncTask<Void, Void, List<Task>> {

        private final int mGroupOrdinal;
        private WeakReference<AsyncExpandableListView<Task, Task>> listviewRef = null;

        public LoadDataTaskContent(int groupOrdinal, AsyncExpandableListView<Task, Task> listview) {
            mGroupOrdinal = groupOrdinal;
            listviewRef = new WeakReference<>(listview);
        }

        @Override
        protected List<Task> doInBackground(Void... params) {
            List<Task> items = new ArrayList<>();
            items.add(listviewRef.get().getHeader(mGroupOrdinal));
            return items;
        }


        @Override
        protected void onPostExecute(List<Task> tasks) {
            if (listviewRef.get() != null && tasks != null) {
                listviewRef.get().onFinishLoadingGroup(mGroupOrdinal, tasks);
            }
        }

    }


    public static class ScheduleItemHolder extends RecyclerView.ViewHolder {
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
                                                                        View.OnClickListener {

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
            if (isChecked) {
                ViewUtil.addClearCenterLine(calendar_item_title);
                calendar_item_title.setTextColor(Color.GRAY);
                mAsyncExpandableListView.getHeader(mGroupOrdinal).setIsFinish(true);
                mAsyncExpandableListView.getHeader(mGroupOrdinal).setFinished_datetime(TimeUtil.formatGMTDate(new Date()));
            } else {
                ViewUtil.removeLine(calendar_item_title);
                calendar_item_title.setTextColor(Color.BLACK);
                mAsyncExpandableListView.getHeader(mGroupOrdinal).setIsFinish(false);
                mAsyncExpandableListView.getHeader(mGroupOrdinal).setFinished_datetime(null);
            }
            Task task = mAsyncExpandableListView.getHeader(mGroupOrdinal);
            Log.e(TAG, "onCheckedChanged() --> header task -->" + task.toString());
            RetrofitHelper.getTaskService().putTaskByUrl(task.getUrl(), task)
                    .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                    .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                    .doOnNext(new Action1<Task>() {
                        @Override
                        public void call(Task task) {
                            // TODO 保存任务信息到本地, java.lang.RuntimeException: Error saving model
                            // TODO abort at 32 in [INSERT INTO `Schedules` (`begin_datetime` ,`content` ,`created_datetime` ,`Cycle` ,`Days` ,`Dose` ,`end_datetime` ,`finished_datetime` ,`is_all_day` ,`is_finished` ,`label` ,`owner` ,`Scanned` ...
//                            DB.schedules().saveAndFireEvent(ModelUtil.toDBTask(task));
//                            Log.e(TAG, "保存任务信息到本地 --> " + task.toString());
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
                            ToastUtil.show("失败");
                            Log.e(TAG, "失败 --> " + e.toString());
                        }

                        @Override
                        public void onNext(Task task) {
                            //请求成功
                            ToastUtil.show("同步成功");
                            Log.e(TAG, "同步成功 --> " + task.toString());
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
                    Intent intent2DialogActivity = new Intent(context, DialogActivity.class);
                    intent2DialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2DialogActivity.putExtra(DialogActivity.TO_SAVE_STR, mAsyncExpandableListView.getHeader(mGroupOrdinal).getContent());
                    startActivity(intent2DialogActivity);
                    ToastUtil.show("to Create a task");
                    break;
            }
        }
    }
    //</内部类>---尽量少用---------------------------------------------------------------------------
}
