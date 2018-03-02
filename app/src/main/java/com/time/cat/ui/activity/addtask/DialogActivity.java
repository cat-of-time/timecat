package com.time.cat.ui.activity.addtask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.shang.commonjar.contentProvider.SPHelper;
import com.time.cat.R;
import com.time.cat.database.DB;
import com.time.cat.mvp.model.DBmodel.DBNote;
import com.time.cat.mvp.model.DBmodel.DBTask;
import com.time.cat.mvp.model.DBmodel.DBUser;
import com.time.cat.mvp.model.Note;
import com.time.cat.mvp.model.Task;
import com.time.cat.mvp.presenter.ActivityPresenter;
import com.time.cat.mvp.view.emotion.adapter.HorizontalRecyclerviewAdapter;
import com.time.cat.mvp.view.emotion.adapter.NoHorizontalScrollerVPAdapter;
import com.time.cat.mvp.view.emotion.fragment.EmotiomComplateFragment;
import com.time.cat.mvp.view.emotion.fragment.Fragment1;
import com.time.cat.mvp.view.emotion.fragment.FragmentFactory;
import com.time.cat.mvp.view.emotion.model.ImageModel;
import com.time.cat.mvp.view.keyboardManager.SmartKeyboardManager;
import com.time.cat.mvp.view.richText.TEditText;
import com.time.cat.mvp.view.viewpaper.NoHorizontalScrollerViewPager;
import com.time.cat.network.ConstantURL;
import com.time.cat.network.RetrofitHelper;
import com.time.cat.ui.activity.TimeCatActivity;
import com.time.cat.ui.activity.WebActivity;
import com.time.cat.ui.base.BaseActivity;
import com.time.cat.util.ConstantUtil;
import com.time.cat.util.ModelUtil;
import com.time.cat.util.SearchEngineUtil;
import com.time.cat.util.UrlCountUtil;
import com.time.cat.util.listener.GlobalOnItemClickManager;
import com.time.cat.util.override.LogUtil;
import com.time.cat.util.override.SharedPreferencedUtils;
import com.time.cat.util.override.ToastUtil;
import com.time.cat.util.string.TimeUtil;
import com.time.cat.util.view.EmotionUtil;
import com.time.cat.util.view.ViewUtil;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author dlink
 * @date 2018/2/14
 * @discription "添加"操作,用activity实现dialog
 */
