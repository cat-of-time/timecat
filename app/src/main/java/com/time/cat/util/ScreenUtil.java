/*
 *    Calendula - An assistant for personal medication management.
 *    Copyright (C) 2016 CITIUS - USC
 *
 *    Calendula is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.time.cat.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.graphics.Palette;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mikepenz.materialize.Materialize;
import com.mikepenz.materialize.MaterializeBuilder;
import com.time.cat.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;


/**
 * Created by joseangel.pineiro on 11/20/13.
 */
public class ScreenUtil {

    private static Palette p;

    public static PointF getDpSize(Activity activity) {

        PointF p = new PointF();
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        p.set(outMetrics.widthPixels / outMetrics.density, outMetrics.heightPixels / outMetrics.density);
        return p;
    }

    /**
     * 获取屏幕宽度 单位：像素
     *
     * @return 屏幕宽度
     */
    public static float getDensity(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics.density;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap getResizedBitmap(Context ctx, String pathOfInputImage, int dstWidth, int dstHeight) {
        try {


            int inWidth = 0;
            int inHeight = 0;

            InputStream in = ctx.getAssets().open(pathOfInputImage);

            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;

            // save width and height
            inWidth = options.outWidth;
            inHeight = options.outHeight;

            // decode full image pre-resized
            in = ctx.getAssets().open(pathOfInputImage);
            options = new BitmapFactory.Options();
            // calc rought re-size (this is no exact resize)
            options.inSampleSize = Math.max(inWidth / dstWidth, inHeight / dstHeight);
            // decode full image
            Bitmap roughBitmap = BitmapFactory.decodeStream(in, null, options);

            // calc exact destination size
            Matrix m = new Matrix();
            RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            RectF outRect = new RectF(0, 0, dstWidth, dstHeight);
            m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
            float[] values = new float[9];
            m.getValues(values);

            // resize bitmap
            return Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);

        } catch (IOException e) {
            Log.e("Image", e.getMessage(), e);
        }
        return null;
    }

    public static int equivalentNoAlpha(int color, float factor) {

        int white = 255;

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        int r = (int) (white + (red - white) * factor);
        int g = (int) (white + (green - white) * factor);
        int b = (int) (white + (blue - white) * factor);

        return Color.rgb(r, g, b);

    }

    public static int equivalentNoAlpha(int color, int background, float factor) {

        int r_background = Color.red(background);
        int g_background = Color.green(background);
        int b_background = Color.blue(background);

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        int r = (int) (r_background + (red - r_background) * factor);
        int g = (int) (g_background + (green - g_background) * factor);
        int b = (int) (b_background + (blue - b_background) * factor);

        return Color.rgb(r, g, b);

    }

    public static void setStatusBarColor(Activity activity, int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(color);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        }
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

    public static Materialize materialize(Activity activity) {
        return materialize(activity, R.color.android_blue_statusbar);
    }

    public static Materialize materialize(Activity activity, int colorRes) {
        return new MaterializeBuilder().withActivity(activity).withTintedStatusBar(true).withTranslucentStatusBar(true).withStatusBarColorRes(colorRes).build();
    }

    public static Materialize materializeForColor(Activity activity, int color) {
        return new MaterializeBuilder().withActivity(activity).withTintedStatusBar(true).withTranslucentStatusBar(true).withStatusBarColor(color).build();
    }

    /**
     * 获取屏幕宽度 单位：像素
     *
     * @return 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }
    /**
     * 获取屏幕高度 单位：像素
     *
     * @return 屏幕高度
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int dpToPx(Resources r, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
    /**
     * dp 转 px
     *
     * @param dp
     *            dp值
     * @return 转换后的像素值
     */
    public static int dp2px(Activity activity, int dp) {
        //0.5f 是四舍五入的偏移量
        return (int) (dp * getDensity(activity) + 0.5f);
    }
    /**
     * SP 转 Pixels
     *
     * @param sp      sp 字体大小
     * @return pixels
     */
    public static float sp2Px(Activity activity, float sp) {
        return sp * getDensity(activity);
    }

    public static int px2dip(Context context, float px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int dip2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int alpha(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.blue(color), Color.green(color));
    }

    private static final float ROUND_CEIL = 0.5f;
    private static DisplayMetrics sDisplayMetrics;
    private static Resources sResources;

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

    private static ScreenUtil screen;
    private static float screenWidth = -1;
    private static float screenHeight = -1;
    private static int stateBarHeight = -1;
    private static float density = 1;
    private static float scale;
    private static int sDefaultKeyboardHeight;

    private ScreenUtil(Context mContext) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        // 屏幕密度（0.75 / 1.0 / 1.5）
        density = dm.density;
        stateBarHeight = getStateBarHeight(mContext);
        scale = mContext.getResources().getDisplayMetrics().density;
    }

    public static ScreenUtil getInstance(Context mContext) {
        if (screen == null) {
            synchronized (ScreenUtil.class) {
                if (null == screen) {
                    screen = new ScreenUtil(mContext);
                }
            }
        }
        return screen;
    }


    public static int getStateBarHeight() {
        return stateBarHeight;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStateBarHeight(Context mContext) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = mContext.getResources().getDimensionPixelSize(x);
            return sbar;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    public static int dip2px(float dipValue) {
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * SP 转 Pixels
     *
     * @param sp      sp 字体大小
     * @return pixels
     */
    public static float sp2Px(float sp) {
        return sp * scale;
    }
}
