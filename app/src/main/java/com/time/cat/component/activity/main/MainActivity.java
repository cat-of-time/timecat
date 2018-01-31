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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.time.cat.AnimationSystem.ViewHelper;
import com.time.cat.R;
import com.time.cat.ThemeSystem.manager.ThemeManager;
import com.time.cat.ThemeSystem.utils.ThemeUtils;
import com.time.cat.component.activity.main.adapter.MenuItemAdapter;
import com.time.cat.component.activity.main.listener.OnDateChangeListener;
import com.time.cat.component.activity.main.listener.OnViewClickListener;
import com.time.cat.component.activity.setting.SettingActivity;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.component.dialog.DialogThemeFragment;
import com.time.cat.mvp.presenter.ActivityPresenter;
import com.time.cat.mvp.view.CustomPagerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dlink
 * @date 2018/1/19
 */
@SuppressLint("SetTextI18n")
public class MainActivity extends BaseActivity implements ActivityPresenter, OnDateChangeListener,
        DialogThemeFragment.ClickListener, DrawerLayout.DrawerListener, ViewPager.OnPageChangeListener,
        BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";


    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);
        getWindow().setBackgroundDrawableResource(R.color.background_material_light_1);
        setStatusBarFullTransparent();
//        setDrawerLayoutFitSystemWindow();

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





    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码-----------------------------------
    private ActionBar ab;
    private DrawerLayout drawerLayout;
    private ListView mLvLeftMenu;

    private Menu menu;

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

        drawerLayout = findViewById(R.id.fd);
        assert drawerLayout != null;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        mLvLeftMenu = findViewById(R.id.id_lv_left_menu);

        tvYear = findViewById(R.id.main_year_view);
        tvMonth = findViewById(R.id.main_month_view);

        customPagerView = findViewById(R.id.main_viewpager);
        navigation = findViewById(R.id.main_navigation);
        setUpDrawer();
        setToolBar();
        setViewPager();
    }

    /**
     * 侧滑栏
     */
    private void setUpDrawer() {
        LayoutInflater inflater = LayoutInflater.from(this);
        mLvLeftMenu.addHeaderView(inflater.inflate(R.layout.nav_header_main, mLvLeftMenu, false));
        mLvLeftMenu.setAdapter(new MenuItemAdapter(this));
    }

    /**
     * 左上角的侧滑栏入口
     */
    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setTitle("");
    }

    /**
     * 多界面
     */
    private void setViewPager() {
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setOnDateChangeListener(this);
        setOnViewClickListener(homeFragment);

        DashboardFragment dashboardFragment = new DashboardFragment();
        DashboardFragment dashboardFragment2 = new DashboardFragment();

        fragmentNames = new String[]{"HomeFragment", "DashboardFragment", "DashboardFragment2"};

        customPagerViewAdapter = new CustomPagerViewAdapter(getSupportFragmentManager());
        customPagerViewAdapter.addFragment(homeFragment);
        customPagerViewAdapter.addFragment(dashboardFragment);
        customPagerViewAdapter.addFragment(dashboardFragment2);
        assert customPagerView != null;
        customPagerView.setAdapter(customPagerViewAdapter);
        customPagerView.setCurrentItem(0);
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
        mLvLeftMenu.setOnItemClickListener(this);
        drawerLayout.setDrawerListener(this);
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
     * @param currentFragment 当前fragment
     */
    private void adjustActionBar(String currentFragment) {
        switch (currentFragment) {
            case "HomeFragment":
                if (menu != null) {
                    menu.setGroupVisible(R.id.main_menu_homeFragment, true);
                    menu.setGroupVisible(R.id.main_menu_dashboardFragment, false);
                    menu.setGroupVisible(R.id.main_menu_dashboardFragment2, false);
                    Log.i(TAG, "main_menu_homeFragment --> setGroupVisible");
                    //不是今天 ? 显示 : 不显示
                    if (!isToday) {
                        menu.findItem(R.id.main_menu_today).setVisible(true);
                    } else {
                        menu.findItem(R.id.main_menu_today).setVisible(false);
                    }
                }
                break;
            case "DashboardFragment":
                if (menu != null) {
                    menu.setGroupVisible(R.id.main_menu_homeFragment, false);
                    menu.setGroupVisible(R.id.main_menu_dashboardFragment, true);
                    menu.setGroupVisible(R.id.main_menu_dashboardFragment2, false);
                    Log.i(TAG, "main_menu_dashboardFragment --> setGroupVisible");
                }
                break;
            case "DashboardFragment2":
                if (menu != null) {
                    menu.setGroupVisible(R.id.main_menu_homeFragment, false);
                    menu.setGroupVisible(R.id.main_menu_dashboardFragment, false);
                    menu.setGroupVisible(R.id.main_menu_dashboardFragment2, true);
                    Log.i(TAG, "main_menu_dashboardFragment2 --> setGroupVisible");
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
                drawerLayout.openDrawer(Gravity.LEFT);
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

    private static final int REQUEST_RECHARGE = 1;
    private static final int REQUEST_WITHDRAW = 2;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
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
                    }
            );
        }
        getWindow().setStatusBarColor(Color.TRANSPARENT);
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
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        }
    }
    //-//</ViewPager.OnPageChangeListener>----------------------------------------------------------


    //-//<DrawerLayout.DrawerListener>--------------------------------------------------------------
    /**
     * Called when a drawer's position changes.
     *
     * @param drawerView  The child view that was moved
     * @param slideOffset The new offset of this drawer within its range, from 0-1
     */
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        View mContent = drawerLayout.getChildAt(0);
        View mMenu = drawerView;
        float scale = 1 - slideOffset;
        float rightScale = 0.8f + scale * 0.2f;
        float leftScale = 1 - 0.3f * scale;
//        Log.e(TAG, "\nscale:" + scale + "\nleftScale:" + leftScale + "\nrightScale:" + rightScale);

        ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
        ViewHelper.setTranslationX(mContent, mMenu.getMeasuredWidth() * (1 - scale));

        mContent.invalidate();
    }
    /**
     * Called when a drawer has settled in a completely open state.
     * The drawer is interactive at this point.
     *
     * @param drawerView Drawer view that is now open
     */
    @Override
    public void onDrawerOpened(View drawerView) {

    }
    /**
     * Called when a drawer has settled in a completely closed state.
     *
     * @param drawerView Drawer view that is now closed
     */
    @Override
    public void onDrawerClosed(View drawerView) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
    }
    /**
     * Called when the drawer motion state changes. The new state will
     * be one of {STATE_IDLE, STATE_DRAGGING, STATE_SETTLING}.
     *
     * @param newState The new drawer motion state
     */
    @Override
    public void onDrawerStateChanged(int newState) {

    }
    //-//</DrawerLayout.DrawerListener>-------------------------------------------------------------


    //-//<HomeFragment.OnDateChangeListener>--------------------------------------------------------
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
    //-//</HomeFragment.OnDateChangeListener>-------------------------------------------------------


    //-//<View.OnClickListener>---------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }
    //-//</View.OnClickListener>--------------------------------------------------------------------


    //-//<View.OnClickListener>---------------------------------------------------------------------
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {
            case 1:
                drawerLayout.closeDrawers();
                break;
            case 2:
                DialogThemeFragment themeDialog = new DialogThemeFragment();
                themeDialog.setClickListener(this);
                themeDialog.show(getSupportFragmentManager(), "theme");
                drawerLayout.closeDrawers();
                break;
            case 3:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawers();
                break;
            case 4:
                drawerLayout.closeDrawers();
                finish();
                break;
            default:
                break;
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
