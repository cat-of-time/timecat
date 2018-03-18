package com.time.cat.ui.activity.main;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.haibin.calendarview.Calendar;
import com.time.cat.R;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.data.model.events.PersistenceEvents;
import com.time.cat.ui.activity.main.listener.OnDateChangeListener;
import com.time.cat.ui.activity.main.listener.OnNoteViewClickListener;
import com.time.cat.ui.activity.main.listener.OnPlanViewClickListener;
import com.time.cat.ui.activity.main.listener.OnRoutineViewClickListener;
import com.time.cat.ui.activity.main.listener.OnScheduleViewClickListener;
import com.time.cat.ui.activity.main.viewmanager.FabMenuManager;
import com.time.cat.ui.activity.main.viewmanager.LeftDrawerManager;
import com.time.cat.ui.adapter.CustomPagerViewAdapter;
import com.time.cat.ui.base.BaseActivity;
import com.time.cat.ui.base.mvp.presenter.ActivityPresenter;
import com.time.cat.ui.modules.about.RoutinesHelpActivity;
import com.time.cat.ui.modules.about.SchedulesHelpActivity;
import com.time.cat.ui.modules.notes.NotesFragment;
import com.time.cat.ui.modules.operate.InfoOperationActivity;
import com.time.cat.ui.modules.plans.FileListFragment;
import com.time.cat.ui.modules.routines.RoutinesFragment;
import com.time.cat.ui.modules.schedules.SchedulesFragment;
import com.time.cat.ui.modules.theme.DialogThemeFragment;
import com.time.cat.ui.modules.user.LoginActivity;
import com.time.cat.ui.widgets.navigation.OnlyIconItemView;
import com.time.cat.ui.widgets.navigation.SpecialTab;
import com.time.cat.ui.widgets.navigation.SpecialTabRound;
import com.time.cat.ui.widgets.theme.ThemeManager;
import com.time.cat.ui.widgets.theme.utils.ThemeUtils;
import com.time.cat.ui.widgets.viewpaper.CustomPagerView;
import com.time.cat.util.override.LogUtil;
import com.time.cat.util.override.ToastUtil;
import com.time.cat.util.view.ScreenUtil;
import com.timecat.commonjar.contentProvider.SPHelper;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

