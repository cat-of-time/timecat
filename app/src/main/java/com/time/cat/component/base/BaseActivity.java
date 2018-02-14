package com.time.cat.component.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.time.cat.R;
import com.time.cat.ThemeSystem.manager.ThemeManager;
import com.time.cat.ThemeSystem.utils.ThemeUtils;
import com.time.cat.TimeCatApp;
import com.time.cat.util.ScreenUtils;
import com.time.cat.util.StatusbarColorUtils;
import com.time.cat.util.StringUtil;
import com.time.cat.util.ThreadManager;
import com.time.cat.util.ToastUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author dlink
 * @date 2018/2/2
 * @discription 基类
 */
public class BaseActivity extends PermissionActivity {
    private static final String TAG = "BaseActivity";
    //<沉浸式状态栏>----------------------------------------------------------------------------------
    static int statusHeight;
    public Intent intent;
    /**
     * 该Activity实例，命名为context是因为大部分方法都只需要context，写成context使用更方便
     *
     * @warn 不能在子类中创建
     */
    protected BaseActivity context = null;
    /**
     * 线程名列表
     */
    protected List<String> threadNameList;
    protected Toolbar toolbar;
    private Fragment currentFragment;
    private boolean isAlive = false;
    private boolean isRunning = false;
    /**
     * 如果需要内容紧贴着StatusBar
     * 应该在对应的xml布局文件中，设置根布局fitsSystemWindows=true。
     */
    private View contentViewGroup;

