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

import com.j256.ormlite.dao.Dao;
import com.time.cat.TimeCatApp;
import com.time.cat.events.PersistenceEvents;
import com.time.cat.mvp.model.DBmodel.DBTask;
import com.time.cat.mvp.model.DBmodel.DBTaskItem;
import com.time.cat.mvp.model.DBmodel.DBUser;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by joseangel.pineiro on 3/26/15.
 */
public class ScheduleDao extends GenericDao<DBTask, Long> {

    public ScheduleDao(DatabaseHelper db) {
        super(db);
    }

    public List<DBTask> findAllForActiveUser(Context ctx) {
        return findAll(DB.users().getActive(ctx));
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
