package com.time.cat.component.activity.addtask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.mvp.presenter.ActivityPresenter;
import com.time.cat.mvp.view.NoHorizontalScrollerViewPager;
import com.time.cat.mvp.view.emotion.fragment.EmotiomComplateFragment;
import com.time.cat.mvp.view.emotion.EmotionKeyboard;
import com.time.cat.mvp.view.emotion.fragment.Fragment1;
import com.time.cat.mvp.view.emotion.fragment.FragmentFactory;
import com.time.cat.mvp.view.emotion.adapter.HorizontalRecyclerviewAdapter;
import com.time.cat.mvp.view.emotion.adapter.NoHorizontalScrollerVPAdapter;
import com.time.cat.mvp.view.emotion.model.ImageModel;
import com.time.cat.mvp.view.keyboardManager.SmartKeyboardManager;
import com.time.cat.mvp.view.richText.TEditText;
import com.time.cat.util.EmotionUtils;
import com.time.cat.util.GlobalOnItemClickManager;
import com.time.cat.util.SharedPreferencedUtils;
import com.time.cat.util.ToastUtil;
import com.time.cat.util.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author dlink
 * @date 2018/2/14
 * @discription
 */
public class DialogActivity extends BaseActivity implements
                                                 ActivityPresenter,
                                                 View.OnClickListener{
    @SuppressWarnings("unused")
    private static final String TAG = "DialogActivity ";


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
        String[] text_color_set = new String[]{
                "#f44336", "#ff9800", "#2196f3", "#4caf50"
        };
        String[] background_color_set = new String[]{
                "#50f44336", "#50ff9800", "#502196f3", "#504caf50"
        };
        String[] label_str_set = new String[] {
                "重要且紧急", "重要不紧急", "紧急不重要", "不重要不紧急",
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
                    ToastUtil.show("important_urgent_label == " + finalI);
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
                "Picture 7", "Picture 8", "其他"
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
        dialog_add_task_select_gv_date.setAdapter(saImageItems);
        dialog_add_task_select_gv_date.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 根据元素位置获取对应的值
                HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);

                String itemText=(String)item.get(TEXT_ITEM);
                Object object=item.get(IMAGE_ITEM);
                ToastUtil.show("You Select "+itemText);
            }
        });
    }

    /**
     * 设置time选择面板
     */
    private void setSelectTimePanel() {
        select_ll_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_tv_start.setTextColor(getResources().getColor(R.color.blue));
                select_tv_start_time.setTextColor(getResources().getColor(R.color.black));
                select_tv_end.setTextColor(getResources().getColor(R.color.gray));
                select_tv_end_time.setTextColor(getResources().getColor(R.color.gray));
                dialog_add_task_tv_time.setText(select_tv_start_time.getText() + "-" + select_tv_end_time.getText());
            }
        });
        select_ll_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_tv_start.setTextColor(getResources().getColor(R.color.gray));
                select_tv_start_time.setTextColor(getResources().getColor(R.color.gray));
                select_tv_end.setTextColor(getResources().getColor(R.color.blue));
                select_tv_end_time.setTextColor(getResources().getColor(R.color.black));
                dialog_add_task_tv_time.setText(select_tv_start_time.getText() + "-" + select_tv_end_time.getText());
            }
        });
        select_tv_all_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
    }

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
        EmotiomComplateFragment f1 = (EmotiomComplateFragment) factory.getFragment(EmotionUtils.EMOTION_CLASSIC_TYPE);
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
     * 是否拦截返回键操作，如果此时表情布局未隐藏，先隐藏表情布局
     *
     * @return true则隐藏表情布局，拦截返回键操作
     * false 则不拦截返回键操作
     */
