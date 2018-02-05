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

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.time.cat.database.typeSerializers.LocalTimePersister;
import com.time.cat.mvp.model.User;

import org.joda.time.LocalTime;

import java.util.List;

/**
 * Created by joseangel.pineiro
 */
@DatabaseTable(tableName = "Routines")
public class Routine {

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIME = "Time";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_USER = "User";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private Long id;

    @DatabaseField(columnName = COLUMN_TIME, persisterClass = LocalTimePersister.class)
    private LocalTime time;

    @DatabaseField(columnName = COLUMN_NAME)
    private String name;

    @DatabaseField(columnName = COLUMN_USER, foreign = true, foreignAutoRefresh = true)
    private User user;

    public Routine() {
    }

    public Routine(LocalTime time, String name) {
        this.time = time;
        this.name = name;
    }

    public Routine(User p, LocalTime time, String name) {
        this.user = p;
        this.time = time;
        this.name = name;
    }

    public static List<Routine> findAll() {
        return DB.routines().findAll();
    }

    public static Routine findById(long id) {
        return DB.routines().findById(id);
    }

    public static Routine findByName(String name) {
        return DB.routines().findOneBy(COLUMN_NAME, name);
    }

    public static List<Routine> findInHour(int hour) {
        return DB.routines().findInHour(hour);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime time() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public User user() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // *************************************
    // DB queries
    // *************************************

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void save() {
        DB.routines().save(this);
    }

    public void deleteCascade() {
        DB.routines().deleteCascade(this, false);
    }

    public List<ScheduleItem> scheduleItems() {
        return DB.scheduleItems().findByRoutine(this);
    }



}
