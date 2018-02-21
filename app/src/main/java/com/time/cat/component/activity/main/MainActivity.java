package com.time.cat.component.activity.main;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.time.cat.R;
import com.time.cat.ThemeSystem.ThemeManager;
import com.time.cat.ThemeSystem.utils.ThemeUtils;
import com.time.cat.component.activity.about.SchedulesHelpActivity;
import com.time.cat.component.activity.addtask.DialogActivity;
import com.time.cat.component.activity.main.listener.OnDateChangeListener;
import com.time.cat.component.activity.main.listener.OnViewClickListener;
import com.time.cat.component.activity.main.routines.RoutinesListFragment;
import com.time.cat.component.activity.main.schedules.SchedulesFragment;
import com.time.cat.component.activity.main.viewmanager.FabMenuManager;
import com.time.cat.component.activity.main.viewmanager.LeftDrawerManager;
import com.time.cat.component.activity.user.LoginActivity;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.component.dialog.DialogThemeFragment;
import com.time.cat.database.DB;
import com.time.cat.events.PersistenceEvents;
import com.time.cat.mvp.model.DBmodel.DBUser;
import com.time.cat.mvp.presenter.ActivityPresenter;
import com.time.cat.mvp.view.navigation.SpecialTab;
import com.time.cat.mvp.view.navigation.SpecialTabRound;
import com.time.cat.mvp.view.viewpaper.CustomPagerView;
import com.time.cat.util.override.ToastUtil;
import com.time.cat.util.view.ScreenUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

/**
 * @author dlink
 * @date 2018/1/19
 */
