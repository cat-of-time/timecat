/*
 *    Calendula - An assistant for personal medication management.
 *    Copyright (C) 2016 CITIUS - USC
 *
 *    Calendula is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.time.cat.test;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;

import com.time.cat.data.database.DB;
import com.time.cat.data.model.Converter;
import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.data.model.DBmodel.DBSubPlan;
import com.time.cat.data.model.DBmodel.DBTask;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.util.override.LogUtil;
import com.time.cat.util.string.TimeUtil;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * @author dlink
 * @date 2018/2/3
 * @discription DefaultDataGenerator
 */
public class DefaultDataGenerator {

    public static void fillDBWithDummyData(Context ctx) {
        Resources r = ctx.getResources();
        if (DB.plans().findAll().size() == 0 && DB.schedules().findAll().size() == 0) {
            try {
                Log.i("DefaultDataGenerator", "Creating dummy data...");
                DBUser u = DB.users().getActive();
                DBPlan dbPlan = new DBPlan();
                dbPlan.setIs_star(1);
                dbPlan.setTitle("抗焦虑锦囊");
                dbPlan.setContent("在夜晚失眠时把担心的事放到锦囊里，在失去动力成为咸鱼时打开锦囊，不忘初心，做真正的自己");
                dbPlan.setCoverImageUrl("http://img.hb.aicdn.com/3f04db36f22e2bf56d252a3bc1eacdd2a0416d75221a7c-rpihP1_fw658");
                dbPlan.setCreated_datetime(TimeUtil.formatGMTDate(new Date()));
                dbPlan.setUpdate_datetime(dbPlan.getCreated_datetime());
                dbPlan.setOwner(Converter.getOwnerUrl(u));
                dbPlan.setActiveUser();
                DB.plans().safeSaveDBPlan(dbPlan);
                for (int i = 0; i < 3; i++) {
                    DBSubPlan subPlan = new DBSubPlan();
                    subPlan.setTitle("子计划" + i);
                    subPlan.setContent("子计划的描述");
                    subPlan.setCreated_datetime(TimeUtil.formatGMTDate(new Date()));
                    subPlan.setUpdate_datetime(subPlan.getCreated_datetime());
                    subPlan.setColor(Color.BLUE);
                    subPlan.setOwner(dbPlan.getOwner());
                    subPlan.setPlan(dbPlan);
                    subPlan.setActiveUser();
                    DB.subPlans().safeSaveDBSubPlan(subPlan);
                    for (int j=0; j<4; j++) {
                        DBTask dbTask = new DBTask();
                        dbTask.setTitle("计划"+i+"的task");
                        dbTask.setContent("计划"+i+"的task的content");
                        dbTask.setCreated_datetime(TimeUtil.formatGMTDate(new Date()));

                        dbTask.setIs_all_day(false);
                        DateTime date = new DateTime();
                        date = date.plusYears(50);
                        Date d = new Date();
                        d.setYear(d.getYear() + 50);
                        d.setMonth(date.getMonthOfYear());
                        d.setDate(date.getDayOfMonth());
                        dbTask.setBegin_datetime(TimeUtil.formatGMTDate(d));
                        dbTask.setEnd_datetime(TimeUtil.formatGMTDate(d));

                        dbTask.setLabel(0);
                        dbTask.setUser(u);
                        dbTask.setOwner(Converter.getOwnerUrl(u));
                        dbTask.setSubplan(subPlan);
                        dbTask.setActiveUser();
                        DB.schedules().safeSaveDBTask(dbTask);
                    }
                }
                LogUtil.i("DefaultDataGenerator", "Dummy data saved successfully!");
            } catch (Exception e) {
                LogUtil.e("DefaultDataGenerator", "Error filling db with dummy data!" + e);
            }
        }
    }

}
