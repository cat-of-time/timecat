package com.time.cat.data.model.APImodel;

import java.util.ArrayList;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/20
 * @discription null
 * @usage null
 */
public class Tag {
    private String url;
    private String owner;
    private String created_datetime;
    private String name;
    private ArrayList<String> tasks;

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

    public String getCreated_datetime() {
        return created_datetime;
    }

    public void setCreated_datetime(String created_datetime) {
        this.created_datetime = created_datetime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<String> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Tag{" + "url='" + url + '\'' + ", owner='" + owner + '\'' + ", created_datetime='" + created_datetime + '\'' + ", name='" + name + '\'' + ", tasks=" + tasks + '}';
    }
}
