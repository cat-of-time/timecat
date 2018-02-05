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
import android.util.Log;

import com.time.cat.database.DB;
import com.time.cat.database.Routine;
import com.time.cat.database.Schedule;
import com.time.cat.mvp.model.User;

import org.joda.time.LocalTime;

/**
 * @author dlink
 * @date 2018/2/3
 * @discription DefaultDataGenerator
 */
public class DefaultDataGenerator {

    public static void fillDBWithDummyData(Context ctx) {
        Resources r = ctx.getResources();
        if (Routine.findAll().size() == 0 && Schedule.findAll().size() == 0) {
            try {
                Log.d("DefaultDataGenerator", "Creating dummy data...");
                User u = DB.users().getActive(ctx);
                new Routine(u, new LocalTime(9, 0), "breakfast").save();
                new Routine(u, new LocalTime(13, 0), "lunch").save();
                new Routine(u, new LocalTime(21, 0), "dinner").save();
                Log.d("DefaultDataGenerator", "Dummy data saved successfully!");
            } catch (Exception e) {
                Log.e("DefaultDataGenerator", "Error filling db with dummy data!", e);
            }
        }
    }

    public static void generateDefaultRoutines(User p, Context ctx){
        Resources r = ctx.getResources();
        new Routine(p, new LocalTime(9, 0), "breakfast").save();
        new Routine(p, new LocalTime(13, 0), "lunch").save();
        new Routine(p, new LocalTime(21, 0), "dinner").save();
    }

}
