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
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.time.cat.mvp.model.User;

import java.sql.SQLException;


/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String TAG = "DatabaseHelper";

    // List of persisted classes to simplify table creation
    public Class<?>[] persistedClasses = new Class<?>[]{
            Routine.class,
//            Medicine.class,
            Schedule.class,
            ScheduleItem.class,
//            DailyScheduleItem.class,
//            Prescription.class,
            // v8
//            HomogeneousGroup.class,
//            PickupInfo.class,
            // v9
            User.class,
            // v10
//            HtmlCacheEntry.class
    };

    // name of the database file for our application
    private static final String DATABASE_NAME = DB.DB_NAME;
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 11;

    // the DAO object we use to access the Medicines table
//    private Dao<Medicine, Long> medicinesDao = null;
    // the DAO object we use to access the Routines table
    private Dao<Routine, Long> routinesDao = null;
    // the DAO object we use to access the Schedules table
    private Dao<Schedule, Long> schedulesDao = null;
    // the DAO object we use to access the ScheduleItems table
    private Dao<ScheduleItem, Long> scheduleItemsDao = null;
    // the DAO object we use to access the DailyScheduleItems table
//    private Dao<DailyScheduleItem, Long> dailyScheduleItemsDao = null;
    // the DAO object we use to access the DailyScheduleItems table
//    private Dao<Prescription, Long> prescriptionsDao = null;
    // the DAO object we use to access the HomogeneousGroups table
//    private Dao<HomogeneousGroup, Long> homogeneousGroupsDao = null;
    // the DAO object we use to access the pcikupInfo table
//    private Dao<PickupInfo, Long> pickupInfoDao = null;
    // the DAO object we use to access the users table
    private Dao<User, Long> userDao = null;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");

            for (Class<?> c : persistedClasses) {
                Log.d(TAG, "Creating table for " + c.getSimpleName());
                TableUtils.createTable(connectionSource, c);
            }

            createDefaultUser();

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    private User createDefaultUser() throws SQLException {
        // Create a default user
        User user = new User();
        user.setName("TestUser");
        user.setDefault(true);
        getUserDao().create(user);
        return user;
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            Log.d(DatabaseHelper.class.getName(), "OldVersion: " + oldVersion + ", newVersion: " + newVersion);

            if (oldVersion < 6) {
                oldVersion = 6;
            }

            switch (oldVersion + 1) {

//                case 7:
//                    // migrate to iCal
//                    migrateToICal();
//                case 8:
//                    getMedicinesDao().executeRaw("ALTER TABLE Medicines ADD COLUMN hg INTEGER;");
//                    // Add column scanned (boolean, INTEGER in SQLite) to schedules table
//                    getSchedulesDao().executeRaw("ALTER TABLE Schedules ADD COLUMN Scanned INTEGER;");
//                    // update schedules scanned value
//                    TransactionManager.callInTransaction(getConnectionSource(), new Callable<Void>() {
//                        @Override
//                        public Void call() throws Exception {
//                            // iterate over schedules and set Scanned to false
//                            List<Schedule> schedules = getSchedulesDao().queryForAll();
//                            for (Schedule s : schedules) {
//                                s.setScanned(false);
//                                s.save();
//                            }
//                            return null;
//                        }
//                    });
//                    // Create HomogeneousGroup and PickupInfo tables
//                    TableUtils.createTable(connectionSource, HomogeneousGroup.class);
//                    TableUtils.createTable(connectionSource, PickupInfo.class);
                case 9:
                    TableUtils.createTable(connectionSource, User.class);
//                    migrateToMultiUser();
//                case 10:
//                    TableUtils.createTable(connectionSource, HtmlCacheEntry.class);
//                case 11:
//                    //delete all html cache entries and change datatypes (bugfix)
//                    TableUtils.dropTable(connectionSource, HtmlCacheEntry.class, true);
//                    TableUtils.createTable(connectionSource, HtmlCacheEntry.class);
//                    LocalDateMigrationHelper.migrateLocalDates(this);

            }

        } catch (Exception e) {
            Log.e(DatabaseHelper.class.getName(), "Can't upgrade databases", e);
            try {
                Log.d(DatabaseHelper.class.getName(), "Will try to recreate db...");
                dropAndCreateAllTables();
                createDefaultUser();
            }catch (Exception ex){
                throw new RuntimeException(e);
            }
        }
    }

