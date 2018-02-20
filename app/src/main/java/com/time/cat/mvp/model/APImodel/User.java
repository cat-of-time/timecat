package com.time.cat.mvp.model.APImodel;

import com.time.cat.mvp.model.Account;

import java.util.ArrayList;

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
    private ArrayList<String> plans;
    private ArrayList<String> tags;
    private ArrayList<String> tasks;

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
        return "User{" + "id=" + id + ", url='" + url + '\'' + ", username='" + username + '\'' + ", email='" + email + '\'' + ", password='" + password + '\'' + ", is_staff=" + is_staff + ", account=" + account + ", plans=" + plans + ", tags=" + tags + ", tasks=" + tasks + '}';
    }
}
