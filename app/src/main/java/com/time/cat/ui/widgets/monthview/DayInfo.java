package com.yumingchuan.rsqmonthcalendar.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumingchuan on 2017/6/19.
 */

public class DayInfo {
    public int position;
    public int day;
    public DayType dayType;
    public int daysOfWeek;//某一周是今天的日期的天数
    public int whichWeek;//是哪一周
    public String date;
    public String lunarStr;
    public int holidays;//1：休 2：班
    public boolean isToday;
    public boolean isSelectedDay;
    public List<ScheduleToDo> todos = new ArrayList<ScheduleToDo>();

    @Override
    public String toString() {
        return String.valueOf(day);
    }
}