//    /**
//     * Method that migrate models to multi-user
//     */
//    private void migrateToMultiUser() throws SQLException {
//
//        // add user column to routines, schedules and medicines
//        getRoutinesDao().executeRaw("ALTER TABLE Routines ADD COLUMN User INTEGER;");
//        getRoutinesDao().executeRaw("ALTER TABLE Medicines ADD COLUMN User INTEGER;");
//        getRoutinesDao().executeRaw("ALTER TABLE Schedules ADD COLUMN User INTEGER;");
//        getRoutinesDao().executeRaw("ALTER TABLE DailyScheduleItems ADD COLUMN User INTEGER;");
//        getRoutinesDao().executeRaw("ALTER TABLE DailyScheduleItems ADD COLUMN Date TEXT;");
//
//        User p = createDefaultUser();
//        // SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences();
//        // prefs.edit().putLong(UserDao.PREFERENCE_ACTIVE_USER, p.id()).commit();
//
//        // Assign all routines to the default user
//        UpdateBuilder<Routine, Long> rUpdateBuilder = getRoutinesDao().updateBuilder();
//        rUpdateBuilder.updateColumnValue(Routine.COLUMN_USER, p.id());
//        rUpdateBuilder.update();
//
//        // Assign all schedules to the default user
//        UpdateBuilder<Schedule, Long> sUpdateBuilder = getSchedulesDao().updateBuilder();
//        sUpdateBuilder.updateColumnValue(Schedule.COLUMN_USER, p.id());
//        sUpdateBuilder.update();
//
//        // Assign all medicines to the default user
//        UpdateBuilder<Medicine, Long> mUpdateBuilder = getMedicinesDao().updateBuilder();
//        mUpdateBuilder.updateColumnValue(Medicine.COLUMN_USER, p.id());
//        mUpdateBuilder.update();
//
//        // Assign all DailyScheduleItems to the default user, for today
//        UpdateBuilder<DailyScheduleItem, Long> siUpdateBuilder = getDailyScheduleItemsDao().updateBuilder();
//        siUpdateBuilder.updateColumnValue(DailyScheduleItem.COLUMN_USER, p.id());
//        siUpdateBuilder.update();
//
//        // date formatter changes on v11, so we can no use LocalDatePersister here
//        String now = LocalDate.now().toString("ddMMYYYY");
//        String updateDateSql = "UPDATE DailyScheduleItems SET " + DailyScheduleItem.COLUMN_DATE + " = '" +now + "'";
//        getDailyScheduleItemsDao().executeRaw(updateDateSql);
//
//    }
//
//    /**
//     * Method that migrate schedules to the iCal format
//     */
//    private void migrateToICal() throws SQLException {
//
//        getSchedulesDao().executeRaw("ALTER TABLE Schedules ADD COLUMN Rrule TEXT;");
//        getSchedulesDao().executeRaw("ALTER TABLE Schedules ADD COLUMN Start TEXT;");
//        getSchedulesDao().executeRaw("ALTER TABLE Schedules ADD COLUMN Starttime TEXT;");
//        getSchedulesDao().executeRaw("ALTER TABLE Schedules ADD COLUMN Dose REAL;");
//        getSchedulesDao().executeRaw("ALTER TABLE Schedules ADD COLUMN Type INTEGER;");
//        getSchedulesDao().executeRaw("ALTER TABLE Schedules ADD COLUMN Cycle TEXT;");
//
//        getDailyScheduleItemsDao().executeRaw(
//                "ALTER TABLE DailyScheduleItems ADD COLUMN Schedule INTEGER;");
//        getDailyScheduleItemsDao().executeRaw(
//                "ALTER TABLE DailyScheduleItems ADD COLUMN Time TEXT;");
//
//        // update schedules
//        TransactionManager.callInTransaction(getConnectionSource(), new Callable<Void>() {
//            @Override
//            public Void call() throws Exception {
//                // iterate over schedules and replace days[] with rrule
//                List<Schedule> schedules = getSchedulesDao().queryForAll();
//                Log.d(TAG, "Upgrade " + schedules.size() + " schedules");
//                for (Schedule s : schedules) {
//                    if (s.rule() == null) {
//                        s.setRepetition(new RepetitionRule(RepetitionRule.DEFAULT_ICAL_VALUE));
//                    }
//                    s.setDays(s.getLegacyDays());
//
//                    if (s.allDaysSelected()) {
//                        s.setType(Schedule.SCHEDULE_TYPE_EVERYDAY);
//                    } else {
//                        s.setType(Schedule.SCHEDULE_TYPE_SOMEDAYS);
//                    }
//                    s.setStart(LocalDate.now());
//                    s.save();
//                }
//
//                return null;
//            }
//        });
//
//
//    }
//
//    /**
//     * Returns the Database Access Object (DAO) for our Medicines class. It will create it or just give the cached
//     * value.
//     */
//    public Dao<Medicine, Long> getMedicinesDao() throws SQLException {
//        if (medicinesDao == null) {
//            medicinesDao = getDao(Medicine.class);
//        }
//        return medicinesDao;
//    }

    /**
     * Returns the Database Access Object (DAO) for our Routines class. It will create it or just give the cached
     * value.
     */
    public Dao<Routine, Long> getRoutinesDao() throws SQLException {
        if (routinesDao == null) {
            routinesDao = getDao(Routine.class);
        }
        return routinesDao;
    }

    /**
     * Returns the Database Access Object (DAO) for our Schedules class. It will create it or just give the cached
     * value.
     */
    public Dao<Schedule, Long> getSchedulesDao() throws SQLException {
        if (schedulesDao == null) {
            schedulesDao = getDao(Schedule.class);
        }
        return schedulesDao;
    }

    /**
     * Returns the Database Access Object (DAO) for our ScheduleItem class. It will create it or just give the cached
     * value.
     */
    public Dao<ScheduleItem, Long> getScheduleItemsDao() throws SQLException {
        if (scheduleItemsDao == null) {
            scheduleItemsDao = getDao(ScheduleItem.class);
        }
        return scheduleItemsDao;
    }

