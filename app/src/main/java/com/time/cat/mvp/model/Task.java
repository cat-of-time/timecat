package com.time.cat.mvp.model;

import com.time.cat.util.string.TimeUtil;

import java.util.ArrayList;
import java.util.Date;

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
    private ArrayList<String> tags1;//一般标签
    private String plan;//一般标签
    private String created_datetime;//创建时间
    private String finished_datetime;//完成时间
    private String begin_datetime;//开始时间
    private String end_datetime;//结束时间
    private boolean is_all_day;//是否全天，1 - 是，0 - 不是
    private boolean is_finished;//是否完成，1 - 是，0 - 不是

    public Task() {
        // 默认
        label = 0;
//        created_datetime = TimeUtil.formatGMTDate(new Date());
        is_all_day = true;
        is_finished = false;
        // require
//        title = null;
//        owner = null;
//        tags = new ArrayList<>();
//        plan = "";
//        // optional
//        content = "";
//        url = "";
//        finished_datetime = "";
//        begin_datetime = "";
//        end_datetime = "";
    }

    public Task(String owner, String title) {
        new Task(owner, title, "", "", "", "", "", new ArrayList<String>(), "");
    }

    public Task(String owner, String title, String content) {
        new Task(owner, title, content, "", "", "", "", new ArrayList<String>(), "");
    }

    public Task(String owner, String title, String content, String url, String begin_datetime, String end_datetime,
                String finished_datetime, ArrayList<String> tags, String plan) {
        // 默认
        this.label = 0;
        this.created_datetime = TimeUtil.formatGMTDate(new Date());
        this.is_all_day = true;
        this.is_finished = false;
        // require
        this.title = title;
        this.owner = owner;
        // optional
        this.content = content;
        this.url = url;
        this.tags1 = tags;
        this.plan = plan;
        this.finished_datetime = finished_datetime;
        this.begin_datetime = begin_datetime;
        this.end_datetime = end_datetime;
    }

//    public Task(Task task) {
//        new Task(task.getOwner(), task.getTitle(), task.getContent(), task.getUrl(), task.getBegin_datetime(), task.getEnd_datetime(), task.getFinished_datetime(), task.getTags());
//    }

//    public Task(Task task, String title) {
//
//    }

    //<getter and setter>---------------------------------------------------------------------------
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
        return tags1;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags1 = tags;
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

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }
    //</getter and setter>---------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Task{" + "url='" + url + '\'' + ", title='" + title + '\'' + ", content='" + content + '\'' + ", owner='" + owner + '\'' + ", label=" + label + ", tags=" + tags1 + ", plan='" + plan + '\'' + ", created_datetime='" + created_datetime + '\'' + ", finished_datetime='" + finished_datetime + '\'' + ", begin_datetime='" + begin_datetime + '\'' + ", end_datetime='" + end_datetime + '\'' + ", is_all_day=" + is_all_day + ", is_finished=" + is_finished + '}';
    }
}
