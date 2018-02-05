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

import com.j256.ormlite.dao.Dao;
import com.time.cat.mvp.model.DBmodel.DBRoutine;
import com.time.cat.mvp.model.DBmodel.DBTask;
import com.time.cat.mvp.model.DBmodel.DBTaskItem;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by joseangel.pineiro on 3/26/15.
 */
public class ScheduleItemDao extends GenericDao<DBTaskItem, Long> {


    public ScheduleItemDao(DatabaseHelper db) {
        super(db);
    }

    @Override
    public Dao<DBTaskItem, Long> getConcreteDao() {
        try {
            return dbHelper.getScheduleItemsDao();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating medicines dao", e);
        }
    }

    public List<DBTaskItem> findBySchedule(DBTask s) {
        return findBy(DBTaskItem.COLUMN_TASK, s.getId());
    }

    public List<DBTaskItem> findByRoutine(DBRoutine r) {
        return findBy(DBTaskItem.COLUMN_ROUTINE, r.getId());
    }

    public void deleteCascade(final DBTaskItem s) {
        DB.transaction(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                DB.scheduleItems().remove(s);
                return null;
            }
        });

    }

}
