package com.time.cat.ui.widgets.weekview

import android.content.Context
import com.time.cat.data.WEEK_MILLI_SECONDS
import com.time.cat.data.database.DB
import com.time.cat.data.model.DBmodel.DBTask
import com.time.cat.ui.modules.schedules_weekview.WeeklyCalendar

class WeeklyCalendarImpl(val mCallback: WeeklyCalendar, val mContext: Context) {
    var dbTasks = DB.schedules().findAll()

    fun updateWeeklyCalendar(weekStartTS: Long) {
        val startTS = weekStartTS
        val endTS = startTS + WEEK_MILLI_SECONDS
//        mContext.dbHelper.getEvents(startTS, endTS) {
//            mEvents = it as ArrayList<Event>
//            mCallback.updateWeeklyCalendar(it)
//        }TODO
        mCallback.updateWeeklyCalendar(dbTasks as ArrayList<DBTask>)
    }
}
