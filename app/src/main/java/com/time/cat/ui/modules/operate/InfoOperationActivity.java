package com.time.cat.ui.modules.operate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.contrarywind.view.WheelView;
import com.time.cat.R;
import com.time.cat.data.Constants;
import com.time.cat.data.SharedPreferenceHelper;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.APImodel.Note;
import com.time.cat.data.model.APImodel.Routine;
import com.time.cat.data.model.APImodel.Tag;
import com.time.cat.data.model.APImodel.Task;
import com.time.cat.data.model.Converter;
import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.data.model.DBmodel.DBRoutine;
import com.time.cat.data.model.DBmodel.DBSubPlan;
import com.time.cat.data.model.DBmodel.DBTask;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.data.network.ConstantURL;
import com.time.cat.data.network.RetrofitHelper;
import com.time.cat.ui.base.mvp.BaseActivity;
import com.time.cat.ui.base.mvp.presenter.ActivityPresenter;
import com.time.cat.ui.modules.activity.TimeCatActivity;
import com.time.cat.ui.modules.activity.WebActivity;
import com.time.cat.ui.modules.editor.EditorActivity;
import com.time.cat.ui.widgets.emotion.adapter.HorizontalRecyclerviewAdapter;
import com.time.cat.ui.widgets.emotion.adapter.NoHorizontalScrollerVPAdapter;
import com.time.cat.ui.widgets.emotion.fragment.EmotionComplateFragment;
import com.time.cat.ui.widgets.emotion.fragment.Fragment1;
import com.time.cat.ui.widgets.emotion.fragment.FragmentFactory;
import com.time.cat.ui.widgets.emotion.fragment.TagFragment;
import com.time.cat.ui.widgets.emotion.model.ImageModel;
import com.time.cat.ui.widgets.keyboardManager.SmartKeyboardManager;
import com.time.cat.ui.widgets.richText.TEditText;
import com.time.cat.ui.widgets.viewpaper.NoHorizontalScrollerViewPager;
import com.time.cat.util.SearchEngineUtil;
import com.time.cat.util.UrlCountUtil;
import com.time.cat.util.listener.GlobalOnItemClickManager;
import com.time.cat.util.override.LogUtil;
import com.time.cat.util.override.ToastUtil;
import com.time.cat.util.string.StringUtil;
import com.time.cat.util.string.TimeUtil;
import com.time.cat.util.view.EmotionUtil;
import com.time.cat.util.view.ViewUtil;
import com.timecat.commonjar.contentProvider.SPHelper;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author dlink
 * @date 2018/2/14
 * @discription 信息操作页面,包括创建、修改、转化,用activity实现dialog
 */
