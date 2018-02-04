package com.time.cat;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.os.MessageQueue;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;

import com.shang.commonjar.contentProvider.Global;
import com.time.cat.ThemeSystem.manager.ThemeManager;
import com.time.cat.ThemeSystem.utils.ThemeUtils;
import com.time.cat.component.service.ListenClipboardService;
import com.time.cat.component.service.TimeCatMonitorService;
import com.time.cat.database.DB;
import com.time.cat.database.PatientDao;
import com.time.cat.mvp.model.Patient;
import com.time.cat.test.DefaultDataGenerator;
import com.time.cat.util.KeepAliveWatcher;
import com.time.cat.util.onestep.AppManager;

import de.greenrobot.event.EventBus;

/**
 * @author dlink
 * @date 2018/2/3
 * @discription app
 */
public class TimeCatApp extends Application implements ThemeUtils.switchColor{
    private static TimeCatApp instance;
    public static final String PHARMACY_MODE_ENABLED = "PHARMACY_MODE_ENABLED";
    private static EventBus eventBus = EventBus.getDefault();
    public static TimeCatApp getInstance() {
        return instance;
    }
    SharedPreferences prefs;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // initialize SQLite engine
        initializeDatabase();

        if (!prefs.getBoolean("DEFAULT_DATA_INSERTED", false)) {
            DefaultDataGenerator.fillDBWithDummyData(getApplicationContext());
            prefs.edit().putBoolean("DEFAULT_DATA_INSERTED", true).commit();
        }

        ThemeUtils.setSwitchColor(this);
        Global.init(this);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {

                KeepAliveWatcher.keepAlive(TimeCatApp.this);
                startService(new Intent(TimeCatApp.this, ListenClipboardService.class));
                startService(new Intent(TimeCatApp.this, TimeCatMonitorService.class));
                return false;
            }
        });
        AppManager.getInstance(this);
    }

    public static boolean isPharmaModeEnabled(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean(PHARMACY_MODE_ENABLED, false);
    }

    public static EventBus eventBus() {
        return eventBus;
    }

    public void initializeDatabase() {
        DB.init(this);
        try{
            if(DB.patients().countOf() == 1) {
                Patient p = DB.patients().getDefault();
                prefs.edit().putLong(PatientDao.PREFERENCE_ACTIVE_PATIENT, p.id()).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //<theme>---------------------------------------------------------------------------------------
    private String getTheme(Context context) {
        switch (ThemeManager.getTheme(context)) {
            case ThemeManager.CARD_STORM:
                return "blue";
            case ThemeManager.CARD_HOPE:
                return "purple";
            case ThemeManager.CARD_WOOD:
                return "green";
            case ThemeManager.CARD_LIGHT:
                return "green_light";
            case ThemeManager.CARD_THUNDER:
                return "yellow";
            case ThemeManager.CARD_SAND:
                return "orange";
            case ThemeManager.CARD_FIREY:
                return "red";
            case ThemeManager.CARD_WHITE:
                return "white";
            case ThemeManager.CARD_BLACK:
                return "black";
            case ThemeManager.CARD_GREY:
                return "gray";
            case ThemeManager.CARD_TRANSPARENT:
                return "transparent";
        }

        return null;
    }

    @Override
    public int replaceColorById(Context context, @ColorRes int colorId) {
        if (ThemeManager.isDefaultTheme(context)) {
            return context.getResources().getColor(colorId);
        }
        String theme = getTheme(context);
        if (theme != null) {
            colorId = getThemeColorId(context, colorId, theme);
        }
        return context.getResources().getColor(colorId);
    }

    @Override
    public int replaceColor(Context context, @ColorInt int originColor) {
        if (ThemeManager.isDefaultTheme(context)) {
            return originColor;
        }
        String theme = getTheme(context);
        int colorId = -1;
        if (theme != null) {
            colorId = getThemeColor(context, originColor, theme);
        }
        return colorId != -1 ? getResources().getColor(colorId) : originColor;
    }

    private
    @ColorRes
    int getThemeColorId(Context context, int colorId, String theme) {
        switch (colorId) {
            case R.color.theme_color_primary:
                return context.getResources().getIdentifier(theme, "color", getPackageName());
            case R.color.theme_color_primary_dark:
                return context.getResources().getIdentifier(theme + "_dark", "color", getPackageName());
            case R.color.playbarProgressColor:
                return context.getResources().getIdentifier(theme + "_trans", "color", getPackageName());
        }
        return colorId;
    }

    private
    @ColorRes
    int getThemeColor(Context context, int color, String theme) {
        switch (color) {
            case 0xd20000:
                return context.getResources().getIdentifier(theme, "color", getPackageName());
        }
        return -1;
    }
    //</theme>--------------------------------------------------------------------------------------
}