    /**
     * 获得状态栏的高度
     *
     * @param context
     */
    public static int getStatusBarHeight(Context context) {
        if (statusHeight <= 0) {
            try {
                Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                Object object = clazz.newInstance();
                int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
                statusHeight = context.getResources().getDimensionPixelSize(height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (BaseActivity) getActivity();
        isAlive = true;
        switch (ThemeManager.getTheme(this)) {
            case ThemeManager.CARD_WHITE:
            case ThemeManager.CARD_THUNDER:
            case ThemeManager.CARD_MAGENTA:
                setStatusBarFontIconDark(true);
                break;
            default:
                setStatusBarFontIconDark(false);
        }
    }

    public void switchFragment(Fragment fragment) {
        if (currentFragment != null && currentFragment == fragment) {
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (currentFragment != null) {
            ft.hide(currentFragment);
        }
        ft.show(fragment);
        ft.commitAllowingStateLoss();
        currentFragment = fragment;
    }

    /**
     * 一般用于对不支持的数据的处理，比如onCreate中获取到不能接受的id(id<=0)可以这样处理
     */
    public void finishWithError(String error) {
        ToastUtil.show(error);
//        enterAnim = exitAnim = R.anim.null_anim;
        finish();
    }

    @Override
    public void finish() {
        super.finish();//必须写在最前才能显示自定义动画
//        runUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (enterAnim > 0 && exitAnim > 0) {
//                    try {
//                        overridePendingTransition(enterAnim, exitAnim);
//                    } catch (Exception e) {
//                        Log.e(TAG, "finish overridePendingTransition(enterAnim, exitAnim);" +
//                                " >> catch (Exception e) {  " + e.getMessage());
//                    }
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "\n onResume <<<<<<<<<<<<<<<<<<<<<<<");
        super.onResume();
        isRunning = true;
        Log.d(TAG, "onResume >>>>>>>>>>>>>>>>>>>>>>>>\n");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "\n onPause <<<<<<<<<<<<<<<<<<<<<<<");
        super.onPause();
        isRunning = false;
        Log.d(TAG, "onPause >>>>>>>>>>>>>>>>>>>>>>>>\n");
    }


    //<启动新Activity方法>---------------------------------------------------------------------------

    /**
     * 销毁并回收内存
     *
     * @warn 子类如果要使用这个方法内用到的变量，应重写onDestroy方法并在super.onDestroy();前操作
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG, "\n onDestroy <<<<<<<<<<<<<<<<<<<<<<<");
//        dismissProgressDialog();
//        BaseBroadcastReceiver.unregister(context, receiver);
        ThreadManager.getInstance().destroyThread(threadNameList);
//        if (view != null) {
//            try {
//                view.destroyDrawingCache();
//            } catch (Exception e) {
//                Log.w(TAG, "onDestroy  try { view.destroyDrawingCache();" +
//                        " >> } catch (Exception e) {\n" + e.getMessage());
//            }
//        }

        isAlive = false;
        isRunning = false;
        super.onDestroy();

        threadNameList = null;

        context = null;

        Log.d(TAG, "onDestroy >>>>>>>>>>>>>>>>>>>>>>>>\n");
    }

    public void registerFragment(int id, Fragment fragment) {
        if (currentFragment == fragment) {
            return;

        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (currentFragment != null) {
            ft.hide(currentFragment);
        }
        ft.add(id, fragment, fragment.getClass().getName());
        ft.commit();
        currentFragment = fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    protected void recoverFragment(String currentFragmentTag) {
        if (currentFragmentTag == null) {
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        List<Fragment> fragments = fm.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment.getTag().equals(currentFragmentTag)) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
        }
        ft.commitAllowingStateLoss();
        Fragment current = fm.findFragmentByTag(currentFragmentTag);
        switchFragment(current);
    }
    //</启动新Activity方法>---------------------------------------------------------------------------


    //<运行线程>----------------------------------------------------------------------------------

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        removeDialogFragment();
        super.onSaveInstanceState(outState);
    }

    protected void removeDialogFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        List<Fragment> fragments = fm.getFragments();
        if (fragments == null) {
            return;
        }
        for (Fragment fragment : fragments) {
            if (fragment instanceof DialogFragment) {
                ft.remove(fragment);
            }
        }
        ft.commitAllowingStateLoss();
    }

    /**
     * 打开新的Activity，向左滑入效果
     *
     * @param intent intent
     */
    public void toActivity(Intent intent) {
        toActivity(intent, true);
    }
    //</运行线程>----------------------------------------------------------------------------------

    /**
     * 打开新的Activity
     *
     * @param intent        intent
     * @param showAnimation showAnimation
     */
    public void toActivity(Intent intent, boolean showAnimation) {
        toActivity(intent, -1, showAnimation);
    }

    /**
     * 打开新的Activity，向左滑入效果
     *
     * @param intent      intent
     * @param requestCode requestCode
     */
    public void toActivity(Intent intent, int requestCode) {
        toActivity(intent, requestCode, true);
    }

    /**
     * 打开新的Activity
     *
     * @param intent        intent
     * @param requestCode   requestCode
     * @param showAnimation showAnimation
     */
    public void toActivity(final Intent intent, final int requestCode, final boolean showAnimation) {
        runUiThread(new Runnable() {
            @Override
            public void run() {
                if (intent == null) {
                    Log.w(TAG, "toActivity  intent == null >> return;");
                    return;
                }
                //fragment中使用context.startActivity会导致在fragment中不能正常接收onActivityResult
                if (requestCode < 0) {
                    startActivity(intent);
                } else {
                    startActivityForResult(intent, requestCode);
                }
//                if (showAnimation) {
//                    overridePendingTransition(R.anim.right_push_in, R.anim.hold);
//                } else {
//                    overridePendingTransition(R.anim.null_anim, R.anim.null_anim);
//                }
            }
        });
    }

    /**
     * 在UI线程中运行，建议用这个方法代替runOnUiThread
     *
     * @param action
     */
    public final void runUiThread(Runnable action) {
        if (isAlive() == false) {
            Log.w(TAG, "runUiThread  isAlive() == false >> return;");
            return;
        }
        runOnUiThread(action);
    }

    /**
     * 运行线程
     *
     * @param name
     * @param runnable
     *
     * @return
     */
    public final Handler runThread(String name, Runnable runnable) {
        if (isAlive() == false) {
            Log.w(TAG, "runThread  isAlive() == false >> return null;");
            return null;
        }
        name = StringUtil.getTrimedString(name);
        Handler handler = ThreadManager.getInstance().runThread(name, runnable);
        if (handler == null) {
            Log.e(TAG, "runThread handler == null >> return null;");
            return null;
        }

        if (threadNameList.contains(name) == false) {
            threadNameList.add(name);
        }
        return handler;
    }

    /**
     * 获取Activity
     *
     * @must 在非抽象Activity中 return this;
     */
//    @Override
    public Activity getActivity() {
        return null;
    }

    //    @Override
    public final boolean isAlive() {
        return isAlive && context != null;// & ! isFinishing();导致finish，onDestroy内runUiThread不可用
    }

    //    @Override
    public final boolean isRunning() {
        return isRunning & isAlive();
    }

    protected BaseActivity setupToolbar(String title, int color, int iconColor) {
        // set up the toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(color);
//        toolbar.setNavigationIcon(getNavigationIcon(iconColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (title == null) {
            //set the back arrow in the toolbar
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
        } else {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(title);
        }
        return this;
    }

    protected BaseActivity setupToolbar(String title, int color) {
        return setupToolbar(title, color, Color.WHITE);
    }

    protected BaseActivity setupStatusBar(int color) {
        ScreenUtils.setStatusBarColor(this, color);
        return this;
    }

    protected BaseActivity subscribeToEvents() {
        TimeCatApp.eventBus().register(this);
        return this;
    }

    protected BaseActivity unsubscribeFromEvents() {
        TimeCatApp.eventBus().unregister(this);
        return this;
    }

    public void refreshTheme(Context c, int currentTheme) {
        ThemeManager.setTheme(c, currentTheme);
        switch (currentTheme) {
            case ThemeManager.CARD_WHITE:
            case ThemeManager.CARD_THUNDER:
            case ThemeManager.CARD_MAGENTA:
                setStatusBarFontIconDark(true);
                break;
            default:
                setStatusBarFontIconDark(false);
        }
        Log.e(TAG, "refreshTheme------------------>");
        ThemeUtils.refreshUI(c, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * <全透状态栏>
     * 全透明状态栏，fitsSystemWindows=false
     * setStatusBarFullTransparent();
     * setFitSystemWindow(false);
     * 全透明状态栏，fitsSystemWindows=true
     * setStatusBarFullTransparent();
     * setFitSystemWindow(true);
     * </全透状态栏>
     */
    protected void setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * <半透明状态栏>
     * 半透明状态栏，fitsSystemWindows=false
     * public void init(Bundle savedInstanceState) {
     * setHalfTransparent();
     * setFitSystemWindow(false);
     * }
     * 半透明状态栏，fitsSystemWindows=true
     * public void init(Bundle savedInstanceState) {
     * setHalfTransparent();
     * setFitSystemWindow(true);
     * }
     * </半透明状态栏>
     */
    protected void setHalfTransparent() {

        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    protected void setFitSystemWindow(boolean fitSystemWindow) {
        if (contentViewGroup == null) {
            contentViewGroup = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        }
        contentViewGroup.setFitsSystemWindows(fitSystemWindow);
    }

    /**
     * 为了兼容4.4的抽屉布局->透明状态栏
     */
    protected void setDrawerLayoutFitSystemWindow() {
        if (Build.VERSION.SDK_INT == 19) {//19表示4.4
            int statusBarHeight = getStatusBarHeight(this);
            if (contentViewGroup == null) {
                contentViewGroup = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            }
            if (contentViewGroup instanceof DrawerLayout) {
                DrawerLayout drawerLayout = (DrawerLayout) contentViewGroup;
                drawerLayout.setClipToPadding(true);
                drawerLayout.setFitsSystemWindows(false);
                for (int i = 0; i < drawerLayout.getChildCount(); i++) {
                    View child = drawerLayout.getChildAt(i);
                    child.setFitsSystemWindows(false);
                    child.setPadding(0, statusBarHeight, 0, 0);
                }

            }
        }
    }

    /**
     * 设置Android状态栏的字体颜色，状态栏为亮色的时候字体和图标是黑色，状态栏为暗色的时候字体和图标为白色
     *
     * @param dark 状态栏字体是否为深色
     */
    public void setStatusBarFontIconDark(boolean dark) {
        Log.i(TAG, "set StatusBar Font&Icon to Dark");

        // 小米MIUI
        try {
            Window window = getWindow();
            Class clazz = getWindow().getClass();
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (dark) {    //状态栏亮色且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
            } else {       //清除黑色字体
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }

        // 魅族FlymeUI
        try {
            StatusbarColorUtils.setStatusBarDarkIcon(this, true);  //参数 false 白色 true 黑色
            Window window = getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        // android6.0+系统
        // 这个设置和在xml的style文件中用这个<item name="android:windowLightStatusBar">true</item>属性是一样的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dark) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
    //</沉浸式状态栏>----------------------------------------------------------------------------------

}
