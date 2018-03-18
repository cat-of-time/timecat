package com.time.cat;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.RemoteViews;

import com.time.cat.ui.modules.operate.InfoOperationActivity;
import com.time.cat.ui.service.RemoteViewServiceImp;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Implementation of App Widget functionality.
 */
public class TimeCatAppWidget extends AppWidgetProvider {

    public static final String ACTION_PREVIOUS_MONTH = "com.time.cat.TimeCatAppWidget.action.PREVIOUS_MONTH";
    public static final String ACTION_NEXT_MONTH = "com.time.cat.TimeCatAppWidget.action.NEXT_MONTH";
    public static final String ACTION_RESET_MONTH = "com.time.cat.TimeCatAppWidget.action.RESET_MONTH";
    public static final String ACTION_REFRESH_TASK = "com.time.cat.TimeCatAppWidget.action.refresh";
    public static final String ACTION_ADD = "com.time.cat.TimeCatAppWidget.action.add";
    public static final String ACTION_ITEM_CLICK = "com.time.cat.TimeCatAppWidget.action.ITEM_CLICK";

    private static final String PREF_MONTH = "month";
    private static final String PREF_YEAR = "year";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            drawWidget(context, appWidgetId);
        }
    }

    private void redrawWidgets(Context context) {
        int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
                new ComponentName(context, TimeCatAppWidget.class));
        for (int appWidgetId : appWidgetIds) {
            drawWidget(context, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();

        if (ACTION_PREVIOUS_MONTH.equals(action)) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            Calendar cal = Calendar.getInstance();
            int thisMonth = sp.getInt(PREF_MONTH, cal.get(Calendar.MONTH));
            int thisYear = sp.getInt(PREF_YEAR, cal.get(Calendar.YEAR));
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, thisMonth);
            cal.set(Calendar.YEAR, thisYear);
            cal.add(Calendar.MONTH, -1);
            sp.edit()
                    .putInt(PREF_MONTH, cal.get(Calendar.MONTH))
                    .putInt(PREF_YEAR, cal.get(Calendar.YEAR))
                    .apply();
            redrawWidgets(context);

        } else if (ACTION_NEXT_MONTH.equals(action)) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            Calendar cal = Calendar.getInstance();
            int thisMonth = sp.getInt(PREF_MONTH, cal.get(Calendar.MONTH));
            int thisYear = sp.getInt(PREF_YEAR, cal.get(Calendar.YEAR));
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, thisMonth);
            cal.set(Calendar.YEAR, thisYear);
            cal.add(Calendar.MONTH, 1);
            sp.edit()
                    .putInt(PREF_MONTH, cal.get(Calendar.MONTH))
                    .putInt(PREF_YEAR, cal.get(Calendar.YEAR))
                    .apply();
            redrawWidgets(context);

        } else if (ACTION_RESET_MONTH.equals(action)) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().remove(PREF_MONTH).remove(PREF_YEAR).apply();
            redrawWidgets(context);

        } else if (ACTION_ADD.equals(action)) {
            Intent intent2DialogActivity = new Intent(context, InfoOperationActivity.class);
            intent2DialogActivity.addFlags(FLAG_ACTIVITY_NEW_TASK);
            intent2DialogActivity.putExtra(InfoOperationActivity.TO_SAVE_STR, "");
            context.startActivity(intent2DialogActivity);

        } else if (ACTION_ITEM_CLICK.equals(action)) {
//            ToastUtil.show(intent.getIntExtra("position", 0) + "");
//            LogUtil.e(intent.getIntExtra("position", 0) + "");
        } else if (ACTION_REFRESH_TASK.equals(action)) {
            redrawWidgets(context);
//            ToastUtil.show("action refresh");
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        drawWidget(context, appWidgetId);
    }

    private void drawWidget(Context context, int appWidgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Resources res = context.getResources();
        Bundle widgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);
        boolean shortMonthName = false;
        boolean mini = false;
        int numWeeks = 6;
        if (widgetOptions != null) {
            int minWidthDp = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            int minHeightDp = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
            shortMonthName = minWidthDp <= res.getInteger(R.integer.max_width_short_month_label_dp);
            mini = minHeightDp <= res.getInteger(R.integer.max_height_mini_view_dp);
            if (mini) {
                numWeeks = 1;
            }
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);

        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_YEAR);
        int todayYear = cal.get(Calendar.YEAR);
        int thisMonth;
        if (!mini) {
            thisMonth = sp.getInt(PREF_MONTH, cal.get(Calendar.MONTH));
            int thisYear = sp.getInt(PREF_YEAR, cal.get(Calendar.YEAR));
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, thisMonth);
            cal.set(Calendar.YEAR, thisYear);
        } else {
            thisMonth = cal.get(Calendar.MONTH);
        }
        rv.setTextViewText(R.id.month_label, DateFormat.format(
                shortMonthName ? "MMM yy" : "MMMM yyyy", cal));

        if (!mini) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            int monthStartDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DAY_OF_MONTH, 1 - monthStartDayOfWeek);
        } else {
            int todayDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DAY_OF_MONTH, 1 - todayDayOfWeek);
        }

        rv.removeAllViews(R.id.calendar);

        RemoteViews headerRowRv = new RemoteViews(context.getPackageName(), R.layout.widget_row_header);
        DateFormatSymbols dfs = DateFormatSymbols.getInstance();
        String[] weekdays = dfs.getShortWeekdays();
        for (int day = Calendar.SUNDAY; day <= Calendar.SATURDAY; day++) {
            RemoteViews dayRv = new RemoteViews(context.getPackageName(), R.layout.widget_cell_header);
            dayRv.setTextViewText(android.R.id.text1, weekdays[day]);
            headerRowRv.addView(R.id.row_container, dayRv);
        }
        rv.addView(R.id.calendar, headerRowRv);

        for (int week = 0; week < numWeeks; week++) {
            RemoteViews rowRv = new RemoteViews(context.getPackageName(), R.layout.widget_row_week);
            for (int day = 0; day < 7; day++) {
                boolean inMonth = cal.get(Calendar.MONTH) == thisMonth;
                boolean inYear  = cal.get(Calendar.YEAR) == todayYear;
                boolean isToday = inYear && inMonth && (cal.get(Calendar.DAY_OF_YEAR) == today);

                boolean isFirstOfMonth = cal.get(Calendar.DAY_OF_MONTH) == 1;
                int cellLayoutResId = R.layout.widget_cell_day;
                if (isToday) {
                    cellLayoutResId = R.layout.widget_cell_today;
                } else if (inMonth) {
                    cellLayoutResId = R.layout.widget_cell_day_this_month;
                }
                RemoteViews cellRv = new RemoteViews(context.getPackageName(), cellLayoutResId);
                cellRv.setTextViewText(android.R.id.text1,
                        Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
                if (isFirstOfMonth) {
                    cellRv.setTextViewText(R.id.month_label, DateFormat.format("MMM", cal));
                }
                rowRv.addView(R.id.row_container, cellRv);
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            rv.addView(R.id.calendar, rowRv);
        }

        rv.setViewVisibility(R.id.prev_month_button, mini ? View.GONE : View.VISIBLE);
        rv.setOnClickPendingIntent(R.id.prev_month_button,
                PendingIntent.getBroadcast(context, 0,
                        new Intent(context, TimeCatAppWidget.class).setAction(ACTION_PREVIOUS_MONTH),
                        PendingIntent.FLAG_UPDATE_CURRENT));
        rv.setViewVisibility(R.id.next_month_button, mini ? View.GONE : View.VISIBLE);
        rv.setOnClickPendingIntent(R.id.next_month_button,
                PendingIntent.getBroadcast(context, 0,
                        new Intent(context, TimeCatAppWidget.class).setAction(ACTION_NEXT_MONTH),
                        PendingIntent.FLAG_UPDATE_CURRENT));

        rv.setOnClickPendingIntent(R.id.month_label,
                PendingIntent.getBroadcast(context, 0,
                        new Intent(context, TimeCatAppWidget.class).setAction(ACTION_RESET_MONTH),
                        PendingIntent.FLAG_UPDATE_CURRENT));

        //创建一个广播，点击按钮发送该广播
        rv.setOnClickPendingIntent(R.id.widget_add_button,
                PendingIntent.getBroadcast(context, 0,
                        new Intent(ACTION_ADD),
                        PendingIntent.FLAG_UPDATE_CURRENT));
        rv.setOnClickPendingIntent(R.id.widget_refresh_button,
                PendingIntent.getBroadcast(context, 0,
                        new Intent(context, TimeCatAppWidget.class).setAction(ACTION_REFRESH_TASK),
                        PendingIntent.FLAG_UPDATE_CURRENT));

//        rv.setViewVisibility(R.id.month_bar, numWeeks <= 1 ? View.GONE : View.VISIBLE);

        //绑定service用来填充listview中的视图
        rv.setRemoteAdapter(R.id.widget_listview, new Intent(context, RemoteViewServiceImp.class));
        //添加item的点击事件
        rv.setPendingIntentTemplate(R.id.widget_listview,
                PendingIntent.getBroadcast(context, 0,
                        new Intent(ACTION_ITEM_CLICK),
                        PendingIntent.FLAG_CANCEL_CURRENT));

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }
}



