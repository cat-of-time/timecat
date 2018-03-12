package com.time.cat.util.date;

import com.time.cat.util.string.StringUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/19
 * @discription null
 * @usage null
 */
public class DateUtil {

    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 比较日期大小
     * @param dateStr0
     * @param dateStr1
     * @return
     */
    public static int compareDate(String dateStr0, String dateStr1) {
        Date date1 = convertDateStrToDate(dateStr0, "datetime");
        Date date2 = convertDateStrToDate(dateStr1, "datetime");
        int result = date1.compareTo(date2);
        return result;
    }

    /**
     * 格式化日期
     * @param date
     * @param pattern
     * 		"date":返回日期(yyyy-MM-dd)
     * 		"time":返回时间(HH:mm:ss)
     * 		"datetime":返回日期和时间(yyyy-MM-dd HH:mm:ss)
     * @return
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return "";
        }

        if ("date".equals(pattern)) {
            pattern = "yyyy-MM-dd";
        } else if ("time".equals(pattern)) {
            pattern = "HH:mm:ss";
        } else if ("datetime".equals(pattern)) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    /**
     * 格式化日期字符串
     * @param dateStr
     * @param type
     * 		"date":返回日期(yyyy-MM-dd)
     * 		"time":返回时间(HH:mm:ss)
     * 		"datetime":返回日期和时间(yyyy-MM-dd HH:mm:ss)
     * @return
     */
    public static String formatDate(String dateStr, String type) {
        if (dateStr == null || "".equals(dateStr.trim())) {
            return "";
        }

        DateFormat formatter = DateFormat.getDateInstance();
        Date date = null;
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatDate(date, type);
    }

    /**
     * 把日期字符串转换成日期
     * @param dateStr
     * 		日期字符串
     * @param pattern
     * 		"date":日期,
     * 		"datetime":日期和时间
     * @return
     */
    public static Date convertDateStrToDate(String dateStr, String pattern) {
        if (dateStr == null || "".equals(dateStr.trim())) {
            return null;
        }
        if (!dateStr.contains(":")) {
            dateStr += " 00:00:00";
        }

        if ("date".equals(pattern)) {
            pattern = "yyyy-MM-dd";
        } else if ("datetime".equals(pattern)) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取当前时间
     * @return
     */
    public static Date getCurrTime() {
        return new Date();
    }

    /**
     * 获取当前时间
     * @param pattern
     * 		"date":返回日期(yyyy-MM-dd),
     * 		"time":返回时间(HH:mm:ss),
     * 		"datetime":返回日期和时间(yyyy-MM-dd HH:mm:ss)
     * @return
     */
    public static String getCurrTime(String pattern) {
        return formatDate(new Date(), pattern);
    }

    /**
     * 获取中文格式当前日期
     * @param pattern
     * 		"long":1984年10月9日
     * 		"full":1984年10月9日 星期二
     * @return
     */
    public static String getCurrDateCN(String pattern) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if ("long".equals(pattern)) {
            return year + "年" + month + "月" + day + "日";
        }
        if ("full".equals(pattern)) {
            return year + "年" + month + "月" + day + "日 " + getDayCN(calendar.getTime());
        }
        return "";
    }

    /**
     * 在原日期加一段时间间隔
     * @param date
     * 		原日期
     * @param field
     * 		"yyyy":年
     * 		"MM":月
     * 		"dd":日
     * @param amount
     * 		间隔长度
     * @return
     */
    public static Date dateAdd(Date date, String field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if ("yyyy".equals(field)) {
            calendar.add(Calendar.YEAR, amount);
        } else if ("MM".equals(field)) {
            calendar.add(Calendar.MONTH, amount);
        } else if ("dd".equals(field)) {
            calendar.add(Calendar.DATE, amount);
        }
        return calendar.getTime();
    }

    /**
     * 在原日期加一段时间间隔
     * @param dateStr
     * 		原日期
     * @param field
     * 		"yyyy":年
     * 		"MM":月
     * 		"dd":日
     * @param amount
     * 		间隔长度
     * @param pattern
     * 		"date":返回日期(yyyy-MM-dd),
     * 		"time":返回时间(HH:mm:ss),
     * 		"datetime":返回日期和时间(yyyy-MM-dd HH:mm:ss)
     * @return
     */
    public static String dateAdd(String dateStr, String field, int amount, String pattern) {
        if (StringUtil.isEmpty(dateStr)) {
            return "";
        }

        Date date = convertDateStrToDate(dateStr, "datetime");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if ("yyyy".equals(field)) {
            calendar.add(Calendar.YEAR, amount);
        } else if ("MM".equals(field)) {
            calendar.add(Calendar.MONTH, amount);
        } else if ("dd".equals(field)) {
            calendar.add(Calendar.DATE, amount);
        }
        date = calendar.getTime();
        dateStr = formatDate(date, pattern);
        return dateStr;
    }

