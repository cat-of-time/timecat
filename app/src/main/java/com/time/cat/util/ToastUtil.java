package com.time.cat.util;

import android.widget.Toast;

import com.time.cat.TimeCatApp;


/**
 * Created by l4656_000 on 2015/12/27.
 */
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
}
