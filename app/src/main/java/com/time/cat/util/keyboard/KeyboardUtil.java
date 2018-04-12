package com.time.cat.util.keyboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.time.cat.util.override.LogUtil;
import com.time.cat.util.view.ScreenUtil;

/**
 * @author dlink
 * @date 2018/2/14
 * @discription 软键盘工具类
 */
public class KeyboardUtil {

    private static KeyboardUtil keyboardManager;
    private Context mContext;
    private InputMethodManager imm;

    private static final String TAG = KeyboardUtil.class.getSimpleName();

    private final static String NAME_PREF_SOFT_KEYBOARD = "name_pref_soft_keyboard";

    private static final String KEY_PREF_SOFT_KEYBOARD_HEIGHT = "key_pref_soft_keyboard_height";

    // “表情键盘”默认高度 240dp
    private static final int DEFAULT_SOFT_KEYBOARD_HEIGHT = 240;

    private KeyboardUtil() {
        //  构造函数私有化
    }

    private KeyboardUtil(Context context) {
        this.mContext = context;
        // 得到InputMethodManager的实例
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public static KeyboardUtil getInstances(Context context) {
        if (keyboardManager == null) {
            keyboardManager = new KeyboardUtil(context);
        }
        return keyboardManager;
    }

    /**
     * 切换软键盘的显示与隐藏
     */
    public void toggleKeyboard() {
// imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * 判断软键盘是否显示
     *
     * @return 软键盘状态
     */
    public boolean keyboardIsActive() {
        return imm.isActive();
    }

    /**
     * 弹出软键盘
     */
    public void showKeyboard(View view) {
        imm.showSoftInput(view, 0);
    }

    /**
     * 针对于EditText 获得焦点，显示软键盘
     *
     * @param edit EditText
     */
    public void showKeyboard(EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        imm.showSoftInput(edit, 0);
    }

    /**
     * 关闭软键盘
     * 针对于 有一个特定的view(EditText)
     *
     * @param view view
     */
    public void hideKeyboard(View view) {
        if (imm.isActive()) {
            // 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 针对于EditText ,失去焦点，隐藏软件盘
     *
     * @param edit
     */
    public void hideKeyboard(EditText edit) {
        edit.clearFocus();
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyboard() {
        View view = ((Activity) mContext).getWindow().peekDecorView();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 检测软键盘的高度
     *
     * @param activity a
     *
     * @return 软键盘的高度 or 0
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getSupportSoftInputHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    // 得到“软键盘”高度
    public static int getSupportSoftKeyboardHeight(Activity activity) {

        int softKeyboardHeight = getCurrentSoftInputHeight(activity);

        // 如果当前的键盘高度大于零，赶紧保存下来
        if (softKeyboardHeight > 0) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(NAME_PREF_SOFT_KEYBOARD, Context.MODE_PRIVATE);
            sharedPreferences.edit().putInt(KEY_PREF_SOFT_KEYBOARD_HEIGHT, softKeyboardHeight).apply();
        }

        // 如果当前“软键盘”高度等于零，可能是被隐藏了，也可能是我的锅，那就使用本地已经保存键盘高度
        if (softKeyboardHeight == 0) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(NAME_PREF_SOFT_KEYBOARD, Context.MODE_PRIVATE);
            softKeyboardHeight = sharedPreferences.getInt(KEY_PREF_SOFT_KEYBOARD_HEIGHT, ScreenUtil.dip2px(activity, DEFAULT_SOFT_KEYBOARD_HEIGHT));
        }

        return softKeyboardHeight;
    }

    // 软键盘是否显示
    public static boolean isSoftKeyboardShown(Activity activity) {
        return getCurrentSoftInputHeight(activity) != 0;
    }

    /**
     * 得到虚拟按键的高度
     *
     * @return 虚拟按键的高度
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static int getNavigationBarHeight(Context context) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        // 获取可用的高度
        DisplayMetrics defaultDisplayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(defaultDisplayMetrics);
        int usableHeight = defaultDisplayMetrics.heightPixels;

        // 获取实际的高度
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(realDisplayMetrics);
        int realHeight = realDisplayMetrics.heightPixels;

        return realHeight > usableHeight ? realHeight - usableHeight : 0;
    }

    /**
     * 得到当前软键盘的高度
     *
     * @return 软键盘的高度
     */
    public static int getCurrentSoftInputHeight(Activity activity) {

        Rect rect = new Rect();

        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();

        int softInputHeight = screenHeight - rect.bottom;

        // Android LOLLIPOP 以上的版本才有"虚拟按键"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            softInputHeight -= getNavigationBarHeight(activity);
        }

        // excuse me?
        if (softInputHeight < 0) {
            LogUtil.e("excuse me，键盘高度小于0？");
        }

        return softInputHeight;
    }
}
