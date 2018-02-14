package com.time.cat.util;

import android.view.View;

import com.qihoo.updatesdk.lib.UpdateHelper;
import com.time.cat.R;
import com.time.cat.TimeCatApp;

/**
 * Created by penglu on 2016/3/3.
 */
public class UpdateUtil {

    static {
        UpdateHelper.getInstance().init(TimeCatApp.getInstance(), TimeCatApp.getInstance().getResources().getColor(R.color.primary));
    }

    public static void autoCheckUpdate() {
        UpdateHelper.getInstance().autoUpdate(TimeCatApp.getInstance().getPackageName());
    }

    public static void UserCheckUpdate(final View view) {
        if (!NetWorkUtil.isWifi(TimeCatApp.getInstance())) {
            SnackBarUtil.show(view, "在非wifi下升级，可能会消耗您少量流量！");
        }
        UpdateHelper.getInstance().manualUpdate(TimeCatApp.getInstance().getPackageName());
    }
}
