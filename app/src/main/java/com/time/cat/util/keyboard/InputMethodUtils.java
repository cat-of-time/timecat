package com.time.cat.util.keyboard;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/16
 * @discription 解决输入法与表情面板之间切换时抖动冲突的控制辅助工具类
 *              能做到将面板与输入法之间平滑切换
 *              另外，具备点击空白处自动收起面板和输入法的功能.
 * @usage 使用方法介绍如下：
 * 申明：【
 *      此类中，我们将表情面板选项、显示表情面板的按钮、表情面板按钮的点击事件作为一个整体，
 *      包装在ViewBinder类中(点击表情面板按钮时，将会展开表情面板 ) 
 * 】
 * 第一步，我们将需要操作的表情面板、按钮、事件绑定在一起，创建ViewBinder类(可以是很多个)代码示例如下:
 * //如果不想监听按钮点击事件，之间将listener参数替换成null即可
 * ViewBinder viewBinder1 = new ViewBinder(btn_1,panel1,listener1);
 * ViewBinder viewBinder2 = new ViewBinder(btn_2,panel2,listener2);
 * ...
 * 第二步：创建InputMethodUtils类
 * InputMethodUtils inputMethodUtils = new InputMethodUtils(this);
 * 第三部：将ViewBinder传递给InputMethodUtils。
 * inputMethodUtils.setViewBinders(viewBinder1,viewBinder2);//这个参数为动态参数，
 * 支持多个参数传递进来
 *
 * 本类还提供两个常用的工具方法：
 * InputMethodUtils.hideKeyboard();//用于隐藏输入法
 * InputMethodUtils.updateSoftInputMethod();//用于将当前Activity的输入法模式切换成指定的输入法模式
 */
public class InputMethodUtils implements View.OnClickListener {

    // 键盘是否展开的标志位
    private boolean sIsKeyboardShowing;
    // 键盘高度
    private int sKeyBoardHeight = 0;
    // 绑定的Activity
    private Activity activity;
    // 记录是否获取了键盘高度
    private boolean haskownKeyboardHeight = false;
    // 触发与面板对象集合
    private Set<ViewBinder> viewBinders = new HashSet<ViewBinder>();

