package com.yumingchuan.rsqmonthcalendar.utils;

import android.content.Context;

import com.yumingchuan.rsqmonthcalendar.bean.DayInfo;
import com.yumingchuan.rsqmonthcalendar.bean.DayType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.yumingchuan.rsqmonthcalendar.utils.CalendarUtils.getHolidayFromSolar;

/**
 * Created by yumingchuan on 2017/6/16.
 */

public class MonthViewCalendarUtil {

    private static final int MAX_DAY_COUNT = 42;//最大格子数量
    private DayInfo[] dayInfos;

    private static MonthViewCalendarUtil monthViewCalendarUtil;
    private int mSelYear;
    private int mSelMonth;
    private final Context mContext;

    public MonthViewCalendarUtil(Context mContext) {
        this.mContext = mContext;
    }

    public static MonthViewCalendarUtil getInstance(Context mContext) {
        if (monthViewCalendarUtil == null) {//双重校验DCL单例模式
            synchronized (MonthViewCalendarUtil.class) {//同步代码块
                if (monthViewCalendarUtil == null) {
                    monthViewCalendarUtil = new MonthViewCalendarUtil(mContext);
                }
            }
        }
        return monthViewCalendarUtil;
    }

    /**
     * 获取日历数据集合
     **/
    public List<DayInfo> getDayInfo(final Calendar calendar) {

        dayInfos = new DayInfo[MAX_DAY_COUNT];

        List<DayInfo> tempListDayInfos = new ArrayList<DayInfo>();

        tempListDayInfos.clear();
        tempListDayInfos.addAll(Arrays.asList(get42DayInfo(calendar)));

        int[] weekDays = {0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < tempListDayInfos.size(); i++) {
            switch (i / 7) {
                case 0:
                    if (tempListDayInfos.get(i).dayType == DayType.DAY_TYPE_NOW) {
                        weekDays[0]++;
                        tempListDayInfos.get(i).whichWeek = 1;
                    }
                    break;
                case 1:
                    if (tempListDayInfos.get(i).dayType == DayType.DAY_TYPE_NOW) {
                        weekDays[1]++;
                        tempListDayInfos.get(i).whichWeek = 2;
                    }
                    break;
                case 2:
                    if (tempListDayInfos.get(i).dayType == DayType.DAY_TYPE_NOW) {
                        weekDays[2]++;
                        tempListDayInfos.get(i).whichWeek = 3;
                    }
                    break;
                case 3:
                    if (tempListDayInfos.get(i).dayType == DayType.DAY_TYPE_NOW) {
                        weekDays[3]++;
                        tempListDayInfos.get(i).whichWeek = 4;
                    }
                    break;
                case 4:
                    if (tempListDayInfos.get(i).dayType == DayType.DAY_TYPE_NOW) {
                        weekDays[4]++;
                        tempListDayInfos.get(i).whichWeek = 5;
                    }
                    break;
                case 5:
                    if (tempListDayInfos.get(i).dayType == DayType.DAY_TYPE_NOW) {
                        weekDays[5]++;
                        tempListDayInfos.get(i).whichWeek = 6;
                    }
                    break;
            }
        }

        for (int i = 0; i < tempListDayInfos.size(); i++) {
            tempListDayInfos.get(i).position = i;
            tempListDayInfos.get(i).daysOfWeek = weekDays[i / 7];///一周有几天是今天
        }

        return tempListDayInfos;
    }


    /**
     * 42天的数据
     **/
    public DayInfo[] get42DayInfo(Calendar calendar) {

        mSelYear = calendar.get(Calendar.YEAR);
        mSelMonth = calendar.get(Calendar.MONTH);

        getLastMonth();
        getThisMonth(calendar);
        getNextMonth();
        getLunarStr(dayInfos);
        getHolidayStr(mContext, dayInfos);
        return dayInfos;
    }

    private void getLastMonth() {

        int lastYear, lastMonth;
        if (mSelMonth == 0) {
            lastYear = mSelYear - 1;
            lastMonth = 11;
        } else {
            lastYear = mSelYear;
            lastMonth = mSelMonth - 1;
        }
        int monthDays = CalendarUtils.getMonthDays(lastYear, lastMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        for (int day = 0; day < weekNumber - 1; day++) {

            if (dayInfos[day] == null)
                dayInfos[day] = new DayInfo();
            dayInfos[day].day = monthDays - weekNumber + day + 2;
            dayInfos[day].dayType = DayType.DAY_TYPE_FORE;

            Calendar tempCalendar = Calendar.getInstance();

            tempCalendar.set(lastYear, lastMonth, dayInfos[day].day);

            dayInfos[day].date = TimestampTool.sdf_yMdp.format(tempCalendar.getTime());

            dayInfos[day].lunarStr = CalendarUtils.getHolidayFromSolar(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH));

        }
    }

