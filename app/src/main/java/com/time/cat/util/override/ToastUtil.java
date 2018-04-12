package com.time.cat.util.override;

import android.support.annotation.StringRes;
import android.widget.Toast;

import com.time.cat.TimeCatApp;

import es.dmoral.toasty.Toasty;

public class ToastUtil {
    public static void show(String msg) {
        Toast.makeText(TimeCatApp.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void show(int rid) {
        Toast.makeText(TimeCatApp.getInstance(), rid, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(int rid) {
        Toast.makeText(TimeCatApp.getInstance(), rid, Toast.LENGTH_LONG).show();
    }

    public static void i(String message) {
        Toasty.info(TimeCatApp.getInstance(), message, Toast.LENGTH_SHORT).show();
    }

    public static void e(String message) {
        Toasty.error(TimeCatApp.getInstance(), message, Toast.LENGTH_SHORT).show();
    }

    public static void w(String message) {
        Toasty.warning(TimeCatApp.getInstance(), message, Toast.LENGTH_SHORT).show();
    }

    public static void ok(String message) {
        Toasty.success(TimeCatApp.getInstance(), message, Toast.LENGTH_SHORT).show();
    }

    public static void i_long(String message) {
        Toasty.info(TimeCatApp.getInstance(), message, Toast.LENGTH_LONG).show();
        
    }

    public static void e_long(String message) {
        Toasty.error(TimeCatApp.getInstance(), message, Toast.LENGTH_LONG).show();
    }

    public static void w_long(String message) {
        Toasty.warning(TimeCatApp.getInstance(), message, Toast.LENGTH_LONG).show();
    }

    public static void ok_long(String message) {
        Toasty.success(TimeCatApp.getInstance(), message, Toast.LENGTH_LONG).show();
    }

    public static void i(@StringRes int message) {
        Toasty.info(TimeCatApp.getInstance(), TimeCatApp.getInstance().getResources().getString(message), Toast.LENGTH_SHORT).show();
    }

    public static void e(@StringRes int message) {
        Toasty.error(TimeCatApp.getInstance(), TimeCatApp.getInstance().getResources().getString(message), Toast.LENGTH_SHORT).show();
    }

    public static void w(@StringRes int message) {
        Toasty.warning(TimeCatApp.getInstance(), TimeCatApp.getInstance().getResources().getString(message), Toast.LENGTH_SHORT).show();
    }

    public static void ok(@StringRes int message) {
        Toasty.success(TimeCatApp.getInstance(), TimeCatApp.getInstance().getResources().getString(message), Toast.LENGTH_SHORT).show();
    }

    public static void i_long(@StringRes int message) {
        Toasty.info(TimeCatApp.getInstance(), TimeCatApp.getInstance().getResources().getString(message), Toast.LENGTH_LONG).show();

    }

    public static void e_long(@StringRes int message) {
        Toasty.error(TimeCatApp.getInstance(), TimeCatApp.getInstance().getResources().getString(message), Toast.LENGTH_LONG).show();
    }

    public static void w_long(@StringRes int message) {
        Toasty.warning(TimeCatApp.getInstance(), TimeCatApp.getInstance().getResources().getString(message), Toast.LENGTH_LONG).show();
    }

    public static void ok_long(@StringRes int message) {
        Toasty.success(TimeCatApp.getInstance(), TimeCatApp.getInstance().getResources().getString(message), Toast.LENGTH_LONG).show();
    }
}
