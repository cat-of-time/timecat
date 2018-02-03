package com.time.cat.component.activity.main;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.ThemeSystem.manager.ThemeManager;
import com.time.cat.ThemeSystem.utils.ThemeUtils;
import com.time.cat.TimeCatApp;
import com.time.cat.component.activity.main.listener.OnDateChangeListener;
import com.time.cat.component.activity.main.listener.OnViewClickListener;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.component.dialog.DialogThemeFragment;
import com.time.cat.mvp.model.Patient;
import com.time.cat.mvp.presenter.ActivityPresenter;
import com.time.cat.mvp.view.CustomPagerView;
import com.time.cat.util.AvatarMgr;
import com.time.cat.util.ScreenUtils;
import com.time.cat.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dlink
 * @date 2018/1/19
 */
@SuppressLint("SetTextI18n")
public class MainActivity extends BaseActivity implements ActivityPresenter, OnDateChangeListener, DialogThemeFragment.ClickListener, ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = "MainActivity";


    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarFullTransparent();
        initDrawer(savedInstanceState);
        //<功能归类分区方法，必须调用>-----------------------------------------------------------------
        initView();
        initData();
        initEvent();
        //</功能归类分区方法，必须调用>----------------------------------------------------------------
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Patient p = DB.patients().getActive(this);
//        Patient p = new Patient();
//        p.setAvatar(AvatarMgr.AVATAR_2);
//        p.setColor(4);
//        p.setName("测试");
//        p.setId((long) 5);
//        leftDrawer.onActivityResume(p);
//        active = true;
//
//        // process pending events
//        while (!pendingEvents.isEmpty()) {
//            Log.d(TAG, "Processing pending event...");
//            onEvent(pendingEvents.poll());
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //</生命周期>------------------------------------------------------------------------------------


    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码-----------------------------------
    private ActionBar ab;

    private Menu menu;
    Toolbar toolbar;
    private LeftDrawerView leftDrawer;

    private TextView tvYear;
    private TextView tvMonth;

    private CustomPagerView customPagerView;
    private CustomPagerViewAdapter customPagerViewAdapter;

    private BottomNavigationView navigation;

    @Override
    public void initView() {//必须调用
        switch (ThemeManager.getTheme(this)) {
            case ThemeManager.CARD_WHITE:
            case ThemeManager.CARD_THUNDER:
            case ThemeManager.CARD_TRANSPARENT:
                setStatusBarFontIconDark(true);
                break;
            default:
                setStatusBarFontIconDark(false);
        }

        tvYear = findViewById(R.id.main_year_view);
        tvMonth = findViewById(R.id.main_month_view);

        customPagerView = findViewById(R.id.main_viewpager);
        navigation = findViewById(R.id.main_navigation);

        setToolBar();
        setViewPager();
    }

    /**
     * 左上角的侧滑栏入口
     */
    private void setToolBar() {
        toolbar = findViewById(R.id.main_toolbar);
        //1.先设置toolbar的高度
//        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
//        int statusBarHeight = ScreenUtils.getStatusBarHeight(this);
//        Log.e(TAG, ""+statusBarHeight+params);
//        params.height += statusBarHeight;
//        toolbar.setLayoutParams(params);
        //2.设置paddingTop，以达到状态栏不遮挡toolbar的内容。
//        toolbar.setPadding(
//                toolbar.getPaddingLeft(),
//                toolbar.getPaddingTop() + ScreenUtils.getStatusBarHeight(this),
//                toolbar.getPaddingRight(),
//                toolbar.getPaddingBottom());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, ScreenUtils.getStatusBarHeight(this), 0, 0);
        toolbar.setLayoutParams(layoutParams);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setTitle("");
    }

    /**
     * 多界面
     */
    private void setViewPager() {
        SchedulesFragment schedulesFragment = new SchedulesFragment();
        schedulesFragment.setOnDateChangeListener(this);
        setOnViewClickListener(schedulesFragment);

        RoutinesFragment routinesFragment = new RoutinesFragment();
        PlansFragment plansFragment = new PlansFragment();

        fragmentNames = new String[]{"SchedulesFragment", "RoutinesFragment", "PlansFragment"};

        customPagerViewAdapter = new CustomPagerViewAdapter(getSupportFragmentManager());
        customPagerViewAdapter.addFragment(schedulesFragment);
        customPagerViewAdapter.addFragment(routinesFragment);
        customPagerViewAdapter.addFragment(plansFragment);
        assert customPagerView != null;
        customPagerView.setAdapter(customPagerViewAdapter);
        customPagerView.setCurrentItem(0);
    }

    /**
     * 侧滑栏
     */
    private void initDrawer(Bundle savedInstanceState) {
        leftDrawer = new LeftDrawerView(this, toolbar);
        leftDrawer.init(savedInstanceState);
    }

    public void showPagerItem(int position) {
        showPagerItem(position, true);
    }

    public void showPagerItem(int position, boolean updateDrawer) {
        if (position >= 0 && position < customPagerView.getChildCount()) {
            customPagerView.setCurrentItem(position);
            if (updateDrawer) {
                leftDrawer.onPagerPositionChange(position);
            }
        }
    }
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>--------------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码--------------------------------------------
    @Override
    public void initData() {//必须调用

    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-------------------------------------------


    //<Event事件区>---只要存在事件监听代码就是-----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用
        customPagerView.addOnPageChangeListener(this);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    //-//<BottomNavigationView.OnNavigationItemSelectedListener>------------------------------------
    String[] fragmentNames;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //点击BottomNavigationView的Item项，切换ViewPager页面
        //menu/main_navigation.xml里加的android:orderInCategory属性就是下面item.getOrder()取的值
        customPagerView.setCurrentItem(item.getOrder());
        adjustActionBar(fragmentNames[item.getOrder()]);
        return true;
    }

    /**
     * 根据当前fragment来动态绑定右上角的button
     *
     * @param currentFragment 当前fragment
     */
    private void adjustActionBar(String currentFragment) {
        switch (currentFragment) {
            case "SchedulesFragment":
                if (menu != null) {
                    menu.setGroupVisible(R.id.main_menu_schedulesFragment, true);
                    menu.setGroupVisible(R.id.main_menu_routinesFragment, false);
                    menu.setGroupVisible(R.id.main_menu_plansFragment, false);
                    Log.i(TAG, "main_menu_schedulesFragment --> setGroupVisible");
                    //不是今天 ? 显示 : 不显示
                    if (!isToday) {
                        menu.findItem(R.id.main_menu_today).setVisible(true);
                    } else {
                        menu.findItem(R.id.main_menu_today).setVisible(false);
                    }
                }
                break;
            case "RoutinesFragment":
                if (menu != null) {
                    menu.setGroupVisible(R.id.main_menu_schedulesFragment, false);
                    menu.setGroupVisible(R.id.main_menu_routinesFragment, true);
                    menu.setGroupVisible(R.id.main_menu_plansFragment, false);
                    Log.i(TAG, "main_menu_routinesFragment --> setGroupVisible");
                }
                break;
            case "PlansFragment":
                if (menu != null) {
                    menu.setGroupVisible(R.id.main_menu_schedulesFragment, false);
                    menu.setGroupVisible(R.id.main_menu_routinesFragment, false);
                    menu.setGroupVisible(R.id.main_menu_plansFragment, true);
                    Log.i(TAG, "main_menu_plansFragment --> setGroupVisible");
                }
                break;
        }
    }
    //-//</BottomNavigationView.OnNavigationItemSelectedListener>-----------------------------------


    //-//<Activity>---------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                //Menu icon
                leftDrawer.getDrawer().openDrawer();
                return true;
            case R.id.main_menu_today:
                if (mViewClickListener != null) {
                    mViewClickListener.onViewTodayClick();
                }
                return true;
            case R.id.main_menu_change_theme:
                if (mViewClickListener != null) {
                    mViewClickListener.onViewChangeMarkThemeClick();
                }
                return true;
            case R.id.main_menu_1:
                if (mViewClickListener != null) {
                    mViewClickListener.onViewTodayClick();
                }
                return true;
            case R.id.main_menu_2:
                if (mViewClickListener != null) {
                    mViewClickListener.onViewChangeMarkThemeClick();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static final int REQUEST_LOGIN = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // 设置用户登录后的界面
                ToastUtil.show("登录成功！");
            }
        }
    }
    //-//</Activity>--------------------------------------------------------------------------------


    //-//<DialogThemeFragment.ClickListener>--------------------------------------------------------
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onConfirm(int currentTheme) {
        Log.e(TAG, "onConfirm----------------->");
        if (ThemeManager.getTheme(this) != currentTheme) {
            ThemeManager.setTheme(this, currentTheme);
            switch (currentTheme) {
                case ThemeManager.CARD_WHITE:
                case ThemeManager.CARD_THUNDER:
                case ThemeManager.CARD_TRANSPARENT:
                    setStatusBarFontIconDark(true);
                    break;
                default:
                    setStatusBarFontIconDark(false);
            }
            Log.e(TAG, "setTheme------------------>");
            ThemeUtils.refreshUI(this, new ThemeUtils.ExtraRefreshable() {
                @Override
                public void refreshGlobal(Activity activity) {
                    //for global setting, just do once
                    if (Build.VERSION.SDK_INT >= 21) {
                        final MainActivity context = MainActivity.this;
                        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(null, null, ThemeUtils.getThemeAttrColor(context, android.R.attr.colorPrimary));
                        setTaskDescription(taskDescription);
                        getWindow().setStatusBarColor(ThemeUtils.getColorById(context, R.color.theme_color_primary));

                    }
                }

                @Override
                public void refreshSpecificView(View view) {
                }
            });
        }
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        Log.e(TAG, "set to transparent");

//        setupStatusBar(TimeCatApp.getInstance().replaceColor(this, 0xd20000));
//        Log.e(TAG, "set to "+ TimeCatApp.getInstance().replaceColor(this, 0xd20000));

        Patient p = new Patient();
        p.setAvatar(AvatarMgr.AVATAR_2);
        p.setColor(TimeCatApp.getInstance().replaceColor(this, 0xd20000));
        p.setName("测试");
        p.setId((long) 5);
        leftDrawer.updateHeaderBackground(p);
//        Log.e(TAG, "set getDrawer color");

    }
    //-//</DialogThemeFragment.ClickListener>-------------------------------------------------------


    //-//<ViewPager.OnPageChangeListener>-----------------------------------------------------------
    /**
     * canOpenDrawer[0]是控制侧滑栏抽屉何时打开的重要参数
     * {state-[1开始2进行中0完毕]
     * -[1->2->0 viewpage间的切换, 1->0 viewpage与drawer间的切换] }
     */
    final int[] canOpenDrawer = {0};

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        //页面滑动的时候，改变BottomNavigationView的Item高亮
        navigation.getMenu().getItem(position).setChecked(true);
        adjustActionBar(fragmentNames[position]);
        leftDrawer.onPagerPositionChange(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        Log.e(TAG, "state" + state);
        if (customPagerView.getCurrentItem() == 0) {
            switch (state) {
                case 1:
                    canOpenDrawer[0] = 0;
                    break;
                case 2:
                    canOpenDrawer[0]--;
                    break;
                case 0:
                    canOpenDrawer[0]++;
                    break;
                default:
                    break;
            }
            if (canOpenDrawer[0] == 1) {
                // 只要有canOpenDrawer[0] == 1,一定已经执行switch(state)case 0,且state==0时手势完毕
                leftDrawer.getDrawer().openDrawer();
            }
        }
    }
    //-//</ViewPager.OnPageChangeListener>----------------------------------------------------------


    //-//<SchedulesFragment.OnDateChangeListener>--------------------------------------------------------
    private boolean isToday;

    @Override
    public void onDateChange(int year, int month, boolean isToday) {
        tvYear.setText(year + getString(R.string.calendar_year));
        tvMonth.setText(month + "");
        this.isToday = isToday;
        if (menu != null) {
//            Log.i(TAG, "menu != null --> setting main_menu_today");
            //不是今天 ? 显示 : 不显示
            if (!isToday) {
                menu.findItem(R.id.main_menu_today).setVisible(true);
            } else {
                menu.findItem(R.id.main_menu_today).setVisible(false);
            }
        }
    }
    //-//</SchedulesFragment.OnDateChangeListener>-------------------------------------------------------


    //-//<View.OnClickListener>---------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }
    //-//</View.OnClickListener>--------------------------------------------------------------------


    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<回调接口>-------------------------------------------------------------------------------------
    private OnViewClickListener mViewClickListener;

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mViewClickListener = onViewClickListener;
    }
    //</回调接口>-------------------------------------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------
    static class CustomPagerViewAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();

        public CustomPagerViewAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

    }
    //</内部类>---尽量少用---------------------------------------------------------------------------

}
