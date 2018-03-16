package com.time.cat.ui.base.mvp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.AppHelper;
import com.time.cat.data.Constants;
import com.time.cat.ui.modules.setting.SettingActivity;
import com.time.cat.ui.widgets.theme.ThemeManager;
import com.time.cat.ui.widgets.theme.utils.ThemeUtils;
import com.time.cat.util.EasyPermissionsManager;
import com.time.cat.util.ThreadManager;
import com.time.cat.util.override.LogUtil;
import com.time.cat.util.string.StringUtil;
import com.time.cat.util.view.StatusBarColorUtil;

import net.grandcentrix.thirtyinch.TiActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/11
 * @discription null
 * @usage null
 */
public abstract class BaseActivity<V extends BaseMVP.View, P extends BasePresenter<V>> extends TiActivity<P, V>
        implements BaseMVP.View,
                   EasyPermissionsManager.PermissionCallbacks {
    private static final String TAG = "BaseActivity";
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



    @LayoutRes
    protected abstract int layout();
    protected abstract boolean canBack();
    public abstract Activity getActivity();//return this;


    //<公共生命周期>------------------------------------------------------------------------------<([{
    @State
    Bundle presenterStateBundle = new Bundle();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);
        getPresenter().onSaveInstanceState(presenterStateBundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppHelper.updateAppLanguage(this);
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
        if (layout() != 0) {
            setContentView(layout());
            ButterKnife.bind(this);
        }
    }

    @Override
    protected void onResume() {
        LogUtil.d("\n onResume <<<<<<<<<<<<<<<<<<<<<<<");
        super.onResume();
        isRunning = true;
        LogUtil.d("onResume >>>>>>>>>>>>>>>>>>>>>>>>\n");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "\n onPause <<<<<<<<<<<<<<<<<<<<<<<");
        super.onPause();
        isRunning = false;
        Log.d(TAG, "onPause >>>>>>>>>>>>>>>>>>>>>>>>\n");
    }

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
//                        LogUtil.e("finish overridePendingTransition(enterAnim, exitAnim);" +
//                                " >> catch (Exception e) {  " + e.getMessage());
//                    }
//                }
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (canBack()) {
            if (item.getItemId() == android.R.id.home) {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    //</公共生命周期>-----------------------------------------------------------------------------}])>





    //<启动新Activity方法>---------------------------------------------------------------------------
    public void toActivity(Intent intent) {
        toActivity(intent, true);
    }

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
                if (showAnimation) {
                    overridePendingTransition(R.anim.right_push_in, R.anim.hold);
                } else {
                    overridePendingTransition(0, 0);
                }
            }
        });
    }

    public void launchActivity(Intent i) {
        startActivity(i);
        this.overridePendingTransition(0, 0);
    }
    //</启动新Activity方法>---------------------------------------------------------------------------






    //<运行线程>----------------------------------------------------------------------------------
    /**
     * 在UI线程中运行，建议用这个方法代替runOnUiThread
     *
     * @param action
     */
    public final void runUiThread(Runnable action) {
        if (!isAlive()) {
            LogUtil.w("runUiThread  isAlive() == false >> return;");
            return;
        }
        runOnUiThread(action);
    }

    /**
     * 运行线程
     *
     * @param name 自定义线程名字
     * @param runnable 回调
     *
     * @return handler
     */
    public final Handler runThread(String name, Runnable runnable) {
        if (!isAlive()) {
            LogUtil.w("runThread  isAlive() == false >> return null;");
            return null;
        }
        name = StringUtil.getTrimedString(name);
        Handler handler = ThreadManager.getInstance().runThread(name, runnable);
        if (handler == null) {
            LogUtil.e("runThread handler == null >> return null;");
            return null;
        }

        if (!threadNameList.contains(name)) {
            threadNameList.add(name);
        }
        return handler;
    }
    //</运行线程>----------------------------------------------------------------------------------





    //<权限检查>---------------------------------------------------------------------------------<([{
    protected static final int RC_PERM = 123;
    protected static int reSting = R.string.ask_again;//默认提示语句
    private CheckPermListener mListener;

    public void checkPermission(CheckPermListener listener, int resString, String... mPerms) {
        mListener = listener;
        if (EasyPermissionsManager.hasPermissions(this, mPerms)) {
            if (mListener != null) mListener.grantPermission();
        } else {
            CharSequence text = Html.fromHtml("<font color=\"#000000\">" + getString(resString) + "</font>");
            EasyPermissionsManager.requestPermissions(this, text, RC_PERM, mPerms);
        }
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //同意了某些权限可能不是全部
        if (mListener != null) mListener.denyPermission();
    }

    @Override
    public void onPermissionsAllGranted() {
        if (mListener != null) mListener.grantPermission();//同意了全部权限的回调
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (!EasyPermissionsManager.checkDeniedPermissionsNeverAskAgain(this, getString(R.string.perm_tip), R.string.setting, R.string.cancel, null, perms)) {
            if (mListener != null) mListener.denyPermission();
        }
    }

    /**
     * 权限回调接口
     */
    public interface CheckPermListener {
        //权限通过后的回调方法
        void grantPermission();

        void denyPermission();
    }
    //</权限检查>--------------------------------------------------------------------------------}])>












    //<刷新>---------------------------------------------------------------------------------<([{
    protected MenuItem refreshItem;

    @SuppressLint("NewApi")
    public void showRefreshAnimation(MenuItem item) {
        hideRefreshAnimation();

        refreshItem = item;

        //这里使用一个ImageView设置成MenuItem的ActionView，这样我们就可以使用这个ImageView显示旋转动画了
        ImageView refreshActionView = (ImageView) getLayoutInflater().inflate(R.layout.action_view, null);
        refreshActionView.setImageResource(R.drawable.ic_autorenew_white_24dp);
        refreshItem.setActionView(refreshActionView);

        //显示刷新动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.refresh);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(1);
        refreshActionView.startAnimation(animation);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideRefreshAnimation();
            }
        }, 1000);
    }

    @SuppressLint("NewApi")
    private void hideRefreshAnimation() {
        if (refreshItem != null) {
            View view = refreshItem.getActionView();
            if (view != null) {
                view.clearAnimation();
                refreshItem.setActionView(null);
            }
        }
    }

    /**
     * 刷新主题
     * @param c context
     * @param currentTheme ThemeManager中的常量，实际为16进制的颜色
     */
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
//        LogUtil.e("refreshTheme------------------>");
        ThemeUtils.refreshUI(c, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
    //</刷新>--------------------------------------------------------------------------------}])>










    //<沉浸式状态栏>----------------------------------------------------------------------------------
    private View contentViewGroup;

    /**
     * 如果需要内容紧贴着StatusBar
     * 应该在对应的xml布局文件中，设置根布局fitsSystemWindows=true。
     */
    protected void setFitSystemWindow(boolean fitSystemWindow) {
        if (contentViewGroup == null) {
            contentViewGroup = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        }
        contentViewGroup.setFitsSystemWindows(fitSystemWindow);
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
            StatusBarColorUtil.setStatusBarDarkIcon(this, true);  //参数 false 白色 true 黑色
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
        // 这个设置和在xml的style文件中用这个<item_search_engine name="android:windowLightStatusBar">true</item_search_engine>属性是一样的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dark) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    /**
     * 设置window的flag
     * @param activity activity
     * @param bits bits
     * @param on on
     */
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
    //</沉浸式状态栏>----------------------------------------------------------------------------------










    //<BaseMVP.View>------------------------------------------------------------------------------
    private Toast toast;
    @State
    boolean isProgressShowing;

    @Override
    public void showMessage(@StringRes int titleRes, @StringRes int msgRes) {
        showMessage(getString(titleRes), getString(msgRes));
    }

    @Override
    public void showMessage(@NonNull String titleRes, @NonNull String msgRes) {
        hideProgress();
        if (toast != null) toast.cancel();
        Context context = TimeCatApp.getInstance(); // WindowManager$BadTokenException
        toast = titleRes.equals(context.getString(R.string.error)) ? Toasty.error(context, msgRes, Toast.LENGTH_LONG) : Toasty.info(context, msgRes, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void showErrorMessage(@NonNull String msgRes) {
        showMessage(getString(R.string.error), msgRes);
    }


    @Override
    public void showProgress(@StringRes int resId) {
        showProgress(resId, true);
    }

    @Override
    public void showBlockingProgress(int resId) {
        showProgress(resId, false);
    }

    @Override
    public void hideProgress() {
//        ProgressDialogFragment fragment = (ProgressDialogFragment) AppHelper.getFragmentByTag(getSupportFragmentManager(), ProgressDialogFragment.TAG);
//        if (fragment != null) {
//            isProgressShowing = false;
//            fragment.dismiss();
//        }
    }


    @Override
    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public void onRequireLogin() {
//        Toasty.warning(TimeCatApp.getInstance(), getString(R.string.unauthorized_user), Toast.LENGTH_LONG).show();
//        final Glide glide = Glide.get(TimeCatApp.getInstance());
//        getPresenter().manageViewDisposable(RxHelper.getObservable(Observable.fromCallable(() -> {
//            glide.clearDiskCache();
//            PrefGetter.setToken(null);
//            PrefGetter.setOtpCode(null);
//            PrefGetter.resetEnterprise();
//            Login.logout();
//            return true;
//        })).subscribe(aBoolean -> {
//            glide.clearMemory();
//            Intent intent = new Intent(this, LoginChooserActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finishAffinity();
//        }));
    }

    @Override
    public void onBackPressed() {
//        if (drawer != null && (drawer.isDrawerOpen(GravityCompat.START) || drawer.isDrawerOpen(GravityCompat.END))) {
//            closeDrawer();
//        } else {
//            boolean clickTwiceToExit = !PrefGetter.isTwiceBackButtonDisabled();
//            superOnBackPressed(clickTwiceToExit);
//        }
    }

    @Override
    public void onLogoutPressed() {
//        MessageDialogView.newInstance(getString(R.string.logout), getString(R.string.confirm_message), Bundler.start().put(BundleConstant.YES_NO_EXTRA, true).put("logout", true).end()).show(getSupportFragmentManager(), MessageDialogView.TAG);
    }

    @Override
    public void onThemeChanged() {
//        if (this instanceof MainActivity) {
//            recreate();
//        } else {
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            finish();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                finishAndRemoveTask();
//            }
//        }
    }

    @Override
    public void onOpenSettings() {
        startActivityForResult(new Intent(this, SettingActivity.class), Constants.REFRESH_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REFRESH_CODE) {
                onThemeChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EasyPermissionsManager.SETTINGS_REQ_CODE) {
            //设置返回
        }
    }

    @Override
    public boolean isEnterprise() {
        return getPresenter() != null && getPresenter().isEnterprise();
    }

    @Override
    public void onOpenUrlInBrowser() {
//        if (!InputHelper.isEmpty(schemeUrl)) {
//            ActivityHelper.startCustomTab(this, schemeUrl);
//            try {
//                finish();
//            } catch (Exception ignored) {
//            }// fragment might be committed and calling finish will crash the app.
//        }
    }

    @Override
    public void showProgress(int resId, boolean cancelable) {
        String msg = getString(R.string.in_progress);
        if (resId != 0) {
            msg = getString(resId);
        }
        if (!isProgressShowing && !isFinishing()) {
            isProgressShowing = true;
            new MaterialDialog.Builder(this).progress(true, 100).show();
        }
    }

    @Override
    public final boolean isAlive() {
        return isAlive && context != null;// & ! isFinishing();导致finish，onDestroy内runUiThread不可用
    }

    @Override
    public final boolean isRunning() {
        return isRunning & isAlive();
    }
    //</BaseMVP.View>------------------------------------------------------------------------------

}
