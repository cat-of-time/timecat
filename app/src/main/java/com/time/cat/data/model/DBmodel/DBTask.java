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

package com.time.cat.data.model.DBmodel;


import android.graphics.Color;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.time.cat.data.database.DB;
import com.time.cat.util.string.TimeUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author dlink
 * @date 2018/2/6
 * @discription 任务类
 */
@DatabaseTable(tableName = "Schedules")
public class DBTask implements Serializable {
    //<editor-fold desc="Field">
    public static final int[] labelColor = new int[]{Color.parseColor("#f44336"), Color.parseColor("#ff8700"), Color.parseColor("#2196f3"), Color.parseColor("#4caf50")};

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
    public static final String COLUMN_PLANS = "plans";

    public static final String COLUMN_URL = "url";
    public static final String COLUMN_OWNER = "owner";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_LABEL = "label";
    public static final String COLUMN_TAGS = "tags";

    public static final String COLUMN_IS_FINISHED = "is_finished";
    public static final String COLUMN_CREATED_DATETIME = "created_datetime";
    public static final String COLUMN_FINISHED_DATETIME = "finished_datetime";

    public static final String COLUMN_IS_ALL_DAY = "is_all_day";
    public static final String COLUMN_BEGIN_DATETIME = "begin_datetime";
    public static final String COLUMN_END_DATETIME = "end_datetime";

    public static final String COLUMN_DAYS = "Days";
    public static final String COLUMN_START = "Start";
    public static final String COLUMN_TYPE = "Type";
    //</editor-fold desc="Field">


    //<editor-fold desc="Database Field">
    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    public Long id;//ID唯一主键
    @DatabaseField(columnName = COLUMN_USER, foreign = true, foreignAutoRefresh = true)
    public DBUser user;

    //----------------------------------------------------------------------------------
    @DatabaseField(columnName = COLUMN_URL, unique = true)
    private String url;// task的url 访问该url可返回该task
    @DatabaseField(columnName = COLUMN_OWNER)
    private String owner;//用户ID
    @DatabaseField(columnName = COLUMN_TITLE)
    private String title;//日程标题
    @DatabaseField(columnName = COLUMN_CONTENT)
    private String content;//日程内容
    @DatabaseField(columnName = COLUMN_LABEL)
    private int label;//重要紧急标签,重要紧急=0，重要不紧急=1，紧急不重要=2，不重要不紧急=3

    @DatabaseField(columnName = COLUMN_TAGS, dataType = DataType.SERIALIZABLE)
    private ArrayList<String> tags;//一般标签，["/tags/1","/tags/2","/tags/3"]

    @DatabaseField(columnName = COLUMN_CREATED_DATETIME)
    private String created_datetime;//创建时间
    @DatabaseField(columnName = COLUMN_FINISHED_DATETIME)
    private String finished_datetime;//完成时间
    //is_finish默认false，finished_datetime默认为null
    @DatabaseField(columnName = COLUMN_IS_FINISHED)
    private boolean is_finished;//是否完成，1 - 是，0 - 不是

    //is_all_day默认true，begin_datetime,end_datetime默认为null
    //isAllDay == true ? 忽略开始及结束时间的Time部分 : 不忽略
    @DatabaseField(columnName = COLUMN_IS_ALL_DAY)
    private boolean is_all_day;//是否全天，1 - 是，0 - 不是
    @DatabaseField(columnName = COLUMN_BEGIN_DATETIME)
    private String begin_datetime;//开始时间
    @DatabaseField(columnName = COLUMN_END_DATETIME)
    private String end_datetime;//结束时间
    //</editor-fold desc="Database Field">


    //<editor-fold desc="getter and setter">
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBegin_datetime() {
        return this.begin_datetime;
    }

    public void setBegin_datetime(String begin_datetime) {
        this.begin_datetime = begin_datetime;
    }

    public String getEnd_datetime() {
        return this.end_datetime;
    }

    public void setEnd_datetime(String end_datetime) {
        this.end_datetime = end_datetime;
    }

    public boolean getIs_all_day() {
        return this.is_all_day;
    }

    public void setIs_all_day(boolean is_all_day) {
        this.is_all_day = is_all_day;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCreated_datetime() {
        return this.created_datetime;
    }

    public void setCreated_datetime(String created_datetime) {
        this.created_datetime = created_datetime;
    }

    public boolean getIsFinish() {
        return is_finished;
    }

    public void setIsFinish(boolean isFinish) {
        this.is_finished = isFinish;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getFinished_datetime() {
        return finished_datetime;
    }

    public void setFinished_datetime(String finished_datetime) {
        this.finished_datetime = finished_datetime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBUser user() {
        return user;
    }

    public void setUser(DBUser user) {
        this.user = user;
    }
    //</editor-fold desc="getter and setter">

    public static List<DBTask> findAll() {
        return DB.schedules().findAll();
    }

    public static DBTask findById(long id) {
        return DB.schedules().findById(id);
    }

    // *************************************
    // DB queries
    // *************************************

    public List<DBTaskItem> items() {
        return DB.scheduleItems().findBySchedule(this);
    }

    @Override
    public String toString() {
        return "DBTask{" + "id=" + id + ", user=" + user + ", url='" + url + '\'' + ", owner='" + owner + '\'' + ", title='" + title + '\'' + ", content='" + content + '\'' + ", label=" + label + ", tags=" + tags + ", created_datetime='" + created_datetime + '\'' + ", finished_datetime='" + finished_datetime + '\'' + ", is_finished=" + is_finished + ", is_all_day=" + is_all_day + ", begin_datetime='" + begin_datetime + '\'' + ", end_datetime='" + end_datetime + '\'' + '}';
    }

    public long getBeginTs() {
        Date d = TimeUtil.formatGMTDateStr(begin_datetime);
        if (d != null) {
            return d.getTime();
        } else {
            return -1;
        }
    }

    public long getEndTs() {
        Date d = TimeUtil.formatGMTDateStr(end_datetime);
        if (d != null) {
            return d.getTime();
        } else {
            return -1;
        }
    }

    public long getCreateTs() {
        Date d = TimeUtil.formatGMTDateStr(created_datetime);
        if (d != null) {
            return d.getTime();
        } else {
            return -1;
        }
    }

    public long getFinishedTs() {
        Date d = TimeUtil.formatGMTDateStr(finished_datetime);
        if (d != null) {
            return d.getTime();
        } else {
            return -1;
        }
    }
}