//    public boolean isInterceptBackPress() {
//        return mEmotionKeyboard.interceptBackPress();
//    }
    /**
     * 设置软键盘和选择面板的平滑交互
     */
    private void setKeyboardManager() {
        EmotionKeyboard.with(this)
                .setEmotionView(dialog_add_task_ll_extra)//绑定表情面板
                .bindToContent(dialog_add_task_ll_content)//绑定内容view
                .bindToEditText(dialog_add_task_et_title)//绑定EditView
                .bindToEmotionButton(dialog_add_task_tv_tag)//绑定表情按钮
                .build();
        SmartKeyboardManager mSmartKeyboardManager = new SmartKeyboardManager.Builder(this)
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
    int important_urgent_label;
    @Override
    public void initData() {//必须调用
        important_urgent_label = 0;
    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------


    //<Event事件区>---只要存在事件监听代码就是----------------------------------------------------------
    boolean isSelfEdit = false;
    //用户是否编辑了title，编辑即视为有自定义的需求，取用户自定义的title，不再同步content里的到title

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
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog_add_task_type_note.setOnClickListener(this);
        dialog_add_task_type_task.setOnClickListener(this);
        dialog_add_task_type_clock.setOnClickListener(this);
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
                dialog_add_task_tv_important_urgent.setVisibility(View.GONE);
                dialog_add_task_tv_date.setVisibility(View.GONE);
                dialog_add_task_tv_time.setVisibility(View.GONE);
                dialog_add_task_tv_remind.setVisibility(View.GONE);
                dialog_add_task_tv_tag.setVisibility(View.VISIBLE);
                type = Type.NOTE;
                dialog_add_task_type_note.setTextColor(Color.parseColor("#ee000000"));
                dialog_add_task_type_task.setTextColor(Color.parseColor("#5e000000"));
                dialog_add_task_type_clock.setTextColor(Color.parseColor("#5e000000"));
                dialog_add_task_type_note.setTextSize(18);
                dialog_add_task_type_task.setTextSize(14);
                dialog_add_task_type_clock.setTextSize(14);
                break;
            case R.id.dialog_add_task_type_task:
                dialog_add_task_tv_important_urgent.setVisibility(View.VISIBLE);
                dialog_add_task_tv_date.setVisibility(View.VISIBLE);
                dialog_add_task_tv_time.setVisibility(View.VISIBLE);
                dialog_add_task_tv_remind.setVisibility(View.GONE);
                dialog_add_task_tv_tag.setVisibility(View.VISIBLE);
                type = Type.TASK;
                dialog_add_task_type_note.setTextColor(Color.parseColor("#5e000000"));
                dialog_add_task_type_task.setTextColor(Color.parseColor("#ee000000"));
                dialog_add_task_type_clock.setTextColor(Color.parseColor("#5e000000"));
                dialog_add_task_type_note.setTextSize(14);
                dialog_add_task_type_task.setTextSize(18);
                dialog_add_task_type_clock.setTextSize(14);
                break;
            case R.id.dialog_add_task_type_clock:
                dialog_add_task_tv_important_urgent.setVisibility(View.VISIBLE);
                dialog_add_task_tv_date.setVisibility(View.VISIBLE);
                dialog_add_task_tv_time.setVisibility(View.VISIBLE);
                dialog_add_task_tv_remind.setVisibility(View.VISIBLE);
                dialog_add_task_tv_tag.setVisibility(View.VISIBLE);
                type = Type.CLOCK;
                dialog_add_task_type_note.setTextColor(Color.parseColor("#5e000000"));
                dialog_add_task_type_task.setTextColor(Color.parseColor("#5e000000"));
                dialog_add_task_type_clock.setTextColor(Color.parseColor("#ee000000"));
                dialog_add_task_type_note.setTextSize(14);
                dialog_add_task_type_task.setTextSize(14);
                dialog_add_task_type_clock.setTextSize(18);
                break;
            case R.id.dialog_add_task_footer_bt_submit:
                String s = "";
                switch (type) {
                    case NOTE:
                        s = "笔记";
                        break;
                    case TASK:
                        s = "任务";
                        break;
                    case CLOCK:
                        s = "时钟";
                        break;
                }
                dialog_add_task_footer_bt_submit.setClickable(false);
                ToastUtil.show("成功添加["+ s +"]:" + dialog_add_task_et_content.getText().toString());
                finish();
                break;
        }
    }
    //-//</View.OnClickListener>--------------------------------------------------------------------


    //-//<Activity>---------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    //-//</Activity>-------------------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------

    //</内部类>---尽量少用---------------------------------------------------------------------------

}
