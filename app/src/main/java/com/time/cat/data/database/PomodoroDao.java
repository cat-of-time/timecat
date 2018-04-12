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
import com.time.cat.data.model.APImodel.Plan;
import com.time.cat.data.model.APImodel.Pomodoro;
import com.time.cat.data.model.Converter;
import com.time.cat.data.model.DBmodel.DBPomodoro;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.data.model.events.PersistenceEvents;
import com.time.cat.util.string.TimeUtil;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PomodoroDao extends GenericDao<DBPomodoro, Long> {
    public static final String TAG = "PomodoroDao";

    public PomodoroDao(DatabaseHelper db) {
        super(db);
    }

    @Override
    public Dao<DBPomodoro, Long> getConcreteDao() {
        try {
            return dbHelper.getPomodoroDao();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating users dao", e);
        }
    }

    public List<DBPomodoro> findAllForActiveUser() {
        return findAll(DB.users().getActive());
    }

    public List<DBPomodoro> findAll(DBUser p) {
        return findAllByUserId(p.id());
    }

    public List<DBPomodoro> findAllByUserId(Long userId) {
        try {
            return dao.queryBuilder().orderBy(DBPomodoro.COLUMN_CREATED_DATETIME, true)
                    .where().eq(DBPomodoro.COLUMN_USER, userId).query();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding models", e);
        }
    }

    public List<DBPomodoro> findBetween(Date start_date, Date end_date) {
        List<DBPomodoro> dbPomodoroList = findAllForActiveUser();
        List<DBPomodoro> result = new ArrayList<>();
        for (DBPomodoro dbPomodoro : dbPomodoroList) {
            Date Created_datetime = TimeUtil.formatGMTDateStr(dbPomodoro.getCreated_datetime());
            if (TimeUtil.isDateEarlier(start_date, Created_datetime) && TimeUtil.isDateEarlier(Created_datetime, end_date)) {
                result.add(dbPomodoro);
            }
        }
        return result;
    }

    public List<DBPomodoro> findFinishedBetween(Date start_date, Date end_date) {
        List<DBPomodoro> dbPomodoroList = findAllForActiveUser();
        List<DBPomodoro> result = new ArrayList<>();
        for (DBPomodoro dbPomodoro : dbPomodoroList) {
            Date Created_datetime = TimeUtil.formatGMTDateStr(dbPomodoro.getCreated_datetime());
            if (TimeUtil.isDateEarlier(start_date, Created_datetime) && TimeUtil.isDateEarlier(Created_datetime, end_date)) {
                if (dbPomodoro.getEnd_datetime() != null) {
                    result.add(dbPomodoro);
                }
            }
        }
        return result;
    }

    public List<DBPomodoro> findUnfinishedBetween(Date start_date, Date end_date) {
        List<DBPomodoro> dbPomodoroList = findAllForActiveUser();
        List<DBPomodoro> result = new ArrayList<>();
        for (DBPomodoro dbPomodoro : dbPomodoroList) {
            Date Created_datetime = TimeUtil.formatGMTDateStr(dbPomodoro.getCreated_datetime());
            if (TimeUtil.isDateEarlier(start_date, Created_datetime) && TimeUtil.isDateEarlier(Created_datetime, end_date)) {
                if (dbPomodoro.getEnd_datetime() == null) {
                    result.add(dbPomodoro);
                }
            }
        }
        return result;
    }

    public List<DBPomodoro> findTotalUnfinished() {
        List<DBPomodoro> dbPomodoroList = findAllForActiveUser();
        List<DBPomodoro> result = new ArrayList<>();
        for (DBPomodoro dbPomodoro : dbPomodoroList) {
            if (dbPomodoro.getEnd_datetime() == null) {
                result.add(dbPomodoro);
            }
        }
        return result;
    }

    public int getTodayWorkTime() {
        DateTime today = new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
        DateTime tomorrow = today.plusDays(1);
        List<DBPomodoro> pomodoroList = findFinishedBetween(today.toDate(), tomorrow.toDate());
        int todayWorkTime = 0;
        for (DBPomodoro dbPomodoro : pomodoroList) {
            if (dbPomodoro.getDuration() > 0) todayWorkTime += dbPomodoro.getDuration();
        }
        return todayWorkTime;
    }

    public int getTotalWorkTime() {
        List<DBPomodoro> pomodoroList = findAllForActiveUser();
        int todayWorkTime = 0;
        for (DBPomodoro dbPomodoro : pomodoroList) {
            if (dbPomodoro.getEnd_datetime() != null && dbPomodoro.getDuration() > 0) {
                todayWorkTime += dbPomodoro.getDuration();
            }
        }
        return todayWorkTime;
    }

    @Override
    public void saveAndFireEvent(DBPomodoro u) {

        Object event = u.getId() <= 0 ? new PersistenceEvents.PomodoroCreateEvent(u) : new PersistenceEvents.PomodoroUpdateEvent(u);
        save(u);
        TimeCatApp.eventBus().post(event);

    }

    public void deleteAndFireEvent(DBPomodoro routine) {
        try {
            delete(routine);
            TimeCatApp.eventBus().post(new PersistenceEvents.PomodoroDeleteEvent());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void createOrUpdateAndFireEvent(DBPomodoro u) throws SQLException {

        Object event = u.getId() <= 0 ? new PersistenceEvents.PomodoroCreateEvent(u) : new PersistenceEvents.PomodoroUpdateEvent(u);
        createOrUpdate(u);
        TimeCatApp.eventBus().post(event);

    }

    public void updateAndFireEvent(DBPomodoro u) {
        Object event = new PersistenceEvents.PomodoroUpdateEvent(u);
        try {
            update(u);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TimeCatApp.eventBus().post(event);
    }

    public void safeSavePomodoroAndFireEvent(Pomodoro pomodoro) {
        Log.i(TAG, "返回的任务信息 --> " + pomodoro.toString());
        //保存用户信息到本地
        DBPomodoro dbPomodoro = Converter.toDBPomodoro(pomodoro);
        List<DBPomodoro> existing = null;
        try {
            existing = DB.pomodoros().queryForEq(DBPomodoro.COLUMN_URL, dbPomodoro.getUrl());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbPomodoro.setId(id);
            DB.pomodoros().updateAndFireEvent(dbPomodoro);
            Log.i(TAG, "更新子计划信息 --> updateAndFireEvent -- > " + dbPomodoro.toString());
        } else {
            DB.pomodoros().saveAndFireEvent(dbPomodoro);
            Log.i(TAG, "保存子计划信息 --> saveAndFireEvent -- > " + dbPomodoro.toString());
        }
    }

    public void safeSavePomodoro(Pomodoro pomodoro) {
        Log.i(TAG, "返回的任务信息 --> " + pomodoro.toString());
        //保存用户信息到本地
        DBPomodoro dbPomodoro = Converter.toDBPomodoro(pomodoro);
        List<DBPomodoro> existing = null;
        try {
            existing = DB.pomodoros().queryForEq(DBPomodoro.COLUMN_URL, dbPomodoro.getUrl());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbPomodoro.setId(id);
            try {
                DB.pomodoros().update(dbPomodoro);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "更新子计划信息 --> update -- > " + dbPomodoro.toString());
        } else {
            DB.pomodoros().save(dbPomodoro);
            Log.i(TAG, "保存子计划信息 --> save -- > " + dbPomodoro.toString());
        }
    }

    public void safeSaveDBPomodoro(DBPomodoro dbPomodoro) {
        List<DBPomodoro> existing = null;
        try {
            existing = DB.pomodoros().queryForEq(DBPomodoro.COLUMN_CREATED_DATETIME, dbPomodoro.getCreated_datetime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbPomodoro.setId(id);
            DB.pomodoros().updateAndFireEvent(dbPomodoro);
            Log.i(TAG, "更新子计划信息 --> updateAndFireEvent -- > " + dbPomodoro.toString());
        } else {
            DB.pomodoros().saveAndFireEvent(dbPomodoro);
            Log.i(TAG, "保存子计划信息 --> saveAndFireEvent -- > " + dbPomodoro.toString());
        }
    }

    /**
     * query by create_datetime, supposed dbPomodoro has create_datetime
     * @param dbPomodoro create_datetime != null
     * @return dbPomodoro's id
     */
    public long findId(DBPomodoro dbPomodoro) {
        List<DBPomodoro> existing = null;
        try {
            existing = DB.pomodoros().queryForEq(DBPomodoro.COLUMN_CREATED_DATETIME, dbPomodoro.getCreated_datetime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbPomodoro.setId(id);
            return id;
        }
        return (long) 0.0;
    }

    public void safeSaveDBPomodoroAndFireEvent(DBPomodoro dbPomodoro) {
        List<DBPomodoro> existing = null;
        try {
            existing = DB.pomodoros().queryForEq(DBPomodoro.COLUMN_CREATED_DATETIME, dbPomodoro.getCreated_datetime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbPomodoro.setId(id);
            DB.pomodoros().updateAndFireEvent(dbPomodoro);
            Log.i(TAG, "更新子计划信息 --> updateAndFireEvent -- > " + dbPomodoro.toString());
        } else {
            DB.pomodoros().saveAndFireEvent(dbPomodoro);
            Log.i(TAG, "保存子计划信息 --> saveAndFireEvent -- > " + dbPomodoro.toString());
        }
    }


    public void updateActiveUserAndFireEvent(DBPomodoro activeDBPomodoro, Plan user) {
//        Object event = new PersistenceEvents.UserUpdateEvent(activeDBPomodoro);
//        try {
//            update(Converter.toActiveDBPomodoro(activeDBPomodoro, user));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        TimeCatApp.eventBus().post(event);
    }

    /**
     * 通过UserId获取所有的task
     *
     * @param userId user id
     *
     * @return List<DBPomodoro>
     */
    public List<DBPomodoro> listByUserId(int userId) {
        try {
            return DB.pomodoros().queryBuilder().where().eq(DBPomodoro.COLUMN_USER, userId).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
