package com.yumingchuan.rsqmonthcalendar.utils;


import com.yumingchuan.rsqmonthcalendar.bean.DayInfo;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class TimestampTool {

    //斜杠（slash）:s，平行杠的约定（parallel):p
    public static SimpleDateFormat simpleDateFormat_1 = new SimpleDateFormat();
    public static SimpleDateFormat simpleDateFormat_2 = new SimpleDateFormat();

    public static SimpleDateFormat sdf_all = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat sdf_ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static SimpleDateFormat sdf_ymdhm_dot = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    public static SimpleDateFormat sdf_hm = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat sdf_all_z = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.CHINA);

    public static SimpleDateFormat sdf_yMd = new SimpleDateFormat("yyyyMMdd");
    public static SimpleDateFormat sdf_yM = new SimpleDateFormat("yyyyMM");
    public static SimpleDateFormat sdf_YM = new SimpleDateFormat("yyyy年M月");
    public static SimpleDateFormat sdf_YMD = new SimpleDateFormat("yyyy年M月d日");
    public static SimpleDateFormat sdf_YMMDD = new SimpleDateFormat("yyyy年MM月dd日");


    public static SimpleDateFormat sdf_Md = new SimpleDateFormat("M月d");
    public static SimpleDateFormat sdf_mdE = new SimpleDateFormat("M月d EEEE");
    public static SimpleDateFormat sdf_MDE = new SimpleDateFormat("MM月dd EEEE");
    public static SimpleDateFormat sdf_MD = new SimpleDateFormat("M月d日");
    public static SimpleDateFormat sdf_MMDD = new SimpleDateFormat("MM月dd日");

    public static SimpleDateFormat sdf_yMds = new SimpleDateFormat("yyyy/MM/dd");
    public static SimpleDateFormat sdf_yMdp = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat sdf_yMp = new SimpleDateFormat("yyyy-MM");
    public static SimpleDateFormat sdf_ymp = new SimpleDateFormat("yyyy-M");
    public static SimpleDateFormat sdf_yMd_dot = new SimpleDateFormat("yyyy.MM.dd");
    public static SimpleDateFormat sdf_yM_dot = new SimpleDateFormat("yyyy.MM");
    public static SimpleDateFormat sdf_Mdp = new SimpleDateFormat("MM.dd");
    public static SimpleDateFormat sdf_d = new SimpleDateFormat("d");
    public static SimpleDateFormat sdf_M = new SimpleDateFormat("M月");
    public static SimpleDateFormat sdf_m = new SimpleDateFormat("M");
    public static SimpleDateFormat sdf_y = new SimpleDateFormat("yyyy");
    public static SimpleDateFormat sdf_Y = new SimpleDateFormat("yyyy年");

    public static SimpleDateFormat sdf_ydp = new SimpleDateFormat("yyyy-dd");

    public static Calendar mCalendar = Calendar.getInstance();//创建一个日期实例


    public static class FormatTypeStr {
        public static final String sdf_all = "yyyy-MM-dd HH:mm:ss";
        public static final String sdf_ymdhm = "yyyy-MM-dd HH:mm";
        public static final String sdf_ymdhm_dot = "yyyy.MM.dd HH:mm";
        public static final String sdf_hm = "HH:mm";
        public static final String sdf_all_z = "yyyy-MM-dd HH:mm:ss z";

        public static final String sdf_yMd = "yyyyMMdd";
        public static final String sdf_yM = ("yyyyMM");
        public static final String sdf_YM = ("yyyy年M月");
        public static final String sdf_YMM = ("yyyy年MM月");
        public static final String sdf_YMD = ("yyyy年M月d日");
        public static final String sdf_YMMDD = ("yyyy年MM月dd日");


        public static final String sdf_Md = ("M月d");
        public static final String sdf_MdE = ("M月d EEEE");
        public static final String sdf_MD = ("M月d日");
        public static final String sdf_MMDD = ("MM月dd日");

        public static final String sdf_yMds = ("yyyy/MM/dd");
        public static final String sdf_yMdp = ("yyyy-MM-dd");
        public static final String sdf_yMp = ("yyyy-MM");
        public static final String sdf_ymp = ("yyyy-M");
        public static final String sdf_yMd_dot = ("yyyy.MM.dd");
        public static final String sdf_yM_dot = ("yyyy.MM");
        public static final String sdf_MDp = ("MM.dd");
        public static final String sdf_MMDp = ("MM月dd");
        public static final String sdf_mdp = ("M.d");
        public static final String sdf_d = ("d");
        public static final String sdf_M = ("M月");
        public static final String sdf_m = ("M");
        public static final String sdf_y = ("yyyy");
        public static final String sdf_Y = ("yyyy年");

        public static final String sdf_ydp = ("yyyy-dd");
    }


    /**
     * @param dateStr 被格式化的日期（类型是 format1）
     * @param format1
     * @param format2 最终被转化的类型
     * @return
     */
    public static String getDateCastDate(String dateStr, String format1, String format2) {
        String tempStr = "";
        try {
            simpleDateFormat_1.applyPattern(format1);
            simpleDateFormat_2.applyPattern(format2);
            tempStr = simpleDateFormat_2.format(simpleDateFormat_1.parse(dateStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempStr;
    }


    /**
     * 7天的数据
     **/
    public static List<DayInfo> get7DayInfo(Calendar calendar) {

        List<DayInfo> temp = new ArrayList<DayInfo>();
        for (int i = 0; i < 7; i++) {
            DayInfo dayInfo = new DayInfo();
            dayInfo.day = Integer.parseInt(sdf_d.format(calendar.getTime()));
            dayInfo.date = sdf_yMdp.format(calendar.getTime());
            temp.add(dayInfo);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }
        return temp;
    }








    /**
     * 获取某天本周第一天是星期日的日期，例如2016-12-28 00:00:00 输出：2016-12-25
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getSomeWeekFirstDay(String dateStr, boolean isLast) {

        String tempStr = "";
        try {
            Calendar mCalendar = Calendar.getInstance();
            Date date = sdf_all.parse(dateStr);
            mCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
            mCalendar.setTime(date);
            mCalendar.add(Calendar.DATE, -mCalendar.get(Calendar.DAY_OF_WEEK) + 1);
            if (isLast) {
                mCalendar.add(Calendar.DATE, 6);
            }
            tempStr = sdf_yMdp.format(mCalendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tempStr;
    }


    /**
     * 获取某天本周第一天是星期日的日期，例如2016-12-28 00:00:00 输出：2016-12-25
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getSomeMonthFirstOrLastDay(String dateStr, boolean isLast) {

        String tempStr = "";
        try {
            Calendar mCalendar = Calendar.getInstance();
            Date date = sdf_all.parse(dateStr);
            mCalendar.setTime(date);

            mCalendar.set(Calendar.DAY_OF_MONTH, 1);//设置为本月1日
            int week = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
            mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.get(Calendar.DAY_OF_MONTH) - (week == 0 ? 7 : week));

            if (isLast) {
                mCalendar.add(Calendar.DATE, 41);
            }
            tempStr = sdf_yMdp.format(mCalendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tempStr;
    }


    /**
     * @param type
     * @param dateStr
     * @return true 表示不延期
     */
    public static boolean compareToToday(int type, String dateStr) {
        boolean flag = false;
        try {
            if (type == 0) {///"yyyy-MM-dd"
                flag = getSomedayTime(dateStr + " 00:00:00") - getSomedayTime(getCurrentDateToWeb()) >= 0;
            } else if (type == 1) {//yyyyMMdd
                flag = getSomedayTime(sdf_yMdp.format(sdf_yMd.parse(dateStr)) + " 00:00:00") - getSomedayTime(getCurrentDateToWeb()) >= 0;
            } else if (type == 2) {///"yyyy-MM-dd  00:00:00"
                flag = getSomedayTime(dateStr) - getSomedayTime(getCurrentDateToWeb()) >= 0;
            }
        } catch (Exception e) {

        }
        return flag;
    }


    /**
     * 获取当前日期的date
     *
     * @return
     * @throws ParseException
     */
    public static Date getDate() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 获得某日前后的某一天
     *
     * @param date java.util.Date
     * @param day  int
     * @return java.util.Date
     */
    public static Date getDay(Date date, int day) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(GregorianCalendar.DATE, day);
        return c.getTime();
    }

    /**
     * @param timeStr yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long getSomedayTime(String timeStr) {

        Date date = null;
        try {
            date = sdf_all.parse(timeStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTimeInMillis();
    }

    /**
     * 获取当前时间的字符串 用来提交给web端
     *
     * @return String ex:2013-11-21 00:00:00
     */
    public static String getCurrentDateToWeb() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
    }

    /**
     * 获取当前时间的字符串 用来提交给web端
     *
     * @return String ex:2013-11-21
     */
    public static String getCurrentDateToWebYMD() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }


    /*
     *20150924->2015-09-24 00:00:00
	 */
    public static String dateToDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyyMMdd").parse(date)) + " 00:00:00";
    }

    /**
     * 获取当前时间的字符串
     *
     * @return String ex:2006-07-07
     */
    public static String getCurrentDate() {
        Timestamp d = new Timestamp(System.currentTimeMillis());
        return d.toString().substring(0, 10);
    }

    /**
     * 获取当前时间的字符串
     *
     * @return String ex:2006-07-07 22:10:10
     */
    public static String getCurrentDateTime() {
        return sdf_all.format(new Date());
    }


    /**
     * 字符串的日期格式的计算 yyyy-MM-dd
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {

        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf_yMdp.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf_yMdp.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Math.abs(Integer.parseInt(String.valueOf(between_days)));
    }


    /**
     * 字符串的日期格式的计算 yyyy-MM-dd
     */
    public static int daysBetween(String sdate, String bdate, int type) throws ParseException {
        String startDate = "";
        String endDate = "";
        startDate = sdf_yMdp.format(sdf_all.parse(sdate));
        endDate = sdf_yMdp.format(sdf_all.parse(bdate));
        return daysBetween(startDate, endDate);
    }


    /**
     * @param sdate 2006-07-07 22:10:10
     * @param bdate 2006-07-07 22:10:10
     * @return
     * @throws ParseException
     */
    public static int monthsBetween(String sdate, String bdate) throws ParseException {

        String str1 = sdf_yMp.format(sdf_all.parse(sdate));
        String str2 = sdf_yMp.format(sdf_all.parse(bdate));
        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        bef.setTime(sdf_yMp.parse(str1));
        aft.setTime(sdf_yMp.parse(str2));
        int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
        int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;

        return Math.abs(month + result);
    }


    /**
     * @param tempDate yyyy-MM-dd HH:mm:ss
     * @return 获取多少周
     */
    public static int getWhichWeek(String tempDate) throws ParseException {
        mCalendar.setTime(TimestampTool.sdf_all.parse(tempDate));
        return mCalendar.get(Calendar.WEEK_OF_YEAR);
    }


    /**
     * 获取周一周末的日期
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss
     * @return 【yyyy-MM-dd】
     */
    public static String[] getMondaySunday(String dateStr) throws ParseException {
        String[] range = new String[2];
        Calendar mCalendar = Calendar.getInstance();
        Date date = sdf_all.parse(dateStr);
        mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        mCalendar.setTime(date);
        mCalendar.add(Calendar.DATE, -mCalendar.get(Calendar.DAY_OF_WEEK) + 2);
        range[0] = sdf_yMdp.format(mCalendar.getTime());
        mCalendar.add(Calendar.DATE, 6);
        range[1] = sdf_yMdp.format(mCalendar.getTime());

        return range;
    }

    /**
     * @param someDate  某一天 yyyy-MM-dd HH:mm:ss
     * @param startDate 开始日期yyyy-MM-dd
     * @param endDate   截止日期
     * @return
     */
    public static boolean someDayBetween(String someDate, String startDate, String endDate) {

        boolean isIn = false;
        try {
            int temp = compareTwoDays(sdf_all.format(sdf_yMd.parse(startDate)), sdf_all.format(sdf_yMd.parse(endDate)));
            if (temp >= 0) {//开始的值大

                int temp1 = compareTwoDays(someDate, sdf_all.format(sdf_yMd.parse(endDate)));

                int temp2 = compareTwoDays(someDate, sdf_all.format(sdf_yMd.parse(startDate)));

                isIn = temp1 >= 0 && temp2 <= 0;
            } else {
                int temp1 = compareTwoDays(someDate, sdf_all.format(sdf_yMd.parse(startDate)));

                int temp2 = compareTwoDays(someDate, sdf_all.format(sdf_yMd.parse(endDate)));

                isIn = temp1 >= 0 && temp2 <= 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isIn;
    }

    /**
     * @param dateStr1 yyyy-MM-dd HH:mm:ss
     * @param dateStr2 yyyy-MM-dd HH:mm:ss
     * @return 比较两天时间的大小
     */
    public static int compareTwoDays(String dateStr1, String dateStr2) {
        return getSomedayTime(dateStr1) - getSomedayTime(dateStr2) > 0 ? 1 : getSomedayTime(dateStr1) - getSomedayTime(dateStr2) == 0 ? 0 : -1;
    }

    public static String ymdToMd(String createTaskDate) {
        try {
            return sdf_MD.format(TimestampTool.sdf_yMd.parse(createTaskDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return createTaskDate;
    }

    public static String allToHm(Date all) {
        return sdf_hm.format(all);
    }

    public static String allToMd(Date all) {
        return sdf_MD.format(all);
    }


    /**
     * 判断是否是周末
     *
     * @return
     */
    public static boolean isWeekend(Calendar cal) {
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week == 6 || week == 0) {//0代表周日，6代表周六
            return true;
        }
        return false;
    }


    /**
     * 判断当前天是不是今天
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static boolean isToday(String dateStr) {
        return dateStr != null && dateStr.contains(getCurrentDate());
    }


    /**
     * 判断当前日期是星期几
     *
     * @param pTime "yyyy-MM-dd 00:00:00" 修要判断的时间
     * @return dayForWeek 判断结果
     * @Exception 发生异常
     */
    public static int dayForWeek(String pTime) {
        int dayForWeek = 0;
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf_all.parse(pTime));
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                dayForWeek = 7;
            } else {
                dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayForWeek;
    }


    /**
     * get Calendar of given year
     *
     * @param year
     * @return
     */
    private static Calendar getCalendarFormYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.YEAR, year);
        return cal;
    }

    /**
     * get the end day of given week no of a year.
     *
     * @param year
     * @param weekNo 0,1:表示日期 2：表示第几周
     * @return
     */
    public static String[] getStartEndDayOfWeekNo(int year, int weekNo) {
        String[] temp = new String[3];
        Calendar cal = getCalendarFormYear(year);
        cal.set(Calendar.WEEK_OF_YEAR, weekNo);
        temp[0] = TimestampTool.sdf_yMdp.format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        temp[1] = TimestampTool.sdf_yMdp.format(cal.getTime());
        temp[2] = weekNo + "";
        return temp;
    }


}
