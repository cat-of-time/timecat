package com.time.cat;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;

import com.time.cat.data.database.DB;
import com.time.cat.data.database.UserDao;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.ui.service.ListenClipboardService;
import com.time.cat.ui.service.TimeCatMonitorService;
import com.time.cat.ui.widgets.theme.ThemeManager;
import com.time.cat.ui.widgets.theme.utils.ThemeUtils;
import com.time.cat.util.KeepAliveWatcher;
import com.time.cat.util.onestep.AppManager;
import com.timecat.commonjar.contentProvider.SPHelper;

import java.util.concurrent.TimeUnit;

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

    public static final int DEFAULT_WORK_LENGTH = 25;
    public static final int DEFAULT_SHORT_BREAK = 5;
    public static final int DEFAULT_LONG_BREAK  = 20;
    public static final int DEFAULT_LONG_BREAK_FREQUENCY = 4; // 默认 4 次开始长休息

    // 场景
    public static final int SCENE_WORK = 0;
    public static final int SCENE_SHORT_BREAK = 1;
    public static final int SCENE_LONG_BREAK = 2;

    // 当前状态
    public static final int STATE_WAIT = 0;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_FINISH = 3;

    private long mStopTimeInFuture;
    private long mMillisInTotal;
    private long mMillisUntilFinished;

    private int mTimes;
    private int mState;

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
        SPHelper.init(this);
        ThemeUtils.setSwitchColor(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // initialize SQLite engine
        initializeDatabase();

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
        mState = STATE_WAIT;
    }

    public void initializeDatabase() {
        DB.init(this);
        try {
            if (DB.users().countOf() == 1) {
                DBUser p = DB.users().getDefault();
                SPHelper.save(UserDao.PREFERENCE_ACTIVE_USER, p.id());
                prefs.edit().putLong(UserDao.PREFERENCE_ACTIVE_USER, p.id()).apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //<>
    public void reload() {
        switch(mState) {
            case STATE_WAIT:
            case STATE_FINISH:
                mMillisInTotal = TimeUnit.MINUTES.toMillis(getMinutesInTotal());
                mMillisUntilFinished = mMillisInTotal;
                break;
            case STATE_RUNNING:
                if (SystemClock.elapsedRealtime() > mStopTimeInFuture) {
                    finish();
                }
                break;
        }
    }

    public void start() {
        setState(STATE_RUNNING);
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInTotal;
    }

    public void pause() {
        setState(STATE_PAUSE);
        mMillisUntilFinished = mStopTimeInFuture - SystemClock.elapsedRealtime();
    }

    public void resume() {
        setState(STATE_RUNNING);
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisUntilFinished;
    }

    public void stop() {
        setState(STATE_WAIT);
        reload();
    }

    public void skip() {
        setState(STATE_WAIT);
        setTimes();
        reload();
    }

    public void finish() {
        setState(STATE_FINISH);
        setTimes();
        reload();
    }

    public void exit() {
        setState(STATE_WAIT);
        mTimes = 0;
        reload();
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

    private void setTimes() {
        mTimes++; // 注意这里不能在 activity 中使用, 如果睡眠中就不能保证会运行
    }

    public int getScene() {
        int frequency = getSharedPreferences()
                .getInt("pref_key_long_break_frequency", DEFAULT_LONG_BREAK_FREQUENCY);
        frequency = frequency * 2; // 工作/短休息/工作/短休息/工作/短休息/工作/长休息

        if (mTimes % 2  == 1) { // 偶数：工作, 奇数：休息

            if ((mTimes + 1 ) % frequency == 0) { // 长休息
                return SCENE_LONG_BREAK;
            }

            return SCENE_SHORT_BREAK;
        }

        return SCENE_WORK;
    }

    public int getMinutesInTotal() {
        int minutes = 0;

        switch (getScene()) {
            case SCENE_WORK:
                minutes = getSharedPreferences()
                        .getInt("pref_key_work_length", DEFAULT_WORK_LENGTH);
                break;
            case SCENE_SHORT_BREAK:
                minutes = getSharedPreferences()
                        .getInt("pref_key_short_break", DEFAULT_SHORT_BREAK);
                break;
            case SCENE_LONG_BREAK:
                minutes = getSharedPreferences()
                        .getInt("pref_key_long_break", DEFAULT_LONG_BREAK);
                break;
        }

        return minutes;
    }

    public long getMillisInTotal() {
        return mMillisInTotal;
    }

    public void setMillisUntilFinished(long millisUntilFinished) {
        mMillisUntilFinished = millisUntilFinished;
    }

    public long getMillisUntilFinished() {
        if (mState == STATE_RUNNING) {
            return mStopTimeInFuture - SystemClock.elapsedRealtime();
        }

        return mMillisUntilFinished;
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }
    //

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