@SuppressLint("SetTextI18n")
public class MainActivity extends BaseActivity implements
                                               ActivityPresenter,
                                               OnDateChangeListener,
                                               DialogThemeFragment.ClickListener,
                                               ViewPager.OnPageChangeListener,
                                               View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOGIN = 0;
    /**
     * canOpenDrawer[0]是控制侧滑栏抽屉何时打开的重要参数
     * {state-[1开始2进行中0完毕]
     * -[1->2->0 viewpage间的切换, 1->0 viewpage与drawer间的切换] }
     */
    final int[] canOpenDrawer = {0};
    Toolbar toolbar;
    String[] fragmentNames;
    boolean active = false;
    private ActionBar ab;
    private Menu menu;
    private LeftDrawerManager leftDrawer;

    private TextView tvYear;
    private TextView tvMonth;

    private CustomPagerView customPagerView;
    private CustomPagerViewAdapter customPagerViewAdapter;

    private PageNavigationView navigation;
    private NavigationController navigationController;

    private FloatingActionButton fab;
    private FloatingActionsMenu addButton;
    private FabMenuManager fabMgr;
    private boolean isToday;
    private Handler handler;
    private Queue<Object> pendingEvents = new LinkedList<>();
    private DBUser activeUser;





    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarFullTransparent();
        initDrawer(savedInstanceState);
        handler = new Handler();
        activeUser = DB.users().getActive(this);
        ThemeManager.setTheme(this, activeUser.color());

        //<功能归类分区方法，必须调用>-----------------------------------------------------------------
        initView();
        initData();
        initEvent();
        //</功能归类分区方法，必须调用>----------------------------------------------------------------
    }

    @Override
    protected void onResume() {
        super.onResume();
        DBUser u = DB.users().getActive(this);
        leftDrawer.onActivityResume(u);
        active = true;
        refreshTheme(this, u.color());

        // process pending events
        while (!pendingEvents.isEmpty()) {
            Log.d(TAG, "Processing pending event...");
            onEvent(pendingEvents.poll());
        }
    }

    @Override
    protected void onPause() {
        active = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //</生命周期>------------------------------------------------------------------------------------






    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码-----------------------------------
    @Override
    public void initView() {//必须调用
        switch (ThemeManager.getTheme(this)) {
            case ThemeManager.CARD_WHITE:
            case ThemeManager.CARD_THUNDER:
            case ThemeManager.CARD_MAGENTA:
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
//        setFab();不要这个了
        setNavigationBar();
    }

    /**
     * 侧滑栏
     */
    private void initDrawer(Bundle savedInstanceState) {
        leftDrawer = new LeftDrawerManager(this, toolbar);
        leftDrawer.init(savedInstanceState);
    }

    /**
     * 左上角的侧滑栏入口
     */
    private void setToolBar() {
        toolbar = findViewById(R.id.main_toolbar);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, ScreenUtil.getStatusBarHeight(this), 0, 0);
        toolbar.setLayoutParams(layoutParams);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        assert ab != null;
        ab.setTitle(null);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    /**
     * 多界面
     */
    private void setViewPager() {
        SchedulesFragment schedulesFragment = new SchedulesFragment();
        schedulesFragment.setOnDateChangeListener(this);
        setOnViewClickListener(schedulesFragment);

//        RoutinesFragment routinesFragment = new RoutinesFragment();
        RoutinesListFragment routinesListFragment = new RoutinesListFragment();
        NotesFragment notesFragment = new NotesFragment();
        PlansFragment plansFragment = new PlansFragment();

        fragmentNames = new String[]{"SchedulesFragment", "RoutinesFragment", "NotesFragment", "PlansFragment"};

        customPagerViewAdapter = new CustomPagerViewAdapter(getSupportFragmentManager());
        customPagerViewAdapter.addFragment(schedulesFragment);
        customPagerViewAdapter.addFragment(routinesListFragment);
        customPagerViewAdapter.addFragment(notesFragment);
        customPagerViewAdapter.addFragment(plansFragment);
        assert customPagerView != null;
        customPagerView.setAdapter(customPagerViewAdapter);
        customPagerView.setCurrentItem(0);
    }

    private void setFab() {
        addButton = findViewById(R.id.fab_menu);
        fab = findViewById(R.id.add_button);
        fabMgr = new FabMenuManager(fab, addButton, leftDrawer, this);
        fabMgr.init();

        fabMgr.onUserUpdate(activeUser);
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

    private void setNavigationBar() {
        navigationController = navigation.custom()
                .addItem(newItem(
                        R.drawable.ic_schedules_black_24dp,
                        R.drawable.ic_schedules_blue_24dp,
                        getResources().getString(R.string.title_schedules)
                ))
                .addItem(newItem(
                        R.drawable.ic_routines_black_24dp,
                        R.drawable.ic_routines_blue_24dp,
                        getResources().getString(R.string.title_routines)
                ))
                .addItem(newRoundItem(
                        R.drawable.ic_add_black_24dp,
                        R.drawable.ic_add_blue_24dp,
                        getResources().getString(R.string.title_add)
                ))
                .addItem(newItem(
                        R.drawable.ic_notes_black_24dp,
                        R.drawable.ic_notes_blue_24dp,
                        getResources().getString(R.string.title_notes)
                ))
                .addItem(newItem(
                        R.drawable.ic_plans_active_black_24dp,
                        R.drawable.ic_plans_active_blue_24dp,
                        getResources().getString(R.string.title_plans)
                ))
                .build();

        //自动适配ViewPager页面切换
//        navigationController.setupWithViewPager(customPagerView);
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                //选中时触发
                if (index == 2) {
                    Intent intent2DialogActivity = new Intent(MainActivity.this, DialogActivity.class);
                    intent2DialogActivity.putExtra(DialogActivity.TO_SAVE_STR, "");
                    startActivity(intent2DialogActivity);
                } else if (index > 2) {
                    customPagerView.setCurrentItem(index - 1);
                    adjustActionBar(fragmentNames[index - 1]);
                } else {
                    customPagerView.setCurrentItem(index);
                    adjustActionBar(fragmentNames[index]);
                }
            }

            @Override
            public void onRepeat(int index) {
                //重复选中时触发
                if (index == 2) {
                    launchActivity(new Intent(MainActivity.this, DialogActivity.class));
                }
            }
        });
    }

    /**
     * 正常tab
     */
    private BaseTabItem newItem(int drawable, int checkedDrawable, String text) {
        SpecialTab mainTab = new SpecialTab(this);
        mainTab.initialize(drawable, checkedDrawable, text);
        mainTab.setTextDefaultColor(0xFF888888);
        mainTab.setTextCheckedColor(0xFF009688);
        return mainTab;
    }

    /**
     * 圆形tab
     */
    private BaseTabItem newRoundItem(int drawable, int checkedDrawable, String text) {
        SpecialTabRound mainTab = new SpecialTabRound(this);
        mainTab.initialize(drawable, checkedDrawable, text);
        mainTab.setTextDefaultColor(0xFF888888);
        mainTab.setTextCheckedColor(0xFF009688);
        return mainTab;
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
        subscribeToEvents();
    }





    //-//<BottomNavigationView.OnNavigationItemSelectedListener>------------------------------------
    private void setMenuGroupVisibleByPosition(int position) {
        int[] menuGroupSet = new int[] {
                R.id.main_menu_schedulesFragment,
                R.id.main_menu_routinesFragment,
                R.id.main_menu_notesFragment,
                R.id.main_menu_plansFragment
        };
        for (int i = 0 ; i < menuGroupSet.length; i++) {
            if (i == position) {
                menu.setGroupVisible(menuGroupSet[i], true);
            } else {
                menu.setGroupVisible(menuGroupSet[i], false);
            }
        }
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
                    setMenuGroupVisibleByPosition(0);
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
                    setMenuGroupVisibleByPosition(1);
                }
                break;
            case "NotesFragment":
                if (menu != null) {
                    setMenuGroupVisibleByPosition(2);
                }
                break;
            case "PlansFragment":
                if (menu != null) {
                    setMenuGroupVisibleByPosition(3);
                }
                break;
        }
    }
    //-//</BottomNavigationView.OnNavigationItemSelectedListener>-----------------------------------





    //-//<Activity>---------------------------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                String email = data.getStringExtra(LoginActivity.INTENT_USER_EMAIL);
                Log.e(TAG, email);
                activeUser = DB.users().findOneBy(DBUser.COLUMN_EMAIL, email);
                DB.users().setActive(activeUser, this);
                // 设置用户登录后的界面
                ToastUtil.show("登录成功！");
            }
        }
    }

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

            // 以下是schedule menu group
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
            case R.id.main_menu_schedule_help:
                launchActivity(new Intent(this, SchedulesHelpActivity.class));
                return true;
            case R.id.main_menu_refresh_schedule:
                if (mViewClickListener != null) {
                    mViewClickListener.onViewRefreshClick();
                }


            // 以下是clock menu group
            case R.id.main_menu_1:
                if (mViewClickListener != null) {
                    mViewClickListener.onViewTodayClick();
                }
                return true;


            // 以下是schedule plan group
            case R.id.main_menu_2:
                if (mViewClickListener != null) {
                    mViewClickListener.onViewChangeMarkThemeClick();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                case ThemeManager.CARD_MAGENTA:
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

            getWindow().setStatusBarColor(Color.TRANSPARENT);
            activeUser = DB.users().getActive(this);
            activeUser.setColor(currentTheme);
            DB.users().saveAndFireEvent(activeUser);
            leftDrawer.updateHeaderBackground(activeUser);
//            fabMgr.onUserUpdate(activeUser);
        }
    }
    //-//</DialogThemeFragment.ClickListener>-------------------------------------------------------





    //-//<ViewPager.OnPageChangeListener>-----------------------------------------------------------
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        //页面滑动的时候，改变BottomNavigationView的Item高亮
        if (position >= 2) {
            navigationController.setSelect(position + 1);
        } else if (position < 2) {
            navigationController.setSelect(position);
        }
        adjustActionBar(fragmentNames[position]);
        leftDrawer.onPagerPositionChange(position);
