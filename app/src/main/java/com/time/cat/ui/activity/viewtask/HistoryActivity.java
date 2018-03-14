package com.time.cat.ui.activity.viewtask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.time.cat.R;
import com.time.cat.ui.base.BaseActivity;
import com.time.cat.ui.base.mvp.presenter.ActivityPresenter;
import com.time.cat.ui.modules.notes.NotesFragment;
import com.time.cat.ui.modules.schedules.SchedulesFragment;
import com.time.cat.ui.modules.schedules_weekview.WeekFragmentsHolder;
import com.time.cat.util.UrlCountUtil;
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
public class HistoryActivity extends BaseActivity implements ActivityPresenter{
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
//        setContentView(R.layout.activity_main);
//        getWindow().setBackgroundDrawableResource(R.color.background_material_light_1);

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
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<Fragment> fragmentList;
    private List<String> fragmentTitles;

    @Override
    public void initView() {//必须调用
        initFragments();
        initViewPager();
        initIndiator();
        viewPager.setCurrentItem(1);
    }

    private void initViewPager() {
        viewPager = findViewById(R.id.container);
//        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return fragmentTitles.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 3) {
                    fragmentList.get(position).setUserVisibleHint(true);
                }
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_FRAGMENT_SWITCHES);
                super.onPageSelected(position);
            }
        });

    }

    private void initFragments() {
        fragmentList = new ArrayList<>();
        fragmentTitles = new ArrayList<>();

        fragmentList.add(new SchedulesFragment());
        fragmentList.add(new NotesFragment());
        WeekFragmentsHolder weekFragmentsHolder = new WeekFragmentsHolder();
        Bundle bundle = new Bundle();
//        bundle.putString(WEEK_START_DATE_TIME, "0");
        weekFragmentsHolder.setArguments(bundle);
        fragmentList.add(weekFragmentsHolder);


        fragmentTitles.add("任务");
        fragmentTitles.add("笔记");
        fragmentTitles.add("计划");
    }

    private void initIndiator() {
        tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
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

    }

    //-//<Listener>------------------------------------------------------------------------------
    //-//</Listener>-----------------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------

    //</内部类>---尽量少用---------------------------------------------------------------------------

}
