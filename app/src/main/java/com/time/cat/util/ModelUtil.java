package com.time.cat.util;

import com.time.cat.ThemeSystem.ThemeManager;
import com.time.cat.mvp.model.APImodel.User;
import com.time.cat.mvp.model.DBmodel.DBTask;
import com.time.cat.mvp.model.DBmodel.DBUser;
import com.time.cat.mvp.model.Task;
import com.time.cat.util.source.AvatarManager;

/**
 * @author dlink
 * @date 2018/2/6
 * @discription 实现本地数据库model和网络请求model的转化
 */
public class ModelUtil {
    public static DBUser toDBUser(User u) {
        DBUser dbUser = new DBUser();
        dbUser.setEmail(u.getEmail());
        dbUser.setName(u.getUsername());
        dbUser.setAvatar(AvatarManager.AVATAR_4);
        dbUser.setColor(ThemeManager.CARD_THEME_0);
        dbUser.setDefault(true);
        return dbUser;
    }
    public static User toAPIUser(DBUser dbUser) {
        User user = new User();
        user.setEmail(dbUser.getEmail());
        user.setUsername(dbUser.name());
        user.setPassword(dbUser.getPassword());
        return user;
    }

    public static DBTask toDBTask(Task task) {
        DBTask dbTask = new DBTask();
//        dbTask.
        return dbTask;
    }
}
