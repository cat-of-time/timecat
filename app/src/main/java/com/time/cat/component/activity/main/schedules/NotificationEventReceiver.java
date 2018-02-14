package com.time.cat.component.activity.main.schedules;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.mvp.model.DBmodel.DBRoutine;
import com.time.cat.mvp.model.DBmodel.DBTask;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

/**
 * This class receives our routine alarms
 */
public class NotificationEventReceiver extends BroadcastReceiver {

    public static final String TAG = NotificationEventReceiver.class.getName();


    @Override
    public void onReceive(Context context, Intent intent) {

        long routineId;
        long scheduleId;
        String scheduleTime;
        LocalDate date;

        int action = intent.getIntExtra(TimeCatApp.INTENT_EXTRA_ACTION, -1);

        Log.d(TAG, "Notification event received - Action : " + action);

        String dateStr = intent.getStringExtra("date");
        if (dateStr != null) {
            date = DateTimeFormat.forPattern(AlarmIntentParams.DATE_FORMAT).parseLocalDate(dateStr);
        } else {
            Log.w(TAG, "Date not supplied, assuming today.");
            date = LocalDate.now();
        }

        switch (action) {

            case TimeCatApp.ACTION_CANCEL_ROUTINE:
                routineId = intent.getLongExtra(TimeCatApp.INTENT_EXTRA_ROUTINE_ID, -1);
                if (routineId != -1) {
                    AlarmScheduler.instance().onIntakeCancelled(DBRoutine.findById(routineId), date, context);
                    Toast.makeText(context, context.getString(R.string.reminder_cancelled_message), Toast.LENGTH_SHORT).show();
                }
                break;

            case TimeCatApp.ACTION_CANCEL_HOURLY_SCHEDULE:
                scheduleId = intent.getLongExtra(TimeCatApp.INTENT_EXTRA_SCHEDULE_ID, -1);
                scheduleTime = intent.getStringExtra(TimeCatApp.INTENT_EXTRA_SCHEDULE_TIME);
                if (scheduleId != -1 && scheduleTime != null) {
                    LocalTime t = DateTimeFormat.forPattern("kk:mm").parseLocalTime(scheduleTime);
                    AlarmScheduler.instance().onIntakeCancelled(DBTask.findById(scheduleId), t, date, context);
                    Toast.makeText(context, context.getString(R.string.reminder_cancelled_message), Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                Log.d(TAG, "Request not handled " + intent.toString());
                break;
        }

    }

}