package com.time.cat.data.model.APImodel;

import com.time.cat.data.model.DBmodel.DBTask;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/28
 * @discription null
 * @usage null
 */
public class SubPlan implements Serializable {

    private long id;
    private String url;// note的url 访问该url可返回该note
    private String title;//笔记标题
    private String content;//笔记内容
    private String owner;//用户ID
    private String created_datetime;
    private String update_datetime;
    private int color;
    private ArrayList<DBTask> tasks;

    public SubPlan(String s1, String s2, String s3) {
        this.url = s1;
        this.title = s2;
        this.content = s3;
    }

    public SubPlan() {
        this.id = -1;
        this.url = "";
        this.title = "";
        this.content = "";
        this.owner = "";
        this.created_datetime = "";
        this.update_datetime = "";
        this.color = 0;
        this.tasks = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCreated_datetime() {
        return created_datetime;
    }

    public void setCreated_datetime(String created_datetime) {
        this.created_datetime = created_datetime;
    }

    public String getUpdate_datetime() {
        return update_datetime;
    }

    public void setUpdate_datetime(String update_datetime) {
        this.update_datetime = update_datetime;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ArrayList<DBTask> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<DBTask> tasks) {
        this.tasks = tasks;
    }

}
