package com.time.cat.ui.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.time.cat.R;
import com.time.cat.TimeCatAppWidget;
import com.time.cat.data.database.DB;
import com.time.cat.data.database.ScheduleDao;
import com.time.cat.data.model.DBmodel.DBTask;
import com.time.cat.util.string.TimeUtil;

import java.util.Date;
import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/7
 * @discription null
 * @usage null
 */
public class RemoteViewServiceImp extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactoryImp(this, intent);
    }

    private static List<DBTask> data;

    public static void loadData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                data = ScheduleDao.sort(ScheduleDao.DBTaskFilter(DB.schedules().findAll(), new Date()));
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class RemoteViewsFactoryImp implements RemoteViewsFactory {
        private Intent requestIntent;
        private Context requestContext;


        public RemoteViewsFactoryImp(Context context, Intent intent) {
            requestContext = context;
            requestIntent = intent;
        }

        @Override
        public void onCreate() {
            loadData();
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(requestContext.getPackageName(), R.layout.item_widget_task_list);
            //listview的点击事件
            Intent intent = new Intent(TimeCatAppWidget.ACTION_ITEM_CLICK);
            intent.putExtra("position", position);
            remoteViews.setOnClickFillInIntent(R.id.widget_list_item, intent);
            
            DBTask dbTask = data.get(position);
            remoteViews.setTextViewText(R.id.widget_list_item_title, dbTask.getTitle());

//            remoteViews.getCalendarItemCheckBox().setUncheckedStrokeColor(DBTask.labelColor[dbTask.getLabel()]);
//            remoteViews.getCalendarItemCheckBox().setChecked(dbTask.getIsFinish());
//
            Date today = new Date();
            if (dbTask.getCreated_datetime() != null && dbTask.getCreated_datetime().length() > 0) {
                Date date = TimeUtil.formatGMTDateStr(dbTask.getCreated_datetime());
                if (date != null) {
                    if (dbTask.getIsFinish()) {
                        today = TimeUtil.formatGMTDateStr(dbTask.getFinished_datetime());
                        remoteViews.setTextColor(R.id.widget_list_item_delay, Color.GRAY);
                        remoteViews.setTextColor(R.id.widget_list_item_title, Color.GRAY);
                    }
                    assert today != null;
                    long during = today.getTime() - date.getTime();
                    long day = during / (1000 * 60 * 60 * 24);
                    if (day >= 1) {
                        remoteViews.setTextViewText(R.id.widget_list_item_delay, day >= 1 ? getString(R.string.calendar_delay) + day + getString(R.string.calendar_day) : "");
                        if (dbTask.getIsFinish()) {
                            remoteViews.setTextColor(R.id.widget_list_item_delay, Color.GRAY);
                            remoteViews.setTextColor(R.id.widget_list_item_title, Color.GRAY);
                        } else {
                            remoteViews.setTextColor(R.id.widget_list_item_delay, getResources().getColor(R.color.red));
                            remoteViews.setTextColor(R.id.widget_list_item_title, Color.WHITE);
                        }
                    } else {
                        remoteViews.setViewVisibility(R.id.widget_list_item_delay, View.INVISIBLE);
                    }
                }
            }
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
