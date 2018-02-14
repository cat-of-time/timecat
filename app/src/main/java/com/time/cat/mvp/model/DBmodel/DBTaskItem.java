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

package com.time.cat.mvp.model.DBmodel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.time.cat.database.DB;

/**
 * Created by joseangel.pineiro on 7/9/14.
 */
@DatabaseTable(tableName = "ScheduleItems")
public class DBTaskItem {

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TASK = "DBTask";
    public static final String COLUMN_ROUTINE = "DBRoutine";
    public static final String COLUMN_DOSE = "Dose";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private Long id;

    @DatabaseField(columnName = COLUMN_TASK, foreign = true, foreignAutoRefresh = true)
    private DBTask DBTask;

    @DatabaseField(columnName = COLUMN_ROUTINE, foreign = true, foreignAutoRefresh = true)
    private DBRoutine DBRoutine;

    @DatabaseField(columnName = COLUMN_DOSE)
    private float dose;

    public DBTaskItem() {
        super();
    }

    public DBTaskItem(DBTask DBTask, DBRoutine DBRoutine, float dose) {
        this();
        this.DBTask = DBTask;
        this.DBRoutine = DBRoutine;
        this.dose = dose;
    }

    public DBTaskItem(DBTask DBTask, DBRoutine DBRoutine) {
        this();
        this.DBTask = DBTask;
        this.DBRoutine = DBRoutine;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBRoutine routine() {
        return DBRoutine;
    }

    public void setDBRoutine(DBRoutine DBRoutine) {
        this.DBRoutine = DBRoutine;
    }

    public DBTask schedule() {
        return DBTask;
    }

    public void setDBTask(DBTask DBTask) {
        this.DBTask = DBTask;
    }

    public float dose() {
        return dose;
    }

    public String displayDose() {
        int integerPart = (int) dose;
        double fraction = dose - integerPart;

        String fractionRational;
        if (fraction == 0.125) fractionRational = "1/8";
        else if (fraction == 0.25) fractionRational = "1/4";
        else if (fraction == 0.5) fractionRational = "1/2";
        else if (fraction == 0.75) fractionRational = "3/4";
        else if (fraction == 0) return "" + ((int) dose);
        else return "" + dose;
        return integerPart + "+" + fractionRational;

    }

    public void setDose(float dose) {
        this.dose = dose;
    }

    // *************************************
    // DB queries
    // *************************************

    public void save() {
        DB.scheduleItems().save(this);
    }

    public void deleteCascade() {
        DB.scheduleItems().deleteCascade(this);
    }


}