    /**
     * 计算日期差
     * @param date1
     * @param date2
     * @param field
     * 		yyyy:年
     * 		MM:月
     * 		dd:日
     * @return
     * 		date2 - date1
     */
    public static int dateDiff(Date date1, Date date2, String field) {
        boolean flag = date1.compareTo(date2) > 0;
        if (flag) {
            Date tmp = date1;
            date1 = date2;
            date2 = tmp;
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        int year1 = cal1.get(Calendar.YEAR);
        int month1 = cal1.get(Calendar.MONTH);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int year2 = cal2.get(Calendar.YEAR);
        int month2 = cal2.get(Calendar.MONTH);
        int yearDiff = year2- year1;

        int diff = 0;
        if ("yyyy".equals(field)) {
            diff = yearDiff;
        } else if ("MM".equals(field)) {
            if (yearDiff <= 0) {
                diff = month2 - month1;
            } else if (yearDiff == 1) {
                diff = 12 - month1 + month2;
            } else {
                diff = 12 - month1 + (year2 - year1 - 1) * 12 + month2;
            }
        } else if ("dd".equals(field)) {
            if (yearDiff <= 0) {
                diff = cal2.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR);
            } else if (yearDiff == 1) {
                diff = getDaysLeftOfYear(date1) + cal2.get(Calendar.DAY_OF_YEAR);
            } else {
                diff = getDaysLeftOfYear(date1);
                Calendar cal;
                for (int i = 1; i < yearDiff; i++) {
                    cal = Calendar.getInstance();
                    cal.setTime(dateAdd(date1, "yyyy", i));
                    diff += cal.getActualMaximum(Calendar.DAY_OF_YEAR);
                }
                diff += cal2.get(Calendar.DAY_OF_YEAR);
            }
        }

        return flag ? -1 * diff : diff;
    }

    /**
     * 计算日期差
     * @param dateStr1
     * @param dateStr2
     * @param field
     * 		yyyy:年
     * 		MM:月
     * 		dd:日
     * @return
     */
    public static int dateDiff(String dateStr1, String dateStr2, String field) {
        Date date1 = convertDateStrToDate(dateStr1, "date");
        Date date2 = convertDateStrToDate(dateStr2, "date");
        int dateDiff = dateDiff(date1, date2, field);
        return dateDiff;
    }

    /**
     * 是否闰年
     * @param year
     * @return
     */
    public static boolean isLeapYear(int year) {
        boolean flag = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
        return flag;
    }

    /**
     * 是否闰年
     * @param date
     * @return
     */
    public static boolean isLeapYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        boolean flag = isLeapYear(cal.get(Calendar.YEAR));
        return flag;
    }

    /**
     * 计算给定日期当年总天数
     * @param date
     * @return
     */
    public static int getDaysOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR);
    }

    /**
     * 计算给定日期当年剩余天数
     * @param date
     * @return
     */
    public static int getDaysLeftOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int cnt = cal.getActualMaximum(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR);
        return cnt;
    }

    /**
     * 获得星期几(周日为1，周六为7)
     * @param date
     * 		给定日期
     * @return
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获得星期几（中文）
     * @param date
     * @return
     */
    public static String getDayCN(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }

    /**
     * 计算给定日期所在月的第一天
     * @param date
     * 		给定日期
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 计算给定日期所在月的最后一天
     * @param date
     * 		给定日期
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * 计算给定日期所在周的第一天(周一)
     * @param date
     * 		给定日期
     * @return
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        dayOfWeek = dayOfWeek == Calendar.SUNDAY ? 8 : dayOfWeek;
        int dValue = Calendar.MONDAY - dayOfWeek;
        calendar.add(Calendar.DAY_OF_WEEK, dValue);
        return calendar.getTime();
    }

    /**
     * 计算给定日期所在月的最后一天(周日)
     * @param date
     * 		给定日期
     * @return
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        dayOfWeek = dayOfWeek == Calendar.SUNDAY ? 8 : dayOfWeek;
        int diff = 8 - dayOfWeek;
        calendar.add(Calendar.DAY_OF_WEEK, diff);
        return calendar.getTime();
    }

    /**
     * 计算月视图中展示的第一天
     * @param date
     * 		给定日期
     * @return
     */
    public static Date getFirstDayOfMonthView(Date date) {
        Date firstDayOfMonth = getFirstDayOfMonth(date);
        int dayOfWeek = getDay(firstDayOfMonth);
        int diff = Calendar.SUNDAY - dayOfWeek;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDayOfMonth);
        calendar.add(Calendar.DAY_OF_WEEK, diff);
        return calendar.getTime();
    }

    /**
     * 计算月视图中展示的最后一天
     * @param date
     * 		给定日期
     * @return
     */
    public static Date getLastDayOfMonthView(Date date) {
        Date lastDayOfMonth = getLastDayOfMonth(date);
        int dayOfWeek = getDay(lastDayOfMonth);
        int diff = Calendar.SATURDAY - dayOfWeek;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastDayOfMonth);
        calendar.add(Calendar.DAY_OF_WEEK, diff);
        return calendar.getTime();
    }

    /**
     * 计算月视图展示行数
     * @param date
     * @return
     */
    public static int getRowCntOfMonthView(Date date) {
        int dayOfWeek1 = getDay(getFirstDayOfMonth(date));
        int dayOfWeek2 = getDay(getLastDayOfMonth(date));

        if (dayOfWeek1 == Calendar.SUNDAY && dayOfWeek2 == Calendar.SATURDAY) {
            return 4;
        }
        if (dayOfWeek1 != Calendar.SUNDAY && dayOfWeek2 != Calendar.SATURDAY) {
            return 6;
        }
        return 5;
    }

}
