package com.time.cat.component.activity.main.schedules;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ldf.calendar.Utils;
import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.MonthPager;
import com.time.cat.R;
import com.time.cat.ThemeSystem.utils.ThemeUtils;
import com.time.cat.component.activity.main.listener.OnDateChangeListener;
import com.time.cat.component.activity.main.listener.OnViewClickListener;
import com.time.cat.component.base.BaseFragment;
import com.time.cat.mvp.model.Task;
import com.time.cat.mvp.presenter.FragmentPresenter;
import com.time.cat.mvp.view.Label_n_Tag.SegmentedRadioGroup;
import com.time.cat.mvp.view.Label_n_Tag.TagCloudView;
import com.time.cat.mvp.view.SmoothCheckBox;
import com.time.cat.mvp.view.asyncExpandableListView.AsyncExpandableListView;
import com.time.cat.mvp.view.asyncExpandableListView.AsyncExpandableListViewCallbacks;
import com.time.cat.mvp.view.asyncExpandableListView.AsyncHeaderViewHolder;
import com.time.cat.mvp.view.asyncExpandableListView.CollectionView;
import com.time.cat.mvp.view.calendar.CustomDayView;
import com.time.cat.mvp.view.calendar.ThemeDayView;
import com.time.cat.mvp.view.progressButton.CircularProgressButton;
import com.time.cat.util.ToastUtil;
import com.time.cat.util.ViewUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author dlink
 * @date 2018/1/24
 * @discription SchedulesFragment
 */
