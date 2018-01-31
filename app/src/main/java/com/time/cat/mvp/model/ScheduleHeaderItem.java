package com.time.cat.mvp.model;

/**
 * @author dlink
 * @date 2018/2/1
 * @discription
 */
public class ScheduleHeaderItem {
    private String title;//日程标题
    private int label;//重要紧急标签

    public ScheduleHeaderItem() {
        new ScheduleHeaderItem("", 0);
    }

    public ScheduleHeaderItem(String title, int label) {
        this.title = title;
        this.label = label;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