public class DialogActivity extends BaseActivity implements
                                                 ActivityPresenter,
                                                 View.OnClickListener{
    @SuppressWarnings("unused")
    private static final String TAG = "DialogActivity ";
    public static final String TO_SAVE_STR = "to_save_str";
    public static final String TO_UPDATE_TASK = "to_update_task";
    public static final String TO_UPDATE_NOTE = "to_update_note";
    public static final String TO_UPDATE_ROUTINE = "to_update_ROUTINE";


    //<启动方法>-------------------------------------------------------------------------------------
    /**
     * 启动这个Activity的Intent
     *
     * @param context 　上下文
     *
     * @return 返回intent实例
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, DialogActivity.class);
    }

    @Override
    public Activity getActivity() {
        return this;
    }
    //</启动方法>------------------------------------------------------------------------------------


    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
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

    //</生命周期>------------------------------------------------------------------------------------


    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------
    private TEditText dialog_add_task_et_title;
    private TEditText dialog_add_task_et_content;

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
    private LinearLayout dialog_add_task_ll_extra;

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
        dialog_add_task_ll_extra = findViewById(R.id.dialog_add_task_ll_extra);
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

    /**
     * 设置 date 选择面板
     */
    private void setSelectDatePanel() {
        String IMAGE_ITEM = "image_item";
        String TEXT_ITEM = "text_item";
        String[] arrText = new String[]{
                "今天", "明天", "后天",
                "下周", "下月", "明年的今天",
                "其他"
        };
        int[] arrImages=new int[]{
                R.drawable.ic_alarm_black_48dp, R.drawable.ic_alarm_black_48dp, R.drawable.ic_alarm_black_48dp,
                R.drawable.ic_alarm_black_48dp, R.drawable.ic_alarm_black_48dp, R.drawable.ic_alarm_black_48dp,
                R.drawable.ic_alarm_black_48dp
        };
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        for (int i=0; i<7; i++) {
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
        dialog_add_task_select_gv_date.setAdapter(saImageItems);
        dialog_add_task_select_gv_date.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 根据元素位置获取对应的值
                HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);

                String itemText=(String)item.get(TEXT_ITEM);
                Object object=item.get(IMAGE_ITEM);
                ToastUtil.show("You Select "+itemText);
                dialog_add_task_tv_date.setText(itemText);
            }
        });
    }

    /**
     * 设置time选择面板
     */
    private void setSelectTimePanel() {
        //时间选择器
        pvTime = new TimePickerView.Builder(getActivity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                //选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                TextView tv = (TextView) v;
                tv.setText(format.format(date));
            }
        })
                .setLayoutRes(R.layout.view_keyboard_time_pickerview, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        WheelView hour = v.findViewById(R.id.hour);
                        WheelView min = v.findViewById(R.id.min);
                        hour.setOnItemSelectedListener(new OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(int index) {
                                is_all_day = false;
                                pvTime.returnData();
                                dialog_add_task_tv_time.setText(select_tv_start_time.getText() + "-" + select_tv_end_time.getText());
                                if (is_setting_start_time) {
                                    start_hour = index + 1;
                                } else if (is_setting_end_time) {
                                    end_hour = index + 1;
                                }
                            }
                        });
                        min.setOnItemSelectedListener(new OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(int index) {
                                is_all_day = false;
                                pvTime.returnData();
                                dialog_add_task_tv_time.setText(select_tv_start_time.getText() + "-" + select_tv_end_time.getText());
                                if (is_setting_start_time) {
                                    start_min = index + 1;
                                } else if (is_setting_end_time) {
                                    end_min = index + 1;
                                }
                            }
                        });
                    }
                })
                .setType(new boolean[]{false, false, false, true, true, false})
                .setLabel("", "", "", "", "", "") //设置空字符串以隐藏单位提示   hide label
                .setDividerColor(Color.DKGRAY)
                .setContentSize(24)
                .isDialog(false)
                .setDecorView(time_picker_fragment)//非dialog模式下,设置ViewGroup, pickerView将会添加到这个ViewGroup中
                .setBackgroundId(0x00000000)
                .isCyclic(true)
                .setLineSpacingMultiplier((float) 1.2)
                .setTextXOffset(0, 0, 0, 0, 0, 0)
                .setOutSideCancelable(false)
                .build();
        pvTime.show(select_tv_start_time, false);
        select_ll_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_all_day = false;
                select_tv_start.setTextColor(getResources().getColor(R.color.blue));
                select_tv_start_time.setTextColor(getResources().getColor(R.color.black));
                select_tv_end.setTextColor(Color.parseColor("#3e000000"));
                select_tv_end_time.setTextColor(Color.parseColor("#3e000000"));
                dialog_add_task_tv_time.setText(select_tv_start_time.getText() + "-" + select_tv_end_time.getText());
                pvTime.show(select_tv_start_time, false);
                is_setting_start_time = true;
                is_setting_end_time = false;
            }
        });
        select_ll_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_all_day = false;
                select_tv_start.setTextColor(Color.parseColor("#3e000000"));
                select_tv_start_time.setTextColor(Color.parseColor("#3e000000"));
                select_tv_end.setTextColor(getResources().getColor(R.color.blue));
                select_tv_end_time.setTextColor(getResources().getColor(R.color.black));
                dialog_add_task_tv_time.setText(select_tv_start_time.getText() + "-" + select_tv_end_time.getText());
                pvTime.show(select_tv_end_time, false);
                is_setting_start_time = false;
                is_setting_end_time = true;
            }
        });
        select_tv_all_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_all_day = true;
                dialog_add_task_tv_time.setText("全天");
            }
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
        dialog_add_task_select_gv_remind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 根据元素位置获取对应的值
                HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);

                String itemText=(String)item.get(TEXT_ITEM);
                Object object=item.get(IMAGE_ITEM);
                ToastUtil.show("You Select "+itemText);
                dialog_add_task_tv_remind.setText(itemText);
            }
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
        SharedPreferencedUtils.setInteger(getActivity(), CURRENT_POSITION_FLAG, CurrentPosition);

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
                int oldPosition = SharedPreferencedUtils.getInteger(getActivity(), CURRENT_POSITION_FLAG, 0);
                //修改背景颜色的标记
                datas.get(oldPosition).isSelected = false;
                //记录当前被选中tab下标
                CurrentPosition = position;
                datas.get(CurrentPosition).isSelected = true;
                SharedPreferencedUtils.setInteger(getActivity(), CURRENT_POSITION_FLAG, CurrentPosition);
                //通知更新，这里我们选择性更新就行了
                horizontalRecyclerviewAdapter.notifyItemChanged(oldPosition);
                horizontalRecyclerviewAdapter.notifyItemChanged(CurrentPosition);
                //viewpager界面切换
                viewPager.setCurrentItem(position, false);
            }

            @Override
            public void onItemLongClick(View view, int position, List<ImageModel> datas) {
            }
        });
    }

    private void replaceFragment() {
        //创建fragment的工厂类
        FragmentFactory factory = FragmentFactory.getSingleFactoryInstance();
        //创建修改实例
        EmotiomComplateFragment f1 = (EmotiomComplateFragment) factory.getFragment(EmotionUtil.EMOTION_CLASSIC_TYPE);
        fragments.add(f1);
        Bundle b = null;
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
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>-----------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-----------------------------------------
    DBTask task;
    DBNote note;
    int important_urgent_label;
    private String title;
    private String content;
    //用户是否编辑了title，编辑即视为有自定义的需求，取用户自定义的title，不再同步content里的到title
    boolean isSelfEdit = false;
    boolean is_all_day = true;
    int start_hour;
    int start_min;
    int end_hour;
    int end_min;
    boolean is_setting_start_time;
    boolean is_setting_end_time;
    String[] text_color_set = new String[]{
            "#f44336", "#ff9800", "#2196f3", "#4caf50"
    };
    String[] background_color_set = new String[]{
            "#50f44336", "#50ff9800", "#502196f3", "#504caf50"
    };
    String[] label_str_set = new String[] {
            "重要且紧急", "重要不紧急", "紧急不重要", "不重要不紧急",
    };

    @SuppressLint("SetTextI18n")
    @Override
    public void initData() {//必须调用
        task = (DBTask) getIntent().getSerializableExtra(TO_UPDATE_TASK);
        note = (DBNote) getIntent().getSerializableExtra(TO_UPDATE_NOTE);

        important_urgent_label = 0;
        title = null;
        content = null;
        Date date = new Date();
        start_hour = date.getHours();
        start_min = date.getMinutes();
        end_hour = start_hour;
        end_min = start_min;
        is_setting_start_time = false;
        is_setting_end_time = false;
        type = Type.NOTE;
        initTextString();
        LogUtil.e("initData --> ");
        if (task != null) {
            LogUtil.e("initData --> task != null --> " + task);
            refreshViewByTask(task);
        }
        if (note != null) {
            LogUtil.e("initData --> note != null --> " + note);
            refreshViewByNote(note);
        }
        select_tv_start_time.setText((start_hour<10?"0"+start_hour:start_hour) + ":" + (start_min<10?"0"+start_min:start_min));
        select_tv_end_time.setText((end_hour<10?"0"+end_hour:end_hour) + ":" + (end_min<10?"0"+end_min:end_min));
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
            SPHelper.save(ConstantUtil.UNIVERSAL_SAVE_COTENT, str);
        }

        if (TextUtils.isEmpty(str)) {
            str = SPHelper.getString(ConstantUtil.UNIVERSAL_SAVE_COTENT, "");
        }



        title = str;
        content = str;
        dialog_add_task_et_title.setText(title);
        dialog_add_task_et_content.setText(content);
        dialog_add_task_et_content.setSelection(content.length());
        SPHelper.save(ConstantUtil.UNIVERSAL_SAVE_COTENT, str);
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
                start_hour = begin_datetime.getHours();
                start_min=begin_datetime.getMinutes();
                end_hour= end_datetime.getHours();
                end_min = end_datetime.getMinutes();
                dialog_add_task_tv_time.setText(
                        (start_hour<10?"0"+start_hour:start_hour) + ":" + (start_min<10?"0"+start_min:start_min)
                                + "-" + (end_hour<10?"0"+end_hour:end_hour) + ":" + (end_min<10?"0"+end_min:end_min)
                );
            }
        } else {
            dialog_add_task_tv_time.setText("全天");
        }
        type = Type.TASK;
        dialog_add_task_type_task.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog_add_task_type_task.callOnClick();
            }
        }, 500);
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

    private void refreshViewByNote(DBNote note) {
        title = note.getTitle();
        content = note.getContent();

        type = Type.NOTE;
        dialog_add_task_type_note.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog_add_task_type_note.callOnClick();
            }
        }, 500);
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
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------


    //<Event事件区>---只要存在事件监听代码就是----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用
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
                if (!isSelfEdit) {
                    dialog_add_task_et_title.setText(dialog_add_task_et_content.getText().toString());
                }
                content = dialog_add_task_et_content.getText().toString();

                SPHelper.save(ConstantUtil.UNIVERSAL_SAVE_COTENT, dialog_add_task_et_content.getText().toString());
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


    //-//<View.OnClickListener>---------------------------------------------------------------------
    public enum Type {
        NOTE, TASK, CLOCK
    }

    private Type type;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                switch (type) {
                    case NOTE:
                        if (note != null) {
                            onUpdateNote();
                        } else {
                            onCreateNote();
                        }
                        break;
                    case TASK:
                        if (task != null) {
                            onUpdateTask();
                        } else {
                            onCreateTask();
                        }
                        break;
                    case CLOCK:
                        ToastUtil.show("添加[ 闹钟 ]失败：功能未完善");
                        break;
                }
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
        if (note != null) {
            dialog_add_task_footer_bt_submit.setText("修改");
        }
        if (task != null) {
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
        if (task != null) {
            dialog_add_task_footer_bt_submit.setText("修改");
        }
        if (note != null) {
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
    }

    private void onClickTimeCat() {
        if (TextUtils.isEmpty(content)) {
            content = "";
            ToastUtil.show("输入为空！");
            return;
        }
        Intent intent2TimeCat = new Intent(DialogActivity.this, TimeCatActivity.class);
        intent2TimeCat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2TimeCat.putExtra(TimeCatActivity.TO_SPLIT_STR, content);
        startActivity(intent2TimeCat);
        finish();
    }

    private void onClickSearch() {
        if (TextUtils.isEmpty(content)) {
            content = "";
            ToastUtil.show("输入为空！");
            return;
        }
        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_SEARCH);
        boolean isUrl = false;
        Uri uri = null;
        try {
            Pattern p = Pattern.compile("^((https?|ftp|news):\\/\\/)?([a-z]([a-z0-9\\-]*[\\.。])+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)|(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))(\\/[a-z0-9_\\-\\.~]+)*(\\/([a-z0-9_\\-\\.]*)(\\?[a-z0-9+_\\-\\.%=&]*)?)?(#[a-z][a-z0-9_]*)?$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(content);
            if (!matcher.matches()) {
                uri = Uri.parse(SearchEngineUtil.getInstance().getSearchEngines().get(SPHelper.getInt(ConstantUtil.BROWSER_SELECTION, 0)).url + URLEncoder.encode(content, "utf-8"));
                isUrl = false;
            } else {
                uri = Uri.parse(content);
                if (!content.startsWith("http")) {
                    content = "http://" + content;
                }
                isUrl = true;
            }

            boolean t = SPHelper.getBoolean(ConstantUtil.USE_LOCAL_WEBVIEW, true);
            Intent intent2Web;
            if (t) {
                intent2Web = new Intent();
                if (isUrl) {
                    intent2Web.putExtra("url", content);
                } else {
                    intent2Web.putExtra("query", content);
                }
                intent2Web.setClass(DialogActivity.this, WebActivity.class);
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
            intent2web.setClass(DialogActivity.this, WebActivity.class);
            startActivity(intent2web);
            finish();
        }
    }

    private void onClickTranslate() {
        if (TextUtils.isEmpty(content)) {
            content = "";
            ToastUtil.show("输入为空！");
            return;
        }
        Intent intent2Translate = new Intent(DialogActivity.this, TimeCatActivity.class);
        intent2Translate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2Translate.putExtra(TimeCatActivity.TO_SPLIT_STR, content);
        intent2Translate.putExtra(TimeCatActivity.IS_TRANSLATE, true);
        startActivity(intent2Translate);
        finish();
    }

    private void onUpdateTask() {
        task.setTitle(title);
        task.setContent(content);
        task.setLabel(important_urgent_label);
        ArrayList<String> tags = new ArrayList<>();
        tags.add("http://192.168.88.105:8000/tags/1/");
        tags.add("http://192.168.88.105:8000/tags/2/");
        task.setTags(tags);
        task.setIs_all_day(is_all_day);
        if (!is_all_day) {
            Date d = new Date();
            d.setHours(start_hour);
            d.setMinutes(start_min);
            task.setBegin_datetime(TimeUtil.formatGMTDate(d));
            d.setHours(end_hour);
            d.setMinutes(end_min);
            task.setEnd_datetime(TimeUtil.formatGMTDate(d));
        }
        DB.schedules().safeSaveDBTaskAndFireEvent(task);

        LogUtil.e("updateAndFireEvent --> " + task);
        if (task.getUrl() == null) {
            // 离线创建的task是没有url的，这里要在服务器端新建一个一摸一样的，然后把url传过来
            RetrofitHelper.getTaskService().createTask(ModelUtil.toTask(task)) //获取Observable对象
                    .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                    .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                    .doOnNext(new Action1<Task>() {
                        @Override
                        public void call(Task task1) {
                            // 将笔记的url保存下来
                            task.setUrl(task1.getUrl());
                            DB.schedules().safeSaveDBTaskAndFireEvent(task);
                            LogUtil.e("保存任务信息到本地 safeSaveNote -->" + task.toString());
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
                            ToastUtil.show("云端没有该任务，补充添加[ 任务 ]同步失败");
                            LogUtil.e("云端没有该任务，补充添加[ 任务 ]同步失败" + e.toString());
                        }

                        @Override
                        public void onNext(Task note) {
                            //请求成功
                            ToastUtil.show("云端没有该任务，成功补充添加[ 任务 ]:" + content);
                            LogUtil.e("云端没有该任务，成功补充添加[ 任务 ]:" + note.toString());
                        }
                    });
        } else {
            RetrofitHelper.getTaskService().putTaskByUrl(task.getUrl(), ModelUtil.toTask(task)) //获取Observable对象
                    .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                    .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                    .doOnNext(new Action1<Task>() {
                        @Override
                        public void call(Task task) {
                            DB.schedules().safeSaveTaskAndFireEvent(task);
                            LogUtil.e("保存任务信息到本地" + task.toString());
                        }
                    }).observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                    .subscribe(new Subscriber<Task>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            //请求失败
                            ToastUtil.show("同步[ 任务 ]失败，保存到本地");
                            LogUtil.e(e.toString());
                        }

                        @Override
                        public void onNext(Task task) {
                            //请求成功
                            ToastUtil.show("成功更新[ 任务 ]:" + content);
                            LogUtil.e("请求成功" + task.toString());
                        }
                    });
        }
        finish();
    }

    private void onCreateTask() {
//        final ProgressDialog progressDialog = new ProgressDialog(DialogActivity.this, R.style.AppTheme_Dark_Dialog);
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
        if (!is_all_day) {
            Date d = new Date();
            d.setHours(start_hour);
            d.setMinutes(start_min);
            dbTask.setBegin_datetime(TimeUtil.formatGMTDate(d));
            d.setHours(end_hour);
            d.setMinutes(end_min);
            dbTask.setEnd_datetime(TimeUtil.formatGMTDate(d));
        }
        DB.schedules().safeSaveDBTaskAndFireEvent(dbTask);

        RetrofitHelper.getTaskService().createTask(ModelUtil.toTask(dbTask)) //获取Observable对象
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
                        ToastUtil.show("添加[ 任务 ]失败");
                        LogUtil.e(e.toString());
                    }

                    @Override
                    public void onNext(Task task) {
                        //请求成功
//                        progressDialog.dismiss();
                        ToastUtil.show("成功添加[ 任务 ]:" + content);
                        LogUtil.e("成功添加[ 任务 ]: " + task.toString());
                    }
                });
        finish();
    }

    private void onUpdateNote() {
        note.setTitle(title);
        note.setContent(content);
        ArrayList<String> tags = new ArrayList<>();
        tags.add("http://192.168.88.105:8000/tags/1/");
        tags.add("http://192.168.88.105:8000/tags/2/");
//        note.setTags(tags);
        note.setUpdate_datetime(TimeUtil.formatGMTDate(new Date()));
        DB.notes().updateAndFireEvent(note);

        if (note.getUrl() == null) {
            RetrofitHelper.getNoteService().createNote(ModelUtil.toNote(note)) //获取Observable对象
                    .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                    .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                    .doOnNext(new Action1<Note>() {
                        @Override
                        public void call(Note note1) {
                            // 将笔记的url保存下来
                            note.setUrl(note1.getUrl());
                            DB.notes().safeSaveDBNoteAndFireEvent(note);
                            LogUtil.e("保存任务信息到本地 safeSaveNote -->" + note1.toString());
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
                            ToastUtil.show("云端没有该笔记，补充添加[ 笔记 ]同步失败");
                            LogUtil.e("云端没有该笔记，补充添加[ 笔记 ]同步失败" + e.toString());
                        }

                        @Override
                        public void onNext(Note note) {
                            //请求成功
                            ToastUtil.show("云端没有该笔记，成功补充添加[ 笔记 ]:" + content);
                            LogUtil.e("云端没有该笔记，成功补充添加[ 笔记 ]:" + note.toString());
                        }
                    });
        } else {
            RetrofitHelper.getNoteService().putNoteByUrl(note.getUrl(), ModelUtil.toNote(note)) //获取Observable对象
                    .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                    .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                    .doOnNext(new Action1<Note>() {
                        @Override
                        public void call(Note note) {
                            // 将笔记的url保存下来
                            DB.notes().safeSaveNoteAndFireEvent(note);
                            LogUtil.e("保存任务信息到本地" + note.toString());
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
                            ToastUtil.show("更新[ 笔记 ]失败");
                            LogUtil.e("更新[ 笔记 ]失败:" + e.toString());
                        }

                        @Override
                        public void onNext(Note note) {
                            //请求成功
                            ToastUtil.show("成功更新[ 笔记 ]:" + content);
                            LogUtil.e("请求成功,成功更新[ 笔记 ]:" + note.toString());
                        }
                    });
        }
        finish();
    }

    private void onCreateNote() {
        DBNote dbNote = new DBNote();
        DBUser activeUser = DB.users().getActive();
        String owner = ModelUtil.getOwnerUrl(activeUser);
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
//        note.setTags(tags);
        DB.notes().saveAndFireEvent(dbNote);
        LogUtil.e("saveAndFireEvent() --> " + dbNote.toString());
        LogUtil.e("DB.notes() --> " + DB.notes().findAll().toString());

        RetrofitHelper.getNoteService().createNote(ModelUtil.toNote(dbNote)) //获取Observable对象
                .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .doOnNext(new Action1<Note>() {
                    @Override
                    public void call(Note note) {
                        dbNote.setUrl(note.getUrl());
                        DB.notes().safeSaveDBNoteAndFireEvent(dbNote);
                        LogUtil.e("保存任务信息到本地 safeSaveNote -->" + note.toString());
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
                        ToastUtil.show("添加[ 笔记 ]同步失败");
                        LogUtil.e(e.toString());
                    }

                    @Override
                    public void onNext(Note note) {
                        //请求成功
                        ToastUtil.show("成功添加[ 笔记 ]:" + content);
                        LogUtil.e("请求成功" + note.toString());
                    }
                });
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

    //</Event事件区>---只要存在事件监听代码就是----------------------------------------------------------


    //<内部类>---尽量少用-----------------------------------------------------------------------------

    //</内部类>---尽量少用----------------------------------------------------------------------------

    //<外部调用>--------------------------------------------------------------------------------------

    //</外部调用>-------------------------------------------------------------------------------------

    //<内部封装方法>-----------------------------------------------------------------------------------

    //</内部封装方法>----------------------------------------------------------------------------------
}
