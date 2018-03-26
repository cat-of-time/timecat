package com.time.cat.ui.modules.week_view.listener

import com.time.cat.data.model.DBmodel.DBRoutine

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/15
 * @description null
 * @usage null
 */
interface WeekCalendarCallback {
    fun updateWeeklyCalendar(dbRoutines : ArrayList<DBRoutine>)
}
