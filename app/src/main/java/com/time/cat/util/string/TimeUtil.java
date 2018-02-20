package com.time.cat.util.string;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author dlink
 * @date 2018/2/1
 * @discription
 */
public class TimeUtil {
    /**
     * @param mss 要转换的毫秒数
     *
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     */
    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return days + " days " + hours + " hours " + minutes + " minutes " + seconds + " seconds ";
    }

    /**
     * @param begin 时间段的开始
     * @param end   时间段的结束
     *
     * @return 输入的两个Date类型数据之间的时间间格用* days * hours * minutes * seconds的格式展示
     */
    public static String formatDuring(Date begin, Date end) {
        return formatDuring(end.getTime() - begin.getTime());
    }

    /**
     * GMT时间格式化,返回Date对象
     *
     * @param gmt_datetime "2018-02-19T16:11:11Z"
     * @return Date对象,Date的时区仍为GMT
     * Mon Feb 19 16:11:11 UTC 2018
     */
    public static Date formatGMTDateStr(String gmt_datetime) {
        //Time in GMT
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return dateFormatGmt.parse(gmt_datetime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 格式化为GMT时间字符串
     *
     * @param gmt_datetime "2018-02-19T16:11:11Z"
     * @return Date对象,Date的时区仍为GMT
     * Mon Feb 19 16:11:11 UTC 2018
     */
    public static String formatGMTDate(Date gmt_datetime) {
        //Time in GMT
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormatGmt.format(gmt_datetime);
    }

    /**
     * 本地时间格式化
     * @param s "2018-02-19T16:11:11Z"
     * @return Date对象,Date的时区仍为GMT
     */
    public static Date formatLocalDateStr(String s) {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

//Local time zone
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

//Time in GMT
        try {
            return dateFormatLocal.parse( dateFormatGmt.format(new Date()) );
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


}
