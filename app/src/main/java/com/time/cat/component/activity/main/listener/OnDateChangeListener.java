package com.time.cat.component.activity.main.listener;

/**
 * @author dlink
 * @date 2018/1/25
 * @discription 回调接口，监听calendar的日期改变
 */
public interface OnDateChangeListener {
    void onDateChange(int year, int month, boolean isToday);
}
