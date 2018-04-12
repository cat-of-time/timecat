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
import com.time.cat.data.model.APImodel.SubPlan;
import com.time.cat.data.model.Converter;
import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.data.model.DBmodel.DBSubPlan;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.data.model.events.PersistenceEvents;
import com.time.cat.util.string.TimeUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SubPlanDao extends GenericDao<DBSubPlan, Long> {
    public static final String TAG = "PlanDao";

    public SubPlanDao(DatabaseHelper db) {
        super(db);
    }

    @Override
    public Dao<DBSubPlan, Long> getConcreteDao() {
        try {
            return dbHelper.getSubPlanDao();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating users dao", e);
        }
    }

    public List<DBSubPlan> findAllForActiveUser() {
        return findAll(DB.users().getActive());
    }

    public List<DBSubPlan> findAll(DBUser p) {
        return findAllByUserId(p.id());
    }

    public List<DBSubPlan> findAllByUserId(Long userId) {
        try {
            return dao.queryBuilder().orderBy(DBSubPlan.COLUMN_CREATED_DATETIME, true)
                    .where().eq(DBSubPlan.COLUMN_USER, userId).query();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding models", e);
        }
    }

    public List<DBSubPlan> findAll(DBPlan p) {
        return findAllByPlanId(p.getId());
    }

    public List<DBSubPlan> findAllByPlanId(Long planId) {
        try {
            return dao.queryBuilder().orderBy(DBSubPlan.COLUMN_CREATED_DATETIME, true)
                    .where().eq(DBSubPlan.COLUMN_PLAN, planId).query();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding models", e);
        }
    }

    public List<DBSubPlan> findBetween(Date start_date, Date end_date) {
        List<DBSubPlan> dbSubPlanList = findAllForActiveUser();
        List<DBSubPlan> result = new ArrayList<>();
        for (DBSubPlan dbSubPlan : dbSubPlanList) {
            Date Created_datetime = TimeUtil.formatGMTDateStr(dbSubPlan.getCreated_datetime());
            if (TimeUtil.isDateEarlier(start_date, Created_datetime) && TimeUtil.isDateEarlier(Created_datetime, end_date)) {
                result.add(dbSubPlan);
            }
        }
        return result;
    }

    @Override
    public void saveAndFireEvent(DBSubPlan u) {

        Object event = u.getId() <= 0 ? new PersistenceEvents.SubPlanCreateEvent(u) : new PersistenceEvents.SubPlanUpdateEvent(u);
        save(u);
        TimeCatApp.eventBus().post(event);

    }

    public void deleteAndFireEvent(DBSubPlan routine) {
        try {
            delete(routine);
            TimeCatApp.eventBus().post(new PersistenceEvents.SubPlanDeleteEvent());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void createOrUpdateAndFireEvent(DBSubPlan u) throws SQLException {

        Object event = u.getId() <= 0 ? new PersistenceEvents.SubPlanCreateEvent(u) : new PersistenceEvents.SubPlanUpdateEvent(u);
        createOrUpdate(u);
        TimeCatApp.eventBus().post(event);

    }

    public void updateAndFireEvent(DBSubPlan u) {
        Object event = new PersistenceEvents.SubPlanUpdateEvent(u);
        try {
            update(u);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TimeCatApp.eventBus().post(event);
    }

    public void safeSaveSubPlanAndFireEvent(SubPlan subPlan) {
        Log.i(TAG, "返回的任务信息 --> " + subPlan.toString());
        //保存用户信息到本地
        DBSubPlan dbSubPlan = Converter.toDBSubPlan(subPlan);
        List<DBSubPlan> existing = null;
        try {
            existing = DB.subPlans().queryForEq(DBSubPlan.COLUMN_URL, dbSubPlan.getUrl());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbSubPlan.setId(id);
            int color = existing.get(0).getColor();
            dbSubPlan.setColor(color);
            DB.subPlans().updateAndFireEvent(dbSubPlan);
            Log.i(TAG, "更新子计划信息 --> updateAndFireEvent -- > " + dbSubPlan.toString());
        } else {
            List<Integer> CardStackViewDataList = new ArrayList<>();
            int[] CardStackViewData = TimeCatApp.getInstance().getResources().getIntArray(R.array.card_stack_view_data);
            for (int aCardStackViewData : CardStackViewData) {
                CardStackViewDataList.add(aCardStackViewData);
            }
            Random random = new Random();
            int randomColor = random.nextInt(CardStackViewDataList.size());
            dbSubPlan.setColor(CardStackViewDataList.get(randomColor));
            DB.subPlans().saveAndFireEvent(dbSubPlan);
            Log.i(TAG, "保存子计划信息 --> saveAndFireEvent -- > " + dbSubPlan.toString());
        }
    }


    public void safeSaveSubPlan(SubPlan note) {
        Log.i(TAG, "返回的任务信息 --> " + note.toString());
        //保存用户信息到本地
        DBSubPlan dbSubPlan = Converter.toDBSubPlan(note);
        List<DBSubPlan> existing = null;
        try {
            existing = DB.subPlans().queryForEq(DBSubPlan.COLUMN_URL, dbSubPlan.getUrl());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbSubPlan.setId(id);
            int color = existing.get(0).getColor();
            dbSubPlan.setColor(color);
            try {
                DB.subPlans().update(dbSubPlan);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "更新子计划信息 --> update -- > " + dbSubPlan.toString());
        } else {
            List<Integer> CardStackViewDataList = new ArrayList<>();
            int[] CardStackViewData = TimeCatApp.getInstance().getResources().getIntArray(R.array.card_stack_view_data);
            for (int aCardStackViewData : CardStackViewData) {
                CardStackViewDataList.add(aCardStackViewData);
            }
            Random random = new Random();
            int randomColor = random.nextInt(CardStackViewDataList.size());
            dbSubPlan.setColor(CardStackViewDataList.get(randomColor));
            DB.subPlans().save(dbSubPlan);
            Log.i(TAG, "保存子计划信息 --> save -- > " + dbSubPlan.toString());
        }
    }

    public void safeSaveDBSubPlan(DBSubPlan dbSubPlan) {
        List<DBSubPlan> existing = null;
        try {
            existing = DB.subPlans().queryForEq(DBSubPlan.COLUMN_CREATED_DATETIME, dbSubPlan.getCreated_datetime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbSubPlan.setId(id);
            int color = existing.get(0).getColor();
            dbSubPlan.setColor(color);
            DB.subPlans().updateAndFireEvent(dbSubPlan);
            Log.i(TAG, "更新子计划信息 --> updateAndFireEvent -- > " + dbSubPlan.toString());
        } else {
            DB.subPlans().saveAndFireEvent(dbSubPlan);
            Log.i(TAG, "保存子计划信息 --> saveAndFireEvent -- > " + dbSubPlan.toString());
        }
    }

    public void safeSaveDBSubPlanAndFireEvent(DBSubPlan dbSubPlan) {
        List<DBSubPlan> existing = null;
        try {
            existing = DB.subPlans().queryForEq(DBSubPlan.COLUMN_CREATED_DATETIME, dbSubPlan.getCreated_datetime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (existing != null && existing.size() > 0) {
            long id = existing.get(0).getId();
            dbSubPlan.setId(id);
            int color = existing.get(0).getColor();
            dbSubPlan.setColor(color);
            DB.subPlans().updateAndFireEvent(dbSubPlan);
            Log.i(TAG, "更新子计划信息 --> updateAndFireEvent -- > " + dbSubPlan.toString());
        } else {
            DB.subPlans().saveAndFireEvent(dbSubPlan);
            Log.i(TAG, "保存子计划信息 --> saveAndFireEvent -- > " + dbSubPlan.toString());
        }
    }


    public void updateActiveUserAndFireEvent(DBSubPlan activeDBSubPlan, Plan user) {
//        Object event = new PersistenceEvents.UserUpdateEvent(activeDBSubPlan);
//        try {
//            update(Converter.toActiveDBSubPlan(activeDBSubPlan, user));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        TimeCatApp.eventBus().post(event);
    }

    /// Mange active user through preferences

    public void removeCascade(DBSubPlan dbSubPlan) {
        DB.subPlans().remove(dbSubPlan);
    }

    /**
     * 通过UserId获取所有的task
     *
     * @param userId user id
     *
     * @return List<DBSubPlan>
     */
    public List<DBSubPlan> listByUserId(int userId) {
        try {
            return DB.subPlans().queryBuilder().where().eq(DBSubPlan.COLUMN_USER, userId).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
