package com.time.cat.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import com.time.cat.R;

import java.lang.reflect.Field;

public class DisplayUtils {
  private static final float ROUND_CEIL = 0.5f;
  private static DisplayMetrics sDisplayMetrics;
  private static Resources sResources;
  private static int sDefaultKeyboardHeight;

  /**
   * 初始化操作
   *
   * @param context context
   */
  public static void init(Context context) {
    sDisplayMetrics = context.getResources().getDisplayMetrics();
    sResources = context.getResources();
    sDefaultKeyboardHeight = context.getResources().getDimensionPixelSize(R.dimen.default_keyboard_height);
  }

  /**
   * 获取屏幕宽度 单位：像素
   *
   * @return 屏幕宽度
   */
  public static int getScreenWidth() {
    return sDisplayMetrics.widthPixels;
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
   * 获取默认软键盘高度 单位：像素
   *
   * @return 默认软键盘高度
   */
  public static int getDefaultKeyboardHeight() {
    return sDefaultKeyboardHeight;
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
    return (int) (dp * sDisplayMetrics.density + ROUND_CEIL);
  }

  /**
   * dp 转 px
   *
   * @param dp dp值
   *
   * @return 转换后的像素值
   */
  public static float dp2px(float dp) {
    return dp * sDisplayMetrics.density + ROUND_CEIL;
  }

  /**
   * px 转 dp
   *
   * @param px px值
   *
   * @return 转换后的dp值
   */
  public static int px2dp(int px) {
    return (int) (px / sDisplayMetrics.density + ROUND_CEIL);
  }

  /**
   * 获取状态栏高度
   *
   * @return 状态栏高度
   */
  public static int getStatusBarHeight() {
    final int defaultHeightInDp = 19;
    int height = DisplayUtils.dp2px(defaultHeightInDp);
    try {
      Class<?> c = Class.forName("com.android.internal.R$dimen");
      Object obj = c.newInstance();
      Field field = c.getField("status_bar_height");
      height = sResources.getDimensionPixelSize(Integer.parseInt(field.get(obj).toString()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return height;
  }
}