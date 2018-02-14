package com.time.cat.mvp.model;

/**
 * @author dlink
 * @date 2018/2/6
 * @discription
 */
public class Account {
    long id;
    String nickname;
    String url;
    String user;

    public Account() {
        nickname = "temp";
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
