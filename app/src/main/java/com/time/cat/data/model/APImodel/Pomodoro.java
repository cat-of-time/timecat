package com.time.cat.data.model.DBmodel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.time.cat.data.database.DB;

import java.io.Serializable;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/6
 * @discription null
 * @usage null
 */
@DatabaseTable(tableName = "Pomodoro")
public class DBPomodoro implements Serializable {
    //<editor-fold desc="Field">
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CREATED_DATETIME = "created_datetime";
    public static final String COLUMN_BEGIN_DATETIME = "begin_datetime";// 开始时间
    public static final String COLUMN_END_DATETIME = "end_datetime";// 结束时间
    public static final String COLUMN_DURATION = "duration"; // 任务时长
    public static final String COLUMN_DATE_ADD = "date_add"; // 添加时间
    public static final String COLUMN_USER = "User";
    //</editor-fold desc="Field">

    //<editor-fold desc="Database Field">
    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private Long id;
    @DatabaseField(columnName = COLUMN_CREATED_DATETIME)
    private String created_datetime;//创建时间
    @DatabaseField(columnName = COLUMN_BEGIN_DATETIME)
    private String begin_datetime;//开始时间
    @DatabaseField(columnName = COLUMN_END_DATETIME)
    private String end_datetime;//结束时间
    @DatabaseField(columnName = COLUMN_DURATION)
    private String duration;//任务时长
    @DatabaseField(columnName = COLUMN_DATE_ADD)
    private String date_add;//添加时间
    @DatabaseField(columnName = COLUMN_USER, foreign = true, foreignAutoRefresh = true)
    private DBUser user = DB.users().getActive();
    //</editor-fold desc="Database Field">

    //<editor-fold desc="getter and setter">

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBegin_datetime() {
        return begin_datetime;
    }

    public void setBegin_datetime(String begin_datetime) {
        this.begin_datetime = begin_datetime;
    }

    public String getEnd_datetime() {
        return end_datetime;
    }

    public void setEnd_datetime(String end_datetime) {
        this.end_datetime = end_datetime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate_add() {
        return date_add;
    }

    public void setDate_add(String date_add) {
        this.date_add = date_add;
    }

    public DBUser getUser() {
        return user;
    }

    public void setUser(DBUser user) {
        this.user = user;
    }

    //</editor-fold desc="getter and setter">

    @Override
    public String toString() {
        return "DBPomodoro{" + "id=" + id + ", begin_datetime='" + begin_datetime + '\'' + ", end_datetime='" + end_datetime + '\'' + ", duration='" + duration + '\'' + ", date_add='" + date_add + '\'' + ", user=" + user + '}';
    }

    public String getCreated_datetime() {
        return created_datetime;
    }

    public void setCreated_datetime(String created_datetime) {
        this.created_datetime = created_datetime;
    }
}
