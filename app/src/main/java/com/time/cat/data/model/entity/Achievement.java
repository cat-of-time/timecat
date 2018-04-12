package com.time.cat.data.model.entity;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/24
 * @discription null
 * @usage null
 */
public class Achievement {
    private int _id;
    private String name;
    private String describe;
    private int complete; // 成就是否获得 ? 获得==true : 没获得==false
    private int defaultImgRes; // 默认没获得
    private int completeImgRes; // 获得成就后的图片
    private int shareCompleteImgRes;
    private int shareDefaultImgRes;

    public int getDefaultImgRes() {
        return defaultImgRes;
    }

    public void setDefaultImgRes(int defaultImgRes) {
        this.defaultImgRes = defaultImgRes;
    }

    public int getCompleteImgRes() {
        return completeImgRes;
    }

    public void setCompleteImgRes(int completeImgRes) {
        this.completeImgRes = completeImgRes;
    }

    public int getShareCompleteImgRes() {
        return shareCompleteImgRes;
    }

    public void setShareCompleteImgRes(int shareCompleteImgRes) {
        this.shareCompleteImgRes = shareCompleteImgRes;
    }

    public int getShareDefaultImgRes() {
        return shareDefaultImgRes;
    }

    public void setShareDefaultImgRes(int shareDefaultImgRes) {
        this.shareDefaultImgRes = shareDefaultImgRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public int get_id() {
        return _id;

    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
