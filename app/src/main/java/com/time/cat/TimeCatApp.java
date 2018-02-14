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
import com.time.cat.database.UserDao;
import com.time.cat.mvp.model.DBmodel.DBUser;
import com.time.cat.test.DefaultDataGenerator;
import com.time.cat.util.KeepAliveWatcher;
import com.time.cat.util.onestep.AppManager;

import de.greenrobot.event.EventBus;

/**
 * @author dlink
 * @date 2018/2/3
 * @discription app
 */
public class TimeCatApp extends Application implements ThemeUtils.switchColor {
    public static final String PHARMACY_MODE_ENABLED = "PHARMACY_MODE_ENABLED";

    // PREFERENCES
    public static final String PREFERENCES_NAME = "CalendulaPreferences";
    public static final String PREF_ALARM_SETTLED = "alarm_settled";

    // INTENTS
    public static final String INTENT_EXTRA_ACTION = "action";
    public static final String INTENT_EXTRA_ROUTINE_ID = "routine_id";
    public static final String INTENT_EXTRA_SCHEDULE_ID = "schedule_id";
    public static final String INTENT_EXTRA_SCHEDULE_TIME = "schedule_time";
    public static final String INTENT_EXTRA_DELAY_ROUTINE_ID = "delay_routine_id";
    public static final String INTENT_EXTRA_DELAY_SCHEDULE_ID = "delay_schedule_id";
    // ACTIONS
    public static final int ACTION_ROUTINE_TIME = 1;
    public static final int ACTION_DAILY_ALARM = 2;
    public static final int ACTION_ROUTINE_DELAYED_TIME = 3;
    public static final int ACTION_DELAY_ROUTINE = 4;
    public static final int ACTION_CANCEL_ROUTINE = 5;
    public static final int ACTION_HOURLY_SCHEDULE_TIME = 6;
    public static final int ACTION_HOURLY_SCHEDULE_DELAYED_TIME = 7;
    public static final int ACTION_DELAY_HOURLY_SCHEDULE = 8;
    public static final int ACTION_CANCEL_HOURLY_SCHEDULE = 9;
    public static final int ACTION_CHECK_PICKUPS_ALARM = 10;

    private static TimeCatApp instance;
    private static EventBus eventBus = EventBus.getDefault();
    SharedPreferences prefs;

    public static TimeCatApp getInstance() {
        return instance;
    }

    public static boolean isPharmaModeEnabled(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean(PHARMACY_MODE_ENABLED, false);
    }

    public static EventBus eventBus() {
        return eventBus;
    }

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

    public void initializeDatabase() {
        DB.init(this);
        try {
            if (DB.users().countOf() == 1) {
                DBUser p = DB.users().getDefault();
                prefs.edit().putLong(UserDao.PREFERENCE_ACTIVE_USER, p.id()).commit();
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
            case ThemeManager.CARD_FIRE:
                return "red";
            case ThemeManager.CARD_WHITE:
                return "white";
            case ThemeManager.CARD_BLACK:
                return "black";
            case ThemeManager.CARD_GREY:
                return "gray";
            case ThemeManager.CARD_MAGENTA:
                return "magenta";

            case ThemeManager.CARD_THEME_0:
                return "theme_0";
            case ThemeManager.CARD_THEME_1:
                return "theme_1";
            case ThemeManager.CARD_THEME_2:
                return "theme_2";
            case ThemeManager.CARD_THEME_3:
                return "theme_3";
            case ThemeManager.CARD_THEME_4:
                return "theme_4";
            case ThemeManager.CARD_THEME_5:
                return "theme_5";
            case ThemeManager.CARD_THEME_6:
                return "theme_6";
            case ThemeManager.CARD_THEME_7:
                return "theme_7";
            case ThemeManager.CARD_THEME_8:
                return "theme_8";
            case ThemeManager.CARD_THEME_9:
                return "theme_9";
            case ThemeManager.CARD_THEME_10:
                return "theme_10";
            case ThemeManager.CARD_THEME_11:
                return "theme_11";
            case ThemeManager.CARD_THEME_12:
                return "theme_12";
            case ThemeManager.CARD_THEME_13:
                return "theme_13";
            case ThemeManager.CARD_THEME_14:
                return "theme_14";
            case ThemeManager.CARD_THEME_15:
                return "theme_15";
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

    private @ColorRes
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

    private @ColorRes
    int getThemeColor(Context context, int color, String theme) {
        switch (color) {
            case 0xd20000:
                return context.getResources().getIdentifier(theme, "color", getPackageName());
        }
        return -1;
    }
    //</theme>--------------------------------------------------------------------------------------
}
