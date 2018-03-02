package com.time.cat.mvp.model;

import java.io.Serializable;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/22
 * @discription null
 * @usage null
 */
public class Note implements Serializable {
    private static final long serialVersionUID = 2L;

    private String created_datetime;
    private String url;// note的url 访问该url可返回该note
    private String title;//笔记标题
    private String content;//笔记内容
    private String owner;//用户ID
    private String update_datetime;

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    @Override
    public String toString() {
        return "Note{" + "url='" + url + '\'' + ", title='" + title + '\'' + ", content='" + content + '\'' + ", owner='" + owner + '\'' + '}';
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
}
