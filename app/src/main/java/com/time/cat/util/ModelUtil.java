package com.time.cat.util;

import com.time.cat.ThemeSystem.manager.ThemeManager;
import com.time.cat.mvp.model.APImodel.User;
import com.time.cat.mvp.model.DBmodel.DBUser;

/**
 * @author dlink
 * @date 2018/2/6
 * @discription
 */
public class ModelUtil {
    public static DBUser toDBUser(User u) {
        DBUser dbUser = new DBUser();
        dbUser.setEmail(u.getEmail());
        dbUser.setName(u.getUsername());
        dbUser.setAvatar(AvatarMgr.AVATAR_4);
        dbUser.setColor(ThemeManager.CARD_THEME_0);
        dbUser.setDefault(true);
        return dbUser;
    }
}
