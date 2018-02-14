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
import com.time.cat.database.typeSerializers.BooleanArrayPersister;
import com.time.cat.database.typeSerializers.LocalDatePersister;
import com.time.cat.database.typeSerializers.LocalTimePersister;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.List;

/**
 * @author dlink
 * @date 2018/2/6
 * @discription 任务类
 */
@DatabaseTable(tableName = "Schedules")
public class DBTask {

    public static final int SCHEDULE_TYPE_EVERYDAY = 0; // DEFAULT
    public static final int SCHEDULE_TYPE_EVERYWEEK = 1;
    public static final int SCHEDULE_TYPE_EVERYMONTH = 2;
    public static final int SCHEDULE_TYPE_EVERYHOUR = 3;
    public static final int SCHEDULE_TYPE_ONEDAY = 4;
    public static final int SCHEDULE_TYPE_CYCLE = 5;


    public static final int LABEL_IMPORTANT_URGENT = 0;
    public static final int LABEL_IMPORTANT_NOT_URGENT = 1;
    public static final int LABEL_NOT_IMPORTANT_URGENT = 2;
    public static final int LABEL_NOT_IMPORTANT_NOT_URGENT = 3;

    public static final long serialVersionUID = 1L;

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER = "user_id";
    public static final String COLUMN_TAG = "tag_id";
    public static final String COLUMN_PLAN = "plan_id";

    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_LABEL = "label";

    public static final String COLUMN_IS_FINISHED = "is_finished";
    public static final String COLUMN_CREATED_DATETIME = "created_datetime";
    public static final String COLUMN_FINISHED_DATETIME = "finished_datetime";

    public static final String COLUMN_IS_ALL_DAY = "is_all_day";
    public static final String COLUMN_BEGIN_DATETIME = "begin_datetime";
    public static final String COLUMN_END_DATETIME = "end_datetime";
    public static final String COLUMN_DAYS = "Days";
    public static final String COLUMN_START = "Start";
    public static final String COLUMN_START_TIME = "Starttime";
    public static final String COLUMN_DOSE = "Dose";
    public static final String COLUMN_TYPE = "Type";
    public static final String COLUMN_CYCLE = "Cycle";
    public static final String COLUMN_SCANNED = "Scanned";
    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    public Long id;//ID唯一主键
    @DatabaseField(columnName = COLUMN_USER, foreign = true, foreignAutoRefresh = true)
    public DBUser user;
    @DatabaseField(columnName = COLUMN_DAYS, persisterClass = BooleanArrayPersister.class)
    public boolean[] days = noWeekDays();
    @DatabaseField(columnName = COLUMN_START, persisterClass = LocalDatePersister.class)
    public LocalDate start;
    @DatabaseField(columnName = COLUMN_START_TIME, persisterClass = LocalTimePersister.class)
    public LocalTime startTime;
    @DatabaseField(columnName = COLUMN_DOSE)
    public float dose = 1f;
    @DatabaseField(columnName = COLUMN_TYPE)
    public int type = SCHEDULE_TYPE_EVERYDAY;
    @DatabaseField(columnName = COLUMN_CYCLE)
    public String cycle;
    @DatabaseField(columnName = COLUMN_SCANNED)
    public boolean scanned;
    //必不为null
    @DatabaseField(columnName = COLUMN_CREATED_DATETIME)
    DateTime created_datetime;//创建时间
    @DatabaseField(columnName = COLUMN_TITLE)
    String title;//任务标题
    //nullable，是否为null取决于业务情景
    int plan_id;//计划外键
    @DatabaseField(columnName = COLUMN_CONTENT)
    String content;//任务内容
    @DatabaseField(columnName = COLUMN_LABEL)
    int label;//重要紧急标签，重要紧急=0，重要不紧急=1，紧急不重要=2，不重要不紧急=3
    List<String> tags;//一般标签，["标签0","标签1","标签2"]
    //is_finish默认false，finished_datetime默认为null
    @DatabaseField(columnName = COLUMN_IS_FINISHED)
    boolean is_finish;//是否完成，true-完成，false-没有完成
    @DatabaseField(columnName = COLUMN_FINISHED_DATETIME)
    DateTime finished_datetime;//完成时间
    //is_all_day默认true，begin_datetime,end_datetime默认为null
    //isAllDay == true ? 忽略开始及结束时间的Time部分 : 不忽略
    @DatabaseField(columnName = COLUMN_IS_ALL_DAY)
    boolean is_all_day = true;//是否全天，true-全天，false-不是全天
    @DatabaseField(columnName = COLUMN_BEGIN_DATETIME)
    DateTime begin_datetime;//开始时间
    @DatabaseField(columnName = COLUMN_END_DATETIME)
    DateTime end_datetime;//结束时间

    public static List<DBTask> findAll() {
        return DB.schedules().findAll();
    }

    public static DBTask findById(long id) {
        return DB.schedules().findById(id);
    }

    public static final boolean[] noWeekDays() {
        return new boolean[]{false, false, false, false, false, false, false};
    }

    public static final boolean[] allWeekDays() {
        return new boolean[]{true, true, true, true, true, true, true};
    }

    public int type() {
        return type;
    }

    public void setType(int type) {
        if (type < 0 || type > 5) {
            throw new RuntimeException("Invalid schedule type");
        }
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate start() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public float dose() {
        return dose;
    }

    public void setDose(float dose) {
        this.dose = dose;
    }

    public DBUser user() {
        return user;
    }

    public void setUser(DBUser user) {
        this.user = user;
    }

    public LocalTime startTime() {
        return startTime;
    }

    public void setStartTime(LocalTime t) {
        startTime = t;
    }

    public boolean scanned() {
        return scanned;
    }

    // *************************************
    // DB queries
    // *************************************

    public void setScanned(boolean scanned) {
        this.scanned = scanned;
    }

    public int getCycleDays() {
        if (cycle == null) {
            return 0;
        }
        String[] parts = cycle.split(",");
        return Integer.valueOf(parts[0]);
    }

    public int getCycleRest() {
        if (cycle == null) {
            return 0;
        }
        String[] parts = cycle.split(",");
        return Integer.valueOf(parts[1]);
    }

    public void setCycle(int days, int rest) {
        this.cycle = days + "," + rest;
    }

    public List<DBTaskItem> items() {
        return DB.scheduleItems().findBySchedule(this);
    }

    public void save() {
        DB.schedules().save(this);
    }

    public void deleteCascade() {
        DB.schedules().deleteCascade(this, false);
    }

    public boolean[] getLegacyDays() {
        return days;
    }

    public boolean repeatsHourly() {
        return type == SCHEDULE_TYPE_EVERYHOUR;
    }

    @Override
    public String toString() {
        return "Task{" + "id=" + id + ", start=" + start + ", dose=" + dose + ", type=" + type + '}';
    }

    public String displayDose() {
        int integerPart = (int) dose;
        double fraction = dose - integerPart;

        String fractionRational;
        if (fraction == 0.125) {
            fractionRational = "1/8";
        } else if (fraction == 0.25) {
            fractionRational = "1/4";
        } else if (fraction == 0.5) {
            fractionRational = "1/2";
        } else if (fraction == 0.75) {
            fractionRational = "3/4";
        } else if (fraction == 0) {
            return "" + ((int) dose);
        } else {
            return "" + dose;
        }
        return integerPart + "+" + fractionRational;
    }

}

