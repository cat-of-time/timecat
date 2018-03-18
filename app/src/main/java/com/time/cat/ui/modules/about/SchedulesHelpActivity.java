package com.time.cat.ui.modules.about;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.time.cat.R;


public class SchedulesHelpActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setFullscreen(true);
        super.onCreate(savedInstanceState);

        addSlide(new SimpleSlide.Builder().layout(R.layout.help_schedules_1).background(R.color.schedule_help_background).backgroundDark(R.color.schedule_help_background_dark).build());

        addSlide(new SimpleSlide.Builder().layout(R.layout.help_schedules_2).background(R.color.schedule_help_background).backgroundDark(R.color.schedule_help_background_dark).build());

        addSlide(new SimpleSlide.Builder().layout(R.layout.help_schedules_3).background(R.color.schedule_help_background).backgroundDark(R.color.schedule_help_background_dark).build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.edit().putBoolean("PREFERENCE_SCHEDULES_HELP_SHOWN", true).apply();
    }

}