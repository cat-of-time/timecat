package com.time.cat.ui.widgets.weekview

import android.content.Context
import com.time.cat.data.Constants.WEEK_MILLI_SECONDS
import com.time.cat.data.database.DB
import com.time.cat.data.model.DBmodel.DBRoutine
import com.time.cat.ui.modules.week_view.listener.WeekCalendarCallback

class WeeklyCalendarImpl(val mCallback: WeekCalendarCallback, val mContext: Context) {
    var dbRoutines = DB.routines().findAll()

    fun updateWeeklyCalendar(weekStartTS: Long) {
        val startTS = weekStartTS
        val endTS = startTS + WEEK_MILLI_SECONDS
//        mContext.dbHelper.getEvents(startTS, endTS) {
//            mEvents = it as ArrayList<Event>
//            mCallback.updateWeeklyCalendar(it)
//        }TODO
        mCallback.updateWeeklyCalendar(dbRoutines as ArrayList<DBRoutine>)
    }
}
