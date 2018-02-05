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
import android.preference.PreferenceManager;

import com.j256.ormlite.dao.Dao;
import com.time.cat.TimeCatApp;
import com.time.cat.events.PersistenceEvents;
import com.time.cat.mvp.model.User;

import java.sql.SQLException;


public class UserDao extends GenericDao<User, Long> {

    public static final String PREFERENCE_ACTIVE_USER = "active_user";

    public static final String TAG = "UserDao";

    public UserDao(DatabaseHelper db) {
        super(db);
    }

    @Override
    public Dao<User, Long> getConcreteDao() {
        try {
            return dbHelper.getUserDao();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating users dao", e);
        }
    }

    @Override
    public void saveAndFireEvent(User p) {

        Object event =  p.id() == null ? new PersistenceEvents.UserCreateEvent(p) : new PersistenceEvents.UserUpdateEvent(p);
        save(p);
        TimeCatApp.eventBus().post(event);

    }

    /// Mange active user through preferences

    public boolean isActive(User p, Context ctx) {
        Long activeId = PreferenceManager.getDefaultSharedPreferences(ctx).getLong(PREFERENCE_ACTIVE_USER, -1);
        return activeId.equals(p.id());
    }

    public User getActive(Context ctx) {
        long id = PreferenceManager.getDefaultSharedPreferences(ctx).getLong(PREFERENCE_ACTIVE_USER, -1);
        User p;
        if (id != -1) {
            p = findById(id);
            if (p == null) {
                p = getDefault();
                setActive(p, ctx);
            }
            return p;
        } else {
            return getDefault();
        }
    }

    public User getDefault() {
        return findOneBy(User.COLUMN_DEFAULT, true);
    }

    public void setActive(User user, Context ctx) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit()
                .putLong(PREFERENCE_ACTIVE_USER, user.id())
                .commit();
        TimeCatApp.eventBus().post(new PersistenceEvents.ActiveUserChangeEvent(user));
    }

    public void setActiveById(Long id, Context ctx) {
        User user = findById(id);
        PreferenceManager.getDefaultSharedPreferences(ctx).edit()
                .putLong(PREFERENCE_ACTIVE_USER, user.id())
                .commit();
        TimeCatApp.eventBus().post(new PersistenceEvents.ActiveUserChangeEvent(user));
    }

    public void removeCascade(User p) {
        // remove all data
        removeAllStuff(p);
        // remove user
        DB.users().remove(p);

    }

    public void removeAllStuff(User p) {
//        for(Medicine m : DB.medicines().findAll()){
//            if(m.user().id() == p.id()){
//                // this also remove schedules
//                DB.medicines().deleteCascade(m, true);
//            }
//        }
//        // remove routines
//        for(Routine r:  DB.routines().findAll()) {
//            if (r.user().id() == p.id()) {
//                DB.routines().remove(r);
//            }
//        }
    }
}
