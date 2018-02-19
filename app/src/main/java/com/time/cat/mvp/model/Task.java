package com.time.cat.mvp.model;

import java.util.Date;
import java.util.List;

/**
 * @author dlink
 * @date 2018/1/25
 * @discription 日程类
 * @usage [
 * {
 * "url": "http://example.com",
"owner": "http://example.com",
"created_datetime": "2018-02-19T16:11:11Z",
"plan": "http://example.com",
"title": "string",
"content": "string",
"label": 0,
"tags": [
"http://example.com"
],
"is_finished": true,
"finished_datetime": "2018-02-19T16:11:11Z",
"is_all_day": true,
"begin_datetime": "2018-02-19T16:11:11Z",
"end_datetime": "2018-02-19T16:11:11Z"
}
]
 */
public class Task {
    public static final int LABEL_IMPORTANT_URGENT = 0;
    public static final int LABEL_IMPORTANT_NOT_URGENT = 1;
    public static final int LABEL_NOT_IMPORTANT_URGENT = 2;
    public static final int LABEL_NOT_IMPORTANT_NOT_URGENT = 3;

    private static final long serialVersionUID = 1L;

    private String url;// task的url 访问该url可返回该task
    private String title;//日程标题
    private String content;//日程内容
    private String owner;//用户ID
    private int label;//重要紧急标签
    private List<String> tags;//一般标签
    private Date created_datetime;//创建时间
    private Date finished_datetime;//完成时间
    private Date begin_datetime;//开始时间
    private Date end_datetime;//结束时间
    private boolean is_all_day;//是否全天，1 - 是，0 - 不是
    private boolean is_finished;//是否完成，1 - 是，0 - 不是

    public Task() {
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getBegin_datetime() {
        return this.begin_datetime;
    }

    public void setBegin_datetime(Date begin_datetime) {
        this.begin_datetime = begin_datetime;
    }

    public Date getEnd_datetime() {
        return this.end_datetime;
    }

    public void setEnd_datetime(Date end_datetime) {
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

    public Date getCreated_datetime() {
        return this.created_datetime;
    }

    public void setCreated_datetime(Date created_datetime) {
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Date getFinished_datetime() {
        return finished_datetime;
    }

    public void setFinished_datetime(Date finished_datetime) {
        this.finished_datetime = finished_datetime;
    }
}
