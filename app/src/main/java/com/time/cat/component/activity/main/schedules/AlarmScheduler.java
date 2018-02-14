package com.time.cat.component.activity.main.schedules;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.time.cat.mvp.model.DBmodel.DBRoutine;
import com.time.cat.mvp.model.DBmodel.DBTask;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;


public class AlarmScheduler {

    public static final String EXTRA_PARAMS = "alarm_params";
    private static final String TAG = "AlarmScheduler";
    private static final AlarmScheduler instance = new AlarmScheduler();

    private AlarmScheduler() {
    }

    // static method to get the AlarmScheduler instance
    public static AlarmScheduler instance() {
        return instance;
    }

    public static boolean isWithinDefaultMargins(DateTime t, Context cxt) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cxt);
        String delayMinutesStr = prefs.getString("alarm_reminder_window", "60");
        long window = Long.parseLong(delayMinutesStr);
        DateTime now = DateTime.now();
        return t.isBefore(now) && t.plusMillis((int) window * 60 * 1000).isAfter(now);
    }

    /*
     * Whether an alarm for a specific time can be scheduled or not based on
     * the alarm time and the alarm reminder window defined by the user. Alarm time plus
     * alarm window must be in the future to allow alarm scheduling
     */
    public static boolean canBeScheduled(DateTime t, Context cxt) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cxt);
        String delayMinutesStr = prefs.getString("alarm_reminder_window", "60");
        int window = (int) Long.parseLong(delayMinutesStr);
        return t.plusMinutes(window).isAfterNow();
    }

    public static void setAlarmParams(Intent intent, AlarmIntentParams parcelable) {
        Bundle b = new Bundle();
        b.putParcelable(EXTRA_PARAMS, parcelable);
        intent.putExtra(EXTRA_PARAMS, b);
        intent.putExtra("date", parcelable.date);
    }

    public static AlarmIntentParams getAlarmParams(Intent intent) {
        // try to get the bundle
        Bundle bundleExtra = intent.getBundleExtra(EXTRA_PARAMS);
        if (bundleExtra != null) {
            return bundleExtra.getParcelable(EXTRA_PARAMS);
        } else {
            // try to get the parcelable from the intent
            return intent.getParcelableExtra(EXTRA_PARAMS);
        }
    }

    private static PendingIntent pendingIntent(Context ctx, DBRoutine DBRoutine, LocalDate date, boolean delayed, int actionType) {
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        AlarmIntentParams params = AlarmIntentParams.forRoutine(DBRoutine.getId(), date, delayed, actionType);
        setAlarmParams(intent, params);
        return PendingIntent.getBroadcast(ctx, params.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent pendingIntent(Context ctx, DBTask DBTask, LocalTime time, LocalDate date, boolean delayed, int actionType) {
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        AlarmIntentParams params = AlarmIntentParams.forSchedule(DBTask.getId(), time, date, delayed, actionType);
        setAlarmParams(intent, params);
        return PendingIntent.getBroadcast(ctx, params.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void onAlarmReceived(AlarmIntentParams params, Context ctx) {

        DBRoutine dbRoutine = DBRoutine.findById(params.routineId);
        if (dbRoutine != null) {
            Log.d(TAG, "onAlarmReceived: " + dbRoutine.getId() + ", " + dbRoutine.name());
            if (params.actionType == AlarmIntentParams.USER || isWithinDefaultMargins(dbRoutine, params.date(), ctx)) {
                Log.d(TAG, "DBRoutine alarm received, is user action: " + (params.actionType == AlarmIntentParams.USER));
                onRoutineTime(dbRoutine, params, ctx);
            } else {
                Log.d(TAG, "DBRoutine lost");
                onRoutineLost(dbRoutine, params, ctx);
            }
        }
    }

    public void onHourlyAlarmReceived(AlarmIntentParams params, Context ctx) {
        DBTask dbTask = DBTask.findById(params.scheduleId);
        if (dbTask != null) {
            DateTime time = params.dateTime();
            if (params.actionType == AlarmIntentParams.USER || isWithinDefaultMargins(time, ctx)) {

                Log.d(TAG, "Hourly alarm received, is user action: " + (params.actionType == AlarmIntentParams.USER));

                onHourlyScheduleTime(dbTask, params, ctx);
            } else {
                Log.d(TAG, "Task lest");
                onHourlyScheduleLost(dbTask, params, ctx);
            }
        }
    }

    public void onDelayRoutine(DBRoutine r, LocalDate date, Context ctx) {
        if (isWithinDefaultMargins(r, date, ctx)) {
            setRepeatAlarm(r, AlarmIntentParams.forRoutine(r.getId(), date, true), ctx, getAlarmRepeatFreq(ctx) * 60 * 1000);
        }
    }

    public void onDelayHourlySchedule(DBTask s, LocalTime t, LocalDate date, Context ctx) {
        if (isWithinDefaultMargins(date.toDateTime(t), ctx)) {
            setRepeatAlarm(s, AlarmIntentParams.forSchedule(s.getId(), t, date, true), ctx, getAlarmRepeatFreq(ctx) * 60 * 1000);
        }
    }

    public void onUserDelayHourlySchedule(DBTask s, LocalTime t, LocalDate date, Context ctx, long delayMinutes) {
        cancelHourlyDelayedAlarm(s, t, date, ctx, AlarmIntentParams.AUTO);
        setRepeatAlarm(s, AlarmIntentParams.forSchedule(s.getId(), t, date, true, AlarmIntentParams.USER), ctx, delayMinutes * 60 * 1000);
    }

    public void onUserDelayRoutine(DBRoutine r, LocalDate date, Context ctx, int delayMinutes) {
        cancelDelayedAlarm(r, date, ctx, AlarmIntentParams.AUTO);
        setRepeatAlarm(r, AlarmIntentParams.forRoutine(r.getId(), date, true, AlarmIntentParams.USER), ctx, delayMinutes * 60 * 1000);
    }

    public void updateAllAlarms(Context ctx) {
        for (DBTask DBTask : DBTask.findAll()) {
            setAlarmsIfNeeded(DBTask, LocalDate.now(), ctx);
        }
    }

    public boolean isWithinDefaultMargins(DBRoutine r, LocalDate date, Context cxt) {
        return isWithinDefaultMargins(date.toDateTime(r.time()), cxt);
    }

    public void onIntakeCancelled(DBRoutine r, LocalDate date, Context ctx) {
        // set time taken
        cancelIntake(r, date, ctx);
        // cancel alarms
        onIntakeCompleted(r, date, ctx);
    }

    public void onIntakeCancelled(DBTask s, LocalTime t, LocalDate date, Context ctx) {
        // set time taken
        cancelIntake(s, t, date, ctx);
        // cancell all alarms
        onIntakeCompleted(s, t, date, ctx);
    }

    public void onIntakeCompleted(DBRoutine r, LocalDate date, Context ctx) {
        // cancel notification
        ReminderNotification.cancel(ctx, ReminderNotification.routineNotificationId(r.getId().intValue()));
        // cancel all delay alarms
        cancelDelayedAlarm(r, date, ctx, AlarmIntentParams.USER);
        cancelDelayedAlarm(r, date, ctx, AlarmIntentParams.AUTO);
    }

    public void onIntakeCompleted(DBTask s, LocalTime t, LocalDate date, Context ctx) {
        // cancel notification
        ReminderNotification.cancel(ctx, ReminderNotification.scheduleNotificationId(s.getId().intValue()));
        // cancel all delay alarms
        cancelHourlyDelayedAlarm(s, t, date, ctx, AlarmIntentParams.USER);
        cancelHourlyDelayedAlarm(s, t, date, ctx, AlarmIntentParams.AUTO);
    }

    public void onCreateOrUpdateRoutine(DBRoutine r, Context ctx) {
        Log.d(TAG, "onCreateOrUpdateRoutine: " + r.getId() + ", " + r.name());
        setFirstAlarm(r, LocalDate.now(), ctx);
    }

    public void onCreateOrUpdateSchedule(DBTask s, Context ctx) {
//        Log.d(TAG, "onCreateOrUpdateSchedule: " + s.getId() + ", " + s.medicine().name());
        setAlarmsIfNeeded(s, LocalDate.now(), ctx);
    }

    public void onDeleteRoutine(DBRoutine r, Context ctx) {
        Log.d(TAG, "onDeleteRoutine: " + r.getId() + ", " + r.name());
        cancelAlarm(r, LocalDate.now(), ctx);
    }

    private Long getAlarmRepeatFreq(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String delayMinutesStr = prefs.getString("alarm_repeat_frequency", "15");
        return Long.parseLong(delayMinutesStr);
    }

    /**
     * Set an alarm for a DBRoutine
     */
    private void setFirstAlarm(DBRoutine DBRoutine, LocalDate date, Context ctx) {
        long timestamp = date.toDateTime(DBRoutine.time()).getMillis();
        PendingIntent routinePendingIntent = pendingIntent(ctx, DBRoutine, date, false, AlarmIntentParams.AUTO);
        setExactAlarm(ctx, timestamp, routinePendingIntent);
    }

    //
    // Methods to check if a intake is available according to the user preferences
    //

    /**
     * Set an alarm for a repeating DBTask item
     */
    private void setFirstAlarm(DBTask DBTask, LocalTime time, LocalDate date, Context ctx) {
        DateTime dateTime = date.toDateTime(time);
        PendingIntent routinePendingIntent = pendingIntent(ctx, DBTask, time, date, false, AlarmIntentParams.AUTO);
        setExactAlarm(ctx, dateTime.getMillis(), routinePendingIntent);
    }

    private void setRepeatAlarm(DBRoutine DBRoutine, AlarmIntentParams firstParams, Context ctx, long delayMillis) {
        PendingIntent routinePendingIntent = pendingIntent(ctx, DBRoutine, firstParams.date(), true, firstParams.actionType);
        setExactAlarm(ctx, DateTime.now().getMillis() + delayMillis, routinePendingIntent);
    }

    private void setRepeatAlarm(DBTask DBTask, AlarmIntentParams firstParams, Context ctx, long delayMillis) {
        PendingIntent schedulePendingIntent = pendingIntent(ctx, DBTask, firstParams.scheduleTime(), firstParams.date(), true, firstParams.actionType);
        setExactAlarm(ctx, DateTime.now().getMillis() + delayMillis, schedulePendingIntent);
    }

    private void setExactAlarm(Context ctx, long millis, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
            }
        }
    }

    private void cancelAlarm(DBRoutine DBRoutine, LocalDate date, Context ctx) {
        PendingIntent routinePendingIntent = pendingIntent(ctx, DBRoutine, date, false, AlarmIntentParams.AUTO);
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(routinePendingIntent);
        }
    }

    private void cancelDelayedAlarm(DBRoutine DBRoutine, LocalDate date, Context ctx, int actionType) {
        // cancel alarm
        // get delay DBRoutine pending intent
        PendingIntent routinePendingIntent = pendingIntent(ctx, DBRoutine, date, true, actionType);
        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(routinePendingIntent);
        }
    }

    private void cancelHourlyDelayedAlarm(DBTask s, LocalTime t, LocalDate date, Context ctx, int actionType) {

//        DailyScheduleItem ds = DB.dailyScheduleItems().findBy(s, date, t);
//        if (ds != null) {
//            // get hourly delay pending intent
//            PendingIntent pendingIntent = pendingIntent(ctx, s, t, date, true, actionType);
//            // Get the AlarmManager service
//            AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
//            if (alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//            }
//        }
    }

    private void setAlarmsIfNeeded(DBTask DBTask, LocalDate date, Context ctx) {
//        if (!DBTask.repeatsHourly()) {
//            for (DBTaskItem scheduleItem : DBTask.items()) {
//                if (scheduleItem.routine() != null && canBeScheduled(scheduleItem.routine().time().toDateTimeToday(), ctx)) {
//                    setFirstAlarm(scheduleItem.routine(), date, ctx);
//                }
//            }
//        } else {
//            List<DateTime> times = DBTask.hourlyItemsAt(date.toDateTimeAtStartOfDay());
//            for (DateTime time : times) {
//                if (canBeScheduled(time, ctx)) {
//                    setFirstAlarm(DBTask, time.toLocalTime(), date, ctx);
//                }
//            }
//        }
    }

    private void onRoutineTime(DBRoutine DBRoutine, AlarmIntentParams firstParams, Context ctx) {

//        List<DBTaskItem> doses = new ArrayList<>();
//        List<DBTaskItem> rItems = DBRoutine.scheduleItems();
//        boolean notify = false;
//        // check if all items have timeTaken (cancelled notifications)
//        for (DBTaskItem scheduleItem : rItems) {
//            Log.d(TAG, "DBRoutine schedule items: " + rItems.size());
//            DailyScheduleItem ds = DB.dailyScheduleItems().findByScheduleItemAndDate(scheduleItem, firstParams.date());
//            if (ds != null) {
//                Log.d(TAG, "DailySchedule Item: " + ds.toString());
//                doses.add(scheduleItem);
//                if (ds.timeTaken() == null) {
//                    Log.d(TAG, ds.scheduleItem().schedule().medicine().name() + " not checked or cancelled. Notify!");
//                    notify = true;
//                }
//            }
//        }
//
//        if (notify) {
//            final Intent intent = new Intent(ctx, ConfirmActivity.class);
//            intent.putExtra("routine_id", DBRoutine.getId());
//            intent.putExtra("date", firstParams.date);
//            intent.putExtra("actionType", firstParams.actionType);
//            ReminderNotification.notify(ctx, ctx.getResources().getString(R.string.meds_time), DBRoutine, doses, firstParams.date(), intent, false);
//            Log.d(TAG, "Show notification");
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
//            boolean repeatAlarms = prefs.getBoolean("alarm_repeat_enabled", false);
//            if (repeatAlarms) {
//                firstParams.actionType = AlarmIntentParams.AUTO;
//                setRepeatAlarm(DBRoutine, firstParams, ctx, getAlarmRepeatFreq(ctx) * 60 * 1000);
//            }
//        }
    }

    //
    // Methods called when there are changes in database, to update the alarm status
    //

    private void onHourlyScheduleTime(DBTask DBTask, AlarmIntentParams firstParams, Context ctx) {

//        boolean notify = false;
//        // check if this item has timeTaken (cancelled notifications)
//
//        DailyScheduleItem ds = DB.dailyScheduleItems().findBy(DBTask, firstParams.date(), firstParams.scheduleTime());
//
//        if (ds != null && ds.timeTaken() == null) {
//            notify = true;
//        }
//
//        if (notify) {
//            final Intent intent = new Intent(ctx, ConfirmActivity.class);
//            intent.putExtra(TimeCatApp.INTENT_EXTRA_SCHEDULE_ID, DBTask.getId());
//            intent.putExtra(TimeCatApp.INTENT_EXTRA_SCHEDULE_TIME, firstParams.scheduleTime);
//            intent.putExtra("date", firstParams.date);
//            intent.putExtra("actionType", firstParams.actionType);
//
//            String title = ctx.getResources().getString(R.string.meds_time);
//            ReminderNotification.notify(ctx, title, DBTask, firstParams.date(), firstParams.scheduleTime(), intent, false);
//            // Handle delay if needed
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
//            boolean repeatAlarms = prefs.getBoolean("alarm_repeat_enabled", false);
//            if (repeatAlarms) {
//                firstParams.actionType = AlarmIntentParams.AUTO;
//                setRepeatAlarm(DBTask, firstParams, ctx, getAlarmRepeatFreq(ctx) * 60 * 1000);
//            }
//        }
    }

    private void onRoutineLost(DBRoutine DBRoutine, AlarmIntentParams params, Context ctx) {
//        // get the schedule items for the current DBRoutine, excluding already taken
//        List<DBTaskItem> doses = ScheduleUtils.getRoutineScheduleItems(DBRoutine, params.date());
//        // cancel intake
//        cancelIntake(DBRoutine, params.date(), ctx);
//        // cancel alarms
//        onIntakeCompleted(DBRoutine, params.date(), ctx);
//
//        // show DBRoutine lost notification
//        final Intent intent = new Intent(ctx, ConfirmActivity.class);
//        intent.putExtra("routine_id", DBRoutine.getId());
//        intent.putExtra("date", params.date);
//        String title = ctx.getResources().getString(R.string.meds_time_lost);
//        ReminderNotification.notify(ctx, title, DBRoutine, doses, params.date(), intent, true);
    }

    private void onHourlyScheduleLost(DBTask DBTask, AlarmIntentParams params, Context ctx) {
//        // cancel intake (set time taken)
//        cancelIntake(DBTask, params.scheduleTime(), params.date(), ctx);
//        // cancel alarms
//        onIntakeCompleted(DBTask, params.scheduleTime(), params.date(), ctx);
//
//        // show DBTask lost notification
//        final Intent intent = new Intent(ctx, ConfirmActivity.class);
//        intent.putExtra(TimeCatApp.INTENT_EXTRA_SCHEDULE_ID, DBTask.getId());
//        intent.putExtra(TimeCatApp.INTENT_EXTRA_SCHEDULE_TIME, params.scheduleTime);
//        intent.putExtra("date", params.date);
//        String title = ctx.getResources().getString(R.string.meds_time_lost);
//        ReminderNotification.notify(ctx, title, DBTask, params.date(), params.scheduleTime(), intent, true);
    }

    private void cancelIntake(DBRoutine r, LocalDate date, Context ctx) {
//        for (DBTaskItem scheduleItem : r.scheduleItems()) {
//            DailyScheduleItem ds = DB.dailyScheduleItems().findByScheduleItemAndDate(scheduleItem, date);
//            if (ds.timeTaken() == null) {
//                Log.d(TAG, "Cancelling schedule item");
//                ds.setTimeTaken(LocalTime.now());
//                ds.save();
//            }
//        }
    }

    private void cancelIntake(DBTask s, LocalTime t, LocalDate date, Context ctx) {
//        DailyScheduleItem ds = DB.dailyScheduleItems().findBy(s, date, t);
//        if (ds != null && ds.timeTaken() == null) {
//            Log.d(TAG, "Cancelling schedule item");
//            ds.setTimeTaken(LocalTime.now());
//            ds.save();
//        }
    }

}