@SuppressLint("SetTextI18n")
public class SchedulesFragment extends BaseFragment implements FragmentPresenter, OnSelectDateListener, View.OnClickListener, OnViewClickListener, AsyncExpandableListViewCallbacks<Task, Task> {
    @SuppressWarnings("unused")
    private static final String TAG = "SchedulesFragment";
    private static final int[] labelColor = new int[]{
//            getResources().getColor(R.color.label_important_urgent_color),
//            getResources().getColor(R.color.label_important_not_urgent_color),
//            getResources().getColor(R.color.label_not_important_urgent_color),
//            getResources().getColor(R.color.label_not_important_not_urgent_color),
            Color.parseColor("#f44336"), Color.parseColor("#ff8700"), Color.parseColor("#2196f3"), Color.parseColor("#4caf50"),};
    private static final int[] checkIds = new int[]{R.id.label_important_urgent, R.id.label_important_not_urgent, R.id.label_not_important_urgent, R.id.label_not_important_not_urgent,};
    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------
    private CoordinatorLayout content;
    //</生命周期>------------------------------------------------------------------------------------
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
    //<回调接口>-------------------------------------------------------------------------------------
    private OnDateChangeListener mDateChangeListener;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedules, container, false);
        context = getContext();
        content = view.findViewById(R.id.content);
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
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>-----------------------------

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
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------

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
    }

    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-----------------------------------------
    @Override
    public void initData() {//必须调用
        initCurrentDate();
        initExpandableListViewData();
//        if (inventory.getTotalItemCount() <= 0) {
//            mAsyncExpandableListView.setVisibility(View.GONE);
//            empty.setVisibility(View.VISIBLE);
//        }
//        else {
//            mAsyncExpandableListView.setVisibility(View.VISIBLE);
//            empty.setVisibility(View.GONE);
//        }
    }
    //-//</View.OnClickListener>---------------------------------------------------------------------

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

        for (int i = 0; i < 5; i++) {
            CollectionView.InventoryGroup<Task, Task> group_i = inventory.newGroup(i); // groupOrdinal is the smallest, displayed first
            String[] titles = getResources().getStringArray(R.array.titles);
            Task task = new Task();
            task.setLabel(i % 4);
            task.setTitle(titles[i]);
            task.setIsFinish(i % 3 == 0);
            task.setCreateTime(new Date(2018 - 1900, 0, i));//month = 0 是 1 月
            task.setAllDay(i % 3 == 0);


            group_i.setHeaderItem(task);
        }

        mAsyncExpandableListView.updateInventory(inventory);
    }

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

    //-//<AsyncExpandableListViewCallbacks>---------------------------------------------------------------------
    @Override
    public void onStartLoadingGroup(int groupOrdinal) {
        new LoadDataTask(groupOrdinal, mAsyncExpandableListView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        long during = today.getTime() - headerItem.getCreateTime().getTime();
        long day = during / (1000 * 60 * 60 * 24);
//        Log.e(TAG, during + getString(R.string.calendar_delay) + day + getString(R.string.calendar_day));
        scheduleHeaderViewHolder.getCalendarItemDelay().setText(day >= 1 ? getString(R.string.calendar_delay) + day + getString(R.string.calendar_day) : "");

        onBindCollectionHeaderView = false;
    }
    //-//</AsyncExpandableListViewCallbacks>---------------------------------------------------------------------

    @Override
    public void bindCollectionItemView(Context context, RecyclerView.ViewHolder holder, int groupOrdinal, Task item) {
        ScheduleItemHolder scheduleItemHolder = (ScheduleItemHolder) holder;
        scheduleItemHolder.getTextViewTitle().setText(item.getTitle());
        scheduleItemHolder.getTextViewContent().setText(item.getContent());
        scheduleItemHolder.getTagCloudView().setTags(item.getTags());
        scheduleItemHolder.getLabelGroup().check(checkIds[item.getLabel()]);
    }

    //-//<MainActivity.OnViewClickListener>---------------------------------------------------------------------
    @Override
    public void onViewTodayClick() {
        refreshMonthPager();
    }

    @Override
    public void onViewChangeMarkThemeClick() {
        refreshSelectBackground();
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
    }
    //-//</MainActivity.OnViewClickListener>---------------------------------------------------------------------

    private void refreshSelectBackground() {
        ThemeDayView themeDayView = new ThemeDayView(context, R.layout.view_calendar_custom_day_focus);
        calendarAdapter.setCustomDayRenderer(themeDayView);
        calendarAdapter.notifyDataSetChanged();
        calendarAdapter.notifyDataChanged(new CalendarDate());
    }

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
    private static class LoadDataTask extends AsyncTask<Void, Void, Void> {

        private final int mGroupOrdinal;
        private WeakReference<AsyncExpandableListView<Task, Task>> listviewRef = null;

        public LoadDataTask(int groupOrdinal, AsyncExpandableListView<Task, Task> listview) {
            mGroupOrdinal = groupOrdinal;
            listviewRef = new WeakReference<>(listview);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            List<Task> items = new ArrayList<>();
            Task task = new Task();
            task.setTitle("Lawyers meet voluntary pro bono target for first time since 2013");
            task.setContent("A voluntary target for the amount of pro bono work done by Australian lawyers has been met for the first time since 2013. Key points: The Australian Pro Bono Centre's asks lawyers to do 35 hours of free community work a year; Pro bono services can help ...\n");
            task.setLabel(Task.LABEL_IMPORTANT_NOT_URGENT);
            List<String> tags = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                tags.add("标签" + i);
            }
            task.setTags(tags);

            items.add(task);
//子任务
//            task = new Task();
//            task.setTitle("子任务标题");
//            task.setContent("子任务内容");
//            items.add(task);

            if (listviewRef.get() != null) {
                listviewRef.get().onFinishLoadingGroup(mGroupOrdinal, items);
            }
        }

    }

    public static class ScheduleItemHolder extends RecyclerView.ViewHolder implements TagCloudView.OnTagClickListener, RadioGroup.OnCheckedChangeListener {
        private static final String TAG = "ScheduleItemHolder";

        private final TextView tvTitle;
        private final TextView tvContent;
        private final TagCloudView tagCloudView;
        private final SegmentedRadioGroup label_group;
        private final CircularProgressButton circular_progress_btn;

        public ScheduleItemHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.title);
            tvContent = v.findViewById(R.id.description);
            label_group = v.findViewById(R.id.label_group);
            tagCloudView = v.findViewById(R.id.tag_cloud_view);
            circular_progress_btn = v.findViewById(R.id.circular_progress_btn);
            circular_progress_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (circular_progress_btn.getProgress() == 0) {
                        simulateErrorProgress(circular_progress_btn);
                    } else {
                        circular_progress_btn.setProgress(0);
                    }
                }
            });

            tagCloudView.setOnTagClickListener(this);
            tagCloudView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, "onClick tagCloudView");
                }
            });
        }

        public TextView getTextViewTitle() {
            return tvTitle;
        }

        public TextView getTextViewContent() {
            return tvContent;
        }

        public TagCloudView getTagCloudView() {
            return tagCloudView;
        }

        public SegmentedRadioGroup getLabelGroup() {
            return label_group;
        }

        private void simulateSuccessProgress(final CircularProgressButton button) {
            ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 100);
            widthAnimation.setDuration(1500);
            widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    button.setProgress(value);
                }
            });
            widthAnimation.start();
        }

        private void simulateErrorProgress(final CircularProgressButton button) {
            ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 99);
            widthAnimation.setDuration(1500);
            widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    button.setProgress(value);
                    if (value == 99) {
                        button.setProgress(-1);
                    }
                }
            });
            widthAnimation.start();
        }

        //-//<TagCloudView.OnTagClickListener>------------------------------------------------------
        @Override
        public void onTagClick(int position) {
            if (position == -1) {
                Log.e(TAG, "onClick tagCloudView at --> 点击末尾文字");
            } else {
                Log.e(TAG, "onClick tagCloudView at --> 点击 position" + position);
            }
        }
        //-//</TagCloudView.OnTagClickListener>------------------------------------------------------

        //-//<RadioGroup.OnCheckedChangeListener>------------------------------------------------------
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.label_important_urgent:
                case R.id.label_important_not_urgent:
                case R.id.label_not_important_urgent:
                case R.id.label_not_important_not_urgent:
            }
        }
        //-//</RadioGroup.OnCheckedChangeListener>------------------------------------------------------
    }

    public class ScheduleHeaderViewHolder extends AsyncHeaderViewHolder implements AsyncExpandableListView.OnGroupStateChangeListener, SmoothCheckBox.OnCheckedChangeListener, View.OnClickListener {

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
//            Log.e(TAG, String.valueOf(onBindCollectionHeaderView));
            if (isChecked) {
                ViewUtil.addClearCenterLine(calendar_item_title);
                calendar_item_title.setTextColor(Color.GRAY);
            } else {
                ViewUtil.removeLine(calendar_item_title);
                calendar_item_title.setTextColor(Color.BLACK);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.calendar_item_rl_container:
                    mAsyncExpandableListView.onGroupClicked(mGroupOrdinal);
                    break;
                case R.id.calendar_item_rl_content_container:
                    ToastUtil.show("to Create a task");
                    break;
            }
        }
    }
    //</内部类>---尽量少用---------------------------------------------------------------------------
}
