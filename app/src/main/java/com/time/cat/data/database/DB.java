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

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.misc.TransactionManager;

import java.util.concurrent.Callable;


public class DB {


    public static final String TAG = DB.class.getSimpleName();

    // Database name
    public static String DB_NAME = "timecat.db";
    // initialized flag
    public static boolean initialized = false;

    // DatabaseManeger reference
    private static DatabaseManager<DatabaseHelper> manager;
    // SQLite DB Helper
    private static DatabaseHelper db;

    // Routines DAO
    private static RoutineDao Routines;
    // Schedules DAO
    private static ScheduleDao Schedules;
    // users DAO
    private static UserDao users;
    // notes DAO
    private static NoteDao notes;
    // plans DAO
    private static PlanDao plans;
    // subPlans DAO
    private static SubPlanDao subPlans;
    // pomodoro DAO
    private static PomodoroDao pomodoros;
    /**
     * Initialize database and DAOs
     */
    public synchronized static void init(Context context) {

        if (!initialized) {
            initialized = true;
            manager = new DatabaseManager<>();
            db = manager.getHelper(context, DatabaseHelper.class);

            db.getReadableDatabase().enableWriteAheadLogging();

            users = new UserDao(db);
            Routines = new RoutineDao(db);
            Schedules = new ScheduleDao(db);
            notes = new NoteDao(db);
            plans = new PlanDao(db);
            subPlans = new SubPlanDao(db);
            pomodoros = new PomodoroDao(db);

            Log.i(TAG, "DB initialized " + DB.DB_NAME);
        }

    }

    /**
     * Dispose DB and DAOs
     */
    public synchronized static void dispose() {
        initialized = false;
        db.close();
        manager.releaseHelper(db);
        Log.v(TAG, "DB disposed");
    }

    public static DatabaseHelper helper() {
        return db;
    }
    public static Object transaction(Callable<?> callable) {
        try {
            return TransactionManager.callInTransaction(db.getConnectionSource(), callable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RoutineDao routines() {
        return Routines;
    }
    public static ScheduleDao schedules() {
        return Schedules;
    }
    public static UserDao users() {
        return users;
    }
    public static NoteDao notes() {
        return notes;
    }
    public static PlanDao plans() {
        return plans;
    }
    public static SubPlanDao subPlans() {
        return subPlans;
    }
    public static PomodoroDao pomodoros() {
        return pomodoros;
    }

    public static void dropAndCreateDatabase() {
        db.dropAndCreateAllTables();
    }
}
