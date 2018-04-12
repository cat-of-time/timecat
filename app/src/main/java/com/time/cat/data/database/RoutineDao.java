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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.time.cat.TimeCatApp;
import com.time.cat.data.model.APImodel.Routine;
import com.time.cat.data.model.Converter;
import com.time.cat.data.model.DBmodel.DBRoutine;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.data.model.events.PersistenceEvents;
import com.time.cat.util.override.LogUtil;
import com.time.cat.util.string.TimeUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoutineDao extends GenericDao<DBRoutine, Long> {

    public static final String TAG = "RoutineDao";

    public RoutineDao(DatabaseHelper db) {
        super(db);
    }

    @Override
    public Dao<DBRoutine, Long> getConcreteDao() {
        try {
            return dbHelper.getRoutinesDao();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating routines dao", e);
        }
    }

    @Override
    public void fireEvent() {
        TimeCatApp.eventBus().post(PersistenceEvents.ROUTINE_EVENT);
    }

    @Override
    public List<DBRoutine> findAll() {
        try {
            return dao.queryBuilder().query();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding models", e);
        }
    }

    public List<DBRoutine> findAllForActiveUser() {
        return findAll(DB.users().getActive());
    }

    public List<DBRoutine> findAll(DBUser p) {
        return findAll(p.id());
    }

    public List<DBRoutine> findAll(Long userId) {
        try {
            return dao.queryBuilder().where().eq(DBRoutine.COLUMN_USER, userId).query();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding models", e);
        }
    }

    public List<DBRoutine> findBetween(Date start_date, Date end_date) {
        List<DBRoutine> dbRoutineList = findAllForActiveUser();
        List<DBRoutine> result = new ArrayList<>();
        for (DBRoutine dbRoutine : dbRoutineList) {
            Date Created_datetime = TimeUtil.formatGMTDateStr(dbRoutine.getCreated_datetime());
            if (TimeUtil.isDateEarlier(start_date, Created_datetime) && TimeUtil.isDateEarlier(Created_datetime, end_date)) {
                result.add(dbRoutine);
            }
        }
        return result;
    }

    public DBRoutine findByUserAndName(String name, DBUser p) {
        try {
            QueryBuilder<DBRoutine, Long> qb = dao.queryBuilder();
            Where w = qb.where();
            w.and(w.eq(DBRoutine.COLUMN_NAME, name), w.eq(DBRoutine.COLUMN_USER, p));
            qb.setWhere(w);
            return qb.queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding routine", e);
        }
    }

    public List<DBRoutine> getEventsAtReboot() {
        List<DBRoutine> list = new ArrayList<>();
        try {
            list = dao.queryBuilder().where()
                    .not(dao.queryBuilder().where().eq(DBRoutine.COLUMN_REMINDER_1_Minutes, -1))
                    .not(dao.queryBuilder().where().eq(DBRoutine.COLUMN_BEGIN_DATETIME, null)).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateAndFireEvent(DBRoutine dbRoutine) {
        try {
            update(dbRoutine);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TimeCatApp.eventBus().post(new PersistenceEvents.RoutineUpdateEvent(dbRoutine));
    }

    public void deleteAndFireEvent(DBRoutine routine) {
        try {
            delete(routine);
            TimeCatApp.eventBus().post(new PersistenceEvents.RoutineDeleteEvent(routine));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void safeSaveRoutine(Routine routine) {
        LogUtil.i(TAG, "返回的任务信息 --> " + routine.toString());
        //保存用户信息到本地
        DBRoutine dbRoutine = Converter.toDBRoutine(routine);
        List<DBRoutine> existing = null;
        try {
            existing = DB.routines().queryForEq(DBRoutine.COLUMN_URL, routine.getUrl());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbRoutine.setId(id);
            try {
                DB.routines().update(dbRoutine);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            LogUtil.i(TAG, "更新任务信息 --> updateAndFireEvent -- > " + dbRoutine.toString());
        } else {
            DB.routines().save(dbRoutine);
            LogUtil.i(TAG, "保存任务信息 --> saveAndFireEvent -- > " + dbRoutine.toString());
        }
    }

    public void safeSaveRoutineAndFireEvent(Routine routine) {
        LogUtil.i(TAG, "返回的任务信息 --> " + routine.toString());
        //保存用户信息到本地
        DBRoutine dbRoutine = Converter.toDBRoutine(routine);
        List<DBRoutine> existing = null;
        try {
            existing = DB.routines().queryForEq(DBRoutine.COLUMN_URL, routine.getUrl());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbRoutine.setId(id);
            DB.routines().updateAndFireEvent(dbRoutine);
            LogUtil.i(TAG, "更新任务信息 --> updateAndFireEvent -- > " + dbRoutine.toString());
        } else {
            DB.routines().saveAndFireEvent(dbRoutine);
            LogUtil.i(TAG, "保存任务信息 --> saveAndFireEvent -- > " + dbRoutine.toString());
        }
    }

    public void safeSaveDBRoutine(DBRoutine dbRoutine) {
        List<DBRoutine> existing = null;
        try {
            existing = DB.routines().queryForEq(DBRoutine.COLUMN_CREATED_DATETIME, dbRoutine.getCreated_datetime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbRoutine.setId(id);
            try {
                DB.routines().update(dbRoutine);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            LogUtil.i(TAG, "更新任务信息 --> updateAndFireEvent -- > " + dbRoutine.toString());
        } else {
            DB.routines().save(dbRoutine);
            LogUtil.i(TAG, "保存任务信息 --> saveAndFireEvent -- > " + dbRoutine.toString());
        }
    }

    public void safeSaveDBRoutineAndFireEvent(DBRoutine dbRoutine) {
        List<DBRoutine> existing = null;
        try {
            existing = DB.routines().queryForEq(DBRoutine.COLUMN_CREATED_DATETIME, dbRoutine.getCreated_datetime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbRoutine.setId(id);
            DB.routines().updateAndFireEvent(dbRoutine);
            LogUtil.i(TAG, "更新任务信息 --> updateAndFireEvent -- > " + dbRoutine.toString());
        } else {
            DB.routines().saveAndFireEvent(dbRoutine);
            LogUtil.i(TAG, "保存任务信息 --> saveAndFireEvent -- > " + dbRoutine.toString());
        }
    }


    public static ArrayList<DBRoutine> filterByTag(ArrayList<DBRoutine> dbTasks, ArrayList<String> tags) {
        ArrayList<DBRoutine> dbTaskArrayList = new ArrayList<>();
        for (DBRoutine dbTask : dbTasks) {
            for (String tag : dbTask.getTags()) {
                if (tags.toString().contains(tag)) {
                    dbTaskArrayList.add(dbTask);
                }
            }
        }
        return dbTaskArrayList;
    }

    public static ArrayList<DBRoutine> filter(ArrayList<DBRoutine> dbRoutines) {
        // TODO
        return dbRoutines;
    }

    public static ArrayList<DBRoutine> DBRoutineFilter(List<DBRoutine> routineList, Date currentDate) {
        ArrayList<DBRoutine> dbRoutines = new ArrayList<>();
        if (routineList == null || routineList.size() <= 0) {
            return dbRoutines;
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
        for (DBRoutine dbRoutine : routineList) {
            if (!dbRoutine.getOwner().equals((Converter.getOwnerUrl(dbUser)))) {
                continue;
            }
            boolean hasAddedTask = false;
            // 把今天刚刚完成的任务(getIsFinish()==true)添加到显示List并标记
            if (dbRoutine.getIsFinish()) {
                Date finished_datetime = TimeUtil.formatGMTDateStr(dbRoutine.getFinished_datetime());
                if (finished_datetime != null) {
                    if (finished_datetime.getDate() == today.getDate() && finished_datetime.getMonth() == today.getMonth() && finished_datetime.getYear() == today.getYear()) {
                        dbRoutines.add(dbRoutine);
//                        LogUtil.i("add task, because task is finished today");
                    }
                }
                hasAddedTask = true; // 只要是完成了的，之后都不再判断
            }
//            LogUtil.e(task.getIs_all_day() + task.toString());
            if (!dbRoutine.getIs_all_day() && !hasAddedTask && dbRoutine.getBegin_datetime() != null && dbRoutine.getEnd_datetime() != null) {
                Date begin_datetime = TimeUtil.formatGMTDateStr(dbRoutine.getBegin_datetime());
                Date end_datetime = TimeUtil.formatGMTDateStr(dbRoutine.getEnd_datetime());
                if (begin_datetime != null && end_datetime != null) {
                    if (TimeUtil.isDateEarlier(begin_datetime, today) && TimeUtil.isDateEarlier(today, end_datetime)) {
                        dbRoutines.add(dbRoutine);
                        hasAddedTask = true;
//                        LogUtil.i("add task, because begin <= today <= end");
                    }
                }
            }
            // 把顺延的添加到显示List并标记
            if (dbRoutine.getCreated_datetime() != null || dbRoutine.getCreated_datetime() != "null") {
                Date created_datetime = TimeUtil.formatGMTDateStr(dbRoutine.getCreated_datetime());
                if (!hasAddedTask && created_datetime != null) {
                    long during = today.getTime() - created_datetime.getTime();
                    if (TimeUtil.isDateEarlier(created_datetime, today)) {
                        if (dbRoutine.getIs_all_day()) {
                            dbRoutines.add(dbRoutine);
//                            LogUtil.i("add task, because of delay");
                        }
                    }
                }
            }
        }

        return dbRoutines;
    }

    public static ArrayList<DBRoutine> sort(ArrayList<DBRoutine> taskArrayList) {
        ArrayList<DBRoutine> sortedDBRoutineList = new ArrayList<>();
        if (taskArrayList == null || taskArrayList.size() <= 0) {
            return sortedDBRoutineList;
        }
        ArrayList<DBRoutine> label_0_DBRoutineList = new ArrayList<>();
        ArrayList<DBRoutine> label_1_DBRoutineList = new ArrayList<>();
        ArrayList<DBRoutine> label_2_DBRoutineList = new ArrayList<>();
        ArrayList<DBRoutine> label_3_DBRoutineList = new ArrayList<>();
        ArrayList<DBRoutine> finished_DBRoutineList = new ArrayList<>();

        for (DBRoutine dbRoutine : taskArrayList) {
            if (dbRoutine.getIsFinish()) {
                finished_DBRoutineList.add(dbRoutine);
                continue;
            }
            switch (dbRoutine.getLabel()) {
                case DBRoutine.LABEL_IMPORTANT_URGENT:
                    label_0_DBRoutineList.add(dbRoutine);
                    break;
                case DBRoutine.LABEL_IMPORTANT_NOT_URGENT:
                    label_1_DBRoutineList.add(dbRoutine);
                    break;
                case DBRoutine.LABEL_NOT_IMPORTANT_URGENT:
                    label_2_DBRoutineList.add(dbRoutine);
                    break;
                case DBRoutine.LABEL_NOT_IMPORTANT_NOT_URGENT:
                    label_3_DBRoutineList.add(dbRoutine);
                    break;
            }
        }
        mergeSort2List(label_0_DBRoutineList, sortedDBRoutineList);
        mergeSort2List(label_1_DBRoutineList, sortedDBRoutineList);
        mergeSort2List(label_2_DBRoutineList, sortedDBRoutineList);
        mergeSort2List(label_3_DBRoutineList, sortedDBRoutineList);
        mergeSort2List(finished_DBRoutineList, sortedDBRoutineList);

        return sortedDBRoutineList;
    }

    private static void reverse(ArrayList<DBRoutine> arr, int i, int j) {
        while (i < j) {
            DBRoutine temp = arr.get(i);
            arr.set(i++, arr.get(j));
            arr.set(j--, temp);
        }
    }

    // swap [bias, bias+headSize) and [bias+headSize, bias+headSize+endSize)
    private static void swapAdjacentBlocks(ArrayList<DBRoutine> arr, int bias, int oneSize, int anotherSize) {
        reverse(arr, bias, bias + oneSize - 1);
        reverse(arr, bias + oneSize, bias + oneSize + anotherSize - 1);
        reverse(arr, bias, bias + oneSize + anotherSize - 1);
    }

    private static void inplaceMerge(ArrayList<DBRoutine> arr, int l, int mid, int r) {
        int i = l;     // 指示左侧有序串
        int j = mid + 1; // 指示右侧有序串
        while (i < j && j <= r) { //原地归并结束的条件。
            while (i < j && isValid(arr, i, j)) {
                i++;
            }
            int index = j;
            while (j <= r && isValid(arr, j, i)) {
                j++;
            }
            swapAdjacentBlocks(arr, i, index - i, j - index);
            i += (j - index);
        }
    }

    private static boolean isValid(ArrayList<DBRoutine> arr, int i, int j) {
        Date date_i = TimeUtil.formatGMTDateStr(arr.get(i).getCreated_datetime());
        Date date_j = TimeUtil.formatGMTDateStr(arr.get(j).getCreated_datetime());
        return (date_i != null ? date_i.getTime() : 0) <= (date_j != null ? date_j.getTime() : 0);
    }

    private static void mergeSort(ArrayList<DBRoutine> arr, int l, int r) {
        if (l < r) {
            int mid = (l + r) / 2;
            mergeSort(arr, l, mid);
            mergeSort(arr, mid + 1, r);
            inplaceMerge(arr, l, mid, r);
        }
    }

    private static void mergeSort2List(ArrayList<DBRoutine> taskArrayList, ArrayList<DBRoutine> result) {
        if (taskArrayList == null || taskArrayList.size() <= 0) {
            return;
        }
        mergeSort(taskArrayList, 0, taskArrayList.size() - 1);
        result.addAll(taskArrayList);
    }


}
