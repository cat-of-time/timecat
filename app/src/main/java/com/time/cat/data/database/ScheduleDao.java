/*
 *    Calendula - An assistant for personal medication management.
 *    Copyright (C) 2016 CITIUS - USC
 *
 *    Calendula is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.time.cat.data.database;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.time.cat.TimeCatApp;
import com.time.cat.data.Constants;
import com.time.cat.data.model.APImodel.Task;
import com.time.cat.data.model.Converter;
import com.time.cat.data.model.DBmodel.DBSubPlan;
import com.time.cat.data.model.DBmodel.DBTask;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.data.model.events.PersistenceEvents;
import com.time.cat.util.override.LogUtil;
import com.time.cat.util.string.TimeUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleDao extends GenericDao<DBTask, Long> {
    private static final String TAG = "ScheduleDao";

    public ScheduleDao(DatabaseHelper db) {
        super(db);
    }

    public List<DBTask> findAllForActiveUser() {
        return findAll(DB.users().getActive());
    }

    public List<DBTask> findAll(DBUser user) {
        return findAll(user.id());
    }

    public List<DBTask> findAll(Long userId) {
        try {
            return dao.queryBuilder().where().eq(DBTask.COLUMN_USER, userId).query();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding models", e);
        }
    }

    public List<DBTask> findAll(DBSubPlan dbSubPlan) {
        return findAllBySubPlan(dbSubPlan.getId());
    }

    public List<DBTask> findAllBySubPlan(Long subPlanId) {
        try {
            return dao.queryBuilder().where().eq(DBTask.COLUMN_SUBPLAN, subPlanId).query();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding models", e);
        }
    }

    public List<DBTask> findBetween(String start, String end) {
        Date start_date = TimeUtil.formatGMTDateStr(start);
        Date end_date = TimeUtil.formatGMTDateStr(end);
        List<DBTask> dbTaskList = findAllForActiveUser();
        List<DBTask> result = new ArrayList<>();
        for (DBTask dbTask : dbTaskList) {
            Date begin_datetime = TimeUtil.formatGMTDateStr(dbTask.getBegin_datetime());
            Date end_datetime= TimeUtil.formatGMTDateStr(dbTask.getEnd_datetime());
            if (TimeUtil.isDateEarlier(start_date, begin_datetime) && TimeUtil.isDateEarlier(end_datetime, end_date)) {
                result.add(dbTask);
            }
        }
        return result;
    }

    public List<DBTask> findFinishedBetween(Date start_date, Date end_date) {
        List<DBTask> dbTaskList = findAllForActiveUser();
        List<DBTask> result = new ArrayList<>();
        for (DBTask dbTask : dbTaskList) {
            if (dbTask.getIsFinish() && dbTask.getFinished_datetime() != null) {
                Date Finished_datetime = TimeUtil.formatGMTDateStr(dbTask.getFinished_datetime());
                if (TimeUtil.isDateEarlier(start_date, Finished_datetime) && TimeUtil.isDateEarlier(Finished_datetime, end_date)) {
                    result.add(dbTask);
                }
            }
        }
        return result;
    }

    public List<DBTask> findBetween(Date start_date, Date end_date) {
        List<DBTask> dbTaskList = findAllForActiveUser();
        List<DBTask> result = new ArrayList<>();
        for (DBTask dbTask : dbTaskList) {
            Date Created_datetime = TimeUtil.formatGMTDateStr(dbTask.getCreated_datetime());
            if (TimeUtil.isDateEarlier(start_date, Created_datetime) && TimeUtil.isDateEarlier(Created_datetime, end_date)) {
                result.add(dbTask);
            }
        }
        return result;
    }

    @Override
    public Dao<DBTask, Long> getConcreteDao() {
        try {
            return dbHelper.getSchedulesDao();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating medicines dao", e);
        }
    }

    public void updateAndFireEvent(DBTask task) {
        try {
            update(task);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TimeCatApp.eventBus().post(new PersistenceEvents.TaskUpdateEvent(task));
    }

    public void saveAndFireEvent(DBTask task) {
        save(task);
        TimeCatApp.eventBus().post(new PersistenceEvents.TaskCreateEvent(task));
    }

    public void deleteAndFireEvent(DBTask dbTask) {
        try {
            delete(dbTask);
            TimeCatApp.eventBus().post(new PersistenceEvents.TaskDeleteEvent(dbTask));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void safeSaveTask(Task task) {
        Log.i(TAG, "返回的任务信息 --> " + task.toString());
        //保存用户信息到本地
        DBTask dbTask = Converter.toDBTask(task);
        List<DBTask> existing = null;
        try {
            existing = DB.schedules().queryForEq(DBTask.COLUMN_URL, task.getUrl());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbTask.setId(id);
            try {
                DB.schedules().update(dbTask);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "更新任务信息 --> updateAndFireEvent -- > " + dbTask.toString());
        } else {
            DB.schedules().save(dbTask);
            Log.i(TAG, "保存任务信息 --> saveAndFireEvent -- > " + dbTask.toString());
        }
    }

    public void safeSaveTaskAndFireEvent(Task task) {
        Log.i(TAG, "返回的任务信息 --> " + task.toString());
        //保存用户信息到本地
        DBTask dbTask = Converter.toDBTask(task);
        List<DBTask> existing = null;
        try {
            existing = DB.schedules().queryForEq(DBTask.COLUMN_URL, task.getUrl());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbTask.setId(id);
            DB.schedules().updateAndFireEvent(dbTask);
            Log.i(TAG, "更新任务信息 --> updateAndFireEvent -- > " + dbTask.toString());
        } else {
            DB.schedules().saveAndFireEvent(dbTask);
            Log.i(TAG, "保存任务信息 --> saveAndFireEvent -- > " + dbTask.toString());
        }
    }

    public void safeSaveDBTask(DBTask dbTask) {
        List<DBTask> existing = null;
        try {
            existing = DB.schedules().queryForEq(DBTask.COLUMN_CREATED_DATETIME, dbTask.getCreated_datetime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbTask.setId(id);
            try {
                DB.schedules().update(dbTask);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "更新任务信息 --> updateAndFireEvent -- > " + dbTask.toString());
        } else {
            DB.schedules().save(dbTask);
            Log.i(TAG, "保存任务信息 --> saveAndFireEvent -- > " + dbTask.toString());
        }
    }

    public void safeSaveDBTaskAndFireEvent(DBTask dbTask) {
        List<DBTask> existing = null;
        try {
            existing = DB.schedules().queryForEq(DBTask.COLUMN_CREATED_DATETIME, dbTask.getCreated_datetime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbTask.setId(id);
            DB.schedules().updateAndFireEvent(dbTask);
            Log.i(TAG, "更新任务信息 --> updateAndFireEvent -- > " + dbTask.toString());
        } else {
            DB.schedules().saveAndFireEvent(dbTask);
            Log.i(TAG, "保存任务信息 --> saveAndFireEvent -- > " + dbTask.toString());
        }
    }

    @Override
    public void fireEvent() {
        TimeCatApp.eventBus().post(PersistenceEvents.SCHEDULE_EVENT);
    }

    public List<DBTask> findHourly() {
        return findBy(DBTask.COLUMN_TYPE, DBTask.SCHEDULE_TYPE_EVERYHOUR);
    }

    public static ArrayList<DBTask> filterByTag(ArrayList<DBTask> dbTasks, ArrayList<String> tags) {
        ArrayList<DBTask> dbTaskArrayList = new ArrayList<>();
        for (DBTask dbTask : dbTasks) {
            for (String tag : dbTask.getTags()) {
                if (tags.toString().contains(tag)) {
                    dbTaskArrayList.add(dbTask);
                }
            }
        }
        return dbTaskArrayList;
    }

    public static ArrayList<DBTask> filter(ArrayList<DBTask> dbTasks) {
        return dbTasks;
    }

    public static ArrayList<DBTask> DBTaskFilter(List<DBTask> taskArrayList, Date currentDate) {
        ArrayList<DBTask> tasks = new ArrayList<>();
        if (taskArrayList == null || taskArrayList.size() <= 0) {
            return tasks;
        }
        // 需要显示的
        // 首先是active user的
        // 今天刚刚finished的
        // 顺延的
        // begin_datetime < today <= end_datetime
        DBUser dbUser = DB.users().getActive();

        Date today = new Date();
        if (currentDate != null) {
            today = currentDate;
        }
        for (DBTask task : taskArrayList) {
            if (!task.getOwner().equals((Converter.getOwnerUrl(dbUser)))
                    || task.getCreated_datetime() == null) {
                continue;
            }
            // 把今天刚刚完成的任务(getIsFinish()==true)添加到显示List并标记
            if (task.getIsFinish()) {
                Date finished_datetime = TimeUtil.formatGMTDateStr(task.getFinished_datetime());
                if (finished_datetime != null) {
                    if (finished_datetime.getDate() == today.getDate()
                            && finished_datetime.getMonth() == today.getMonth()
                            && finished_datetime.getYear() == today.getYear()) {
                        tasks.add(task);
                        LogUtil.i("add task, because task is finished today");
                    }
                }
                continue; // 只要是完成了的，之后都不再判断
            }
            //begin_datetime < today <= end_datetime
            //end_datetime   < today 顺延
            if (task.getBegin_datetime() != null && task.getEnd_datetime() != null) {
                Date begin_datetime = TimeUtil.formatGMTDateStr(task.getBegin_datetime());
                Date end_datetime = TimeUtil.formatGMTDateStr(task.getEnd_datetime());
                if (begin_datetime != null && end_datetime != null) {
                    if ((TimeUtil.isDateEarlier(begin_datetime, new Date(today.getTime() + Constants.DAY_MILLI_SECONDS))
                            && TimeUtil.isDateEarlier(today, end_datetime))) {
                        tasks.add(task);
                        LogUtil.i("add task, because begin <= today <= end");
                    } else if (TimeUtil.isDateEarlier(begin_datetime, new Date(today.getTime() + Constants.DAY_MILLI_SECONDS))
                            && task.getIs_all_day()) {
                        tasks.add(task);
                        LogUtil.i("add task, because of delay");
                    }
                }
                continue;
            }
            // 把顺延的添加到显示List并标记
            Date created_datetime = TimeUtil.formatGMTDateStr(task.getCreated_datetime());
            if (created_datetime != null) {
                if (TimeUtil.isDateEarlier(created_datetime, new Date(today.getTime() + Constants.DAY_MILLI_SECONDS))) {
                    if (task.getIs_all_day()) {
                        tasks.add(task);
                        LogUtil.i("add task, because of delay");
                    }
                }
            }

        }

        return tasks;
    }

    public static ArrayList<DBTask> sort(ArrayList<DBTask> taskArrayList) {
        ArrayList<DBTask> sortedDBTaskList = new ArrayList<>();
        if (taskArrayList == null || taskArrayList.size() <= 0) {
            return sortedDBTaskList;
        }
        ArrayList<DBTask> label_0_DBTaskList = new ArrayList<>();
        ArrayList<DBTask> label_1_DBTaskList = new ArrayList<>();
        ArrayList<DBTask> label_2_DBTaskList = new ArrayList<>();
        ArrayList<DBTask> label_3_DBTaskList = new ArrayList<>();
        ArrayList<DBTask> finished_DBTaskList = new ArrayList<>();

        for (DBTask dbTask : taskArrayList) {
            if (dbTask.getIsFinish()) {
                finished_DBTaskList.add(dbTask);
                continue;
            }
            switch (dbTask.getLabel()) {
                case DBTask.LABEL_IMPORTANT_URGENT:
                    label_0_DBTaskList.add(dbTask);
                    break;
                case DBTask.LABEL_IMPORTANT_NOT_URGENT:
                    label_1_DBTaskList.add(dbTask);
                    break;
                case DBTask.LABEL_NOT_IMPORTANT_URGENT:
                    label_2_DBTaskList.add(dbTask);
                    break;
                case DBTask.LABEL_NOT_IMPORTANT_NOT_URGENT:
                    label_3_DBTaskList.add(dbTask);
                    break;
            }
        }
        mergeSort2List(label_0_DBTaskList, sortedDBTaskList);
        mergeSort2List(label_1_DBTaskList, sortedDBTaskList);
        mergeSort2List(label_2_DBTaskList, sortedDBTaskList);
        mergeSort2List(label_3_DBTaskList, sortedDBTaskList);
        mergeSort2List(finished_DBTaskList, sortedDBTaskList);

        return sortedDBTaskList;
    }

    private static void reverse(ArrayList<DBTask> arr, int i, int j) {
        while(i < j) {
            DBTask temp = arr.get(i);
            arr.set(i++, arr.get(j));
            arr.set(j--, temp);
        }
    }

    // swap [bias, bias+headSize) and [bias+headSize, bias+headSize+endSize)
    private static void swapAdjacentBlocks(ArrayList<DBTask> arr, int bias, int oneSize, int anotherSize) {
        reverse(arr, bias, bias + oneSize - 1);
        reverse(arr, bias + oneSize, bias + oneSize + anotherSize - 1);
        reverse(arr, bias, bias + oneSize + anotherSize - 1);
    }

    private static void inplaceMerge(ArrayList<DBTask> arr, int l, int mid, int r) {
        int i = l;     // 指示左侧有序串
        int j = mid + 1; // 指示右侧有序串
        while(i < j && j <= r) { //原地归并结束的条件。
            while(i < j && isValid(arr, i, j)) {
                i++;
            }
            int index = j;
            while(j <= r && isValid(arr, j, i)) {
                j++;
            }
            swapAdjacentBlocks(arr, i, index-i, j-index);
            i += (j-index);
        }
    }

    private static boolean isValid(ArrayList<DBTask> arr, int i, int j) {
        Date date_i = TimeUtil.formatGMTDateStr(arr.get(i).getCreated_datetime());
        Date date_j = TimeUtil.formatGMTDateStr(arr.get(j).getCreated_datetime());
        return (date_i != null ? date_i.getTime() : 0) <= (date_j != null ? date_j.getTime() : 0);
    }

    private static void mergeSort(ArrayList<DBTask> arr, int l, int r) {
        if(l < r) {
            int mid = (l + r) / 2;
            mergeSort(arr, l, mid);
            mergeSort(arr, mid + 1, r);
            inplaceMerge(arr, l, mid, r);
        }
    }

    private static void mergeSort2List(ArrayList<DBTask> taskArrayList, ArrayList<DBTask> result) {
        if (taskArrayList == null || taskArrayList.size() <= 0) {
            return;
        }
        mergeSort(taskArrayList, 0, taskArrayList.size()-1);
        result.addAll(taskArrayList);
    }

}
