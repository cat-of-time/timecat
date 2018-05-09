package com.yumingchuan.rsqmonthcalendar.bean;

/**
 * Created by yumingchuan on 16/10/21.
 */

public class CurrentMonthInfo {

    public boolean isExpand;//是否是展开的
    public int weeks = 6;//一共多少周
    public int currentOpenWeek = -1;//打开的行
    public int currentOpenDayOfWeek = -1;//打开的天在本周的位置
    public int currentOpenDayOfMonth = -1;//打开的位置在月中的位置

    public float monthAreaHeight;//月视图的高
    public float monthAreaWidth;//月视图的宽度
    public int weekAreaHeight;//每周的高

    public int initY;//空隙的高度

    public String currentYMD;//yyyy-MM-dd

}
