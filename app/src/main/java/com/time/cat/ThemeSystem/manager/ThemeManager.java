/*
 * Copyright (C) 2016 Bilibili
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.time.cat.ThemeSystem.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author dlinking-lxy
 * @PackageName com.time.cat.ThemeSystem.manager
 * @date 17-7-5.
 * @discription 主题控制统一入口
 */
public class ThemeManager {
    public static final int CARD_SAKURA = 0xFF1565C0;
    public static final int CARD_STORM = 0xFF2196F3;
    public static final int CARD_HOPE = 0xFF673AB7;
    public static final int CARD_WOOD = 0xFF4CAF50;
    public static final int CARD_LIGHT = 0xFF8BC34A;
    public static final int CARD_THUNDER = 0xFFFDD835;
    public static final int CARD_SAND = 0xFFFF9800;
    public static final int CARD_FIRE = 0xFFF44336;
    public static final int CARD_WHITE = 0xFFFFFFFF;
    public static final int CARD_BLACK = 0xFF000000;
    public static final int CARD_GREY = 0xFF727272;
    public static final int CARD_MAGENTA = 0xFFff00ff;
    public static final int CARD_THEME_0 = 0xff1abc9c;
    public static final int CARD_THEME_1 = 0xff16a085;
    public static final int CARD_THEME_2 = 0xfff1c40f;
    public static final int CARD_THEME_3 = 0xfff39c12;
    public static final int CARD_THEME_4 = 0xff2ecc71;
    public static final int CARD_THEME_5 = 0xff27ae60;
    public static final int CARD_THEME_6 = 0xffe67e22;
    public static final int CARD_THEME_7 = 0xffd35400;
    public static final int CARD_THEME_8 = 0xffc0392b;
    public static final int CARD_THEME_9 = 0xffe74c3c;
    public static final int CARD_THEME_10 = 0xff2980b9;
    public static final int CARD_THEME_11 = 0xff3498db;
    public static final int CARD_THEME_12 = 0xff9b59b6;
    public static final int CARD_THEME_13 = 0xff8e44ad;
    public static final int CARD_THEME_14 = 0xff2c3e50;
    public static final int CARD_THEME_15 = 0xff34495e;
    private static final String TAG = "ThemeManager";
    private static final String CURRENT_THEME = "theme_current";

    public static SharedPreferences getSharePreference(Context context) {
        return context.getSharedPreferences("multiple_theme", Context.MODE_PRIVATE);
    }

    public static void setTheme(Context context, int themeId) {
        getSharePreference(context).edit().putInt(CURRENT_THEME, themeId).commit();
    }

    public static int getTheme(Context context) {
        return getSharePreference(context).getInt(CURRENT_THEME, CARD_SAKURA);
    }

    public static boolean isDefaultTheme(Context context) {
        return getTheme(context) == CARD_SAKURA;
    }

    public static String getName(int currentTheme) {
        switch (currentTheme) {
            case CARD_SAKURA:
                return "THE SAKURA";
            case CARD_STORM:
                return "THE STORM";
            case CARD_WOOD:
                return "THE WOOD";
            case CARD_LIGHT:
                return "THE LIGHT";
            case CARD_HOPE:
                return "THE HOPE";
            case CARD_THUNDER:
                return "THE THUNDER";
            case CARD_SAND:
                return "THE SAND";
            case CARD_FIRE:
                return "THE FIREY";
            case CARD_WHITE:
                return "THE WHITE";
            case CARD_BLACK:
                return "THE BLACK";
            case CARD_GREY:
                return "THE GRAY";
            case CARD_MAGENTA:
                return "THE MAGENTAT";

            case CARD_THEME_0:
                return "CARD_THEME_0";
            case CARD_THEME_1:
                return "CARD_THEME_1";
            case CARD_THEME_2:
                return "CARD_THEME_2";
            case CARD_THEME_3:
                return "CARD_THEME_3";
            case CARD_THEME_4:
                return "CARD_THEME_4";
            case CARD_THEME_5:
                return "CARD_THEME_5";
            case CARD_THEME_6:
                return "CARD_THEME_6";
            case CARD_THEME_7:
                return "CARD_THEME_7";
            case CARD_THEME_8:
                return "CARD_THEME_8";
            case CARD_THEME_9:
                return "CARD_THEME_9";
            case CARD_THEME_10:
                return "CARD_THEME_10";
            case CARD_THEME_11:
                return "CARD_THEME_11";
            case CARD_THEME_12:
                return "CARD_THEME_12";
            case CARD_THEME_13:
                return "CARD_THEME_13";
            case CARD_THEME_14:
                return "CARD_THEME_14";
            case CARD_THEME_15:
                return "CARD_THEME_15";
        }
        return "THE RETURN";
    }
}
