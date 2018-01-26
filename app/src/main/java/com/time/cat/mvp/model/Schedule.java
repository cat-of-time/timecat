package com.time.cat.mvp.model;

import java.util.Date;

/**
 * @author dlink
 * @date 2018/1/25
 * @discription 日程类
 */
public class Schedule {
    private static final long serialVersionUID = 1L;
    private String id;//ID
    private String title;//日程内容
    private Date startTime;//开始时间
    private Date endTime;//结束时间
    private String allDay;//是否全天，1 - 是，0 - 不是
    private String color;//颜色
    private String userID;//用户ID
    private String isFinish;//是否完成，1 - 是，0 - 不是
    private Date createTime;//创建时间

    public Schedule() {}

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getAllDay() {
        return this.allDay;
    }

    public void setAllDay(String allDay) {
        this.allDay = allDay;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }
}
