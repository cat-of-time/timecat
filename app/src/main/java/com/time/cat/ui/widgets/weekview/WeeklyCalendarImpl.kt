package com.time.cat.ui.widgets.weekview

import android.content.Context
import com.time.cat.data.WEEK_SECONDS
import com.time.cat.data.model.DBmodel.Event
import com.time.cat.ui.modules.schedules_weekview.WeeklyCalendar
import java.util.*

class WeeklyCalendarImpl(val mCallback: WeeklyCalendar, val mContext: Context) {
    var mEvents = ArrayList<Event>()

    fun updateWeeklyCalendar(weekStartTS: Int) {
        val startTS = weekStartTS
        val endTS = startTS + WEEK_SECONDS
//        mContext.dbHelper.getEvents(startTS, endTS) {
//            mEvents = it as ArrayList<Event>
//            mCallback.updateWeeklyCalendar(it)
//        }TODO
        mCallback.updateWeeklyCalendar(mEvents)
    }
}
