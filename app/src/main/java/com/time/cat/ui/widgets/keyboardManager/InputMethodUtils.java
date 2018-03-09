package com.time.cat.ui.widgets.keyboardManager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.time.cat.util.view.DisplayUtil;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/17
 * @discription null
 * @usage null
 */
public class InputMethodUtils {
    private static final String TAG = InputMethodUtils.class.getSimpleName().trim();

    /**
     * 监听软键盘弹出/关闭接口
     */
    public interface OnKeyboardEventListener {
        /**
         * 软键盘弹出
         */
        public void onSoftKeyboardOpened();

        /**
         * 软键盘关闭
         */
        public void onSoftKeyboardClosed();

        /**
         * 修改键盘高度
         *
         * @param keyboardHeight 键盘高度
         */
        public void updateEmotionPanelHeight(int keyboardHeight);
    }

    private static boolean sIsKeyboardShowing;
    private static int sKeyBoardHeight = DisplayUtil.getDefaultKeyboardHeight();

    public static boolean isKeyboardShowing() {
        return sIsKeyboardShowing;
    }

    public static void setKeyboardShowing(boolean show) {
        sIsKeyboardShowing = show;
    }

    public static int getKeyBoardHeight() {
        return sKeyBoardHeight;
    }

    public static void setKeyBoardHeight(int keyBoardHeight) {
        sKeyBoardHeight = keyBoardHeight;
    }

    /**
     * 隐藏输入法
     *
     * @param currentFocusView 当前焦点view
     */
    public static void hideKeyboard(View currentFocusView) {
        if (currentFocusView != null) {
            IBinder token = currentFocusView.getWindowToken();
            if (token != null) {
                InputMethodManager im = (InputMethodManager) currentFocusView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(token, 0);
            }
            sIsKeyboardShowing = false;
        }
    }

    /**
     * 开关输入法
     *
     * @param currentFocusView 当前焦点view
     */
    public static void toggleSoftInput(View currentFocusView) {
        InputMethodManager imm = (InputMethodManager) currentFocusView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(currentFocusView, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 只有当Activity的windowSoftInputMode设置为adjustResize时才有效
     */
    public static void detectKeyboard(final Activity activity, final OnKeyboardEventListener listener) {
        final View activityRootView = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        if (activityRootView != null) {
            ViewTreeObserver viewTreeObserver = activityRootView.getViewTreeObserver();
            if (viewTreeObserver != null) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final Rect r = new Rect();
                        activityRootView.getWindowVisibleDisplayFrame(r);
                        int heightDiff = DisplayUtil.getScreenHeight() - (r.bottom - r.top);
                        boolean show = heightDiff >= sKeyBoardHeight / 2;
                        if (show ^ sIsKeyboardShowing) {
                            sIsKeyboardShowing = show;
                            if (show) {
                                int keyboardHeight = heightDiff - DisplayUtil.getStatusBarHeight();
                                if (keyboardHeight != sKeyBoardHeight) {
                                    sKeyBoardHeight = keyboardHeight;
                                    if (null != listener) {
                                        listener.updateEmotionPanelHeight(keyboardHeight);
                                    }
                                }
                                if (listener != null) {
                                    listener.onSoftKeyboardOpened();
                                }
                            } else {
                                if (listener != null) {
                                    listener.onSoftKeyboardClosed();
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    public static void updateSoftInputMethod(Activity activity, int softInputMode) {
        if (!activity.isFinishing()) {
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            if (params.softInputMode != softInputMode) {
                params.softInputMode = softInputMode;
                activity.getWindow().setAttributes(params);
            }
        }
    }

    /**
     * 多少时间后显示软键盘
     */
    public static void showInputMethod(final View view, long delayMillis) {
        if (view == null) {
            return;
        }
        // 显示输入法
        view.postDelayed(() -> toggleSoftInput(view), delayMillis);
        sIsKeyboardShowing = true;
    }


    private final static String NAME_PREF_SOFT_KEYBOARD = "name_pref_soft_keyboard";

    private static final String KEY_PREF_SOFT_KEYBOARD_HEIGHT = "key_pref_soft_keyboard_height";

    // 自定义键盘默认高度 240dp
    private static final int DEFAULT_SOFT_KEYBOARD_HEIGHT = 240;

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
            softKeyboardHeight = sharedPreferences.getInt(KEY_PREF_SOFT_KEYBOARD_HEIGHT, dip2px(activity, DEFAULT_SOFT_KEYBOARD_HEIGHT));
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
            Log.w(TAG, "excuse me，键盘高度小于0？");
        }

        return softInputHeight;
    }

    public static int dip2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}