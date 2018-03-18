package com.time.cat.ui.activity.viewtask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.time.cat.R;
import com.time.cat.ui.base.BaseActivity;
import com.time.cat.ui.base.mvp.presenter.ActivityPresenter;
import com.time.cat.ui.modules.notes.NotesFragment;
import com.time.cat.ui.modules.plans.FileListFragment;
import com.time.cat.ui.modules.routines.RoutinesFragment;
import com.time.cat.ui.modules.schedules.SchedulesFragment;
import com.time.cat.ui.modules.schedules_weekview.RoutinesWeekFragment;
import com.time.cat.util.view.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/23
 * @discription null
 * @usage null
 */
public class HistoryActivity extends BaseActivity implements ActivityPresenter, View.OnClickListener{
    @SuppressWarnings("unused")
    private static final String TAG = "HistoryActivity";


    //<启动方法>-------------------------------------------------------------------------------------
    /**
     * 启动这个Activity的Intent
     *
     * @param context 　上下文
     *
     * @return 返回intent实例
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, HistoryActivity.class);
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
        CardView cardView = new CardView(this);
        cardView.setRadius(ViewUtil.dp2px(10));
        View view = LayoutInflater.from(this).inflate(R.layout.activity_history, null, false);
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
    //</生命周期>------------------------------------------------------------------------------------


    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------
    private List<Fragment> fragmentList;

    private ImageView history_schedules;
    private ImageView history_notes;
    private ImageView history_routines;
    private ImageView history_plans;
    @Override
    public void initView() {//必须调用
        fragmentList = new ArrayList<>();
        fragmentList.add(new SchedulesFragment());
        updateViewPager(0);
        history_schedules = findViewById(R.id.history_schedules);
        history_notes = findViewById(R.id.history_notes);
        history_routines = findViewById(R.id.history_routines);
        history_plans = findViewById(R.id.history_plans);
    }

    private void updateViewPager(int id) {
        Fragment fragment = getFragments(id);
        for (Fragment fragment1:fragmentList) {
            getSupportFragmentManager().beginTransaction().remove(fragment1).commitNow();
        }
        fragmentList.clear();
        fragmentList.add(fragment);
        getSupportFragmentManager().beginTransaction().add(R.id.fragments_container, fragment).commitNow();
    }


    private Fragment getFragments(int id) {
        switch (id) {
            case 0: return new SchedulesFragment();
            case 1: return new RoutinesFragment();
            case 2: return new NotesFragment();
            case 3: return new FileListFragment();
        }
        return new RoutinesWeekFragment();
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
        history_schedules.setOnClickListener(this);
        history_routines.setOnClickListener(this);
        history_notes.setOnClickListener(this);
        history_plans.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history_schedules:
                updateViewPager(0);
                break;
            case R.id.history_routines:
                updateViewPager(1);
                break;
            case R.id.history_notes:
                updateViewPager(2);
                break;
            case R.id.history_plans:
                updateViewPager(3);
                break;
        }
    }

    //-//<Listener>------------------------------------------------------------------------------

    //-//</Listener>-----------------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------

    //</内部类>---尽量少用---------------------------------------------------------------------------

}
