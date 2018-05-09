package com.yumingchuan.rsqmonthcalendar.bean;

/**
 * Created by yumingchuan on 2017/6/19.
 */

/**
 * 日期类型
 **/
public enum DayType {
    DAY_TYPE_NONE(0),
    DAY_TYPE_FORE(1),
    DAY_TYPE_NOW(2),
    DAY_TYPE_NEXT(3);
    private int value;

    DayType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}