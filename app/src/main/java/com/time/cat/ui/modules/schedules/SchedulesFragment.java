package com.time.cat.ui.modules.schedules;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.time.cat.R;
import com.time.cat.data.async.LoadContentDataTask;
import com.time.cat.data.database.DB;
import com.time.cat.data.database.ScheduleDao;
import com.time.cat.data.model.APImodel.Task;
import com.time.cat.data.model.APImodel.User;
import com.time.cat.data.model.Converter;
import com.time.cat.data.model.DBmodel.DBTask;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.data.network.RetrofitHelper;
import com.time.cat.ui.modules.main.listener.OnDateChangeListener;
import com.time.cat.ui.modules.main.listener.OnScheduleViewClickListener;
import com.time.cat.ui.adapter.viewholder.ScheduleItemHolder;
import com.time.cat.ui.base.BaseFragment;
import com.time.cat.ui.base.mvp.presenter.FragmentPresenter;
import com.time.cat.ui.modules.operate.InfoOperationActivity;
import com.time.cat.ui.widgets.SmoothCheckBox;
import com.time.cat.ui.widgets.asyncExpandableListView.AsyncExpandableListView;
import com.time.cat.ui.widgets.asyncExpandableListView.AsyncExpandableListViewCallbacks;
import com.time.cat.ui.widgets.asyncExpandableListView.AsyncHeaderViewHolder;
import com.time.cat.ui.widgets.asyncExpandableListView.CollectionView;
import com.time.cat.util.date.DateUtil;
import com.time.cat.util.override.LogUtil;
import com.time.cat.util.override.ToastUtil;
import com.time.cat.util.string.TimeUtil;
import com.time.cat.util.view.ViewUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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
                                                    CalendarView.OnDateSelectedListener,
                                                    CalendarView.OnYearChangeListener,
                                                    OnScheduleViewClickListener,
                                                    AsyncExpandableListViewCallbacks<DBTask, DBTask> {

    private ProgressBar progressBar;
    private CalendarLayout mCalendarLayout;
    private CalendarView mCalendarView;
    private int mYear;
    private Calendar currentCalendar;
    private Calendar today;
    private AsyncExpandableListView<DBTask, DBTask> mAsyncExpandableListView;
    private CollectionView.Inventory<DBTask, DBTask> inventory;
    private Context context;
    private DBUser dbUser;
    private Handler handler = new Handler();


    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentConfig(false, true);
    }
    //</生命周期>------------------------------------------------------------------------------------





    //<editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码">------------------------------------
    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedules, container, false);
        context = getContext();
        progressBar = view.findViewById(R.id.progress_bar);
        mCalendarView = view.findViewById(R.id.calendarView);
        mCalendarLayout = view.findViewById(R.id.calendarLayout);


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
    }
    //</editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码">)>--------------------------------






    //<editor-fold desc="Data数据区--存在数据获取或处理代码，但不存在事件监听代码">--------------------------------------------
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
                mCalendarLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }, 500);

    }

    /**
     * 初始化currentDate
     */
    private void initCurrentDate() {
        today = mCalendarView.getSelectedCalendar();
        currentCalendar = today;
        mYear = mCalendarView.getCurYear();
        List<Calendar> schemes = new ArrayList<>();
        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();

        schemes.add(getSchemeCalendar(year, month, 3, 0xFF40db25, "假"));
        schemes.add(getSchemeCalendar(year, month, 6, 0xFFe69138, "事"));
        schemes.add(getSchemeCalendar(year, month, 10, 0xFFdf1356, "议"));
        schemes.add(getSchemeCalendar(year, month, 11, 0xFFedc56d, "记"));
        schemes.add(getSchemeCalendar(year, month, 14, 0xFFedc56d, "记"));
        schemes.add(getSchemeCalendar(year, month, 15, 0xFFaacc44, "假"));
        schemes.add(getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记"));
        schemes.add(getSchemeCalendar(year, month, 25, 0xFF13acf0, "假"));
        schemes.add(getSchemeCalendar(year, month, 27, 0xFF13acf0, "多"));
        mCalendarView.setSchemeDate(schemes);
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
//                        LogUtil.i(dbUser.toString());
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
//                        LogUtil.e(e.toString());
//                        ToastUtil.show("更新用户信息失败");
                    }

                    @Override
                    public void onNext(User user) {
                        //请求成功
//                        Log.i(TAG, "更新用户信息成功 --> " + user.toString());
//                        ToastUtil.show("更新用户信息成功");
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
    }

    public void refreshData() {

        if (mCalendarLayout != null) {
            mCalendarLayout.setVisibility(View.GONE);
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
//                        LogUtil.e(e.toString());
//                        ToastUtil.show("更新用户信息失败");
                    }

                    @Override
                    public void onNext(User user) {
                        //请求成功
//                        Log.i(TAG, "更新用户信息成功 --> " + user.toString());
//                        ToastUtil.show("更新用户信息成功");
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
    }


    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800, "假");
        calendar.addScheme(0xFF008800, "节");
        return calendar;
    }

    //</editor-fold desc="Data数据区--存在数据获取或处理代码，但不存在事件监听代码">-------------------------------------------







    //<editor-fold desc="Event事件区--只要存在事件监听代码就是">-----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用
        mCalendarView.setOnDateSelectedListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mAsyncExpandableListView.setCallbacks(this);
    }



    //-//<AsyncExpandableListViewCallbacks>---------------------------------------------------------
    @Override
    public void onStartLoadingGroup(int groupOrdinal) {
        new LoadContentDataTask(groupOrdinal, mAsyncExpandableListView)
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
        ScheduleHeaderViewHolder scheduleHeaderViewHolder = (ScheduleHeaderViewHolder) holder;
        scheduleHeaderViewHolder.getCalendarItemCheckBox().setUncheckedStrokeColor(DBTask.labelColor[headerItem.getLabel()]);
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




    //-//<MainActivity.OnScheduleViewClickListener>-------------------------------------------------
    @Override
    public void onViewTodayClick() {
        mCalendarView.scrollToCurrent();
        if (mDateChangeListener != null) {
            if (today == null) {
                Calendar calendar = getSchemeCalendar(DateUtil.getYear(), DateUtil.getMonth(), DateUtil.getDay(), 0xFF40db25, "假");
                mDateChangeListener.onDateChange(calendar);
            } else {
                mDateChangeListener.onDateChange(today);
            }
        }
    }

    @Override
    public void onViewRefreshClick() {
        refreshData();
    }

    @Override
    public void onViewExpand() {
        if (!mCalendarLayout.isExpand()) {
            mCalendarView.showYearSelectLayout(mYear);
            return;
        }
        mCalendarView.showYearSelectLayout(mYear);
    }
    //-//</MainActivity.OnScheduleViewClickListener>------------------------------------------------





    //-//<CalendarView>---------------------------------------------------------------------
    @Override
    public void onDateSelected(Calendar calendar, boolean isClick) {
        currentCalendar = calendar;
        if (mDateChangeListener != null) {
            mDateChangeListener.onDateChange(calendar);
        }
        mYear = calendar.getYear();
        if (dbUser != null) refreshExpandableListViewData();
    }

    @Override
    public void onYearChange(int year) {
//        mTextMonthDay.setText(String.valueOf(year));
    }
    //-//</CalendarView>--------------------------------------------------------------------

    //</editor-fold desc="Event事件区--只要存在事件监听代码就是">----------------------------------------------------------






    //<回调接口>-------------------------------------------------------------------------------------
    private OnDateChangeListener mDateChangeListener;
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
//                                LogUtil.e("count[0] == "+count[0] + " --> 失败 --> " + e.toString());
                                count[0] -= 1;
                            }

                            @Override
                            public void onNext(Task task) {
                                //请求成功
                                count[0] -= 1;
//                                ToastUtil.show("成功获取任务");
//                                Log.i(TAG, "请求成功 --> count[0] == " + count[0] + " --> " + task.toString());
                            }
                        });
            }
            List<DBTask> taskList = DB.schedules().findAll();
            Date date = TimeUtil.transferCalendarDate(currentCalendar);
            tasks = ScheduleDao.sort(ScheduleDao.DBTaskFilter(taskList, date));
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
            //文字抗锯齿
            calendar_item_title.getPaint().setAntiAlias(true);
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
//            Log.i(TAG, "onCheckedChanged() --> header task -->" + task.toString());
            DB.schedules().safeSaveDBTask(task);

            RetrofitHelper.getTaskService().putTaskByUrl(task.getUrl(), Converter.toTask(task))
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
//                            ToastUtil.show("网络请求失败，已保存到本地");
//                            LogUtil.e("网络请求失败 --> " + e.toString());
                        }

                        @Override
                        public void onNext(Task task) {
                            //请求成功
//                            ToastUtil.show("同步成功");
//                            LogUtil.e("同步成功 --> " + task.toString());
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
                    Intent intent2DialogActivity = new Intent(context, InfoOperationActivity.class);
                    intent2DialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2DialogActivity.putExtra(InfoOperationActivity.TO_SAVE_STR, task.getContent());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(InfoOperationActivity.TO_UPDATE_TASK, task);
                    intent2DialogActivity.putExtras(bundle);
                    startActivity(intent2DialogActivity);
                    ToastUtil.i("编辑任务");
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
//                                LogUtil.e("onLongClick() --> 确定删除 task -->" + task.toString());
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
//                                                ToastUtil.show("删除操作同步失败");
//                                                LogUtil.e("删除操作同步失败 --> " + e.toString());
                                            }

                                            @Override
                                            public void onNext(Task task) {
                                                //请求成功
//                                                ToastUtil.show("删除成功");
//                                                LogUtil.e("删除成功 --> " + task.toString());
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
