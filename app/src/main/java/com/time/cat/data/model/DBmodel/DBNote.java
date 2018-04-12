package com.time.cat.data.model.DBmodel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.time.cat.data.database.DB;

import java.io.Serializable;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/22
 * @discription null
 * @usage null
 */
@DatabaseTable(tableName = "Notes")
public class DBNote implements Serializable {
    //<editor-fold desc="Field">
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_URL = "Url";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_CONTENT = "Content";
    public static final String COLUMN_OWNER = "Owner";
    public static final String COLUMN_CREATED_DATETIME = "created_datetime";
    public static final String COLUMN_UPDATE_DATETIME = "update_datetime";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_USER = "user_id";
    //</editor-fold desc="Field">

    //<editor-fold desc="Database Field">
    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private long id;

    @DatabaseField(columnName = COLUMN_URL)
    private String url;// note的url 访问该url可返回该note

    @DatabaseField(columnName = COLUMN_TITLE)
    private String title;//笔记标题

    @DatabaseField(columnName = COLUMN_CONTENT)
    private String content;//笔记内容

    @DatabaseField(columnName = COLUMN_OWNER)
    private String owner;//用户ID

    @DatabaseField(columnName = COLUMN_CREATED_DATETIME)
    private String created_datetime;

    @DatabaseField(columnName = COLUMN_UPDATE_DATETIME)
    private String update_datetime;

    @DatabaseField(columnName = COLUMN_COLOR)
    private int color;

    @DatabaseField(columnName = COLUMN_USER, foreign = true, foreignAutoRefresh = true)
    private DBUser user = DB.users().getActive();
    //</editor-fold desc="Database Field">

    //<editor-fold desc="getter and setter">
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

    public DBUser getUser() {
        return user;
    }

    public void setActiveUser() {
        this.user = DB.users().getActive();
    }

    public void setUser(DBUser user) {
        this.user = user;
    }

    //</editor-fold desc="getter and setter">

    @Override
    public String toString() {
        return "DBNote{" + "id=" + id + ", url='" + url + '\'' + ", title='" + title + '\'' + ", content='" + content + '\'' + ", owner='" + owner + '\'' + ", created_datetime='" + created_datetime + '\'' + ", update_datetime='" + update_datetime + '\'' + ", color=" + color + ", user=" + user + '}';
    }
}