//        fabMgr.onViewPagerItemChange(position);
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
    @Override
    public void onDateChange(int year, int month, boolean isToday) {
        tvYear.setText(year + getString(R.string.calendar_year));
        tvMonth.setText(month + "月");
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
    //-//</SchedulesFragment.OnDateChangeListener>--------------------------------------------------





    //-//<View.OnClickListener>---------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }
    //-//</View.OnClickListener>--------------------------------------------------------------------





    //-//<Method called from the event bus>---------------------------------------------------------
    public void onEvent(final Object evt) {
        if (active) {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    if (evt instanceof PersistenceEvents.ModelCreateOrUpdateEvent) {
                        PersistenceEvents.ModelCreateOrUpdateEvent event = (PersistenceEvents.ModelCreateOrUpdateEvent) evt;
                        Log.d(TAG, "onEvent: " + event.clazz.getName());
//                        ((DailyAgendaFragment) getViewPagerFragment(0)).notifyDataChange();
//                        ((RoutinesListFragment) getViewPagerFragment(1)).notifyDataChange();
//                        ((MedicinesListFragment) getViewPagerFragment(2)).notifyDataChange();
                    } else if (evt instanceof PersistenceEvents.ActiveUserChangeEvent) {
                        activeUser = ((PersistenceEvents.ActiveUserChangeEvent) evt).user;
                        onUserUpdate(activeUser);
                    } else if (evt instanceof PersistenceEvents.UserUpdateEvent) {
                        DBUser user = ((PersistenceEvents.UserUpdateEvent) evt).user;
                        onUserUpdate(user);
                        if (DB.users().isActive(user, MainActivity.this)) {
                            activeUser = user;
                        }
                    } else if (evt instanceof PersistenceEvents.UserCreateEvent) {
                        DBUser created = ((PersistenceEvents.UserCreateEvent) evt).user;
                        leftDrawer.onUserCreated(created);
                    }
                }
            });
        } else {
            pendingEvents.add(evt);
        }
    }
    //-//</Method called from the event bus>--------------------------------------------------------



    public void launchActivityDelayed(final Class<?> activityClazz, int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, activityClazz));
                overridePendingTransition(0, 0);
            }
        }, delay);

    }
    public void onUserUpdate(DBUser user) {
//        DB.users().setActive(user, this);
        leftDrawer.onUserUpdated(user);
//        fabMgr.onUserUpdate(user);
        refreshTheme(MainActivity.this, user.color());
    }
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

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
        }
    }
    //</内部类>---尽量少用---------------------------------------------------------------------------

}
