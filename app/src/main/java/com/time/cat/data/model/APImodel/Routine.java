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

package com.time.cat.data.model.APImodel;

import android.graphics.Color;

import com.j256.ormlite.table.DatabaseTable;
import com.time.cat.data.model.DBmodel.DBUser;

import org.joda.time.LocalTime;

import java.util.ArrayList;

@DatabaseTable(tableName = "Routines")
public class Routine {
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
    //</editor-fold desc="Field">


    //<editor-fold desc="Database Field">
    private Long id;

    private LocalTime time;

    private String name;

    private DBUser user;

    private String url;// routine的url 访问该url可返回该routine
    private String owner;//用户ID
    private String title;//日程标题
    private String content;//日程内容
    private int label;//重要紧急标签,重要紧急=0，重要不紧急=1，紧急不重要=2，不重要不紧急=3

    private ArrayList<String> tags;//一般标签，["/tags/1","/tags/2","/tags/3"]

    private String created_datetime;//创建时间
    private String finished_datetime;//完成时间
    //is_finish默认false，finished_datetime默认为null
    private boolean is_finished;//是否完成，1 - 是，0 - 不是

    //is_all_day默认true，begin_datetime,end_datetime默认为null
    //isAllDay == true ? 忽略开始及结束时间的Time部分 : 不忽略
    private boolean is_all_day;//是否全天，1 - 是，0 - 不是
    private String begin_datetime;//开始时间
    private String end_datetime;//结束时间

    private int reminder1Minutes;
    private int reminder2Minutes;
    private int reminder3Minutes;

    private int repeatInterval;
    private int repeatLimit;
    private int repeatRule;

    private long lastUpdated;

    private String location;
    //</editor-fold desc="Database Field">


    public Routine() {
    }

    public Routine(LocalTime time, String name) {
        this.time = time;
        this.name = name;
    }

    public Routine(DBUser p, LocalTime time, String name) {
        this.user = p;
        this.time = time;
        this.name = name;
    }


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

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setUser(DBUser user) {
        this.user = user;
    }

    public String name() {
        return name;
    }

    public LocalTime time() {
        return time;
    }

    public DBUser user() {
        return user;
    }

    public String getName() {
        return name;
    }

    public LocalTime getTime() {
        return time;
    }

    public DBUser getUser() {
        return user;
    }

    public boolean isIs_finished() {
        return is_finished;
    }

    public void setIs_finished(boolean is_finished) {
        this.is_finished = is_finished;
    }

    public boolean isIs_all_day() {
        return is_all_day;
    }

    public int getReminder1Minutes() {
        return reminder1Minutes;
    }

    public void setReminder1Minutes(int reminder1Minutes) {
        this.reminder1Minutes = reminder1Minutes;
    }

    public int getReminder2Minutes() {
        return reminder2Minutes;
    }

    public void setReminder2Minutes(int reminder2Minutes) {
        this.reminder2Minutes = reminder2Minutes;
    }

    public int getReminder3Minutes() {
        return reminder3Minutes;
    }

    public void setReminder3Minutes(int reminder3Minutes) {
        this.reminder3Minutes = reminder3Minutes;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public int getRepeatLimit() {
        return repeatLimit;
    }

    public void setRepeatLimit(int repeatLimit) {
        this.repeatLimit = repeatLimit;
    }

    public int getRepeatRule() {
        return repeatRule;
    }

    public void setRepeatRule(int repeatRule) {
        this.repeatRule = repeatRule;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    //</editor-fold desc="getter and setter">

    @Override
    public String toString() {
        return "DBRoutine{" + "id=" + id + ", time=" + time + ", name='" + name + '\'' + ", user=" + user + ", url='" + url + '\'' + ", owner='" + owner + '\'' + ", title='" + title + '\'' + ", content='" + content + '\'' + ", label=" + label + ", tags=" + tags + ", created_datetime='" + created_datetime + '\'' + ", finished_datetime='" + finished_datetime + '\'' + ", is_finished=" + is_finished + ", is_all_day=" + is_all_day + ", begin_datetime='" + begin_datetime + '\'' + ", end_datetime='" + end_datetime + '\'' + ", reminder1Minutes=" + reminder1Minutes + ", reminder2Minutes=" + reminder2Minutes + ", reminder3Minutes=" + reminder3Minutes + ", repeatInterval=" + repeatInterval + ", repeatLimit=" + repeatLimit + ", repeatRule=" + repeatRule + ", lastUpdated=" + lastUpdated + ", location='" + location + '\'' + '}';
    }

}
