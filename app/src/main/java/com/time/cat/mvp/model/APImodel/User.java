package com.time.cat.mvp.model.APImodel;

import com.time.cat.mvp.model.Account;

/**
 * @author dlink
 * @date 2018/2/6
 * @discription 网络请求用的model
 */
public class User {
    private long id;
    private String url;
    private String username;
    private String email;
    private String password;
    private boolean is_staff;
    private Account account;
    private String[] plans;
    private String[] tags;
    private String[] tasks;

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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public boolean isIs_staff() {
        return is_staff;
    }

    public void setIs_staff(boolean is_staff) {
        this.is_staff = is_staff;
    }

    public String[] getPlans() {
        return plans;
    }

    public void setPlans(String[] plans) {
        this.plans = plans;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getTasks() {
        return tasks;
    }

    public void setTasks(String[] tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", url='" + url + '\'' + ", account=" + account + ", username='" + username + '\'' + ", email='" + email + '\'' + ", password='" + password + '\'' + ", is_staff=" + is_staff + ", plans='" + plans + '\'' + ", tags='" + tags + '\'' + '}';
    }
}
