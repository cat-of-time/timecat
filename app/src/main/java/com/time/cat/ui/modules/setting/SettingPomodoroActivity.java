package com.icodechef.android.tick.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.icodechef.android.tick.R;
import com.icodechef.android.tick.TickApplication;
import com.icodechef.android.tick.TickService;
import com.icodechef.android.tick.util.NetWorkUtil;
import com.icodechef.android.tick.util.PackageUtil;
import com.icodechef.android.tick.util.SeekBarPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 使用 PreferenceFragment 然后 addPreferencesFromResource(xml) 会更简洁
 */
public class SettingActivity extends AppCompatActivity {

    private Toast mToast;

    public static Intent newIntent(Context context) {
        return new Intent(context, SettingActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

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
                        .getInt("pref_key_work_length", TickApplication.DEFAULT_WORK_LENGTH))
                .build();
        // 短时休息
        (new SeekBarPreference(this))
                .setSeekBar((SeekBar)findViewById(R.id.pref_key_short_break))
                .setSeekBarValue((TextView)findViewById(R.id.pref_key_short_break_value))
                .setMax(res.getInteger(R.integer.pref_short_break_max))
                .setMin(res.getInteger(R.integer.pref_short_break_min))
                .setUnit(R.string.pref_title_time_value)
                .setProgress(PreferenceManager.getDefaultSharedPreferences(this)
                        .getInt("pref_key_short_break", TickApplication.DEFAULT_SHORT_BREAK))
                .build();
        // 长时休息
        (new SeekBarPreference(this))
                .setSeekBar((SeekBar)findViewById(R.id.pref_key_long_break))
                .setSeekBarValue((TextView)findViewById(R.id.pref_key_long_break_value))
                .setMax(res.getInteger(R.integer.pref_long_break_max))
                .setMin(res.getInteger(R.integer.pref_long_break_min))
                .setUnit(R.string.pref_title_time_value)
                .setProgress(PreferenceManager.getDefaultSharedPreferences(this)
                        .getInt("pref_key_long_break", TickApplication.DEFAULT_LONG_BREAK))
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
                                TickApplication.DEFAULT_LONG_BREAK_FREQUENCY))
                .build();

        SwitchCompat pomodoroMode = (SwitchCompat)findViewById(R.id.pref_key_pomodoro_mode);
        pomodoroMode.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_key_pomodoro_mode", true));
        pomodoroMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putBoolean("pref_key_pomodoro_mode", isChecked);

                if (isChecked) {
                    Intent i = TickService.newIntent(getApplicationContext());
                    i.setAction(TickService.ACTION_POMODORO_MODE_ON);
                    startService(i);
                }

                try {
                    editor.apply();
                } catch (AbstractMethodError unused) {
                    // The app injected its own pre-Gingerbread
                    // SharedPreferences.Editor implementation without
                    // an apply method.
                    editor.commit();
                }
            }
        });

        SwitchCompat infinityMode = (SwitchCompat)findViewById(R.id.pref_key_infinity_mode);
        infinityMode.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_key_infinity_mode", false));
        infinityMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putBoolean("pref_key_infinity_mode", isChecked);

                try {
                    editor.apply();
                } catch (AbstractMethodError unused) {
                    editor.commit();
                }
            }
        });

        SwitchCompat tickSound = (SwitchCompat)findViewById(R.id.pref_key_tick_sound);
        tickSound.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_key_tick_sound", true));
        tickSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putBoolean("pref_key_tick_sound", isChecked);

                try {
                    editor.apply();
                } catch (AbstractMethodError unused) {
                    editor.commit();
                }

                Intent i = TickService.newIntent(getApplicationContext());

                if (isChecked) {
                    i.setAction(TickService.ACTION_TICK_SOUND_ON);
                } else {
                    i.setAction(TickService.ACTION_TICK_SOUND_OFF);
                }

                startService(i);
            }
        });

        SwitchCompat useNotification = (SwitchCompat)findViewById(R.id.pref_key_use_notification);
        useNotification.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_key_use_notification", true));
        useNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext()).edit();
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
            }
        });

        SwitchCompat notificationSound =
                (SwitchCompat)findViewById(R.id.pref_key_notification_sound);

        if (useNotification.isChecked()) {
            notificationSound.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("pref_key_notification_sound", true));
        } else {
            notificationSound.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("pref_key_notification_sound_checked", false));
            notificationSound.setEnabled(false);
        }

        notificationSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putBoolean("pref_key_notification_sound", isChecked);

                try {
                    editor.apply();
                } catch (AbstractMethodError unused) {
                    editor.commit();
                }
            }
        });

        SwitchCompat notificationVibrate =
                (SwitchCompat)findViewById(R.id.pref_key_notification_vibrate);
        if (useNotification.isChecked()) {
            notificationVibrate.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("pref_key_notification_vibrate", true));
        } else {
            notificationVibrate.setEnabled(false);
            notificationVibrate.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("pref_key_notification_vibrate_checked", false));
        }

        notificationVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putBoolean("pref_key_notification_vibrate", isChecked);

                try {
                    editor.apply();
                } catch (AbstractMethodError unused) {
                    editor.commit();
                }
            }
        });

        SwitchCompat screenOn = (SwitchCompat)findViewById(R.id.pref_key_screen_on);
        screenOn.setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_key_screen_on", false));
        screenOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putBoolean("pref_key_screen_on", isChecked);

                try {
                    editor.apply();
                } catch (AbstractMethodError unused) {
                    editor.commit();
                }
            }
        });

        RelativeLayout checkUpdate = (RelativeLayout)findViewById(R.id.check_update_app_layout);
        TextView versionText = (TextView)findViewById(R.id.version_text);
        versionText.setText(PackageUtil.versionName(this));

        checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetWorkUtil.hasNetConnection(getApplicationContext())) {
                    showToast(R.string.check_update_checking);
                    new ReadJSONFeedTask().execute(
                            "http://tick.icodechef.com/upgrade.php");
                } else {
                    showToast(R.string.check_update_connected_failed);
                }
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

    private void showToast(int resId) {
        mToast = Toast.makeText(this, getResources().getString(resId), Toast.LENGTH_LONG);
        mToast.show();
    }

    private String readJSONFeed(String address) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        URL url = new URL(address);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

        try {
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream content = new BufferedInputStream(
                        urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
        } finally {
            urlConnection.disconnect();
        }

        return stringBuilder.toString();
    }

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return readJSONFeed(urls[0]);
            } catch (IOException e) {
                showToast(R.string.check_update_connected_error);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            int newVersionCode;
            int curVersionCode = PackageUtil.versionCode(getApplicationContext());

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    newVersionCode = jsonObject.getInt("versionCode");

                    if (curVersionCode < newVersionCode) {
                        String newVersionName = jsonObject.getString("versionName");
                        String updateMessage = jsonObject.getString("updateMessage");
                        String message = getString(R.string.check_update_message,
                                newVersionName, updateMessage);
                        final String url = jsonObject.getString("url");

                        if (NetWorkUtil.isMobileConnection(getApplicationContext())) {
                            message += "\n\n" + getResources().getString(
                                    R.string.check_update_mobile_network);
                        }

                        if (mToast != null) {
                            mToast.cancel();
                        }

                        final AlertDialog.Builder builder =
                                new AlertDialog.Builder(SettingActivity.this);
                        builder.setTitle(getResources().getString(R.string.check_update_new_version));
                        builder.setMessage(message);
                        builder.setNegativeButton(getResources().getString(
                                R.string.check_update_update_later), null);
                        builder.setPositiveButton(R.string.check_update_update_now,
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        showToast(R.string.check_update_last_version);
                    }
                } catch (JSONException e) {
                    showToast(R.string.check_update_get_info_failed);
                    e.printStackTrace();
                }
            } else {
                showToast(R.string.check_update_get_info_failed);
            }
        }
    }
}
