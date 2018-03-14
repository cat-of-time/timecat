package com.time.cat.ui.widgets.weekview

import android.content.Context
import com.time.cat.data.WEEK_SECONDS
import com.time.cat.data.database.DB
import com.time.cat.data.model.DBmodel.DBTask
import com.time.cat.ui.modules.schedules_weekview.WeeklyCalendar

class WeeklyCalendarImpl(val mCallback: WeeklyCalendar, val mContext: Context) {
    var mEvents = DB.schedules().findAll()

    fun updateWeeklyCalendar(weekStartTS: Int) {
        val startTS = weekStartTS
        val endTS = startTS + WEEK_SECONDS
//        mContext.dbHelper.getEvents(startTS, endTS) {
//            mEvents = it as ArrayList<Event>
//            mCallback.updateWeeklyCalendar(it)
//        }TODO
        mCallback.updateWeeklyCalendar(mEvents as ArrayList<DBTask>)
    }
}
