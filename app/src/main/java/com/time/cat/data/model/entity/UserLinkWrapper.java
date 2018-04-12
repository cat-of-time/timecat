package com.time.cat.data.model.entity;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/16
 * @discription for UserDetailActivity
 * @usage null
 */
public class UserLinkWrapper {
    public String name;
    public String id;
    public String token;

    @Override
    public String toString() {
        return "UserLinkWrapper{" + "name='" + name + '\'' + ", id='" + id + '\'' + ", token='" + token + '\'' + '}';
    }
}
