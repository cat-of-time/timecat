package com.yumingchuan.rsqmonthcalendar.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yumingchuan.rsqmonthcalendar.bean.DayInfo;
import com.yumingchuan.rsqmonthcalendar.bean.DayType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by Jimmy on 2016/10/6 0006.
 */
public class CalendarUtils {

    private static CalendarUtils sUtils;
    private static Map<String, int[]> sAllHolidays;

    public static synchronized CalendarUtils getInstance(Context context) {
        if (sUtils == null) {
            sUtils = new CalendarUtils();
            initAllHolidays(context);
        }
        return sUtils;
    }

    private static void initAllHolidays(Context context) {
        try {
            InputStream is = context.getAssets().open("holiday.json");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i;
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
            sAllHolidays = new Gson().fromJson(baos.toString(), new TypeToken<Map<String, int[]>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过年份和月份 得到当月的日子
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        month++;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }

    /**
     * 返回当前月份1号位于周几
     *
     * @param year  年份
     * @param month 月份，传入系统获取的，不需要正常的
     * @return 日：1		一：2		二：3		三：4		四：5		五：6		六：7
     */
    public static int getFirstDayWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获得两个日期距离几周
     *
     * @return
     */
    public static int getWeeksAgo(int lastYear, int lastMonth, int lastDay, int year, int month, int day) {
        Calendar lastClickDay = Calendar.getInstance();
        lastClickDay.set(lastYear, lastMonth, lastDay, 0, 0, 0);
        int week = lastClickDay.get(Calendar.DAY_OF_WEEK) - 1;
        Calendar clickDay = Calendar.getInstance();
        clickDay.set(year, month, day, 0, 0, 0);
        if (clickDay.getTimeInMillis() > lastClickDay.getTimeInMillis()) {
            return (int) ((clickDay.getTimeInMillis() - lastClickDay.getTimeInMillis() + week * 24 * 3600 * 1000) / (7 * 24 * 3600 * 1000));
        } else {
            return (int) ((clickDay.getTimeInMillis() - lastClickDay.getTimeInMillis() + (week - 6) * 24 * 3600 * 1000) / (7 * 24 * 3600 * 1000));
        }
    }

    /**
     * 获得两个日期距离几个月
     *
     * @return
     */
    public static int getMonthsAgo(int lastYear, int lastMonth, int year, int month) {
        return (year - lastYear) * 12 + (month - lastMonth);
    }

    public static int getWeekRow(int year, int month, int day) {
        int week = getFirstDayWeek(year, month);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int lastWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (lastWeek == 7)
            day--;
        return (day + week - 1) / 7;
    }

    /**
     * 根据国历获取假期
     *
     * @return
     */
    public static String getHolidayFromSolar(int year, int month, int day) {
        String message = "";
        if (month == 0 && day == 1) {
            message = "元旦";
        } else if (month == 1 && day == 14) {
            message = "情人节";
        } else if (month == 2 && day == 8) {
            message = "妇女节";
        } else if (month == 2 && day == 12) {
            message = "植树节";
        } else if (month == 3) {
            if (day == 1) {
                message = "愚人节";
            } else if (day >= 4 && day <= 6) {
                if (year <= 1999) {
                    int compare = (int) (((year - 1900) * 0.2422 + 5.59) - ((year - 1900) / 4));
                    if (compare == day) {
                        message = "清明节";
                    }
                } else {
                    int compare = (int) (((year - 2000) * 0.2422 + 4.81) - ((year - 2000) / 4));
                    if (compare == day) {
                        message = "清明节";
                    }
                }
            }
        } else if (month == 4 && day == 1) {
            message = "劳动节";
        } else if (month == 4 && day == 4) {
            message = "青年节";
        } else if (month == 4 && day == 12) {
            message = "护士节";
        } else if (month == 5 && day == 1) {
            message = "儿童节";
        } else if (month == 6 && day == 1) {
            message = "建党节";
        } else if (month == 7 && day == 1) {
            message = "建军节";
        } else if (month == 8 && day == 10) {
            message = "教师节";
        } else if (month == 9 && day == 1) {
            message = "国庆节";
        } else if (month == 10 && day == 11) {
            message = "光棍节";
        } else if (month == 11 && day == 25) {
            message = "圣诞节";
        }
        return message;
    }

    public int[] getHolidays(int year, int month) {
        int holidays[];
        if (sAllHolidays != null) {
            holidays = sAllHolidays.get(year + "" + month);
            if (holidays == null) {
                holidays = new int[42];
            }
        } else {
            holidays = new int[42];
        }
        return holidays;
    }

    public static int getMonthRows(int year, int month) {
        int size = getFirstDayWeek(year, month) + getMonthDays(year, month) - 1;
        return size % 7 == 0 ? size / 7 : (size / 7) + 1;
    }


    private static final int MAX_DAY_COUNT = 42;//最大格子数量
    private DayInfo[] dayInfos = new DayInfo[MAX_DAY_COUNT];//每月应该有的天数，36为最大格子数
    private DayInfo[] day7Infos = new DayInfo[7];//每周应该有7天

    /**
     * 42天的数据
     **/
    public DayInfo[] get42DayInfo(Calendar calendar) {
        getLastMonth(calendar);
        getThisMonth(calendar);
        getNextMonth(calendar);
        return dayInfos;
    }


    private void getLastMonth(Calendar calendar) {

        String currentSelectedDay = TimestampTool.sdf_yMdp.format(calendar.getTime());

        int mSelYear = calendar.get(Calendar.YEAR);
        int mSelMonth = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DATE);

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


            dayInfos[day].isToday = dayInfos[day].date.equals(TimestampTool.sdf_yMdp.format(Calendar.getInstance().getTime()));//
            dayInfos[day].isSelectedDay = dayInfos[day].date.equals(currentSelectedDay);
            dayInfos[day].lunarStr = getHolidayFromSolar(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH));

        }
    }


    private void getThisMonth(Calendar calendar) {
        String currentSelectedDay = TimestampTool.sdf_yMdp.format(calendar.getTime());

        int mSelYear = calendar.get(Calendar.YEAR);
        int mSelMonth = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DATE);

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

    private void getNextMonth(Calendar calendar) {
        String currentSelectedDay = TimestampTool.sdf_yMdp.format(calendar.getTime());

        int mSelYear = calendar.get(Calendar.YEAR);
        int mSelMonth = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DATE);

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


            dayInfos[row * 7 + column].isToday = dayInfos[row * 7 + column].date.equals(TimestampTool.sdf_yMdp.format(Calendar.getInstance().getTime()));//
            dayInfos[row * 7 + column].isSelectedDay = dayInfos[row * 7 + column].date.equals(currentSelectedDay);
            dayInfos[row * 7 + column].lunarStr = getHolidayFromSolar(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH));
        }
    }

}

