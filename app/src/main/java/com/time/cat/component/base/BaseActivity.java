package com.time.cat.component.base;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.time.cat.util.StatusbarColorUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by penglu on 2016/4/27.
 */
public class BaseActivity extends PermissionActivity {
    private static final String TAG = "BaseActivity";
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    //<沉浸式状态栏>----------------------------------------------------------------------------------
    static int statusHeight;

    /**
     * 获得状态栏的高度
     *
     * @param context
     */
    public static int getStatusHeight(Context context) {
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
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
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

    /**
     * 如果需要内容紧贴着StatusBar
     * 应该在对应的xml布局文件中，设置根布局fitsSystemWindows=true。
     */
    private View contentViewGroup;

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
            int statusBarHeight = getStatusHeight(this);
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        // android6.0+系统
        // 这个设置和在xml的style文件中用这个<item name="android:windowLightStatusBar">true</item>属性是一样的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dark) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
    //</沉浸式状态栏>----------------------------------------------------------------------------------

}
