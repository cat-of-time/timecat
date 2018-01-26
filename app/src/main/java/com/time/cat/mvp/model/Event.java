package com.time.cat.mvp.model;

/**
 * @author dlink
 * @date 2018/1/25
 * @discription POST/GET 状态
 */
public class Event {
    private String id;
    private String title;
    private String start;
    private String end;
    private String color;
    private String allDay;
    private String className;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAllDay() {
        return allDay;
    }

    public void setAllDay(String allDay) {
        this.allDay = allDay;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}