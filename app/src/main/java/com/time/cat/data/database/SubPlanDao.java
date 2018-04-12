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
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.model.APImodel.Plan;
import com.time.cat.data.model.Converter;
import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.data.model.events.PersistenceEvents;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PlanDao extends GenericDao<DBPlan, Long> {
    public static final String TAG = "PlanDao";

    public PlanDao(DatabaseHelper db) {
        super(db);
    }

    @Override
    public Dao<DBPlan, Long> getConcreteDao() {
        try {
            return dbHelper.getPlanDao();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating users dao", e);
        }
    }

    @Override
    public void saveAndFireEvent(DBPlan u) {

        Object event = u.getId() <= 0 ? new PersistenceEvents.PlanCreateEvent(u) : new PersistenceEvents.PlanUpdateEvent(u);
        save(u);
        TimeCatApp.eventBus().post(event);

    }

    public void createOrUpdateAndFireEvent(DBPlan u) throws SQLException {

        Object event = u.getId() <= 0 ? new PersistenceEvents.PlanCreateEvent(u) : new PersistenceEvents.PlanUpdateEvent(u);
        createOrUpdate(u);
        TimeCatApp.eventBus().post(event);

    }

    public void updateAndFireEvent(DBPlan u) {
        Object event = new PersistenceEvents.PlanUpdateEvent(u);
        try {
            update(u);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TimeCatApp.eventBus().post(event);
    }

    public void safeSavePlanAndFireEvent(Plan note) {
        Log.i(TAG, "返回的任务信息 --> " + note.toString());
        //保存用户信息到本地
        DBPlan dbPlan = Converter.toDBPlan(note);
        List<DBPlan> existing = null;
        try {
            existing = DB.notes().queryForEq(DBPlan.COLUMN_URL, dbPlan.getUrl());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbPlan.setId(id);
            int color = existing.get(0).getColor();
            dbPlan.setColor(color);
            DB.notes().updateAndFireEvent(dbPlan);
            Log.i(TAG, "更新笔记信息 --> updateAndFireEvent -- > " + dbPlan.toString());
        } else {
            List<Integer> CardStackViewDataList = new ArrayList<>();
            int[] CardStackViewData = TimeCatApp.getInstance().getResources().getIntArray(R.array.card_stack_view_data);
            for (int aCardStackViewData : CardStackViewData) {
                CardStackViewDataList.add(aCardStackViewData);
            }
            Random random = new Random();
            int randomColor = random.nextInt(CardStackViewDataList.size());
            dbPlan.setColor(CardStackViewDataList.get(randomColor));
            DB.notes().saveAndFireEvent(dbPlan);
            Log.i(TAG, "保存笔记信息 --> saveAndFireEvent -- > " + dbPlan.toString());
        }
    }


    public void safeSavePlan(Plan note) {
        Log.i(TAG, "返回的任务信息 --> " + note.toString());
        //保存用户信息到本地
        DBPlan dbPlan = Converter.toDBPlan(note);
        List<DBPlan> existing = null;
        try {
            existing = DB.notes().queryForEq(DBPlan.COLUMN_URL, dbPlan.getUrl());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbPlan.setId(id);
            int color = existing.get(0).getColor();
            dbPlan.setColor(color);
            try {
                DB.notes().update(dbPlan);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "更新笔记信息 --> update -- > " + dbPlan.toString());
        } else {
            List<Integer> CardStackViewDataList = new ArrayList<>();
            int[] CardStackViewData = TimeCatApp.getInstance().getResources().getIntArray(R.array.card_stack_view_data);
            for (int aCardStackViewData : CardStackViewData) {
                CardStackViewDataList.add(aCardStackViewData);
            }
            Random random = new Random();
            int randomColor = random.nextInt(CardStackViewDataList.size());
            dbPlan.setColor(CardStackViewDataList.get(randomColor));
            DB.notes().save(dbPlan);
            Log.i(TAG, "保存笔记信息 --> save -- > " + dbPlan.toString());
        }
    }

    public void safeSaveDBPlan(DBPlan dbPlan) {
        List<DBPlan> existing = null;
        try {
            existing = DB.notes().queryForEq(DBPlan.COLUMN_CREATED_DATETIME, dbPlan.getCreated_datetime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbPlan.setId(id);
            int color = existing.get(0).getColor();
            dbPlan.setColor(color);
            DB.notes().updateAndFireEvent(dbPlan);
            Log.i(TAG, "更新笔记信息 --> updateAndFireEvent -- > " + dbPlan.toString());
        } else {
            DB.notes().saveAndFireEvent(dbPlan);
            Log.i(TAG, "保存笔记信息 --> saveAndFireEvent -- > " + dbPlan.toString());
        }
    }

    public void safeSaveDBPlanAndFireEvent(DBPlan dbPlan) {
        List<DBPlan> existing = null;
        try {
            existing = DB.notes().queryForEq(DBPlan.COLUMN_CREATED_DATETIME, dbPlan.getCreated_datetime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbPlan.setId(id);
            int color = existing.get(0).getColor();
            dbPlan.setColor(color);
            DB.notes().updateAndFireEvent(dbPlan);
            Log.i(TAG, "更新笔记信息 --> updateAndFireEvent -- > " + dbPlan.toString());
        } else {
            DB.notes().saveAndFireEvent(dbPlan);
            Log.i(TAG, "保存笔记信息 --> saveAndFireEvent -- > " + dbPlan.toString());
        }
    }


    public void updateActiveUserAndFireEvent(DBPlan activeDBPlan, Plan user) {
//        Object event = new PersistenceEvents.UserUpdateEvent(activeDBPlan);
//        try {
//            update(Converter.toActiveDBPlan(activeDBPlan, user));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        TimeCatApp.eventBus().post(event);
    }

    /// Mange active user through preferences

    public void removeCascade(DBPlan u) {
        // remove all data
//        removeAllStuff(u);
        // remove note
        DB.notes().remove(u);

    }

}
