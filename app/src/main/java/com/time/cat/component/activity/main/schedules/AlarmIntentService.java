package com.time.cat.component.activity.main.schedules;

import android.content.Intent;
import android.util.Log;

import com.time.cat.TimeCatApp;

/**
 * Created by joseangel.pineiro on 11/20/15.
 */
public class AlarmIntentService extends WakeIntentService {

    public static final String TAG = "AlarmIntentService";

    public AlarmIntentService() {
        super("AlarmIntentService");
    }

    @Override
    public void doReminderWork(Intent intent) {


        Log.d(TAG, "Service started");

        // get intent params with alarm info
        AlarmIntentParams params = AlarmScheduler.getAlarmParams(intent);

        if (params == null) {
            Log.w(TAG, "No extra params supplied");
            return;
        }

        Log.d(TAG, "Alarm received: " + params.toString());

        if (params.action != TimeCatApp.ACTION_DAILY_ALARM) {
            try {
                params.date();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        switch (params.action) {
            case TimeCatApp.ACTION_ROUTINE_TIME:
            case TimeCatApp.ACTION_ROUTINE_DELAYED_TIME:
                AlarmScheduler.instance().onAlarmReceived(params, this.getApplicationContext());
                break;

            case TimeCatApp.ACTION_HOURLY_SCHEDULE_TIME:
            case TimeCatApp.ACTION_HOURLY_SCHEDULE_DELAYED_TIME:
                AlarmScheduler.instance().onHourlyAlarmReceived(params, this.getApplicationContext());
                break;

            case TimeCatApp.ACTION_DAILY_ALARM:
                Log.d(TAG, "Received daily alarm");
//                DailyAgenda.instance().setupForToday(this.getApplicationContext(), false);
                break;
            default:
                Log.w(TAG, "Unknown action received");
                break;
        }


    }
}
