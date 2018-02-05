package com.time.cat.component.activity.main.schedules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This class receives our routine alarms
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final String TAG = "AlarmReceiver.class";

    @Override
    public void onReceive(Context context, Intent intent) {
//
//        if (TimeCatApp.disableReceivers) {
//            return;
//        }

        AlarmIntentParams params = AlarmScheduler.getAlarmParams(intent);

        if (params == null) {
            Log.w(TAG, "No extra params supplied");
            return;
        } else {
            Log.d(TAG, "Received alarm: " + params.action);
        }

        Intent serviceIntent = new Intent(context, AlarmIntentService.class);
        AlarmScheduler.setAlarmParams(serviceIntent, params);
        context.startService(serviceIntent);
    }
}