    private void getThisMonth(Calendar calendar) {

        String currentSelectedDay = TimestampTool.sdf_yMdp.format(calendar.getTime());

        int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        for (int day = 0; day < monthDays; day++) {
            int col = (day + weekNumber - 1) % 7;
            int row = (day + weekNumber - 1) / 7;
            if (dayInfos[row * 7 + col] == null)
                dayInfos[row * 7 + col] = new DayInfo();
            dayInfos[row * 7 + col].day = day + 1;
            dayInfos[row * 7 + col].dayType = DayType.DAY_TYPE_NOW;

            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.set(mSelYear, mSelMonth, dayInfos[row * 7 + col].day);

            dayInfos[row * 7 + col].date = TimestampTool.sdf_yMdp.format(tempCalendar.getTime());

            dayInfos[row * 7 + col].isToday = dayInfos[row * 7 + col].date.equals(TimestampTool.sdf_yMdp.format(Calendar.getInstance().getTime()));//
            dayInfos[row * 7 + col].isSelectedDay = dayInfos[row * 7 + col].date.equals(currentSelectedDay);

            dayInfos[row * 7 + col].lunarStr = getHolidayFromSolar(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH));

        }
    }

    private void getNextMonth() {
        int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        int nextMonthDays = 42 - monthDays - weekNumber + 1;
        int nextMonth = mSelMonth + 1;
        int nextYear = mSelYear;
        if (nextMonth == 12) {
            nextMonth = 0;
            nextYear += 1;
        }
        for (int day = 0; day < nextMonthDays; day++) {
            int column = (monthDays + weekNumber - 1 + day) % 7;
            int row = 5 - (nextMonthDays - day - 1) / 7;
            if (dayInfos[row * 7 + column] == null)
                dayInfos[row * 7 + column] = new DayInfo();
            dayInfos[row * 7 + column].day = day + 1;
            dayInfos[row * 7 + column].dayType = DayType.DAY_TYPE_NEXT;

            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.set(nextYear, nextMonth, dayInfos[row * 7 + column].day);
            dayInfos[row * 7 + column].date = TimestampTool.sdf_yMdp.format(tempCalendar.getTime());

            dayInfos[row * 7 + column].lunarStr = getHolidayFromSolar(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH));
        }
    }


    /**
     * 获取农历的信息
     *
     * @param dayInfos
     */
    private void getLunarStr(DayInfo[] dayInfos) {

        int firstYear, firstMonth, firstDay;
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        if (weekNumber == 1) {
            firstYear = mSelYear;
            firstMonth = mSelMonth + 1;
            firstDay = 1;
        } else {
            int monthDays;
            if (mSelMonth == 0) {
                firstYear = mSelYear - 1;
                firstMonth = 11;
                monthDays = CalendarUtils.getMonthDays(firstYear, firstMonth);
                firstMonth = 12;
            } else {
                firstYear = mSelYear;
                firstMonth = mSelMonth - 1;
                monthDays = CalendarUtils.getMonthDays(firstYear, firstMonth);
                firstMonth = mSelMonth;
            }
            firstDay = monthDays - weekNumber + 2;
        }

        //LogUtils.i("firstYear", firstYear + "-" + firstMonth + "-" + firstDay);
        LunarCalendarUtils.Lunar lunar = LunarCalendarUtils.solarToLunar(new LunarCalendarUtils.Solar(firstYear, firstMonth, firstDay));
        int days;
        int day = lunar.lunarDay;//
        int leapMonth = LunarCalendarUtils.leapMonth(lunar.lunarYear);

        days = LunarCalendarUtils.daysInMonth(lunar.lunarYear, lunar.lunarMonth, lunar.isLeap);

        for (int i = 0; i < 42; i++) {
            if (day > days) {
                day = 1;
                if (lunar.lunarMonth == 12) {
                    lunar.lunarMonth = 1;
                    lunar.lunarYear = lunar.lunarYear + 1;
                } else {
                    if (lunar.lunarMonth == leapMonth) {
                        days = LunarCalendarUtils.daysInLunarMonth(lunar.lunarYear, lunar.lunarMonth);
                    } else {
                        lunar.lunarMonth++;
                        days = LunarCalendarUtils.daysInLunarMonth(lunar.lunarYear, lunar.lunarMonth);
                    }
                }
            }


            if ("".equals(dayInfos[i].lunarStr)) {
                dayInfos[i].lunarStr = LunarCalendarUtils.getLunarHoliday(lunar.lunarYear, lunar.lunarMonth, day);
            }
            if ("".equals(dayInfos[i].lunarStr)) {
                dayInfos[i].lunarStr = LunarCalendarUtils.getLunarDayString(day);
            }

            day++;
        }
    }

    private void getHolidayStr(Context mContext, DayInfo[] dayInfos) {

        int[] mHolidays = CalendarUtils.getInstance(mContext).getHolidays(mSelYear, mSelMonth + 1);

        for (int i = 0; i < 42; i++) {
            if (dayInfos[i].dayType == DayType.DAY_TYPE_NOW) {
                if (mHolidays[i] == 1) {
                    dayInfos[i].holidays = 1;
                } else if (mHolidays[i] == 2) {
                    dayInfos[i].holidays = 2;
                } else {
                    dayInfos[i].holidays = 0;
                }
            }
        }

    }


}