import static com.time.cat.data.Constants.ROUTINES_VIEW_TYPE;
import static com.time.cat.data.Constants.SCHEDULES_VIEW_TYPE;

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

    private TextView mTextMonthDay;
    private TextView mTextYear;
    private TextView mTextLunar;

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
        activeUser = DB.users().getActive();
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
        DBUser u = DB.users().getActive();
        leftDrawer.onActivityResume(u);
        active = true;
        refreshTheme(this, u.color());
        // process pending events
        while (!pendingEvents.isEmpty()) {
            LogUtil.e("Processing pending event...");
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

        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextYear = findViewById(R.id.tv_year);
        mTextLunar = findViewById(R.id.tv_lunar);
//        mTextCurrentDay = findViewById(R.id.tv_current_day);

        customPagerView = findViewById(R.id.main_viewpager);
        navigation = findViewById(R.id.main_navigation);

        Calendar c = new Calendar();
        mTextYear.setText(String.valueOf(c.getYear()));
        mTextMonthDay.setText(c.getMonth() + "月" + c.getDay() + "日");
        mTextLunar.setText("今日");
//        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));

        setToolBar();
        setViewPager();
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
//        toolbar.setContentInsetsAbsolute(0,0);
        toolbar.setContentInsetEndWithActions(0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
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

        RoutinesFragment routinesFragment = new RoutinesFragment();
        setOnViewClickListener(routinesFragment);

        NotesFragment notesFragment = new NotesFragment();
        setOnViewClickListener(notesFragment);

//        PlansFragment plansFragment = new PlansFragment();
        FileListFragment fileListFragment = new FileListFragment();
        setOnViewClickListener(fileListFragment);

        fragmentNames = new String[]{"SchedulesFragment", "RoutinesFragment", "NotesFragment", "PlansFragment"};

        customPagerViewAdapter = new CustomPagerViewAdapter(getSupportFragmentManager());
        customPagerViewAdapter.addFragment(schedulesFragment);
        customPagerViewAdapter.addFragment(routinesFragment);
        customPagerViewAdapter.addFragment(notesFragment);
        customPagerViewAdapter.addFragment(fileListFragment);
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
        BaseTabItem tab1 = newItem(
                R.drawable.ic_schedules_grey_24dp,
                R.drawable.ic_schedules_black_24dp,
                getResources().getString(R.string.title_schedules)
        );
        tab1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MaterialDialog.Builder(MainActivity.this)
                        .content("改变 [日程页面] 视图")
                        .positiveText("确定")
                        .items(R.array.schedules_view_types)
                        .itemsCallbackSingleChoice(
                                SPHelper.getInt(SCHEDULES_VIEW_TYPE, 0),
                                (dialog, view, which, text) -> {
                                    SPHelper.save(SCHEDULES_VIEW_TYPE, which);
                                    ToastUtil.ok("设置成功！");
                                    customPagerViewAdapter.notifyDataChanged();
                                    return true;
                                }
                        )
                        .negativeText("取消")
                        .onNegative((dialog, which) -> dialog.dismiss())
                        .show();
                return false;
            }
        });
        BaseTabItem tab2 = newItem(
                R.drawable.ic_routines_grey_24dp,
                R.drawable.ic_routines_black_24dp,
                getResources().getString(R.string.title_routines)
        );
        tab2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MaterialDialog.Builder(MainActivity.this)
                        .content("改变 [生物钟页面] 视图")
                        .positiveText("确定")
                        .items(R.array.routines_view_types)
                        .itemsCallbackSingleChoice(
                                SPHelper.getInt(ROUTINES_VIEW_TYPE, 0),
                                (dialog, view, which, text) -> {
                                    SPHelper.save(ROUTINES_VIEW_TYPE, which);
                                    ToastUtil.ok("设置成功！");
                                    customPagerViewAdapter.notifyDataChanged();
                                    return true;
                                }
                        )
                        .negativeText("取消")
                        .onNegative((dialog, which) -> dialog.dismiss())
                        .show();
                return false;
            }
        });
        navigationController = navigation.custom()
                .addItem(tab1)
                .addItem(tab2)
                .addItem(newIconItem(
                        R.drawable.ic_add_circle_blue_24dp
                ))
                .addItem(newItem(
                        R.drawable.ic_notes_grey_24dp,
                        R.drawable.ic_notes_black_24dp,
                        getResources().getString(R.string.title_notes)
                ))
                .addItem(newItem(
                        R.drawable.ic_plans_active_grey_24dp,
                        R.drawable.ic_plans_active_black_24dp,
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
                    Intent intent2DialogActivity = new Intent(MainActivity.this, InfoOperationActivity.class);
                    intent2DialogActivity.putExtra(InfoOperationActivity.TO_SAVE_STR, "");
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
                    launchActivity(new Intent(MainActivity.this, InfoOperationActivity.class));
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
        mainTab.setTextDefaultColor(0x50000000);
        mainTab.setTextCheckedColor(0xff000000);
        return mainTab;
    }

    /**
     * 圆形tab
     */
    private BaseTabItem newRoundItem(int drawable, int checkedDrawable, String text) {
        SpecialTabRound mainTab = new SpecialTabRound(this);
        mainTab.initialize(drawable, checkedDrawable, text);
        mainTab.setTextDefaultColor(0xFF888888);
        mainTab.setTextCheckedColor(0xff03A9F4);
        return mainTab;
    }

    private BaseTabItem newIconItem(int drawable) {
        OnlyIconItemView mainTab = new OnlyIconItemView(this);
        mainTab.initialize(drawable);

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
        mTextMonthDay.setOnClickListener(this);
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
                LogUtil.e(email);
                activeUser = DB.users().findOneBy(DBUser.COLUMN_EMAIL, email);
                DB.users().setActive(activeUser);
                // 设置用户登录后的界面
                ToastUtil.ok("登录成功！");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPlanViewClickListener != null) {
                    mPlanViewClickListener.initSearchView(menu, MainActivity.this);
                }
            }
        }, 1000);

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
                View refreshActionView = getLayoutInflater().inflate(R.layout.menu_action_today, null);
                TextView textView = refreshActionView.findViewById(R.id.tv_current_day);
                Date d = new Date();
                textView.setText(String.valueOf(d.getDate()));
                item.setActionView(refreshActionView);
                refreshActionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mScheduleViewClickListener != null) {
                            mScheduleViewClickListener.onViewTodayClick();
                        }
                    }
                });
                if (mScheduleViewClickListener != null) {
                    mScheduleViewClickListener.onViewTodayClick();
                }
                return true;
            case R.id.main_menu_refresh_schedule:
                showRefreshAnimation(item);
                if (mScheduleViewClickListener != null) {
                    mScheduleViewClickListener.onViewRefreshClick();
                }
                return true;
            case R.id.main_menu_schedule_help:
                launchActivity(new Intent(this, SchedulesHelpActivity.class));
                return true;


            // 以下是clock menu group
            case R.id.main_menu_1:
                if (mScheduleViewClickListener != null) {
                    mScheduleViewClickListener.onViewTodayClick();
                }
                return true;
            case R.id.main_menu_refresh_routine:
                showRefreshAnimation(item);
                if (mRoutineViewClickListener != null) {
                    mRoutineViewClickListener.onViewRefreshClick();
                }
                return true;
            case R.id.main_menu_routines_help:
                launchActivity(new Intent(this, RoutinesHelpActivity.class));
                return true;

            // 以下是note menu group
            case R.id.main_menu_note_refresh:
                showRefreshAnimation(item);
                if (mNoteViewClickListener != null) {
                    mNoteViewClickListener.onViewNoteRefreshClick();
                }
                return true;

            // 以下是schedule plan group
            case R.id.main_menu_plan_sort:
                if (mPlanViewClickListener != null) {
                    mPlanViewClickListener.onViewSortClick();
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
//        LogUtil.e("onConfirm----------------->");
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
//            LogUtil.e("setTheme------------------>");
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
            activeUser = DB.users().getActive();
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
    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        LogUtil.e("state" + state);
        if (customPagerView.getCurrentItem() == 0) {
            if (canOpenDrawer[0] == 1) {
                // 只要有canOpenDrawer[0] == 1,一定已经执行switch(state)case 0,且state==0时手势完毕
//                leftDrawer.getDrawer().openDrawer();
            }
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
        }
    }
    //-//</ViewPager.OnPageChangeListener>----------------------------------------------------------





    //-//<SchedulesFragment.OnDateChangeListener>--------------------------------------------------------
    @Override
    public void onDateChange(Calendar calendar) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        this.isToday = isToday;
        if (menu != null) {
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
            case R.id.tv_month_day:
                if (mScheduleViewClickListener != null) {
                    mScheduleViewClickListener.onViewExpand();
                }
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
                        customPagerViewAdapter.notifyDataChanged();
                    } else if (evt instanceof PersistenceEvents.ActiveUserChangeEvent) {
                        activeUser = ((PersistenceEvents.ActiveUserChangeEvent) evt).user;
                        onUserUpdate(activeUser);
                        customPagerViewAdapter.notifyDataChanged();
                        LogUtil.e("ActiveUserChangeEvent --> customPagerViewAdapter.notifyDataSetChanged()");
                    } else if (evt instanceof PersistenceEvents.UserUpdateEvent) {
                        DBUser user = ((PersistenceEvents.UserUpdateEvent) evt).user;
                        onUserUpdate(user);
                        if (DB.users().isActive(user)) {
                            activeUser = user;
                        }
                        // 不要在UserUpdateEvent里setActive(),因为setActive()也是一个UserUpdateEvent,会造成递归update
//                        DB.users().setActive(user, EditorActivity.this);
                    } else if (evt instanceof PersistenceEvents.UserCreateEvent) {
                        DBUser created = ((PersistenceEvents.UserCreateEvent) evt).user;
                        leftDrawer.onUserCreated(created);
                        onUserUpdate(created);
//                        DB.users().setActive(created, EditorActivity.this);
                    } else if (evt instanceof PersistenceEvents.NoteUpdateEvent) {
                        customPagerViewAdapter.notifyDataChanged();
                    } else if (evt instanceof PersistenceEvents.NoteCreateEvent) {
                        customPagerViewAdapter.notifyDataChanged();
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
        leftDrawer.onUserUpdated(user);
        leftDrawer.updateHeaderBackground(user);

//        fabMgr.onUserUpdate(user);
        refreshTheme(this, user.color());
    }
    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<回调接口>-------------------------------------------------------------------------------------
    private OnScheduleViewClickListener mScheduleViewClickListener;
    public void setOnViewClickListener(OnScheduleViewClickListener onViewClickListener) {
        mScheduleViewClickListener = onViewClickListener;
    }

    private OnRoutineViewClickListener mRoutineViewClickListener;
    public void setOnViewClickListener(OnRoutineViewClickListener onViewClickListener) {
        mRoutineViewClickListener = onViewClickListener;
    }

    private OnNoteViewClickListener mNoteViewClickListener;
    public void setOnViewClickListener(OnNoteViewClickListener onViewClickListener) {
        mNoteViewClickListener = onViewClickListener;
    }

    private OnPlanViewClickListener mPlanViewClickListener;
    public void setOnViewClickListener(OnPlanViewClickListener onViewClickListener) {
        mPlanViewClickListener = onViewClickListener;
    }
    //</回调接口>-------------------------------------------------------------------------------------

}
