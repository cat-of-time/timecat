package com.time.cat.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.AttrRes;

/**
 * Created by Administrator on 2016/12/3.
 */
public class ColorUtil {
    public static int getPropertyTextColor(int color, int alpha) {
        if (alpha < 20) {

        }
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        if (red > 128 && green > 128 && blue > 128) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }

    public static int resolveColor(Context context, @AttrRes int attr) {
        return resolveColor(context, attr, 0);
    }

    public static int resolveColor(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, fallback);
        } finally {
            a.recycle();
        }
    }

    public static String getColorHex(int color) {
        return "#" + CHexConverUtil.algorismToHEXString(color, 8);
    }
}
