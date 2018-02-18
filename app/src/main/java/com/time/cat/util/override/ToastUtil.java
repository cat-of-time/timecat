package com.time.cat.util.override;

import android.widget.Toast;

import com.time.cat.TimeCatApp;


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
