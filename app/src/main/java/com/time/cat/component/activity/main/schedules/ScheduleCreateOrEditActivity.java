package com.time.cat.component.activity.main.schedules;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shang.commonjar.contentProvider.SPHelper;
import com.time.cat.R;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.mvp.presenter.ActivityPresenter;
import com.time.cat.util.ConstantUtil;
import com.time.cat.util.ViewUtil;

/**
 * @author dlink
 * @date 2018/2/7
 * @discription
 */
public class ScheduleCreateOrEditActivity extends BaseActivity implements ActivityPresenter, View.OnClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = "ScheduleCreateActivity";


    //<启动方法>-------------------------------------------------------------------------------------
    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------
    private int alpha;
    private int lastPickedColor;
    //</启动方法>------------------------------------------------------------------------------------
    private AppCompatImageView add_task_iv_cancel;
    private AppCompatImageView add_task_iv_success;
    //</生命周期>------------------------------------------------------------------------------------
    private EditText add_task_et_title;
    private EditText add_task_et_content;
    private TextView add_task_tv_important_urgent;
    private TextView add_task_tv_date;
    private TextView add_task_tv_time;
    private LinearLayout add_task_select_ll_important_urgent;
    private LinearLayout add_task_select_ll_date;
    private LinearLayout add_task_select_ll_time;

    /**
     * 启动这个Activity的Intent
     *
     * @param context 　上下文
     *
     * @return 返回intent实例
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, ScheduleCreateOrEditActivity.class);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alpha = SPHelper.getInt(ConstantUtil.TIMECAT_ALPHA, 100);
        lastPickedColor = SPHelper.getInt(ConstantUtil.TIMECAT_DIY_BG_COLOR, Color.parseColor("#fff7ca"));
        int value = (int) ((alpha / 100.0f) * 255);
        CardView cardView = new CardView(this);
        cardView.setRadius(ViewUtil.dp2px(20));
        cardView.setCardBackgroundColor(Color.argb(value, Color.red(lastPickedColor), Color.green(lastPickedColor), Color.blue(lastPickedColor)));
        View view = LayoutInflater.from(this).inflate(R.layout.items_add_task, null, false);
        cardView.addView(view);

        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.trans));
        setContentView(cardView);

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

    @Override
    public void initView() {//必须调用
        add_task_iv_cancel = findViewById(R.id.add_task_iv_cancel);
        add_task_iv_success = findViewById(R.id.add_task_iv_success);
        add_task_et_title = findViewById(R.id.add_task_et_title);
        add_task_et_content = findViewById(R.id.add_task_et_content);
        add_task_tv_important_urgent = findViewById(R.id.add_task_tv_important_urgent);
        add_task_tv_date = findViewById(R.id.add_task_tv_date);
        add_task_tv_time = findViewById(R.id.add_task_tv_time);
    }
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>-----------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-----------------------------------------
    @Override
    public void initData() {//必须调用

    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------


    //<Event事件区>---只要存在事件监听代码就是----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用
        add_task_tv_important_urgent.setOnClickListener(this);
        add_task_tv_date.setOnClickListener(this);
        add_task_tv_time.setOnClickListener(this);
    }

    //-//<Listener>------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_task_tv_important_urgent:
//                if (add_task_select_ll_important_urgent == null) {
//                    ViewStub viewStub = findViewById(R.id.items_add_task_select_important_urgent_view_stub);
//                    viewStub.inflate();
//                    add_task_select_ll_important_urgent = findViewById(R.id.add_task_select_ll_important_urgent);
//                }
//                add_task_select_ll_important_urgent.setVisibility(View.VISIBLE);
//                if (add_task_select_ll_date != null) {
//                    add_task_select_ll_date.setVisibility(View.GONE);
//                }
//                if (add_task_select_ll_time != null) {
//                    add_task_select_ll_time.setVisibility(View.GONE);
//                }
                Log.e(TAG, "create add_task_select_ll_important_urgent");
                //创建一个相对布局relative
                break;
            case R.id.add_task_tv_date:
//                if (add_task_select_ll_date == null) {
//                    ViewStub viewStub = findViewById(R.id.items_add_task_select_date_view_stub);
//                    viewStub.inflate();
//                    add_task_select_ll_date = findViewById(R.id.add_task_select_ll_date);
//                }
//                add_task_select_ll_date.setVisibility(View.VISIBLE);
//                if (add_task_select_ll_important_urgent != null) {
//                    add_task_select_ll_important_urgent.setVisibility(View.GONE);
//                }
//                if (add_task_select_ll_time != null) {
//                    add_task_select_ll_time.setVisibility(View.GONE);
//                }
                Log.e(TAG, "create add_task_select_ll_date");

                break;
            case R.id.add_task_tv_time:

//                if (add_task_select_ll_time == null) {
//                    ViewStub viewStub = findViewById(R.id.items_add_task_select_time_view_stub);
//                    viewStub.inflate();
//                    add_task_select_ll_time = findViewById(R.id.add_task_select_ll_time);
//                }
//                add_task_select_ll_time.setVisibility(View.VISIBLE);
//                if (add_task_select_ll_date != null) {
//                    add_task_select_ll_date.setVisibility(View.GONE);
//                }
//                if (add_task_select_ll_important_urgent != null) {
//                    add_task_select_ll_important_urgent.setVisibility(View.GONE);
//                }
                Log.e(TAG, "create add_task_select_ll_time");
                break;
        }
    }

    //-//</Listener>-----------------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------

    //</内部类>---尽量少用---------------------------------------------------------------------------

}
