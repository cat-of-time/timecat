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

package com.time.cat.database;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.time.cat.TimeCatApp;
import com.time.cat.events.PersistenceEvents;
import com.time.cat.mvp.model.DBmodel.DBTask;
import com.time.cat.mvp.model.DBmodel.DBTaskItem;
import com.time.cat.mvp.model.DBmodel.DBUser;
import com.time.cat.mvp.model.Task;
import com.time.cat.util.model.ModelUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

public class ScheduleDao extends GenericDao<DBTask, Long> {
    private static final String TAG = "ScheduleDao";

    public ScheduleDao(DatabaseHelper db) {
        super(db);
    }

    public List<DBTask> findAllForActiveUser() {
        return findAll(DB.users().getActive());
    }

    public List<DBTask> findAll(DBUser p) {
        return findAll(p.id());
    }


    public List<DBTask> findAll(Long userId) {
        try {
            return dao.queryBuilder().where().eq(DBTask.COLUMN_USER, userId).query();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding models", e);
        }
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

    public void safeSaveTask(Task task) {
        Log.i(TAG, "返回的任务信息 --> " + task.toString());
        //保存用户信息到本地
        DBTask dbTask = ModelUtil.toDBTask(task);
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
        DBTask dbTask = ModelUtil.toDBTask(task);
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

    public void deleteCascade(final DBTask s, boolean fireEvent) {
        DB.transaction(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for (DBTaskItem i : s.items()) {
                    DB.scheduleItems().deleteCascade(i);
                }
                DB.schedules().remove(s);
                return null;
            }
        });

        if (fireEvent) {
            fireEvent();
        }
    }

    public List<DBTask> findHourly() {
        return findBy(DBTask.COLUMN_TYPE, DBTask.SCHEDULE_TYPE_EVERYHOUR);
    }

}
