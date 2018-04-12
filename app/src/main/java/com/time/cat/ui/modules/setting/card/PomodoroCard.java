package com.time.cat.ui.modules.setting.card;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.ui.base.baseCard.AbsCard;
import com.time.cat.ui.modules.setting.SettingPomodoroActivity;
import com.time.cat.ui.service.TickService;

public class PomodoroCard extends AbsCard {

    public PomodoroCard(Context context) {
        super(context);
        initView(context);
    }

    public PomodoroCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PomodoroCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    protected void initView(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.card_pomodoro, this);

        findViewById(R.id.setting_pomodoro).setOnClickListener(v -> {
            Intent intent2About = new Intent(mContext, SettingPomodoroActivity.class);
            mContext.startActivity(intent2About);
        });

        SwitchCompat pomodoroMode = findViewById(R.id.pref_key_pomodoro_mode);
        pomodoroMode.setChecked(PreferenceManager.getDefaultSharedPreferences(TimeCatApp.getInstance())
                .getBoolean("pref_key_pomodoro_mode", true));
        pomodoroMode.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferences.Editor editor = PreferenceManager
                    .getDefaultSharedPreferences(TimeCatApp.getInstance()).edit();
            editor.putBoolean("pref_key_pomodoro_mode", isChecked);

            if (isChecked) {

                Intent i = TickService.newIntent(TimeCatApp.getInstance());
                i.setAction(TickService.ACTION_POMODORO_MODE_ON);
                TimeCatApp.getInstance().startService(i);
            }

            try {
                editor.apply();
            } catch (AbstractMethodError unused) {
                // The app injected its own pre-Gingerbread
                // SharedPreferences.Editor implementation without
                // an apply method.
                editor.commit();
            }
        });

        SwitchCompat infinityMode = findViewById(R.id.pref_key_infinity_mode);
        infinityMode.setChecked(PreferenceManager.getDefaultSharedPreferences(TimeCatApp.getInstance())
                .getBoolean("pref_key_infinity_mode", false));
        infinityMode.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferences.Editor editor = PreferenceManager
                    .getDefaultSharedPreferences(TimeCatApp.getInstance()).edit();
            editor.putBoolean("pref_key_infinity_mode", isChecked);

            try {
                editor.apply();
            } catch (AbstractMethodError unused) {
                editor.commit();
            }
        });

        SwitchCompat tickSound = findViewById(R.id.pref_key_tick_sound);
        tickSound.setChecked(PreferenceManager.getDefaultSharedPreferences(TimeCatApp.getInstance())
                .getBoolean("pref_key_tick_sound", true));
        tickSound.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferences.Editor editor = PreferenceManager
                    .getDefaultSharedPreferences(TimeCatApp.getInstance()).edit();
            editor.putBoolean("pref_key_tick_sound", isChecked);

            try {
                editor.apply();
            } catch (AbstractMethodError unused) {
                editor.commit();
            }

            Intent i = TickService.newIntent(TimeCatApp.getInstance());

            if (isChecked) {
                i.setAction(TickService.ACTION_TICK_SOUND_ON);
            } else {
                i.setAction(TickService.ACTION_TICK_SOUND_OFF);
            }

            TimeCatApp.getInstance().startService(i);
        });

        SwitchCompat useNotification = findViewById(R.id.pref_key_use_notification);
        useNotification.setChecked(PreferenceManager.getDefaultSharedPreferences(TimeCatApp.getInstance())
                .getBoolean("pref_key_use_notification", true));
        useNotification.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferences.Editor editor = PreferenceManager
                    .getDefaultSharedPreferences(TimeCatApp.getInstance()).edit();
            editor.putBoolean("pref_key_use_notification", isChecked);

            SwitchCompat notificationVibrate =
                    (SwitchCompat)findViewById(R.id.pref_key_notification_vibrate);
            SwitchCompat notificationSound =
                    (SwitchCompat)findViewById(R.id.pref_key_notification_sound);

            // 保存当前的状态
            editor.putBoolean("pref_key_notification_sound_checked",
                    notificationSound.isChecked());
            editor.putBoolean("pref_key_notification_vibrate_checked",
                    notificationVibrate.isChecked());

            if (isChecked) {
                editor.putBoolean("pref_key_notification_sound",
                        notificationSound.isChecked());
                editor.putBoolean("pref_key_notification_vibrate",
                        notificationVibrate.isChecked());
            } else {
                editor.putBoolean("pref_key_notification_sound", false);
                editor.putBoolean("pref_key_notification_vibrate", false);
            }

            try {
                editor.apply();
            } catch (AbstractMethodError unused) {
                editor.commit();
            }

            notificationSound.setEnabled(isChecked);
            notificationVibrate.setEnabled(isChecked);
        });

    }

}