    /**
     * 构造函数
     *
     * @param activity 需要处理输入法的当前的Activity
     */
    public InputMethodUtils(Activity activity) {
        this.activity = activity;
        DisplayUtils.init(activity);
        // 默认键盘高度为267dp
        setKeyBoardHeight(DisplayUtils.dp2px(267));
        updateSoftInputMethod(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        detectKeyboard();// 监听View树变化，以便监听键盘是否弹出
        enableCloseKeyboardOnTouchOutside(activity);
    }

    /**
     * 添加ViewBinder
     *
     * @param viewBinder 变长参数
     */
    public void setViewBinders(ViewBinder... viewBinder) {
        for (ViewBinder vBinder : viewBinder) {
            if (vBinder != null) {
                viewBinders.add(vBinder);
                vBinder.trigger.setTag(vBinder);
                vBinder.trigger.setOnClickListener(this);
            }
        }
        updateAllPanelHeight(sKeyBoardHeight);
    }

    @Override
    public void onClick(View v) {
        ViewBinder viewBinder = (ViewBinder) v.getTag();
        View panel = viewBinder.panel;
        resetOtherPanels(panel);// 重置所有面板
        if (isKeyboardShowing()) {
            updateSoftInputMethod(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            panel.setVisibility(View.VISIBLE);
            hideKeyBordAndSetFlag(activity.getCurrentFocus());
        } else if (panel.isShown()) {
            updateSoftInputMethod(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            panel.setVisibility(View.GONE);
        } else if (haskownKeyboardHeight) {
            updateSoftInputMethod(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            panel.setVisibility(View.VISIBLE);
        } else {
            updateSoftInputMethod(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            panel.setVisibility(View.VISIBLE);
        }
        if (viewBinder.listener != null) {
            viewBinder.listener.onClick(v);
        }
    }

    /**
     * 获取键盘是否弹出
     *
     * @return true表示弹出
     */
    public boolean isKeyboardShowing() {
        return sIsKeyboardShowing;
    }

    /**
     * 获取键盘的高度
     *
     * @return 键盘的高度(px单位)
     */
    public int getKeyBoardHeight() {
        return sKeyBoardHeight;
    }

    /**
     * 关闭所有的面板
     */
    public void closeAllPanels() {
        resetOtherPanels(null);
        updateSoftInputMethod(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    /**
     * 判断是否存在正在显示的面板
     *
     * @return true表示存在，false表示不存在
     */
    public boolean hasPanelShowing() {
        for (ViewBinder viewBinder : viewBinders) {
            if (viewBinder.panel.isShown()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新输入法的弹出模式
     *
     * @param softInputMode 
     *                      键盘弹出模式：WindowManager.LayoutParams的参数有：
     *                      &nbsp;&nbsp;&nbsp;&nbsp;可见状态： SOFT_INPUT_STATE_UNSPECIFIED,
     *                      SOFT_INPUT_STATE_UNCHANGED, SOFT_INPUT_STATE_HIDDEN,
     *                      SOFT_INPUT_STATE_ALWAYS_VISIBLE, or SOFT_INPUT_STATE_VISIBLE.
     *                      &nbsp;&nbsp;&nbsp;&nbsp;适配选项有： SOFT_INPUT_ADJUST_UNSPECIFIED,
     *                      SOFT_INPUT_ADJUST_RESIZE, or SOFT_INPUT_ADJUST_PAN.
     */
    public void updateSoftInputMethod(int softInputMode) {
        updateSoftInputMethod(activity, softInputMode);
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
        }
    }

    /**
     * 更新输入法的弹出模式
     *
     * @param activity
     * @param softInputMode 
     *                      键盘弹出模式：WindowManager.LayoutParams的参数有：
     *                      &nbsp;&nbsp;&nbsp;&nbsp;可见状态： SOFT_INPUT_STATE_UNSPECIFIED,
     *                      SOFT_INPUT_STATE_UNCHANGED, SOFT_INPUT_STATE_HIDDEN,
     *                      SOFT_INPUT_STATE_ALWAYS_VISIBLE, or SOFT_INPUT_STATE_VISIBLE.
     *                      &nbsp;&nbsp;&nbsp;&nbsp;适配选项有： SOFT_INPUT_ADJUST_UNSPECIFIED,
     *                      SOFT_INPUT_ADJUST_RESIZE, or SOFT_INPUT_ADJUST_PAN.
     */
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
     * 隐藏键盘，并维护显示或不显示的逻辑
     *
     * @param currentFocusView 当前的焦点View
     */
    private void hideKeyBordAndSetFlag(View currentFocusView) {
        sIsKeyboardShowing = false;
        hideKeyboard(currentFocusView);
    }

    /**
     * 重置所有面板
     */
    private void resetOtherPanels(View dstPanel) {
        for (ViewBinder vBinder : viewBinders) {
            if (dstPanel != vBinder.panel) {
                vBinder.panel.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 更新所有面板的高度
     *
     * @param height 具体高度
     */
    private void updateAllPanelHeight(int height) {
        for (ViewBinder vBinder : viewBinders) {
            ViewGroup.LayoutParams params = vBinder.panel.getLayoutParams();
            params.height = height;
            vBinder.panel.setLayoutParams(params);
        }
    }

    /**
     * 设置键盘弹出与否状态
     *
     * @param show true表示弹出，false表示未弹出
     */
    private void setKeyboardShowing(boolean show) {
        sIsKeyboardShowing = show;
        if (show) {
            resetOtherPanels(null);
            updateSoftInputMethod(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    /**
     * 设置键盘的高度
     *
     * @param keyBoardHeight 键盘的高度(px单位)
     */
    private void setKeyBoardHeight(int keyBoardHeight) {
        sKeyBoardHeight = keyBoardHeight;
        updateAllPanelHeight(keyBoardHeight);
    }

    /**
     * 是否点击软键盘和输入法外面区域
     *
     * @param touchY   点击y坐标(不包括statusBar的高度)
     */
    private boolean isTouchKeyboardOutside(int touchY) {
        View foucusView = activity.getCurrentFocus();
        if (foucusView == null) {
            return false;
        }
        int[] location = new int[2];
        foucusView.getLocationOnScreen(location);
        int editY = location[1] - DisplayUtils.getStatusBarHeight();
        int offset = touchY - editY;
        if (offset > 0) {// 输入框一下的有可能是表情面板，所以，我们算作范围内
            return false;
        }
        return true;
    }

    /**
     * 是否点击的是当前焦点View的范围
     *
     * @param x x方向坐标
     * @param y y方向坐标(不包括statusBar的高度)
     *
     * @return true表示点击的焦点View，false反之
     */
    private boolean isTouchedFoucusView(int x, int y) {
        View foucusView = activity.getCurrentFocus();
        if (foucusView == null) {
            return false;
        }
        int[] location = new int[2];
        foucusView.getLocationOnScreen(location);
        int foucusViewTop = location[1] - DisplayUtils.getStatusBarHeight();
        int offsetY = y - foucusViewTop;
        if (offsetY > 0 && offsetY < foucusView.getMeasuredHeight()) {
            int foucusViewLeft = location[0];
            int foucusViewLength = foucusView.getWidth();
            int offsetX = x - foucusViewLeft;
            if (offsetX >= 0 && offsetX <= foucusViewLength) {
                return true;
            }
        }
        return false;
    }

    /**
     * 开启点击外部关闭键盘的功能
     *
     * @param activity
     */
    private void enableCloseKeyboardOnTouchOutside(Activity activity) {
        CloseKeyboardOnOutsideContainer frameLayout = new CloseKeyboardOnOutsideContainer(activity);
        activity.addContentView(frameLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 设置View树监听，以便判断键盘是否弹出。
     * 【只有当Activity的windowSoftInputMode设置为adjustResize时才有效】
     */
    private void detectKeyboard() {
        final View activityRootView = activity.findViewById(android.R.id.content);
        if (activityRootView != null) {
            ViewTreeObserver observer = activityRootView.getViewTreeObserver();
            if (observer == null) {
                return;
            }
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    final Rect r = new Rect();
                    activityRootView.getWindowVisibleDisplayFrame(r);
                    int heightDiff = DisplayUtils.getScreenHeight() - (r.bottom - r.top);
                    boolean show = heightDiff >= sKeyBoardHeight / 3;
                    setKeyboardShowing(show);// 设置键盘是否展开状态
                    if (show) {
                        int keyboardHeight = heightDiff - DisplayUtils.getStatusBarHeight();
                        // 设置新的键盘高度
                        setKeyBoardHeight(keyboardHeight);
                        haskownKeyboardHeight = true;
                    }
                }
            });
        }
    }

    /**
     * ViewBinder的触发按钮点击的监听器
     *
     * @author 李长军
     */
    public static interface OnTriggerClickListener {
        /**
         * @param v
         */
        public void onClick(View v);
    }

    /**
     * 用于控制点击某个按钮显示或者隐藏“表情面板”的绑定bean对象。
     * 例如：我想点击“表情”按钮显示“表情面板”,我就可以这样做：
     * ViewBinder viewBinder = new ViewBinder(btn_emotion,emotionPanel);
     * 这样就创建出了一个ViewBinder对象
     * <font color='red'>【注意事项，使用此类时，千万不要使用trigger的setOnClickListener来监听事件(
     * 使用OnTriggerClickListener来代替)，也不要使用setTag来设置Tag，否则会导致使用异常】</font>
     *
     * @author 李长军
     */
    public static class ViewBinder {
        private View trigger;
        private View panel;
        private OnTriggerClickListener listener;

        /**
         * 创建ViewBinder对象
         * 例如：我想点击“表情”按钮显示“表情面板”,我就可以这样做：
         * ViewBinder viewBinder = new
         * ViewBinder(btn_emotion,emotionPanel,listener);
         * 这样就创建出了一个ViewBinder对象
         *
         * @param trigger  触发对象
         * @param panel    点击触发对象需要显示/隐藏的面板对象
         * @param listener Trigger点击的监听器（千万不要使用setOnClickListener，否则会覆盖本工具类的监听器）
         */
        public ViewBinder(View trigger, View panel, OnTriggerClickListener listener) {
            this.trigger = trigger;
            this.panel = panel;
            this.listener = listener;
            trigger.setClickable(true);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            ViewBinder other = (ViewBinder) obj;
            if (panel == null) {
                if (other.panel != null) return false;
            } else if (!panel.equals(other.panel)) return false;
            if (trigger == null) {
                if (other.trigger != null) return false;
            } else if (!trigger.equals(other.trigger)) return false;
            return true;
        }

        public OnTriggerClickListener getListener() {
            return listener;
        }

        public void setListener(OnTriggerClickListener listener) {
            this.listener = listener;
        }

        public View getTrigger() {
            return trigger;
        }

        public void setTrigger(View trigger) {
            this.trigger = trigger;
        }

        public View getPanel() {
            return panel;
        }

        public void setPanel(View panel) {
            this.panel = panel;
        }

    }

    /**
     * 点击软键盘区域以外自动关闭软键盘的遮罩View
     *
     * @author 李长军
     */
    private class CloseKeyboardOnOutsideContainer extends FrameLayout {

        public CloseKeyboardOnOutsideContainer(Context context) {
            this(context, null);
        }

        public CloseKeyboardOnOutsideContainer(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public CloseKeyboardOnOutsideContainer(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            boolean isKeyboardShowing = isKeyboardShowing();
            boolean isEmotionPanelShowing = hasPanelShowing();
            if ((isKeyboardShowing || isEmotionPanelShowing) && event.getAction() == MotionEvent.ACTION_DOWN) {
                int touchY = (int) (event.getY());
                int touchX = (int) (event.getX());
                if (isTouchKeyboardOutside(touchY)) {
                    if (isKeyboardShowing) {
                        hideKeyBordAndSetFlag(activity.getCurrentFocus());
                    }
                    if (isEmotionPanelShowing) {
                        closeAllPanels();
                    }
                }
                if (isTouchedFoucusView(touchX, touchY)) {
                    // 如果点击的是输入框，那么延时折叠表情面板
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setKeyboardShowing(true);
                        }
                    }, 500);

                }
            }
            return super.onTouchEvent(event);
        }
    }

    /**
     * 屏幕参数的辅助工具类。例如：获取屏幕高度，宽度，statusBar的高度，px和dp互相转换等
     * 【注意，使用之前一定要初始化！一次初始化就OK(建议APP启动时进行初始化)。 初始化代码 DisplayUtil.init(context)】
     *
     * @author 李长军 2016.11.25
     */
    private static class DisplayUtils {

        // 四舍五入的偏移值
        private static final float ROUND_CEIL = 0.5f;
        // 屏幕矩阵对象
        private static DisplayMetrics sDisplayMetrics;
        // 资源对象（用于获取屏幕矩阵）
        private static Resources sResources;
        // statusBar的高度（由于这里获取statusBar的高度使用的反射，比较耗时，所以用变量记录）
        private static int statusBarHeight = -1;

        /**
         * 初始化操作
         *
         * @param context context上下文对象
         */
        public static void init(Context context) {
            sDisplayMetrics = context.getResources().getDisplayMetrics();
            sResources = context.getResources();
        }

        /**
         * 获取屏幕高度 单位：像素
         *
         * @return 屏幕高度
         */
        public static int getScreenHeight() {
            return sDisplayMetrics.heightPixels;
        }

        /**
         * 获取屏幕宽度 单位：像素
         *
         * @return 屏幕宽度
         */
        public static float getDensity() {
            return sDisplayMetrics.density;
        }

        /**
         * dp 转 px
         *
         * @param dp dp值
         *
         * @return 转换后的像素值
         */
        public static int dp2px(int dp) {
            return (int) (dp * getDensity() + ROUND_CEIL);
        }

        /**
         * 获取状态栏高度
         *
         * @return 状态栏高度
         */
        public static int getStatusBarHeight() {
            // 如果之前计算过，直接使用上次的计算结果
            if (statusBarHeight == -1) {
                final int defaultHeightInDp = 19;// statusBar默认19dp的高度
                statusBarHeight = DisplayUtils.dp2px(defaultHeightInDp);
                try {
                    Class<?> c = Class.forName("com.android.internal.R$dimen");
                    Object obj = c.newInstance();
                    Field field = c.getField("status_bar_height");
                    statusBarHeight = sResources.getDimensionPixelSize(Integer.parseInt(field.get(obj).toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return statusBarHeight;
        }
    }
}