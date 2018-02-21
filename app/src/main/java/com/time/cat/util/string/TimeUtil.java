package com.time.cat.util.string;

import android.content.Context;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

    public static long getDuringDay(long mss) {
        return mss / (1000 * 60 * 60 * 24);
    }

    public static boolean isDateEarlier(Date dateEarly, Date date) {
        if (dateEarly.getYear() <= date.getYear()
                && dateEarly.getMonth() <= date.getMonth()
                &&  dateEarly.getDay() <= date.getDay()) {
            return true;
        }
        return false;
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
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
//        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return dateFormatGmt.parse(gmt_datetime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatMonthDay(Date d) {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("MM月dd日");
//        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormatGmt.format(d);
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
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
//        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormatGmt.format(gmt_datetime);
    }

    /**
     * 本地时间格式化
     * @param s "2018-02-19T16:11:11Z"
     * @return Date对象,Date的时区仍为GMT
     */
    public static Date formatLocalDateStr(String s) {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

//Local time zone
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

//Time in GMT
        try {
            return dateFormatLocal.parse( dateFormatGmt.format(new Date()) );
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    public static String getCurrentUTCTimestamp() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a Z", Locale.US);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatGmt.format(new Date());
    }

    public static String getUTCTimestamp(Date date) {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a Z", Locale.US);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatGmt.format(date);
    }

    public static String getTimeAgo(final Context context, long time) {
        // TODO: use DateUtils methods instead
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;

        // TODO: localize the time information
        if (diff < MINUTE) {
            return "just now";
        } else if (diff < 2 * MINUTE) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE) {
            return diff / MINUTE + " minutes ago";
        } else if (diff < 90 * MINUTE) {
            return "an hour ago";
        } else if (diff < 24 * HOUR) {
            return diff / HOUR + " hours ago";
        } else if (diff < 48 * HOUR) {
            return "yesterday";
        } else {
            return diff / DAY + " days ago";
        }
    }

    private static final SimpleDateFormat[] ACCEPTED_TIMESTAMP_FORMATS = {
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US),
            new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a Z", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z", Locale.US)
    };

    private static final SimpleDateFormat VALID_IFMODIFIEDSINCE_FORMAT =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

    public static Date parseTimestamp(String timestamp) {
        for (SimpleDateFormat format : ACCEPTED_TIMESTAMP_FORMATS) {
//            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            format.setTimeZone(TimeZone.getDefault());
            try {
                return format.parse(timestamp);
            } catch (ParseException ex) {
                continue;
            }
        }

        // All attempts to parse have failed
        return null;
    }

    public static boolean isValidFormatForIfModifiedSinceHeader(String timestamp) {
        try {
            return VALID_IFMODIFIEDSINCE_FORMAT.parse(timestamp)!=null;
        } catch (Exception ex) {
            return false;
        }
    }

    public static long timestampToMillis(String timestamp, long defaultValue) {
        if (TextUtils.isEmpty(timestamp)) {
            return defaultValue;
        }
        Date d = parseTimestamp(timestamp);
        return d == null ? defaultValue : d.getTime();
    }

//    public static String formatShortDate(Context context, Date date) {
//        StringBuilder sb = new StringBuilder();
//        Formatter formatter = new Formatter(sb);
//        return DateUtils.formatDateRange(context, formatter, date.getTime(), date.getTime(),
//                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_YEAR,
//                TimeZone.getDefault().getID()).toString();
//    }
//
//    public static String formatShortTime(Context context, Date time) {
//        DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
//        TimeZone tz = TimeZone.getDefault();
//        if (tz != null) {
//            format.setTimeZone(tz);
//        }
//        return format.format(time);
//    }
//
//    /**
//     * Returns "Today", "Tomorrow", "Yesterday", or a short date format.
//     */
//    public static String formatHumanFriendlyShortDate(final Context context, long timestamp) {
//        long localTimestamp, localTime;
//        long now = System.currentTimeMillis();
//
//        TimeZone tz = TimeZone.getDefault();
//        localTimestamp = timestamp + tz.getOffset(timestamp);
//        localTime = now + tz.getOffset(now);
//
//        long dayOrd = localTimestamp / 86400000L;
//        long nowOrd = localTime / 86400000L;
//
//        if (dayOrd == nowOrd) {
//            return context.getString(R.string.day_title_today);
//        } else if (dayOrd == nowOrd - 1) {
//            return context.getString(R.string.day_title_yesterday);
//        } else if (dayOrd == nowOrd + 1) {
//            return context.getString(R.string.day_title_tomorrow);
//        } else {
//            return formatShortDate(context, new Date(timestamp));
//        }
//    }

}