public class InfoOperationActivity extends BaseActivity<InfoOperationMVP.View, InfoOperationPresenter>
        implements InfoOperationMVP.View,
                   ActivityPresenter,
                   View.OnClickListener,
                   TagFragment.OnTagAddListener {
//public class InfoOperationActivity extends BaseActivity implements ActivityPresenter, View.OnClickListener{

    public static final String TO_SAVE_STR = "to_save_str";
    public static final String TO_UPDATE_TASK = "to_update_task";
    public static final String TO_UPDATE_NOTE = "to_update_note";
    public static final String TO_UPDATE_ROUTINE = "to_update_ROUTINE";
    public static final String TO_ATTACH_NOTE = "to_attach_note";
    public static final String TO_ATTACH_PLAN = "to_attach_plan";
    public static final String TO_ATTACH_SUBPLAN = "to_attach_subplan";

    //<editor-fold desc="启动方法">-------------------------------------------------------------------------------------
    /**
     * 启动这个Activity的Intent
     *
     * @param context 　上下文
     *
     * @return 返回intent实例
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, InfoOperationActivity.class);
    }

    @Override
    protected int layout() {
        return R.layout.activity_dialog;
    }

    @Override
    protected boolean canBack() {
        return false;
    }

    public Activity getActivity() {
        return this;
    }
    //</editor-fold desc="启动方法">------------------------------------------------------------------------------------


    //<editor-fold desc="生命周期">-------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.background_material_light_1);

        //<功能归类分区方法，必须调用>-----------------------------------------------------------------
        initView();
        initData();
        initEvent();
        //</功能归类分区方法，必须调用>----------------------------------------------------------------
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    //</editor-fold desc="生命周期">------------------------------------------------------------------------------------


    //<editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码">--------------------------------
    private TEditText dialog_add_task_et_title;
    private TEditText dialog_add_task_et_content;
    private ImageView to_editor;

    private TextView dialog_add_task_tv_important_urgent;
    private TextView dialog_add_task_tv_date;
    private TextView dialog_add_task_tv_time;
    private TextView dialog_add_task_tv_remind;
    private TextView dialog_add_task_tv_tag;

    private TextView dialog_add_task_type_note;
    private TextView dialog_add_task_type_task;
    private TextView dialog_add_task_type_clock;
    private ImageView dialog_add_task_footer_iv_timecat;
    private ImageView dialog_add_task_footer_iv_translate;
    private ImageView dialog_add_task_footer_iv_search;
    private Button dialog_add_task_footer_bt_submit;

    private LinearLayout dialog_add_task_ll_content;

    // 重要紧急选择面板
    private LinearLayout dialog_add_task_select_ll_important_urgent;
    private TextView select_tv_important_urgent;
    private TextView  select_tv_important_not_urgent;
    private TextView  select_tv_not_important_urgent;
    private TextView  select_tv_not_important_not_urgent;

    // date选择面板
    private GridView dialog_add_task_select_gv_date;

    // time选择面板
    private LinearLayout dialog_add_task_select_ll_time;
    private TextView select_tv_all_day;
    private LinearLayout select_ll_start;
    private TextView select_tv_start;
    private TextView select_tv_start_time;
    private LinearLayout select_ll_end;
    private TextView select_tv_end;
    private TextView select_tv_end_time;
    private FrameLayout time_picker_fragment;
    private TimePickerView pvTime;

    // 提醒选择面板
    private GridView dialog_add_task_select_gv_remind;

    // 标签选择面板
    private LinearLayout dialog_add_task_select_ll_tag;

    List<Fragment> fragments = new ArrayList<>();
    //不可横向滚动的ViewPager
    private NoHorizontalScrollerViewPager viewPager;
    //当前被选中底部tab
    private static final String CURRENT_POSITION_FLAG = "CURRENT_POSITION_FLAG";
    private int CurrentPosition = 0;
    //底部水平tab
    private RecyclerView recyclerview_horizontal;
    private HorizontalRecyclerviewAdapter horizontalRecyclerviewAdapter;

    // 软键盘管理
    SmartKeyboardManager mSmartKeyboardManager;

    @Override
    public void initView() {//必须调用
        setWindow();

        dialog_add_task_et_title = findViewById(R.id.dialog_add_task_et_title);
        dialog_add_task_et_content = findViewById(R.id.dialog_add_task_et_content);
        to_editor = findViewById(R.id.to_editor);
        //获取焦点 光标出现
        dialog_add_task_et_content.setFocusable(true);
        dialog_add_task_et_content.setFocusableInTouchMode(true);
        dialog_add_task_et_content.requestFocus();

        // 这里给出个延迟弹出键盘，如果直接弹出键盘会和界面view渲染一起，体验不太好
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                ViewUtil.showInputMethod(dialog_add_task_et_content);
            }
        }, 256);

        dialog_add_task_tv_important_urgent = findViewById(R.id.dialog_add_task_tv_important_urgent);
        dialog_add_task_tv_date = findViewById(R.id.dialog_add_task_tv_date);
        dialog_add_task_tv_time = findViewById(R.id.dialog_add_task_tv_time);
        dialog_add_task_tv_remind = findViewById(R.id.dialog_add_task_tv_remind);
        dialog_add_task_tv_tag = findViewById(R.id.dialog_add_task_tv_tag);

        dialog_add_task_type_note = findViewById(R.id.dialog_add_task_type_note);
        dialog_add_task_type_task = findViewById(R.id.dialog_add_task_type_task);
        dialog_add_task_type_clock = findViewById(R.id.dialog_add_task_type_clock);

        dialog_add_task_footer_iv_timecat = findViewById(R.id.dialog_add_task_footer_iv_timecat);
        dialog_add_task_footer_iv_translate = findViewById(R.id.dialog_add_task_footer_iv_translate);
        dialog_add_task_footer_iv_search = findViewById(R.id.dialog_add_task_footer_iv_search);
        dialog_add_task_footer_bt_submit = findViewById(R.id.dialog_add_task_footer_bt_submit);

        dialog_add_task_ll_content = findViewById(R.id.dialog_add_task_ll_content);
        // 重要紧急选择面板
        dialog_add_task_select_ll_important_urgent = findViewById(R.id.dialog_add_task_select_ll_important_urgent);
        select_tv_important_urgent = findViewById(R.id.select_tv_important_urgent);
        select_tv_important_not_urgent = findViewById(R.id.select_tv_important_not_urgent);
        select_tv_not_important_urgent = findViewById(R.id.select_tv_not_important_urgent);
        select_tv_not_important_not_urgent = findViewById(R.id.select_tv_not_important_not_urgent);
        // date选择面板
        dialog_add_task_select_gv_date = findViewById(R.id.dialog_add_task_select_gv_date);
        // time选择面板
        dialog_add_task_select_ll_time = findViewById(R.id.dialog_add_task_select_ll_time);
        select_tv_all_day = findViewById(R.id.select_tv_all_day);
        select_ll_start = findViewById(R.id.select_ll_start);
        select_tv_start = findViewById(R.id.select_tv_start);
        select_tv_start_time = findViewById(R.id.select_tv_start_time);
        select_ll_end = findViewById(R.id.select_ll_end);
        select_tv_end = findViewById(R.id.select_tv_end);
        select_tv_end_time = findViewById(R.id.select_tv_end_time);
        time_picker_fragment = findViewById(R.id.time_picker_fragment);
        // 提醒选择面板
        dialog_add_task_select_gv_remind = findViewById(R.id.dialog_add_task_select_gv_remind);
        // 标签选择面板
        dialog_add_task_select_ll_tag = findViewById(R.id.dialog_add_task_select_ll_tag);
        viewPager = findViewById(R.id.select_vp_layout);
        recyclerview_horizontal = findViewById(R.id.select_rv_horizontal);

        setSelectImportantUrgentPanel();
        setSelectDatePanel();
        setSelectTimePanel();
        setSelectRemindPanel();
        setSelectTagPanel();
        setKeyboardManager();

    }

    /**
     * 设置窗口样式
     */
    private void setWindow() {
        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
        win.setAttributes(lp);
    }

    /**
     * 设置重要紧急选择面板
     */
    private void setSelectImportantUrgentPanel() {
        TextView[] select_tv_important_urgent_set = new TextView[] {
                select_tv_important_urgent,
                select_tv_important_not_urgent,
                select_tv_not_important_urgent,
                select_tv_not_important_not_urgent
        };
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            select_tv_important_urgent_set[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_add_task_tv_important_urgent.setText(label_str_set[finalI]);
                    dialog_add_task_tv_important_urgent.setTextColor(Color.parseColor(text_color_set[finalI]));
                    dialog_add_task_tv_important_urgent.setBackgroundColor(Color.parseColor(background_color_set[finalI]));

                    for (int j = 0; j < 4; j++) {
                        if (j == finalI) {
                            select_tv_important_urgent_set[j].setBackgroundColor(Color.parseColor(background_color_set[j]));
                        } else {
                            select_tv_important_urgent_set[j].setBackgroundColor(Color.parseColor("#ffffff"));
                        }
                    }

                    important_urgent_label = finalI;
//                    ToastUtil.show("important_urgent_label == " + finalI);
                }
            });
        }

    }

    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
    TimePickerView pickerView;
    CalendarView calendarView;
    int selectDateMoreType = 0;
    int selectDateRepeatType = 0;
    Duration repeatDuration;
    DateTime repeatBound;
    List<Calendar> calendarList;
    /**
     * 设置 date 选择面板
     */
    private void setSelectDatePanel() {
        String IMAGE_ITEM = "image_item";
        String TEXT_ITEM = "text_item";
        String[] arrText = new String[]{
                "今天", "明天", "后天",
                "下一周", "下个月", "其他"
        };
        int[] arrImages=new int[]{
                R.drawable.ic_alarm_black_48dp, R.drawable.ic_alarm_black_48dp, R.drawable.ic_alarm_black_48dp,
                R.drawable.ic_alarm_black_48dp, R.drawable.ic_alarm_black_48dp, R.drawable.ic_alarm_black_48dp
        };
        List<HashMap<String, Object>> list = new ArrayList<>();

        for (int i=0; i<6; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put(IMAGE_ITEM, arrImages[i]);
            map.put(TEXT_ITEM, arrText[i]);
            list.add(map);
        }

        SimpleAdapter saImageItems = new SimpleAdapter(this,
                list,
                R.layout.view_keyboard_date_item,
                new String[] { IMAGE_ITEM, TEXT_ITEM },
                new int[] { R.id.dialog_add_task_select_iv, R.id.dialog_add_task_select_tv });

        //<editor-fold desc="dialog view">
        View dialog = LayoutInflater.from(getActivity()).inflate(R.layout.view_keyboard_card, null);
        LinearLayout repeat_card = dialog.findViewById(R.id.repeat_card);
        TextView stop_at = dialog.findViewById(R.id.stop_at);
        TextView how_to_repeat = dialog.findViewById(R.id.how_to_repeat);
        FrameLayout repeat_time_picker_fragment = dialog.findViewById(R.id.repeat_time_picker_fragment);
        DateTime today = new DateTime();
        repeatBound = new DateTime();
        repeatDuration = new Duration(0L);
        calendarList = new ArrayList<>();
        pickerView = new TimePickerBuilder(this, null)
                .setLayoutRes(R.layout.view_keyboard_date_pickerview, v -> {
                    //do nothing
                })
                .setTimeSelectChangeListener(date -> {
                    stop_at.setText(formatDate.format(date));
                    repeatBound = repeatBound.withDate(new LocalDate(date));
                })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "") //设置空字符串以隐藏单位提示   hide label
                .setDividerColor(Color.DKGRAY)
                .setContentTextSize(18)
                .setRangDate(today.toGregorianCalendar(), today.plusYears(50).toGregorianCalendar())
                .setDecorView(repeat_time_picker_fragment)//非dialog模式下,设置ViewGroup, pickerView将会添加到这个ViewGroup中
                .setLineSpacingMultiplier((float) 2)
                .setOutSideCancelable(false)
                .build();
        pickerView.show(stop_at, false);
        ((RadioGroup) dialog.findViewById(R.id.rg_selection_repeat_type)).setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_repeat_day:
                    how_to_repeat.setText("每天");
                    selectDateRepeatType = 0;
                    break;
                case R.id.rb_repeat_week:
                    selectDateRepeatType = 1;
                    how_to_repeat.setText("每周");
                    break;
                case R.id.rb_repeat_month:
                    selectDateRepeatType = 2;
                    how_to_repeat.setText("每月");
                    break;
                case R.id.rb_repeat_year:
                    how_to_repeat.setText("每年");
                    selectDateRepeatType = 3;
                    break;
            }
        });
        calendarView = dialog.findViewById(R.id.view_keyboard_calendar);
        calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);
        ((RadioGroup) dialog.findViewById(R.id.rg_selection_type)).setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_single:
                    repeat_card.setVisibility(View.GONE);
                    calendarView.setVisibility(View.VISIBLE);
                    calendarView.setSelectionType(SelectionType.SINGLE);
                    selectDateMoreType = 0;
                    break;
                case R.id.rb_multiple:
                    repeat_card.setVisibility(View.GONE);
                    calendarView.setVisibility(View.VISIBLE);
                    calendarView.setSelectionType(SelectionType.MULTIPLE);
                    selectDateMoreType = 1;
                    break;
                case R.id.rb_range:
                    repeat_card.setVisibility(View.GONE);
                    calendarView.setVisibility(View.VISIBLE);
                    calendarView.setSelectionType(SelectionType.RANGE);
                    selectDateMoreType = 2;
                    break;
                case R.id.rb_repeat:
                    repeat_card.setVisibility(View.VISIBLE);
                    calendarView.setVisibility(View.GONE);
                    selectDateMoreType = 3;
                    break;
            }
        });
        //</editor-fold desc="dialog view">

        dialog_add_task_tv_date.postDelayed(()->dialog_add_task_tv_date.setText(startDateTime.toString("MM:dd")), 50);
        // 设置GridView的adapter。GridView继承于AbsListView。
        dialog_add_task_select_gv_date.setAdapter(saImageItems);
        dialog_add_task_select_gv_date.setOnItemClickListener((parent, view, position, id) -> {
            // 根据元素位置获取对应的值
            HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);
            String itemText=(String)item.get(TEXT_ITEM);
            if (position < arrText.length - 1) dialog_add_task_tv_date.setText(itemText);
            DateTime date = new DateTime();
            switch (position) {
                case 0:/*今天*/ break;
                case 1:/*明天*/ date = date.plusDays(1); break;
                case 2:/*后天*/ date = date.plusDays(2); break;
                case 3:/*下一周*/ date = date.plusWeeks(1); break;
                case 4:/*下个月*/ date = date.plusMonths(1); break;
                case 5:/*其他*/
                    MaterialDialog.OnDismissListener dismissListener = dialogToDismiss -> {
                        switch (selectDateMoreType) {
                            case 0://单日
                            case 1://多日
                            case 2://起止
                                calendarList = calendarView.getSelectedDates();
                                break;
                            case 3://重复
                                List<Calendar> calendarList1 = new ArrayList<>();
                                switch (selectDateRepeatType) {
                                    case 0:
                                        //计算区间天数
                                        Period pDays = new Period(today, repeatBound, PeriodType.days());
                                        for (int i=0; i<pDays.getDays(); i++) {
                                            calendarList1.add(today.plusDays(i).toCalendar(Locale.CHINA));
                                        }
                                        break;
                                    case 1:
                                        //每星期
                                        Period pWeeks = new Period(today, repeatBound, PeriodType.weeks());
                                        for (int i=0; i<pWeeks.getWeeks(); i++) {
                                            calendarList1.add(today.plusWeeks(i).toCalendar(Locale.CHINA));
                                        }
                                        break;
                                    case 2:
                                        //每月
                                        Period pMonths = new Period(today, repeatBound, PeriodType.months());
                                        for (int i=0; i<pMonths.getMonths(); i++) {
                                            calendarList1.add(today.plusMonths(i).toCalendar(Locale.CHINA));
                                        }
                                        break;
                                    case 3:
                                        //每年
                                        Period pYears = new Period(today, repeatBound, PeriodType.years());
                                        for (int i=0; i<pYears.getYears(); i++) {
                                            calendarList1.add(today.plusYears(i).toCalendar(Locale.CHINA));
                                        }
                                        break;
                                }
                                calendarList = calendarList1;
                                break;
                        }
                    };
                    new MaterialDialog.Builder(InfoOperationActivity.this)
                            .customView(dialog, false)
                            .dismissListener(dismissListener).show();
                    break;
            }
            startDateTime = startDateTime.withYear(date.getYear())
                    .withMonthOfYear(date.getMonthOfYear())
                    .withDayOfMonth(date.getDayOfMonth());
            endDateTime = endDateTime.withYear(date.getYear())
                    .withMonthOfYear(date.getMonthOfYear())
                    .withDayOfMonth(date.getDayOfMonth());
        });
    }

    /**
     * 设置time选择面板
     */
    private void setSelectTimePanel() {
        DateTime dateTime = new DateTime();
        //时间选择器
        pvTime = new TimePickerBuilder(this, (date, v) -> {
            //选中事件回调
            // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
            ((TextView) v).setText(format.format(date));
            if (is_setting_start_time) {
                startDateTime = startDateTime.withHourOfDay(date.getHours()).withMinuteOfHour(date.getMinutes());
            } else if (is_setting_end_time) {
                endDateTime = endDateTime.withHourOfDay(date.getHours()).withMinuteOfHour(date.getMinutes());
            }
        })
                .setLayoutRes(R.layout.view_keyboard_time_pickerview, v -> {
                    WheelView hour = v.findViewById(R.id.hour);
                    WheelView min = v.findViewById(R.id.min);
                    hour.setOnItemSelectedListener(index -> {
                        is_all_day = false;
                        pvTime.returnData();
                        dialog_add_task_tv_time.setText(select_tv_start_time.getText() + "-" + select_tv_end_time.getText());
                        if (is_setting_start_time) {
                            startDateTime = startDateTime.withHourOfDay(index);
                        } else if (is_setting_end_time) {
                            endDateTime = endDateTime.withHourOfDay(index);
                        }
                    });
                    min.setOnItemSelectedListener(index -> {
                        is_all_day = false;
                        pvTime.returnData();
                        dialog_add_task_tv_time.setText(select_tv_start_time.getText() + "-" + select_tv_end_time.getText());
                        if (is_setting_start_time) {
                            startDateTime = startDateTime.withMinuteOfHour(index);
                        } else if (is_setting_end_time) {
                            endDateTime = endDateTime.withMinuteOfHour(index);
                        }
                    });
                })
                .setType(new boolean[]{false, false, false, true, true, false})
                .setTimeSelectChangeListener(date -> {
                    pvTime.returnData();

                    if (is_setting_start_time) {
                        startDateTime = startDateTime.withHourOfDay(date.getHours()).withMinuteOfHour(date.getMinutes());
                    } else if (is_setting_end_time) {
                        endDateTime = endDateTime.withHourOfDay(date.getHours()).withMinuteOfHour(date.getMinutes());
                    }
                })
                .setLabel("", "", "", "", "", "") //设置空字符串以隐藏单位提示   hide label
                .setDividerColor(Color.DKGRAY)
                .setContentTextSize(24)
                .isDialog(false)
                .setOutSideCancelable(false)
                .setDecorView(time_picker_fragment)//非dialog模式下,设置ViewGroup, pickerView将会添加到这个ViewGroup中
                .isCyclic(true)
                .setLineSpacingMultiplier((float) 1.2)
                .build();
        pvTime.show(select_tv_start_time, false);
        select_ll_start.setOnClickListener(v -> {
            is_all_day = false;
            select_tv_start.setTextColor(getResources().getColor(R.color.blue));
            select_tv_start_time.setTextColor(getResources().getColor(R.color.black));
            select_tv_end.setTextColor(Color.parseColor("#3e000000"));
            select_tv_end_time.setTextColor(Color.parseColor("#3e000000"));
            dialog_add_task_tv_time.setText(select_tv_start_time.getText() + "-" + select_tv_end_time.getText());
            pvTime.show(select_tv_start_time, false);
            is_setting_start_time = true;
            is_setting_end_time = false;
        });
        select_ll_end.setOnClickListener(v -> {
            is_all_day = false;
            select_tv_start.setTextColor(Color.parseColor("#3e000000"));
            select_tv_start_time.setTextColor(Color.parseColor("#3e000000"));
            select_tv_end.setTextColor(getResources().getColor(R.color.blue));
            select_tv_end_time.setTextColor(getResources().getColor(R.color.black));
            dialog_add_task_tv_time.setText(select_tv_start_time.getText() + "-" + select_tv_end_time.getText());
            pvTime.show(select_tv_end_time, false);
            is_setting_start_time = false;
            is_setting_end_time = true;
        });
        select_tv_all_day.setOnClickListener(v -> {
            is_all_day = true;
            dialog_add_task_tv_time.setText("全天");
        });

    }

    /**
     * 设置 提醒 选择面板
     */
    private void setSelectRemindPanel() {
        String IMAGE_ITEM = "image_item";
        String TEXT_ITEM = "text_item";
        String[] arrText = new String[]{
                "不提醒", "开始时", "开始前5分钟",
                "开始前10分钟", "开始前15分钟", "开始前30分钟",
                "开始前40分钟", "开始前60分钟", "其他"
        };
        int[] arrImages=new int[]{
                R.drawable.ic_alarm_black_48dp, R.drawable.ic_alarm_black_48dp, R.drawable.ic_alarm_black_48dp,
                R.drawable.ic_alarm_black_48dp, R.drawable.ic_alarm_black_48dp, R.drawable.ic_alarm_black_48dp,
                R.drawable.ic_alarm_black_48dp, R.drawable.ic_alarm_black_48dp, R.drawable.ic_alarm_black_48dp
        };
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        for (int i=0; i<9; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(IMAGE_ITEM, arrImages[i]);
            map.put(TEXT_ITEM, arrText[i]);
            list.add(map);
        }

        SimpleAdapter saImageItems = new SimpleAdapter(this,
                list,
                R.layout.view_keyboard_date_item,
                new String[] { IMAGE_ITEM, TEXT_ITEM },
                new int[] { R.id.dialog_add_task_select_iv, R.id.dialog_add_task_select_tv });
        // 设置GridView的adapter。GridView继承于AbsListView。
        dialog_add_task_select_gv_remind.setAdapter(saImageItems);
        dialog_add_task_select_gv_remind.setOnItemClickListener((parent, view, position, id) -> {
            // 根据元素位置获取对应的值
            HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);

            String itemText=(String)item.get(TEXT_ITEM);
            Object object=item.get(IMAGE_ITEM);
            ToastUtil.i("You Select "+itemText);
            dialog_add_task_tv_remind.setText(itemText);
        });
    }

    /**
     * 设置 tag 选择面板
     */
    private void setSelectTagPanel() {
        replaceFragment();
        GlobalOnItemClickManager globalOnItemClickManager = GlobalOnItemClickManager.getInstance(getActivity());
        globalOnItemClickManager.attachToEditText(dialog_add_task_et_content);

        List<ImageModel> list = new ArrayList<>();
        for (int i = 0; i < fragments.size(); i++) {
            if (i == 0) {
                ImageModel model1 = new ImageModel();
                model1.icon = getResources().getDrawable(R.drawable.ic_emotion);
                model1.flag = "经典笑脸";
                model1.isSelected = true;
                list.add(model1);
            } else {
                ImageModel model = new ImageModel();
                model.icon = getResources().getDrawable(R.drawable.ic_plus);
                model.flag = "其他笑脸" + i;
                model.isSelected = false;
                list.add(model);
            }
        }

        //记录底部默认选中第一个
        CurrentPosition = 0;
        SharedPreferenceHelper.setInteger(getActivity(), CURRENT_POSITION_FLAG, CurrentPosition);

        //底部tab
        horizontalRecyclerviewAdapter = new HorizontalRecyclerviewAdapter(getActivity(), list);
        recyclerview_horizontal.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能
        recyclerview_horizontal.setAdapter(horizontalRecyclerviewAdapter);
        recyclerview_horizontal.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        //初始化recyclerview_horizontal监听器
        horizontalRecyclerviewAdapter.setOnClickItemListener(new HorizontalRecyclerviewAdapter.OnClickItemListener() {
            @Override
            public void onItemClick(View view, int position, List<ImageModel> datas) {
                //获取先前被点击tab
                int oldPosition = SharedPreferenceHelper.getInteger(getActivity(), CURRENT_POSITION_FLAG, 0);
                //修改背景颜色的标记
                datas.get(oldPosition).isSelected = false;
                //记录当前被选中tab下标
                CurrentPosition = position;
                datas.get(CurrentPosition).isSelected = true;
                SharedPreferenceHelper.setInteger(getActivity(), CURRENT_POSITION_FLAG, CurrentPosition);
                //通知更新，这里我们选择性更新就行了
                horizontalRecyclerviewAdapter.notifyItemChanged(oldPosition);
                horizontalRecyclerviewAdapter.notifyItemChanged(CurrentPosition);
                //viewpager界面切换
                viewPager.setCurrentItem(position, false);
            }

            @Override
            public void onItemLongClick(View view, int position, List<ImageModel> datas) {}
        });
    }

    private void replaceFragment() {
        //创建fragment的工厂类
        FragmentFactory factory = FragmentFactory.getSingleFactoryInstance();
        //创建修改实例
        EmotionComplateFragment f1 = (EmotionComplateFragment) factory.getFragment(EmotionUtil.EMOTION_CLASSIC_TYPE);
        fragments.add(f1);
        TagFragment f2 = new TagFragment();
        f2.setOnTagAddListener(this);
        fragments.add(f2);
        Bundle b;
        for (int i = 0; i < 7; i++) {
            b = new Bundle();
            b.putString("Interge", "Fragment-" + i);
            Fragment1 fg = Fragment1.newInstance(Fragment1.class, b);
            fragments.add(fg);
        }

        NoHorizontalScrollerVPAdapter adapter = new NoHorizontalScrollerVPAdapter(
                getSupportFragmentManager(), fragments
        );
        viewPager.setAdapter(adapter);
    }


    /**
     * 设置软键盘和选择面板的平滑交互
     */
    private void setKeyboardManager() {
        mSmartKeyboardManager = new SmartKeyboardManager.Builder(this)
                .setContentView(dialog_add_task_ll_content)
                .setEditText(dialog_add_task_et_content)
                .addKeyboard(dialog_add_task_tv_important_urgent, dialog_add_task_select_ll_important_urgent)
                .addKeyboard(dialog_add_task_tv_date, dialog_add_task_select_gv_date)
                .addKeyboard(dialog_add_task_tv_time, dialog_add_task_select_ll_time)
                .addKeyboard(dialog_add_task_tv_remind, dialog_add_task_select_gv_remind)
                .addKeyboard(dialog_add_task_tv_tag, dialog_add_task_select_ll_tag)
                .create();
    }
    //</editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码">)>-----------------------------


    //<editor-fold desc="Data数据区--存在数据获取或处理代码，但不存在事件监听代码">-----------------------------------------
    DBTask task_toUpdate;
    DBNote note_toUpdate;
    DBRoutine routine_toUpdate;
    DBSubPlan subPlan_toAttach;
    int important_urgent_label;
    private String title;
    private String content;
    //用户是否编辑了title，编辑即视为有自定义的需求，取用户自定义的title，不再同步content里的到title
    boolean isSelfEdit = false;
    boolean is_all_day = true;
    DateTime startDateTime;
    DateTime endDateTime;
    boolean is_setting_start_time;
    boolean is_setting_end_time;
    int repeat;
    String[] text_color_set = new String[]{"#f44336", "#ff9800", "#2196f3", "#4caf50"};
    String[] background_color_set = new String[]{"#50f44336", "#50ff9800", "#502196f3", "#504caf50"};
    String[] label_str_set = new String[] {"重要且紧急", "重要不紧急", "紧急不重要", "不重要不紧急",};

    @SuppressLint("SetTextI18n")
    @Override
    public void initData() {//必须调用
        task_toUpdate = (DBTask) getIntent().getSerializableExtra(TO_UPDATE_TASK);
        note_toUpdate = (DBNote) getIntent().getSerializableExtra(TO_UPDATE_NOTE);
        routine_toUpdate = (DBRoutine) getIntent().getSerializableExtra(TO_UPDATE_ROUTINE);
        subPlan_toAttach = (DBSubPlan) getIntent().getSerializableExtra(TO_ATTACH_SUBPLAN);

        important_urgent_label = 0;
        title = null;
        content = null;
        startDateTime = new DateTime();
        endDateTime = new DateTime();
        is_setting_start_time = true;
        is_setting_end_time = false;
        repeat = 0;
        type = Type.NOTE;
        initTextString();
        if (task_toUpdate != null) {
            refreshViewByTask(task_toUpdate);
        } else if (subPlan_toAttach != null) {
            refreshViewByAttach(subPlan_toAttach);
        }
        if (note_toUpdate != null) {
            refreshViewByNote(note_toUpdate);
        }
        if (routine_toUpdate != null) {
            refreshViewByRoutine(routine_toUpdate);
        }
        select_tv_start_time.setText(startDateTime.toString("HH:mm"));
        select_tv_end_time.setText(endDateTime.toString("HH:mm"));
    }

    private void initTextString() {
        Intent intent = getIntent();
        String str = null;
        try {
            str = intent.getStringExtra(TO_SAVE_STR);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(str)) {
            String action = intent.getAction();
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                    str = sharedText;
                }
            }
        } else {
            SPHelper.save(Constants.UNIVERSAL_SAVE_COTENT, str);
        }

        if (TextUtils.isEmpty(str)) {
            str = SPHelper.getString(Constants.UNIVERSAL_SAVE_COTENT, "");
        }



        title = str;
        content = str;
        dialog_add_task_et_title.setText(title);
        dialog_add_task_et_content.setText(content);
        dialog_add_task_et_content.setSelection(content.length());
        SPHelper.save(Constants.UNIVERSAL_SAVE_COTENT, str);
    }

    @SuppressLint("SetTextI18n")
    private void refreshViewByTask(DBTask task) {
        important_urgent_label = task.getLabel();
        title = task.getTitle();
        content = task.getContent();

        is_all_day = task.getIs_all_day();
        if (!is_all_day) {
            Date begin_datetime = TimeUtil.formatGMTDateStr(task.getBegin_datetime());
            Date end_datetime = TimeUtil.formatGMTDateStr(task.getEnd_datetime());
            if (begin_datetime!=null&&end_datetime!=null) {
                startDateTime = new DateTime(begin_datetime);
                endDateTime = new DateTime(end_datetime);
                dialog_add_task_tv_time.setText(startDateTime.toString("HH:mm") +"-"+ endDateTime.toString("HH:mm"));
            }
        } else {
            dialog_add_task_tv_time.setText("全天");
        }
        type = Type.TASK;
        dialog_add_task_type_task.postDelayed(() -> dialog_add_task_type_task.callOnClick(), 500);
        dialog_add_task_et_title.setText(title);
        dialog_add_task_et_content.setText(content);
        dialog_add_task_tv_important_urgent.setVisibility(View.VISIBLE);
        dialog_add_task_tv_date.setVisibility(View.VISIBLE);
        dialog_add_task_tv_time.setVisibility(View.VISIBLE);
        dialog_add_task_tv_remind.setVisibility(View.GONE);
        dialog_add_task_tv_tag.setVisibility(View.VISIBLE);
        dialog_add_task_type_note.setTextColor(Color.parseColor("#3e000000"));
        dialog_add_task_type_task.setTextColor(Color.parseColor("#ee03a9f4"));
        dialog_add_task_type_clock.setTextColor(Color.parseColor("#3e000000"));
        dialog_add_task_type_note.setTextSize(14);
        dialog_add_task_type_task.setTextSize(18);
        dialog_add_task_type_clock.setTextSize(14);
        dialog_add_task_tv_important_urgent.setText(label_str_set[task.getLabel()]);
        dialog_add_task_tv_important_urgent.setTextColor(Color.parseColor(text_color_set[task.getLabel()]));
        dialog_add_task_tv_important_urgent.setBackgroundColor(Color.parseColor(background_color_set[task.getLabel()]));
        dialog_add_task_footer_bt_submit.setText("修改");
    }

    @SuppressLint("SetTextI18n")
    private void refreshViewByNote(DBNote note) {
        title = note.getTitle();
        content = note.getContent();

        type = Type.NOTE;
        dialog_add_task_type_note.postDelayed(() -> dialog_add_task_type_note.callOnClick(), 500);
        dialog_add_task_et_title.setText(title);
        dialog_add_task_et_content.setText(content);
        dialog_add_task_tv_tag.setVisibility(View.VISIBLE);
        dialog_add_task_type_note.setTextColor(Color.parseColor("#3e000000"));
        dialog_add_task_type_task.setTextColor(Color.parseColor("#ee03a9f4"));
        dialog_add_task_type_clock.setTextColor(Color.parseColor("#3e000000"));
        dialog_add_task_type_note.setTextSize(14);
        dialog_add_task_type_task.setTextSize(18);
        dialog_add_task_type_clock.setTextSize(14);
        dialog_add_task_footer_bt_submit.setText("修改");
    }

    @SuppressLint("SetTextI18n")
    private void refreshViewByRoutine(DBRoutine dbRoutine) {
        important_urgent_label = dbRoutine.getLabel();
        title = dbRoutine.getTitle();
        content = dbRoutine.getContent();

        is_all_day = dbRoutine.getIs_all_day();
        if (!is_all_day) {
            Date begin_datetime = TimeUtil.formatGMTDateStr(dbRoutine.getBegin_datetime());
            Date end_datetime = TimeUtil.formatGMTDateStr(dbRoutine.getEnd_datetime());
            if (begin_datetime!=null&&end_datetime!=null) {
                startDateTime = new DateTime(begin_datetime);
                endDateTime = new DateTime(end_datetime);
                dialog_add_task_tv_time.setText(startDateTime.toString("HH:mm") +"-"+ endDateTime.toString("HH:mm"));
            }
        } else {
            dialog_add_task_tv_time.setText("全天");
        }
        repeat = dbRoutine.getRepeatInterval();
        type = Type.CLOCK;
        dialog_add_task_type_clock.postDelayed(() -> dialog_add_task_type_clock.callOnClick(), 500);
        dialog_add_task_et_title.setText(title);
        dialog_add_task_et_content.setText(content);
        dialog_add_task_tv_important_urgent.setVisibility(View.VISIBLE);
        dialog_add_task_tv_date.setVisibility(View.VISIBLE);
        dialog_add_task_tv_time.setVisibility(View.VISIBLE);
        dialog_add_task_tv_remind.setVisibility(View.GONE);
        dialog_add_task_tv_tag.setVisibility(View.VISIBLE);
        dialog_add_task_type_note.setTextColor(Color.parseColor("#3e000000"));
        dialog_add_task_type_task.setTextColor(Color.parseColor("#ee03a9f4"));
        dialog_add_task_type_clock.setTextColor(Color.parseColor("#3e000000"));
        dialog_add_task_type_note.setTextSize(14);
        dialog_add_task_type_task.setTextSize(18);
        dialog_add_task_type_clock.setTextSize(14);
        dialog_add_task_tv_important_urgent.setText(label_str_set[dbRoutine.getLabel()]);
        dialog_add_task_tv_important_urgent.setTextColor(Color.parseColor(text_color_set[dbRoutine.getLabel()]));
        dialog_add_task_tv_important_urgent.setBackgroundColor(Color.parseColor(background_color_set[dbRoutine.getLabel()]));
        dialog_add_task_footer_bt_submit.setText("修改");
    }

    private void refreshViewByAttach(DBSubPlan dbSubPlan) {
        dialog_add_task_type_task.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog_add_task_type_task.callOnClick();
            }
        }, 500);
        DateTime date = new DateTime();
        date = date.plusYears(50);
        startDateTime = startDateTime.withYear(date.getYear());
        endDateTime = endDateTime.withYear(date.getYear());
    }
    //</editor-fold desc="Data数据区--存在数据获取或处理代码，但不存在事件监听代码">


    //<editor-fold desc="Event事件区--只要存在事件监听代码就是">----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用
        to_editor.setOnClickListener(this);
        dialog_add_task_et_title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isSelfEdit = true;
                }
            }
        });
        dialog_add_task_et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //同步content里的到title
                if (!isSelfEdit && note_toUpdate == null && task_toUpdate == null) {
                    dialog_add_task_et_title.setText(dialog_add_task_et_content.getText().toString());
                }
                content = dialog_add_task_et_content.getText().toString();

                SPHelper.save(Constants.UNIVERSAL_SAVE_COTENT, dialog_add_task_et_content.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog_add_task_type_note.setOnClickListener(this);
        dialog_add_task_type_task.setOnClickListener(this);
        dialog_add_task_type_clock.setOnClickListener(this);
        dialog_add_task_footer_iv_timecat.setOnClickListener(this);
        dialog_add_task_footer_iv_translate.setOnClickListener(this);
        dialog_add_task_footer_iv_search.setOnClickListener(this);
        dialog_add_task_footer_bt_submit.setOnClickListener(this);
    }

    @NonNull
    @Override
    public InfoOperationPresenter providePresenter() {
        return new InfoOperationPresenter();
    }

    //-//< TagFragment.OnTagAddListener>------------------------------------------------------------
    @Override
    public void insertTag(Tag tag) {
        dialog_add_task_et_content.insertTopic(tag.getName());
    }
    //-//</ TagFragment.OnTagAddListener>-----------------------------------------------------------


    //-//<View.OnClickListener>---------------------------------------------------------------------
    public enum Type {
        NOTE, TASK, CLOCK
    }

    private Type type;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_editor:
                Intent intent2EditorActivity = new Intent();
                Bundle args = new Bundle();
                args.putBoolean(Constants.BUNDLE_KEY_FROM_FILE, false);
                intent2EditorActivity.putExtras(args);
                intent2EditorActivity.setClass(this, EditorActivity.class);
                startActivity(intent2EditorActivity);
                break;
            case R.id.dialog_add_task_type_note:
                onSelectTypeNote();
                break;
            case R.id.dialog_add_task_type_task:
                onSelectTypeTask();
                break;
            case R.id.dialog_add_task_type_clock:
                onSelectTypeClock();
                break;
            case R.id.dialog_add_task_footer_iv_timecat:
                onClickTimeCat();
                break;
            case R.id.dialog_add_task_footer_iv_translate:
                onClickTranslate();
                break;
            case R.id.dialog_add_task_footer_iv_search:
                onClickSearch();
                break;
            case R.id.dialog_add_task_footer_bt_submit:
                dialog_add_task_footer_bt_submit.setClickable(false);
                dialog_add_task_footer_bt_submit.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog_add_task_footer_bt_submit.setClickable(true);
                    }
                }, 3000);
                title = dialog_add_task_et_title.getText().toString();
                content = dialog_add_task_et_content.getText().toString();
                if (StringUtil.isEmail(title)) {
                    ToastUtil.e("标题不能为空！");
                    break;
                }
                switch (type) {
                    case NOTE:
                        if (note_toUpdate != null) {
                            onUpdateNote();
                        } else {
                            onCreateNote();
                        }
                        break;
                    case TASK:
                        if (task_toUpdate != null) {
                            onUpdateTask();
                        } else {
                            onCreateTask();
                        }
                        break;
                    case CLOCK:
                        if (routine_toUpdate != null) {
                            onUpdateRoutine();
                        } else {
                            onCreateRoutine();
                        }
                        break;
                }
                SPHelper.save(Constants.UNIVERSAL_SAVE_COTENT, "");
                ViewUtil.hideInputMethod(dialog_add_task_et_title);
                ViewUtil.hideInputMethod(dialog_add_task_et_content);
                break;
        }
    }

    private void onSelectTypeNote() {
        dialog_add_task_tv_important_urgent.setVisibility(View.GONE);
        dialog_add_task_tv_date.setVisibility(View.GONE);
        dialog_add_task_tv_time.setVisibility(View.GONE);
        dialog_add_task_tv_remind.setVisibility(View.GONE);
        dialog_add_task_tv_tag.setVisibility(View.VISIBLE);
        type = Type.NOTE;
        dialog_add_task_type_note.setTextColor(Color.parseColor("#ee03a9f4"));
        dialog_add_task_type_task.setTextColor(Color.parseColor("#3e000000"));
        dialog_add_task_type_clock.setTextColor(Color.parseColor("#3e000000"));
        dialog_add_task_type_note.setTextSize(18);
        dialog_add_task_type_task.setTextSize(14);
        dialog_add_task_type_clock.setTextSize(14);
        if (note_toUpdate != null) {
            dialog_add_task_footer_bt_submit.setText("修改");
        }
        if (task_toUpdate != null) {
            dialog_add_task_footer_bt_submit.setText("转化");
        }
        if (routine_toUpdate != null) {
            dialog_add_task_footer_bt_submit.setText("转化");
        }
    }

    private void onSelectTypeTask() {
        dialog_add_task_tv_important_urgent.setVisibility(View.VISIBLE);
        dialog_add_task_tv_date.setVisibility(View.VISIBLE);
        dialog_add_task_tv_time.setVisibility(View.VISIBLE);
        dialog_add_task_tv_remind.setVisibility(View.GONE);
        dialog_add_task_tv_tag.setVisibility(View.VISIBLE);
        type = Type.TASK;
        dialog_add_task_type_note.setTextColor(Color.parseColor("#3e000000"));
        dialog_add_task_type_task.setTextColor(Color.parseColor("#ee03a9f4"));
        dialog_add_task_type_clock.setTextColor(Color.parseColor("#3e000000"));
        dialog_add_task_type_note.setTextSize(14);
        dialog_add_task_type_task.setTextSize(18);
        dialog_add_task_type_clock.setTextSize(14);
        if (task_toUpdate != null) {
            dialog_add_task_footer_bt_submit.setText("修改");
        }
        if (note_toUpdate != null) {
            dialog_add_task_footer_bt_submit.setText("转化");
        }
        if (routine_toUpdate != null) {
            dialog_add_task_footer_bt_submit.setText("转化");
        }
    }

    private void onSelectTypeClock() {
        dialog_add_task_tv_important_urgent.setVisibility(View.VISIBLE);
        dialog_add_task_tv_date.setVisibility(View.VISIBLE);
        dialog_add_task_tv_time.setVisibility(View.VISIBLE);
        dialog_add_task_tv_remind.setVisibility(View.VISIBLE);
        dialog_add_task_tv_tag.setVisibility(View.VISIBLE);
        type = Type.CLOCK;
        dialog_add_task_type_note.setTextColor(Color.parseColor("#3e000000"));
        dialog_add_task_type_task.setTextColor(Color.parseColor("#3e000000"));
        dialog_add_task_type_clock.setTextColor(Color.parseColor("#ee03a9f4"));
        dialog_add_task_type_note.setTextSize(14);
        dialog_add_task_type_task.setTextSize(14);
        dialog_add_task_type_clock.setTextSize(18);
        if (task_toUpdate != null) {
            dialog_add_task_footer_bt_submit.setText("转化");
        }
        if (note_toUpdate != null) {
            dialog_add_task_footer_bt_submit.setText("转化");
        }
        if (routine_toUpdate != null) {
            dialog_add_task_footer_bt_submit.setText("修改");
        }
    }

    private void onClickTimeCat() {
        if (TextUtils.isEmpty(content)) {
            content = "";
            ToastUtil.w("输入为空！");
            return;
        }
        Intent intent2TimeCat = new Intent(InfoOperationActivity.this, TimeCatActivity.class);
        intent2TimeCat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2TimeCat.putExtra(TimeCatActivity.TO_SPLIT_STR, content);
        startActivity(intent2TimeCat);
        finish();
    }

    private void onClickSearch() {
        if (TextUtils.isEmpty(content)) {
            content = "";
            ToastUtil.w("输入为空！");
            return;
        }
        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_SEARCH);
        boolean isUrl = false;
        Uri uri = null;
        try {
            Pattern p = Pattern.compile("^((https?|ftp|news):\\/\\/)?([a-z]([a-z0-9\\-]*[\\.。])+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)|(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))(\\/[a-z0-9_\\-\\.~]+)*(\\/([a-z0-9_\\-\\.]*)(\\?[a-z0-9+_\\-\\.%=&]*)?)?(#[a-z][a-z0-9_]*)?$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(content);
            if (!matcher.matches()) {
                uri = Uri.parse(SearchEngineUtil.getInstance().getSearchEngines().get(SPHelper.getInt(Constants.BROWSER_SELECTION, 0)).url + URLEncoder.encode(content, "utf-8"));
                isUrl = false;
            } else {
                uri = Uri.parse(content);
                if (!content.startsWith("http")) {
                    content = "http://" + content;
                }
                isUrl = true;
            }

            boolean t = SPHelper.getBoolean(Constants.USE_LOCAL_WEBVIEW, true);
            Intent intent2Web;
            if (t) {
                intent2Web = new Intent();
                if (isUrl) {
                    intent2Web.putExtra("url", content);
                } else {
                    intent2Web.putExtra("query", content);
                }
                intent2Web.setClass(InfoOperationActivity.this, WebActivity.class);
                startActivity(intent2Web);
            } else {
                intent2Web = new Intent(Intent.ACTION_VIEW, uri);
                intent2Web.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2Web);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent2web = new Intent();
            if (isUrl) {
                intent2web.putExtra("url", content);
            } else {
                intent2web.putExtra("query", content);
            }
            intent2web.setClass(InfoOperationActivity.this, WebActivity.class);
            startActivity(intent2web);
            finish();
        }
    }

    private void onClickTranslate() {
        if (TextUtils.isEmpty(content)) {
            content = "";
            ToastUtil.w("输入为空！");
            return;
        }
        Intent intent2Translate = new Intent(InfoOperationActivity.this, TimeCatActivity.class);
        intent2Translate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2Translate.putExtra(TimeCatActivity.TO_SPLIT_STR, content);
        intent2Translate.putExtra(TimeCatActivity.IS_TRANSLATE, true);
        startActivity(intent2Translate);
        finish();
    }

    private void onUpdateTask() {

        task_toUpdate.setTitle(title);
        task_toUpdate.setContent(content);
        task_toUpdate.setLabel(important_urgent_label);
        ArrayList<String> tags = new ArrayList<>();
        tags.add("http://192.168.88.105:8000/tags/1/");
        tags.add("http://192.168.88.105:8000/tags/2/");
        task_toUpdate.setTags(tags);
        task_toUpdate.setIs_all_day(is_all_day);

        task_toUpdate.setBegin_datetime(TimeUtil.formatGMTDate(startDateTime.toDate()));
        task_toUpdate.setEnd_datetime(TimeUtil.formatGMTDate(endDateTime.toDate()));

        if (subPlan_toAttach != null) task_toUpdate.setSubplan(subPlan_toAttach);
        task_toUpdate.setUser(DB.users().getActive());


        //使用Observable.create()创建被观察者
        Observable<Calendar> observable1 = Observable.create(subscriber -> {
            if (calendarList == null || calendarList.size() <= 0) {
                DB.schedules().safeSaveDBTaskAndFireEvent(task_toUpdate);
                return;
            }

            for (Calendar calendar : calendarList) {
                subscriber.onNext(calendar);
            }
            subscriber.onCompleted();
        });
        //订阅
        observable1.onBackpressureBuffer(1000)
                .subscribeOn(Schedulers.newThread())//指定 subscribe() 发生在新的线程
                .observeOn(Schedulers.io())// 指定 Subscriber 的回调发生在主线程
                .subscribe(new Subscriber<Calendar>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(e.toString());
                    }

                    @Override
                    public void onNext(Calendar s) {
                        //请求成功
                        task_toUpdate.setBegin_datetime(TimeUtil.formatGMTDate(
                                startDateTime.withYear(s.get(Calendar.YEAR))
                                        .withMonthOfYear(s.get(Calendar.MONTH)+1)
                                        .withDayOfMonth(s.get(Calendar.DAY_OF_MONTH)).toDate()
                        ));
                        task_toUpdate.setEnd_datetime(TimeUtil.formatGMTDate(
                                endDateTime.withYear(s.get(Calendar.YEAR))
                                        .withMonthOfYear(s.get(Calendar.MONTH)+1)
                                        .withDayOfMonth(s.get(Calendar.DAY_OF_MONTH)).toDate()
                        ));
                        DB.schedules().safeSaveDBTaskAndFireEvent(task_toUpdate);
                        //避免更新而不创建
                        task_toUpdate.setCreated_datetime(TimeUtil.formatGMTDate(new DateTime().toDate()));
                        task_toUpdate.setId(-1L);

                    }
                });

