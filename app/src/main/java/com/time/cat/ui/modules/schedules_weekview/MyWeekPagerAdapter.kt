package com.time.cat.ui.modules.schedules_weekview

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.SparseArray
import com.time.cat.data.WEEK_START_TIMESTAMP

class MyWeekPagerAdapter(fm: FragmentManager, val mWeekTimestamps: List<Int>, val mListener: WeekFragmentListener) : FragmentStatePagerAdapter(fm) {
    private val mFragments = SparseArray<WeekFragment>()

    override fun getCount() = mWeekTimestamps.size

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        val weekTimestamp = mWeekTimestamps[position]
        bundle.putInt(WEEK_START_TIMESTAMP, weekTimestamp)

        val fragment = WeekFragment()
        fragment.arguments = bundle
        fragment.mListener = mListener

        mFragments.put(position, fragment)
        return fragment
    }

    fun updateScrollY(pos: Int, y: Int) {
        mFragments[pos - 1]?.updateScrollY(y)
        mFragments[pos + 1]?.updateScrollY(y)
    }

    fun updateCalendars(pos: Int) {
        for (i in -1..1) {
            mFragments[pos + i]?.updateCalendar()
        }
    }
}
