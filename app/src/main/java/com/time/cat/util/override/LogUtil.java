package com.time.cat.util.override;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.time.cat.TimeCatApp;


public class LogUtil {

    public static final String tag = "TimeCat";
    public static boolean isEshow = true;
    public static boolean isWshow = true;
    public static boolean isIshow = true;
    public static boolean isDshow = true;
    public static boolean isVshow = true;


    static {
        if (isApkDebugable(TimeCatApp.getInstance())) {
            isEshow = true;
            isWshow = true;
            isIshow = true;
            isDshow = true;
            isVshow = true;
        } else {
            isEshow = true;
            isWshow = false;
            isIshow = false;
            isDshow = false;
            isVshow = false;
        }
    }

    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE;
        } catch (Exception e) {

        }
        return false;
    }

    public static void d(String msg) {
        if (isDshow) Log.d(tag, msg);
    }

    public static void d(String tag, String msg) {
        d(tag + "," + msg);
    }

    public static void e(String msg) {
        if (isEshow) Log.e(tag, msg);
    }

    public static void e(String tag, String msg) {
        e(tag + "," + msg);
    }

    public static void i(String msg) {
        if (isIshow) Log.i(tag, msg);
    }

    public static void i(String tag, String msg) {
        i(tag + "," + msg);
    }

    public static void w(String msg) {
        if (isWshow) Log.w(tag, msg);
    }

    public static void w(String tag, String msg) {
        w(tag + "," + msg);
    }

    public static void v(String msg) {
        if (isVshow) Log.v(tag, msg);
    }

    public static void v(String tag, String msg) {
        v(tag + "," + msg);
    }

    /**
     * true:打开log  false:关闭所有的日志
     */
    public static boolean OPEN_LOG = true;

    /**
     * true : 打开debug 日志  false:关闭debug日志
     */
    public static boolean DEBUG = true;

    private String mClassName;
    private static LogUtil log;
    private static final String USER_NAME = "@tool@";

    private LogUtil(String name) {
        mClassName = name;
    }

    /**
     * Get The Current Function Name
     *
     * @return Name
     */
    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }
            return mClassName + "[ " + Thread.currentThread().getName() + ": "
                    + st.getFileName() + ":" + st.getLineNumber() + " "
                    + st.getMethodName() + " ]";
        }
        return null;
    }

    public static void i(Object str) {
        print(Log.INFO, str);
    }

    public static void d(Object str) {
        print(Log.DEBUG, str);
    }

    public static void v(Object str) {
        print(Log.VERBOSE, str);
    }

    public static void w(Object str) {
        print(Log.WARN, str);
    }

    public static void e(Object str) {
        print(Log.ERROR, str);
    }

    /**
     * 用于区分不同接口数据 打印传入参数
     *
     * @param index
     * @param str
     */

    private static void print(int index, Object str) {
        if (!OPEN_LOG) {
            return;
        }
        if (log == null) {
            log = new LogUtil(USER_NAME);
        }
        String name = log.getFunctionName();
        if (name != null) {
            str = name + " - " + str;
        }

        // Close the debug log When DEBUG is false
        if (!DEBUG) {
            if (index <= Log.DEBUG) {
                return;
            }
        }
        switch (index) {
            case Log.VERBOSE:
                Log.v(tag, str.toString());
                break;
            case Log.DEBUG:
                Log.d(tag, str.toString());
                break;
            case Log.INFO:
                Log.i(tag, str.toString());
                break;
            case Log.WARN:
                Log.w(tag, str.toString());
                break;
            case Log.ERROR:
                Log.e(tag, str.toString());
                break;
            default:
                break;
        }
    }
}