//        DB.schedules().safeSaveDBTaskAndFireEvent(task_toUpdate);

        if (task_toUpdate.getUrl() == null) {
            // 离线创建的task是没有url的，这里要在服务器端新建一个一摸一样的，然后把url传过来
            RetrofitHelper.getTaskService().createTask(Converter.toTask(task_toUpdate)) //获取Observable对象
                    .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                    .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                    .doOnNext(new Action1<Task>() {
                        @Override
                        public void call(Task task1) {
                            // 将笔记的url保存下来
                            task_toUpdate.setUrl(task1.getUrl());
                            DB.schedules().safeSaveDBTaskAndFireEvent(task_toUpdate);
                            LogUtil.e("保存任务信息到本地 safeSaveNote -->" + task_toUpdate.toString());
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
//                            ToastUtil.show("云端没有该任务，补充添加[ 任务 ]同步失败");
//                            LogUtil.e("云端没有该任务，补充添加[ 任务 ]同步失败" + e.toString());
                        }

                        @Override
                        public void onNext(Task note) {
                            //请求成功
//                            ToastUtil.show("云端没有该任务，成功补充添加[ 任务 ]:" + content);
//                            LogUtil.e("云端没有该任务，成功补充添加[ 任务 ]:" + dbPlan.toString());
                        }
                    });
        } else {
            RetrofitHelper.getTaskService().putTaskByUrl(task_toUpdate.getUrl(), Converter.toTask(task_toUpdate)) //获取Observable对象
                    .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                    .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                    .doOnNext(new Action1<Task>() {
                        @Override
                        public void call(Task task) {
                            DB.schedules().safeSaveTaskAndFireEvent(task);
//                            LogUtil.e("保存任务信息到本地" + task.toString());
                        }
                    }).observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                    .subscribe(new Subscriber<Task>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            //请求失败
//                            ToastUtil.show("同步[ 任务 ]失败，保存到本地");
//                            LogUtil.e(e.toString());
                        }

                        @Override
                        public void onNext(Task task) {
                            //请求成功
//                            ToastUtil.show("成功更新[ 任务 ]:" + content);
//                            LogUtil.e("请求成功" + task.toString());
                        }
                    });
        }
        ToastUtil.ok("成功更新[ 任务 ]:" + content);
        finish();
    }

    private void onCreateTask() {
//        final ProgressDialog progressDialog = new ProgressDialog(InfoOperationActivity.this, R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Saving Task...");
//        progressDialog.show();

        DBTask dbTask = new DBTask();
        DBUser activeUser = DB.users().getActive();
        String owner = ConstantURL.BASE_URL_USERS + activeUser.getEmail() + "/";
        dbTask.setOwner(owner);
        dbTask.setTitle(title);
        dbTask.setContent(content);
        dbTask.setLabel(important_urgent_label);
        ArrayList<String> tags = new ArrayList<>();
        tags.add("http://192.168.88.105:8000/tags/1/");
        tags.add("http://192.168.88.105:8000/tags/2/");
        dbTask.setTags(tags);
        dbTask.setCreated_datetime(TimeUtil.formatGMTDate(new Date()));

        dbTask.setIs_all_day(is_all_day);
        dbTask.setBegin_datetime(TimeUtil.formatGMTDate(startDateTime.toDate()));
        dbTask.setEnd_datetime(TimeUtil.formatGMTDate(endDateTime.toDate()));

        dbTask.setUser(activeUser);
        DBSubPlan dbSubPlan = (DBSubPlan) getIntent().getSerializableExtra(TO_ATTACH_SUBPLAN);
        if (dbSubPlan != null) dbTask.setSubplan(dbSubPlan);


        //使用Observable.create()创建被观察者
        Observable<Calendar> observable1 = Observable.create(subscriber -> {
            if (calendarList == null || calendarList.size() <= 0) {
                DB.schedules().safeSaveDBTaskAndFireEvent(dbTask);
                return;
            }

            for (Calendar calendar : calendarList) {
                subscriber.onNext(calendar);
            }
            subscriber.onCompleted();
        });
        //订阅
        observable1.onBackpressureBuffer(1000)
                .subscribeOn(Schedulers.newThread())//指定 subscribe() 发生在新的线程
                .observeOn(Schedulers.io())// 指定 Subscriber 的回调发生在主线程
                .subscribe(new Subscriber<Calendar>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(e.toString());
                    }

                    @Override
                    public void onNext(Calendar s) {
                        //请求成功
                        dbTask.setBegin_datetime(TimeUtil.formatGMTDate(
                                startDateTime.withYear(s.get(Calendar.YEAR))
                                        .withMonthOfYear(s.get(Calendar.MONTH)+1)
                                        .withDayOfMonth(s.get(Calendar.DAY_OF_MONTH)).toDate()
                        ));
                        dbTask.setEnd_datetime(TimeUtil.formatGMTDate(
                                endDateTime.withYear(s.get(Calendar.YEAR))
                                        .withMonthOfYear(s.get(Calendar.MONTH)+1)
                                        .withDayOfMonth(s.get(Calendar.DAY_OF_MONTH)).toDate()
                        ));
                        //避免更新而不创建
                        dbTask.setCreated_datetime(TimeUtil.formatGMTDate(new DateTime().toDate()));
                        dbTask.setId(-1L);
                        DB.schedules().safeSaveDBTaskAndFireEvent(dbTask);

                    }
                });


        RetrofitHelper.getTaskService().createTask(Converter.toTask(dbTask)) //获取Observable对象
                .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .doOnNext(new Action1<Task>() {
                    @Override
                    public void call(Task task) {
                        // 将task的url保存下来
                        dbTask.setUrl(task.getUrl());
                        DB.schedules().safeSaveDBTaskAndFireEvent(dbTask);
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
//                        progressDialog.dismiss();
//                        ToastUtil.show("添加[ 任务 ]失败");
//                        LogUtil.e(e.toString());
                    }

                    @Override
                    public void onNext(Task task) {
                        //请求成功
//                        progressDialog.dismiss();
//                        ToastUtil.show("成功添加[ 任务 ]:" + content);
//                        LogUtil.e("成功添加[ 任务 ]: " + task.toString());
                    }
                });
        ToastUtil.ok("成功添加[ 任务 ]:" + content);

        finish();
    }

    private void onUpdateNote() {
        note_toUpdate.setTitle(title);
        note_toUpdate.setContent(content);
        ArrayList<String> tags = new ArrayList<>();
        tags.add("http://192.168.88.105:8000/tags/1/");
        tags.add("http://192.168.88.105:8000/tags/2/");
//        dbPlan.setTags(tags);
        note_toUpdate.setUpdate_datetime(TimeUtil.formatGMTDate(new Date()));
        DB.notes().updateAndFireEvent(note_toUpdate);
        note_toUpdate.setUser(DB.users().getActive());

        if (note_toUpdate.getUrl() == null) {
            RetrofitHelper.getNoteService().createNote(Converter.toNote(note_toUpdate)) //获取Observable对象
                    .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                    .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                    .doOnNext(new Action1<Note>() {
                        @Override
                        public void call(Note note1) {
                            // 将笔记的url保存下来
                            note_toUpdate.setUrl(note1.getUrl());
                            DB.notes().safeSaveDBNoteAndFireEvent(note_toUpdate);
//                            LogUtil.e("保存任务信息到本地 safeSaveNote -->" + note1.toString());
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                    .subscribe(new Subscriber<Note>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            //请求失败
//                            ToastUtil.show("云端没有该笔记，补充添加[ 笔记 ]同步失败");
//                            LogUtil.e("云端没有该笔记，补充添加[ 笔记 ]同步失败" + e.toString());
                        }

                        @Override
                        public void onNext(Note note) {
                            //请求成功
//                            ToastUtil.show("云端没有该笔记，成功补充添加[ 笔记 ]:" + content);
//                            LogUtil.e("云端没有该笔记，成功补充添加[ 笔记 ]:" + dbPlan.toString());
                        }
                    });
        } else {
            RetrofitHelper.getNoteService().putNoteByUrl(note_toUpdate.getUrl(), Converter.toNote(note_toUpdate)) //获取Observable对象
                    .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                    .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                    .doOnNext(new Action1<Note>() {
                        @Override
                        public void call(Note note) {
                            // 将笔记的url保存下来
                            DB.notes().safeSaveNoteAndFireEvent(note);
//                            LogUtil.e("保存任务信息到本地" + dbPlan.toString());
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                    .subscribe(new Subscriber<Note>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            //请求失败
//                            ToastUtil.show("更新[ 笔记 ]失败");
//                            LogUtil.e("更新[ 笔记 ]失败:" + e.toString());
                        }

                        @Override
                        public void onNext(Note note) {
                            //请求成功
//                            ToastUtil.show("成功更新[ 笔记 ]:" + content);
//                            LogUtil.e("请求成功,成功更新[ 笔记 ]:" + dbPlan.toString());
                        }
                    });
        }
        ToastUtil.ok("成功修改[ 笔记 ]:" + content);

        finish();
    }

    private void onCreateNote() {
        DBNote dbNote = new DBNote();
        DBUser activeUser = DB.users().getActive();
        String owner = Converter.getOwnerUrl(activeUser);
        dbNote.setOwner(owner);
        dbNote.setTitle(title);
        dbNote.setContent(content);
        dbNote.setCreated_datetime(TimeUtil.formatGMTDate(new Date()));
        dbNote.setUpdate_datetime(dbNote.getCreated_datetime());
        ArrayList<String> tags = new ArrayList<>();
        tags.add("http://192.168.88.105:8000/tags/1/");
        tags.add("http://192.168.88.105:8000/tags/2/");
        List<Integer> CardStackViewDataList = new ArrayList<>();
        int[] CardStackViewData = getResources().getIntArray(R.array.card_stack_view_data);
        for (int aCardStackViewData : CardStackViewData) {
            CardStackViewDataList.add(aCardStackViewData);
        }
        Random random = new Random();
        int randomColor = random.nextInt(CardStackViewDataList.size());
        dbNote.setColor(CardStackViewDataList.get(randomColor));
        dbNote.setUser(activeUser);
        DB.notes().saveAndFireEvent(dbNote);

        RetrofitHelper.getNoteService().createNote(Converter.toNote(dbNote)) //获取Observable对象
                .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .doOnNext(new Action1<Note>() {
                    @Override
                    public void call(Note note) {
                        dbNote.setUrl(note.getUrl());
                        DB.notes().safeSaveDBNoteAndFireEvent(dbNote);
//                        LogUtil.e("保存任务信息到本地 safeSaveNote -->" + dbPlan.toString());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(new Subscriber<Note>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //请求失败
//                        ToastUtil.show("添加[ 笔记 ]同步失败");
//                        LogUtil.e(e.toString());
                    }

                    @Override
                    public void onNext(Note note) {
                        //请求成功
//                        ToastUtil.show("成功添加[ 笔记 ]:" + content);
//                        LogUtil.e("请求成功" + dbPlan.toString());
                    }
                });
        ToastUtil.ok("成功添加[ 笔记 ]:" + content);
        finish();
    }

    private void onUpdateRoutine() {
        routine_toUpdate.setTitle(title);
        routine_toUpdate.setContent(content);
        routine_toUpdate.setLabel(important_urgent_label);
        ArrayList<String> tags = new ArrayList<>();
        tags.add("http://192.168.88.105:8000/tags/1/");
        tags.add("http://192.168.88.105:8000/tags/2/");
        routine_toUpdate.setTags(tags);

        routine_toUpdate.setRepeatInterval(repeat);
        routine_toUpdate.setIs_all_day(is_all_day);
        routine_toUpdate.setBegin_datetime(TimeUtil.formatGMTDate(startDateTime.toDate()));
        routine_toUpdate.setEnd_datetime(TimeUtil.formatGMTDate(endDateTime.toDate()));
        routine_toUpdate.setUser(DB.users().getActive());

        //使用Observable.create()创建被观察者
        Observable<Calendar> observable1 = Observable.create(subscriber -> {
            if (calendarList == null || calendarList.size() <= 0) {
                DB.routines().safeSaveDBRoutineAndFireEvent(routine_toUpdate);
                return;
            }

            for (Calendar calendar : calendarList) {
                subscriber.onNext(calendar);
            }
            subscriber.onCompleted();
        });
        //订阅
        observable1.onBackpressureBuffer(1000)
                .subscribeOn(Schedulers.newThread())//指定 subscribe() 发生在新的线程
                .observeOn(Schedulers.io())// 指定 Subscriber 的回调发生在主线程
                .subscribe(new Subscriber<Calendar>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(e.toString());
                    }

                    @Override
                    public void onNext(Calendar s) {
                        //请求成功
                        routine_toUpdate.setBegin_datetime(TimeUtil.formatGMTDate(
                                startDateTime.withYear(s.get(Calendar.YEAR))
                                        .withMonthOfYear(s.get(Calendar.MONTH)+1)
                                        .withDayOfMonth(s.get(Calendar.DAY_OF_MONTH)).toDate()
                        ));
                        routine_toUpdate.setEnd_datetime(TimeUtil.formatGMTDate(
                                endDateTime.withYear(s.get(Calendar.YEAR))
                                        .withMonthOfYear(s.get(Calendar.MONTH)+1)
                                        .withDayOfMonth(s.get(Calendar.DAY_OF_MONTH)).toDate()
                        ));
                        DB.routines().safeSaveDBRoutineAndFireEvent(routine_toUpdate);
                        //避免更新而不创建
                        routine_toUpdate.setCreated_datetime(TimeUtil.formatGMTDate(new DateTime().toDate()));
                        routine_toUpdate.setId(-1L);

                    }
                });


        if (routine_toUpdate.getUrl() == null) {
            // 离线创建的task是没有url的，这里要在服务器端新建一个一摸一样的，然后把url传过来
            RetrofitHelper.getRoutineService().createRoutine(Converter.toRoutine(routine_toUpdate)) //获取Observable对象
                    .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                    .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                    .doOnNext(new Action1<Routine>() {
                        @Override
                        public void call(Routine task1) {
                            // 将笔记的url保存下来
                            routine_toUpdate.setUrl(task1.getUrl());
                            DB.routines().safeSaveDBRoutineAndFireEvent(routine_toUpdate);
                            LogUtil.e("保存任务信息到本地 safeSaveNote -->" + routine_toUpdate.toString());
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                    .subscribe(new Subscriber<Routine>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            //请求失败
//                            ToastUtil.show("云端没有该任务，补充添加[ 任务 ]同步失败");
//                            LogUtil.e("云端没有该任务，补充添加[ 任务 ]同步失败" + e.toString());
                        }

                        @Override
                        public void onNext(Routine note) {
                            //请求成功
//                            ToastUtil.show("云端没有该任务，成功补充添加[ 任务 ]:" + content);
//                            LogUtil.e("云端没有该任务，成功补充添加[ 任务 ]:" + dbPlan.toString());
                        }
                    });
        } else {
            RetrofitHelper.getRoutineService().putRoutineByUrl(routine_toUpdate.getUrl(), Converter.toRoutine(routine_toUpdate)) //获取Observable对象
                    .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                    .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                    .doOnNext(new Action1<Routine>() {
                        @Override
                        public void call(Routine task) {
                            DB.routines().safeSaveRoutineAndFireEvent(task);
//                            LogUtil.e("保存任务信息到本地" + task.toString());
                        }
                    }).observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                    .subscribe(new Subscriber<Routine>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            //请求失败
//                            ToastUtil.show("同步[ 任务 ]失败，保存到本地");
//                            LogUtil.e(e.toString());
                        }

                        @Override
                        public void onNext(Routine task) {
                            //请求成功
//                            ToastUtil.show("成功更新[ 任务 ]:" + content);
//                            LogUtil.e("请求成功" + task.toString());
                        }
                    });
        }
        ToastUtil.ok("成功更新[ 生物钟 ]:" + content);
        finish();
    }

    private void onCreateRoutine() {
        DBRoutine dbRoutine = new DBRoutine();
        DBUser activeUser = DB.users().getActive();
        String owner = ConstantURL.BASE_URL_USERS + activeUser.getEmail() + "/";
        dbRoutine.setOwner(owner);
        dbRoutine.setTitle(title);
        dbRoutine.setContent(content);
        dbRoutine.setLabel(important_urgent_label);
        ArrayList<String> tags = new ArrayList<>();
        tags.add("http://192.168.88.105:8000/tags/1/");
        tags.add("http://192.168.88.105:8000/tags/2/");
        dbRoutine.setTags(tags);
        dbRoutine.setCreated_datetime(TimeUtil.formatGMTDate(new Date()));

        dbRoutine.setRepeatInterval(repeat);
        dbRoutine.setIs_all_day(is_all_day);
        dbRoutine.setBegin_datetime(TimeUtil.formatGMTDate(startDateTime.toDate()));
        dbRoutine.setEnd_datetime(TimeUtil.formatGMTDate(endDateTime.toDate()));

        dbRoutine.setUser(activeUser);

        //使用Observable.create()创建被观察者
        Observable<Calendar> observable1 = Observable.create(subscriber -> {
            if (calendarList == null || calendarList.size() <= 0) {
                DB.routines().safeSaveDBRoutineAndFireEvent(dbRoutine);
                return;
            }

            for (Calendar calendar : calendarList) {
                subscriber.onNext(calendar);
            }
            subscriber.onCompleted();
        });
        //订阅
        observable1.onBackpressureBuffer(1000)
                .subscribeOn(Schedulers.newThread())//指定 subscribe() 发生在新的线程
                .observeOn(Schedulers.io())// 指定 Subscriber 的回调发生在主线程
                .subscribe(new Subscriber<Calendar>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.e(e.toString());
                    }

                    @Override
                    public void onNext(Calendar s) {
                        //请求成功
                        dbRoutine.setBegin_datetime(TimeUtil.formatGMTDate(
                                startDateTime.withYear(s.get(Calendar.YEAR))
                                        .withMonthOfYear(s.get(Calendar.MONTH)+1)
                                        .withDayOfMonth(s.get(Calendar.DAY_OF_MONTH)).toDate()
                        ));
                        dbRoutine.setEnd_datetime(TimeUtil.formatGMTDate(
                                endDateTime.withYear(s.get(Calendar.YEAR))
                                        .withMonthOfYear(s.get(Calendar.MONTH)+1)
                                        .withDayOfMonth(s.get(Calendar.DAY_OF_MONTH)).toDate()
                        ));
                        DB.routines().safeSaveDBRoutineAndFireEvent(dbRoutine);
                        //避免更新而不创建
                        dbRoutine.setCreated_datetime(TimeUtil.formatGMTDate(new DateTime().toDate()));
                        dbRoutine.setId(-1L);
                    }
                });


        RetrofitHelper.getRoutineService().createRoutine(Converter.toRoutine(dbRoutine)) //获取Observable对象
                .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .doOnNext(new Action1<Routine>() {
                    @Override
                    public void call(Routine task) {
                        // 将task的url保存下来
                        dbRoutine.setUrl(task.getUrl());
                        DB.routines().safeSaveDBRoutineAndFireEvent(dbRoutine);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(new Subscriber<Routine>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //请求失败
//                        progressDialog.dismiss();
//                        ToastUtil.show("添加[ 任务 ]失败");
//                        LogUtil.e(e.toString());
                    }

                    @Override
                    public void onNext(Routine task) {
                        //请求成功
//                        progressDialog.dismiss();
//                        ToastUtil.show("成功添加[ 任务 ]:" + content);
//                        LogUtil.e("成功添加[ 任务 ]: " + task.toString());
                    }
                });
        ToastUtil.ok("成功添加[ 生物钟 ]:" + content);

        finish();
    }
    //-//</View.OnClickListener>--------------------------------------------------------------------


    //-//<Activity>---------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        /*
         * 拦截返回键操作，如果此时表情布局未隐藏，先隐藏表情布局
         */
        if (!mSmartKeyboardManager.interceptBackPressed()) {
            super.onBackPressed();
        }
    }
    //-//</Activity>-------------------------------------------------------------------------------

    //</editor-fold desc="Event事件区--只要存在事件监听代码就是">----------------------------------------------------------

}
