package com.time.cat.data.model.APImodel;

import com.time.cat.data.database.DB;
import com.time.cat.data.model.DBmodel.DBUser;

import java.io.Serializable;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/6
 * @discription null
 * @usage null
 */
public class Pomodoro implements Serializable {

    //<editor-fold desc="Database Field">
    private long id;
    private String created_datetime;//创建时间
    private String begin_datetime;//开始时间
    private String end_datetime;//结束时间
    private long duration;//任务时长
    private String date_add;//添加时间
    private DBUser user = DB.users().getActive();
    private String url;// routine的url 访问该url可返回该routine
    private String owner;//用户ID
    //</editor-fold desc="Database Field">

    //<editor-fold desc="getter and setter">

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
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

    public String getCreated_datetime() {
        return created_datetime;
    }

    public void setCreated_datetime(String created_datetime) {
        this.created_datetime = created_datetime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    //</editor-fold desc="getter and setter">


    @Override
    public String toString() {
        return "Pomodoro{" + "id=" + id + ", created_datetime='" + created_datetime + '\'' + ", begin_datetime='" + begin_datetime + '\'' + ", end_datetime='" + end_datetime + '\'' + ", duration='" + duration + '\'' + ", date_add='" + date_add + '\'' + ", user=" + user + ", url='" + url + '\'' + ", owner='" + owner + '\'' + '}';
    }
}
