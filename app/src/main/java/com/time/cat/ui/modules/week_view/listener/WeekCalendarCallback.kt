package com.time.cat.ui.modules.schedules_weekview

import com.time.cat.data.model.DBmodel.DBTask

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/15
 * @description null
 * @usage null
 */
interface WeeklyCalendar {
    fun updateWeeklyCalendar(dbTasks : ArrayList<DBTask>)
}