//    /**
//     * Returns the Database Access Object (DAO) for our DailyScheduleItem class. It will create it or just give the cached
//     * value.
//     */
//    public Dao<DailyScheduleItem, Long> getDailyScheduleItemsDao() throws SQLException {
//        if (dailyScheduleItemsDao == null) {
//            dailyScheduleItemsDao = getDao(DailyScheduleItem.class);
//        }
//        return dailyScheduleItemsDao;
//    }
//
//    /**
//     * Returns the Database Access Object (DAO) for our DailyScheduleItem class. It will create it or just give the cached
//     * value.
//     */
//    public Dao<Prescription, Long> getPrescriptionsDao() throws SQLException {
//        if (prescriptionsDao == null) {
//            prescriptionsDao = getDao(Prescription.class);
//        }
//        return prescriptionsDao;
//    }
//
//    /**
//     * Returns the Database Access Object (DAO) for our HomogeneousGroup class. It will create it or just give the cached
//     * value.
//     */
//    public Dao<HomogeneousGroup, Long> getHomogeneousGroupsDao() throws SQLException {
//        if (homogeneousGroupsDao == null) {
//            homogeneousGroupsDao = getDao(HomogeneousGroup.class);
//        }
//        return homogeneousGroupsDao;
//    }
//
//
//    /**
//     * Returns the Database Access Object (DAO) for our PickupInfo class. It will create it or just give the cached
//     * value.
//     */
//    public Dao<PickupInfo, Long> getPickupInfosDao() throws SQLException {
//        if (pickupInfoDao == null) {
//            pickupInfoDao = getDao(PickupInfo.class);
//        }
//        return pickupInfoDao;
//    }

    /**
     * Returns the Database Access Object (DAO) for our User class. It will create it or just give the cached
     * value.
     */
    public Dao<User, Long> getUserDao() throws SQLException {
        if (userDao == null) {
            userDao = getDao(User.class);
        }
        return userDao;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void dropAndCreateAllTables() {

        Log.i(DatabaseHelper.class.getName(), "Dropping all tables...");
        for (Class<?> c : persistedClasses) {
            Log.d(TAG, "Dropping table " + c.getSimpleName());
            try {
                TableUtils.dropTable(connectionSource, c, true);
            } catch (SQLException e) {
                // ignore
                Log.e(TAG, "Erro dropping table " + c.getSimpleName());
            }

        }

        try {

            Log.i(DatabaseHelper.class.getName(), "Creating tables...");
            for (Class<?> c : persistedClasses) {
                Log.d(TAG, "Creating table " + c.getSimpleName());
                TableUtils.createTable(connectionSource, c);
            }

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't recreate database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
//        medicinesDao = null;
    }

}
