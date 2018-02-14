package com.time.cat.util;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.time.cat.TimeCatApp;


/**
 * Created by l4656_000 on 2015/12/10.
 */
public class SnackBarUtil {
    public static void show(View view, String str) {
        try {
            Snackbar.make(view, str, Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void show(View view, int str) {
        show(view, TimeCatApp.getInstance().getString(str));
    }

    public static void show(View view, String str, String cancel, final View.OnClickListener listener) {
        try {
            Snackbar.make(view, str, Snackbar.LENGTH_LONG).setAction(cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onClick(v);
                }
            }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void show(View view, int str, String cancel, final View.OnClickListener listener) {
        show(view, TimeCatApp.getInstance().getString(str), cancel, listener);
    }

    public static void show(View view, String str, int cancel, final View.OnClickListener listener) {
        show(view, str, TimeCatApp.getInstance().getString(cancel), listener);
    }

    public static void show(View view, int str, int cancel, final View.OnClickListener listener) {
        show(view, TimeCatApp.getInstance().getString(str), TimeCatApp.getInstance().getString(cancel), listener);
    }


}
