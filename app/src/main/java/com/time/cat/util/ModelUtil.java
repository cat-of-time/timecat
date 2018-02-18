package com.time.cat.util;

import com.time.cat.ThemeSystem.ThemeManager;
import com.time.cat.mvp.model.APImodel.User;
import com.time.cat.mvp.model.DBmodel.DBUser;
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
}
