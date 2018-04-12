package com.time.cat.ui.widgets.FlipView;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/5
 * @discription null
 * @usage null
 */
public class FlipMenuItem  {

    public String title;
    public int titleColor = Color.BLACK;
    public int bgColor = Color.WHITE;
    public Bitmap icon;

    public FlipMenuItem(String title) {
        this.title = title;
    }

    public FlipMenuItem(String title, Bitmap icon) {
        this.title = title;
        this.icon = icon;
    }

    public FlipMenuItem(String title, int bgColor) {
        this.title = title;
        this.bgColor = bgColor;
    }

    public FlipMenuItem(String title, int titleColor, int bgColor) {
        this.title = title;
        this.titleColor = titleColor;
        this.bgColor = bgColor;
    }

    public FlipMenuItem(String title, int bgColor, Bitmap icon) {
        this.title = title;
        this.bgColor = bgColor;
        this.icon = icon;
    }

    public FlipMenuItem(String title, int titleColor, int bgColor, Bitmap icon) {
        this.title = title;
        this.titleColor = titleColor;
        this.bgColor = bgColor;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "FlipMenuItem{"
                + "title='" + title + '\''
                + ", titleColor=" + titleColor
                + ", bgColor=" + bgColor
                + ", icon=" + icon
                + '}';
    }
}