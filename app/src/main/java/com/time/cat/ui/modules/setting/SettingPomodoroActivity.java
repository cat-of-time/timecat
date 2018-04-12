package com.time.cat.ui.modules.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.util.SeekBarPreference;

/**
 * 使用 PreferenceFragment 然后 addPreferencesFromResource(xml) 会更简洁
 */
public class SettingPomodoroActivity extends AppCompatActivity {

    private Toast mToast;

    public static Intent newIntent(Context context) {
        return new Intent(context, SettingActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_pomodoro);

        setToolBar();

        Resources res = getResources();

        // 工作时长
        (new SeekBarPreference(this))
                .setSeekBar((SeekBar)findViewById(R.id.pref_key_work_length))
                .setSeekBarValue((TextView)findViewById(R.id.pref_key_work_length_value))
                .setMax(res.getInteger(R.integer.pref_work_length_max))
                .setMin(res.getInteger(R.integer.pref_work_length_min))
                .setUnit(R.string.pref_title_time_value)
                .setProgress(PreferenceManager.getDefaultSharedPreferences(this)
                        .getInt("pref_key_work_length", TimeCatApp.DEFAULT_WORK_LENGTH))
                .build();
        // 短时休息
        (new SeekBarPreference(this))
                .setSeekBar((SeekBar)findViewById(R.id.pref_key_short_break))
                .setSeekBarValue((TextView)findViewById(R.id.pref_key_short_break_value))
                .setMax(res.getInteger(R.integer.pref_short_break_max))
                .setMin(res.getInteger(R.integer.pref_short_break_min))
                .setUnit(R.string.pref_title_time_value)
                .setProgress(PreferenceManager.getDefaultSharedPreferences(this)
                        .getInt("pref_key_short_break", TimeCatApp.DEFAULT_SHORT_BREAK))
                .build();
        // 长时休息
        (new SeekBarPreference(this))
                .setSeekBar((SeekBar)findViewById(R.id.pref_key_long_break))
                .setSeekBarValue((TextView)findViewById(R.id.pref_key_long_break_value))
                .setMax(res.getInteger(R.integer.pref_long_break_max))
                .setMin(res.getInteger(R.integer.pref_long_break_min))
                .setUnit(R.string.pref_title_time_value)
                .setProgress(PreferenceManager.getDefaultSharedPreferences(this)
                        .getInt("pref_key_long_break", TimeCatApp.DEFAULT_LONG_BREAK))
                .build();
        // 长时休息间隔
        (new SeekBarPreference(this))
                .setSeekBar((SeekBar)findViewById(R.id.pref_key_long_break_frequency))
                .setSeekBarValue((TextView)findViewById(R.id.pref_key_long_break_frequency_value))
                .setMax(res.getInteger(R.integer.pref_long_break_frequency_max))
                .setMin(res.getInteger(R.integer.pref_long_break_frequency_min))
                .setUnit(R.string.pref_title_frequency_value)
                .setProgress(PreferenceManager.getDefaultSharedPreferences(this)
                        .getInt("pref_key_long_break_frequency",
                                TimeCatApp.DEFAULT_LONG_BREAK_FREQUENCY))
                .build();


        SwitchCompat notificationSound = findViewById(R.id.pref_key_notification_sound);

        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_key_use_notification", true)) {
            notificationSound.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("pref_key_notification_sound", true));
        } else {
            notificationSound.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("pref_key_notification_sound_checked", false));
            notificationSound.setEnabled(false);
        }

        notificationSound.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferences.Editor editor = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putBoolean("pref_key_notification_sound", isChecked);

            try {
                editor.apply();
            } catch (AbstractMethodError unused) {
                editor.commit();
            }
        });

        SwitchCompat notificationVibrate = findViewById(R.id.pref_key_notification_vibrate);
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_key_use_notification", true)) {
            notificationVibrate.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("pref_key_notification_vibrate", true));
        } else {
            notificationVibrate.setEnabled(false);
            notificationVibrate.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("pref_key_notification_vibrate_checked", false));
        }

        notificationVibrate.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferences.Editor editor = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putBoolean("pref_key_notification_vibrate", isChecked);

            try {
                editor.apply();
            } catch (AbstractMethodError unused) {
                editor.commit();
            }
        });


        SwitchCompat screenOn = (SwitchCompat)findViewById(R.id.pref_key_screen_on);
        screenOn.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_key_screen_on", false));
        screenOn.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferences.Editor editor = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putBoolean("pref_key_screen_on", isChecked);

            try {
                editor.apply();
            } catch (AbstractMethodError unused) {
                editor.commit();
            }
        });

    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
