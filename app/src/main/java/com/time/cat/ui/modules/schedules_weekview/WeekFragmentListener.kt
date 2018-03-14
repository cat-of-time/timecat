package com.time.cat.ui.modules.schedules_weekview

interface WeekFragmentListener {
    fun scrollTo(y: Int)

    fun updateHoursTopMargin(margin: Int)

    fun getCurrScrollY(): Int
}
