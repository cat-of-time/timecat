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

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.time.cat.TimeCatApp;
import com.time.cat.events.PersistenceEvents;
import com.time.cat.mvp.model.DBmodel.DBRoutine;
import com.time.cat.mvp.model.DBmodel.DBTaskItem;
import com.time.cat.mvp.model.DBmodel.DBUser;

import org.joda.time.LocalTime;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by joseangel.pineiro on 3/26/15.
 */
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
            return dao.queryBuilder().orderBy(DBRoutine.COLUMN_TIME, true).query();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding models", e);
        }
    }


    public List<DBRoutine> findAllForActiveUser(Context ctx) {
        return findAll(DB.users().getActive(ctx));
    }

    public List<DBRoutine> findAll(DBUser p) {
        return findAll(p.id());
    }


    public List<DBRoutine> findAll(Long userId) {
        try {
            return dao.queryBuilder().orderBy(DBRoutine.COLUMN_TIME, true).where().eq(DBRoutine.COLUMN_USER, userId).query();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding models", e);
        }
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


    public List<DBRoutine> findInHour(int hour) {
        try {
            LocalTime time = new LocalTime(hour, 0);
            // get one hour interval [h:00, h:59:]
            String start = time.toString("kk:mm");
            String end = time.plusMinutes(59).toString("kk:mm");


            LocalTime endTime = time.plusMinutes(59);

            return queryBuilder().where().between(DBRoutine.COLUMN_TIME, time, endTime).query();
        } catch (Exception e) {
            Log.e(TAG, "Error in findInHour", e);
            throw new RuntimeException(e);
        }
    }

    public void deleteCascade(final DBRoutine r, boolean fireEvent) {

        DB.transaction(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Collection<DBTaskItem> items = r.scheduleItems();
                for (DBTaskItem i : items) {
                    i.deleteCascade();
                }
                DB.routines().remove(r);
                return null;
            }
        });

        if (fireEvent) {
            fireEvent();
        }
    }
}
