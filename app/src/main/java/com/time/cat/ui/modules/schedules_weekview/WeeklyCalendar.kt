package com.time.cat.ui.modules.schedules_weekview

import com.time.cat.data.model.DBmodel.DBTask

interface WeeklyCalendar {
    fun updateWeeklyCalendar(events: ArrayList<DBTask>)
}
