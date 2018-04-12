package com.time.cat.data.model.DBmodel;

import android.graphics.Color;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.time.cat.util.source.AvatarManager;

import java.io.Serializable;

/**
 * @author dlink
 * @date 2018/2/5
 * @discription 用户设置
 */
@DatabaseTable(tableName = "Settings")
public class DBSettings implements Serializable  {
    public static final String COLUMN_AVATAR = "Avatar";
    public static final String COLUMN_COLOR = "Color";

    @DatabaseField(columnName = COLUMN_AVATAR)
    private String avatar = AvatarManager.DEFAULT_AVATAR;

    @DatabaseField(columnName = COLUMN_COLOR)
    private int color = Color.parseColor("#3498db");

}
