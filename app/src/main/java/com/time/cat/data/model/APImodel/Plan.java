package com.time.cat.data.model.APImodel;

import com.j256.ormlite.table.DatabaseTable;
import com.time.cat.data.model.DBmodel.DBSubPlan;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/28
 * @discription null
 * @usage null
 */
@DatabaseTable(tableName = "Plans")
public class Plan implements Serializable {

    private long id;
    private String url;// note的url 访问该url可返回该note
    private String title;//笔记标题
    private String content;//笔记内容
    private String owner;//用户ID
    private String created_datetime;
    private String update_datetime;
    private int color;
    private String image;
    private String coverImageUrl;
    private ArrayList<DBSubPlan> sub_plans;
    private int is_star = 0;

    public Plan(String s1, String s2, ArrayList<DBSubPlan> s3) {
        this.url = s1;
        this.title = s2;
        this.sub_plans = s3;
    }

    public Plan(JSONObject jsonObject) {
        this.title = jsonObject.optString("title");
        this.content = jsonObject.optString("content");
        this.created_datetime = jsonObject.optString("created_datetime");
        this.update_datetime = jsonObject.optString("update_datetime");
        this.owner = jsonObject.optString("coverImageUrl");
        this.coverImageUrl = jsonObject.optString("coverImageUrl");
        this.url = jsonObject.optString("mapImageUrl");
    }

    public Plan() {

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

    public ArrayList<DBSubPlan> getSub_plans() {
        return sub_plans;
    }

    public void setSub_plans(ArrayList<DBSubPlan> sub_plans) {
        this.sub_plans = sub_plans;
    }

    public int getIs_star() {
        return is_star;
    }

    public void setIs_star(int is_star) {
        this.is_star = is_star;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
}
