package com.time.cat.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author dlink
 * @date 2018/2/14
 * @discription 软键盘工具类
 */
public class KeyboardUtil {

    private static KeyboardUtil keyboardManager;
    private Context mContext;
    private InputMethodManager imm;

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

    /** * 切换软键盘的显示与隐藏 */
    public void toggleKeyboard() {
// imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /** * 判断软键盘是否显示 * * @return 软键盘状态 */
    public boolean keyboardIsActive() {
        return imm.isActive();
    }

    /** * 弹出软键盘 */
    public void showKeyboard(View view) {
        imm.showSoftInput(view, 0);
    }

    /** * 针对于EditText 获得焦点，显示软键盘 * * @param edit EditText */
    public void showKeyboard(EditText edit) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        imm.showSoftInput(edit, 0);
    }

    /** * 关闭软键盘 * 针对于 有一个特定的view(EditText) * * @param view view */
    public void hideKeyboard(View view) {
        if (imm.isActive()) {
            // 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /** * 针对于EditText ,失去焦点，隐藏软件盘 * * @param edit */
    public void hideKeyboard(EditText edit) {
        edit.clearFocus();
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    /** * 隐藏软键盘 */
    public void hideKeyboard() {
        View view = ((Activity) mContext).getWindow().peekDecorView();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
