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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.time.cat.AnimationSystem.ViewHelper;
import com.time.cat.R;
import com.time.cat.ThemeSystem.manager.ThemeManager;
import com.time.cat.ThemeSystem.utils.ThemeUtils;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.component.dialog.DialogThemeFragment;
import com.time.cat.mvp.presenter.ActivityPresenter;
import com.time.cat.mvp.presenter.OnViewClickListener;
import com.time.cat.mvp.view.CustomPagerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dlink
 * @date 2018/1/19
 */
@SuppressLint("SetTextI18n")
public class MainActivity extends BaseActivity implements ActivityPresenter, DialogThemeFragment.ClickListener, DrawerLayout.DrawerListener, ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener, HomeFragment.OnDateChangeListener {
    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";


    //<生命周期>-------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);
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
    private ActionBar ab;
    private TextView tvYear;
    private TextView tvMonth;
    private TextView tv1;
    private TextView tv2;
    private DrawerLayout drawerLayout;
    private ListView mLvLeftMenu;
    private CustomPagerView customPagerView;
    private CustomPagerViewAdapter customPagerViewAdapter;
    private BottomNavigationView navigation;

    @Override
    public void initView() {//必须调用
        drawerLayout = findViewById(R.id.fd);
        assert drawerLayout != null;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        mLvLeftMenu = findViewById(R.id.id_lv_left_menu);

        tvYear = findViewById(R.id.main_year_view);
        tvMonth = findViewById(R.id.main_month_view);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);

        customPagerView = findViewById(R.id.main_viewpager);
        navigation = findViewById(R.id.navigation);
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
        mLvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        drawerLayout.closeDrawers();
                        break;
                    case 2:
                        DialogThemeFragment themeDialog = new DialogThemeFragment();
                        themeDialog.setClickListener(MainActivity.this);
                        themeDialog.show(getSupportFragmentManager(), "theme");
                        drawerLayout.closeDrawers();
                        break;
                    case 3:
                        drawerLayout.closeDrawers();
                        break;
                    case 4:
                        drawerLayout.closeDrawers();
                        break;
                    case 5:
                        drawerLayout.closeDrawers();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 左上角的侧滑栏入口
     */
    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        customPagerViewAdapter = new CustomPagerViewAdapter(getSupportFragmentManager());
        customPagerViewAdapter.addFragment(homeFragment);
        assert customPagerView != null;
        customPagerView.setAdapter(customPagerViewAdapter);
        customPagerView.setCurrentItem(0);
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
        drawerLayout.setDrawerListener(this);
        customPagerView.addOnPageChangeListener(this);
        navigation.setOnNavigationItemSelectedListener(this);
        if (mViewClickListener != null) {
            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewClickListener.onView1Click();
                }
            });
            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewClickListener.onView2Click();
                }
            });
        }
    }

    //-//<Listener>------------------------------------------------------------------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //点击BottomNavigationView的Item项，切换ViewPager页面
        //menu/navigation.xml里加的android:orderInCategory属性就是下面item.getOrder()取的值
        customPagerView.setCurrentItem(item.getOrder());
        return true;
    }
    //-//</Listener>-----------------------------------------------------------------------------


    //-//<Activity>---------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                //Menu icon
                drawerLayout.openDrawer(Gravity.LEFT);
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


    //-//<DialogThemeFragment.ClickListener>------------------------------------------------------------
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onConfirm(int currentTheme) {
        if (ThemeManager.getTheme(MainActivity.this) != currentTheme) {
            ThemeManager.setTheme(MainActivity.this, currentTheme);
            ThemeUtils.refreshUI(MainActivity.this, new ThemeUtils.ExtraRefreshable() {
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
//        changeTheme();
    }
    //-//</DialogThemeFragment.ClickListener>-----------------------------------------------------------


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
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.e(TAG, "state" + state);
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
        Log.e(TAG, "\nscale:" + scale + "\nleftScale:" + leftScale + "\nrightScale:" + rightScale);

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
    @Override
    public void onDateChange(int year, int month) {
        tvYear.setText(year + getString(R.string.calendar_year));
        tvMonth.setText(month + "");
    }
    //-//</HomeFragment.OnDateChangeListener>-------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


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

    private OnViewClickListener mViewClickListener;

    public void setOnViewClickListener(OnViewClickListener viewClickListener) {
        mViewClickListener = viewClickListener;
    }
    //</内部类>---尽量少用---------------------------------------------------------------------------

}
