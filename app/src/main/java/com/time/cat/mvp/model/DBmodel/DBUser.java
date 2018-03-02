/*
 *    Calendula - An assistant for personal medication management.
 *    Copyright (C) 2016 CITIUS - USC
 *
 *    Calendula is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.time.cat.mvp.model.DBmodel;

import android.graphics.Color;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.time.cat.util.source.AvatarManager;

import java.util.ArrayList;


/**
 * @author dlink
 * @date 2018/1/25
 * @discription 用户类
 */
@DatabaseTable(tableName = "Users")
public class DBUser {

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_DEFAULT = "Default";
    public static final String COLUMN_AVATAR = "Avatar";
    public static final String COLUMN_COLOR = "Color";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_PASSWORD = "Password";
    public static final String COLUMN_PLANS = "Plans";
    public static final String COLUMN_TAGS = "Tags";
    public static final String COLUMN_TASKS = "Tasks";

//TODO setting={}

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private Long id;

    @DatabaseField(columnName = COLUMN_NAME)
    private String name;

    @DatabaseField(columnName = COLUMN_DEFAULT)
    private boolean isDefault = false;

    @DatabaseField(columnName = COLUMN_AVATAR)
    private String avatar = AvatarManager.DEFAULT_AVATAR;

    @DatabaseField(columnName = COLUMN_COLOR)
    private int color = Color.parseColor("#3498db");
    // color是16进制数的int型表示，使用时可直接作color资源

    @DatabaseField(columnName = COLUMN_EMAIL, canBeNull = false, unique = true)
    private String email;

    @DatabaseField(columnName = COLUMN_PASSWORD)
    private String password;

    @DatabaseField(columnName = COLUMN_PLANS, dataType = DataType.SERIALIZABLE)
    private ArrayList<String> plans;
    @DatabaseField(columnName = COLUMN_TAGS, dataType = DataType.SERIALIZABLE)
    private ArrayList<String> tags;
    @DatabaseField(columnName = COLUMN_TASKS, dataType = DataType.SERIALIZABLE)
    private ArrayList<String> tasks;
//    @ForeignCollectionField
//    private Collection<DBTask> DBTasks;

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String avatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int color() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getPlans() {
        return plans;
    }

    public void setPlans(ArrayList<String> plans) {
        this.plans = plans;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<String> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "DBUser{" + "id=" + id + ", name='" + name + '\'' + ", isDefault=" + isDefault + ", avatar='" + avatar + '\'' + ", color=" + color + ", email='" + email + '\'' + ", password='" + password + '\'' + ", plans=" + plans + ", tags=" + tags + ", tasks=" + tasks + '}';
    }
}
