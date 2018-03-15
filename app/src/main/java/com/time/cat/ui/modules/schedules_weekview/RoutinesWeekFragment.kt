package com.time.cat.ui.modules.schedules_weekview

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.time.cat.R
import com.time.cat.config
import com.time.cat.data.WEEK_MILLI_SECONDS
import com.time.cat.data.WEEK_START_DATE_TIME
import com.time.cat.helper.Formatter
import com.time.cat.helper.seconds
import com.time.cat.ui.activity.main.MainActivity
import com.time.cat.ui.base.BaseFragment
import com.time.cat.ui.base.mvpframework.presenter.BaseMvpPresenter
import com.time.cat.ui.base.mvpframework.view.BaseMvpView
import com.time.cat.ui.widgets.weekview.MyScrollView
import kotlinx.android.synthetic.main.fragment_schedules_weekview_holder.*
import kotlinx.android.synthetic.main.fragment_schedules_weekview_holder.view.*
import org.joda.time.DateTime

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/15
 * @description null
 * @usage null
 */
class SchedulesWeekFragment : BaseFragment<BaseMvpView, BaseMvpPresenter<BaseMvpView>>(), WeekFragmentListener {
    private val PREFILLED_WEEKS = 61

    private var weekHolder: ViewGroup? = null
    private var defaultWeeklyPage = 0
    private var thisWeekTS = 0L
    private var currentWeekTS = 0L
    private var isGoToTodayVisible = false
    private var weekScrollY = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dateTimeString = arguments?.getString(WEEK_START_DATE_TIME) ?: getThisWeekDateTime()
        currentWeekTS = (DateTime.parse(dateTimeString) ?: DateTime()).seconds()
        thisWeekTS = currentWeekTS
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        weekHolder = inflater.inflate(R.layout.fragment_schedules_weekview_holder, container, false) as ViewGroup
        weekHolder!!.background = ColorDrawable(context!!.config.backgroundColor)
        setupFragment()
        return weekHolder
    }

    private fun setupFragment() {
        val weekTSs = getWeekTimestamps(currentWeekTS)
        val weeklyAdapter = MyWeekPagerAdapter(activity!!.supportFragmentManager, weekTSs, this)

        val textColor = context!!.config.textColor
        weekHolder!!.week_view_hours_holder.removeAllViews()
        val hourDateTime = DateTime().withDate(2000, 1, 1).withTime(0, 0, 0, 0)
        for (i in 1..23) {
            val formattedHours = Formatter.getHours(context!!, hourDateTime.withHourOfDay(i))
            (layoutInflater.inflate(R.layout.weekly_view_hour_textview, null, false) as TextView).apply {
                text = formattedHours
                setTextColor(textColor)
                weekHolder!!.week_view_hours_holder.addView(this)
            }
        }

        defaultWeeklyPage = weekTSs.size / 2
        weekHolder!!.week_view_view_pager.apply {
            adapter = weeklyAdapter
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    currentWeekTS = weekTSs[position]
                    val shouldGoToTodayBeVisible = shouldGoToTodayBeVisible()
                    if (isGoToTodayVisible != shouldGoToTodayBeVisible) {
//                        (activity as? MainActivity)?.toggleGoToTodayVisibility(shouldGoToTodayBeVisible)TODO
                        isGoToTodayVisible = shouldGoToTodayBeVisible
                    }

                    setupWeeklyActionbarTitle(weekTSs[position])
                }
            })
            currentItem = defaultWeeklyPage
        }

        weekHolder!!.week_view_hours_scrollview.setOnScrollviewListener(object : MyScrollView.ScrollViewListener {
            override fun onScrollChanged(scrollView: MyScrollView, x: Int, y: Int, oldx: Int, oldy: Int) {
                weekScrollY = y
                weeklyAdapter.updateScrollY(week_view_view_pager.currentItem, y)
            }
        })
        weekHolder!!.week_view_hours_scrollview.setOnTouchListener { _, motionEvent -> true }
        updateActionBarTitle()
    }

    private fun getThisWeekDateTime(): String {
        var thisweek = DateTime().withDayOfWeek(1).withTimeAtStartOfDay().minusDays(if (context!!.config.isSundayFirst) 1 else 0)
        if (DateTime().minusDays(7).seconds() > thisweek.seconds()) {
            thisweek = thisweek.plusDays(7)
        }
        return thisweek.toString()
    }

    private fun getWeekTimestamps(targetSeconds: Long): List<Long> {
        val weekTSs = ArrayList<Long>(PREFILLED_WEEKS)
        for (i in (- PREFILLED_WEEKS / 2)..(PREFILLED_WEEKS / 2)) {
            weekTSs.add(Formatter.getDateTimeFromTS(targetSeconds).plusWeeks(i).seconds())
        }
        return weekTSs
    }

    private fun setupWeeklyActionbarTitle(timestamp: Long) {
        val startDateTime = Formatter.getDateTimeFromTS(timestamp)
        val endDateTime = Formatter.getDateTimeFromTS(timestamp + WEEK_MILLI_SECONDS)
        val startMonthName = Formatter.getMonthName(context!!, startDateTime.monthOfYear)
        if (startDateTime.monthOfYear == endDateTime.monthOfYear) {
            var newTitle = startMonthName
            if (startDateTime.year != DateTime().year) {
                newTitle += " - ${startDateTime.year}"
            }
            (activity as? MainActivity)?.supportActionBar?.title = newTitle
        } else {
            val endMonthName = Formatter.getMonthName(context!!, endDateTime.monthOfYear)
            (activity as? MainActivity)?.supportActionBar?.title = "$startMonthName - $endMonthName"
        }
        (activity as? MainActivity)?.supportActionBar?.subtitle = "${getString(R.string.week)} ${startDateTime.plusDays(3).weekOfWeekyear}"
    }

    fun goToToday() {
        currentWeekTS = thisWeekTS
        setupFragment()
    }

    fun refreshEvents() {
        val viewPager = weekHolder?.week_view_view_pager
        (viewPager?.adapter as? MyWeekPagerAdapter)?.updateCalendars(viewPager.currentItem)
    }

    fun shouldGoToTodayBeVisible() = currentWeekTS != thisWeekTS

    fun updateActionBarTitle() {
        setupWeeklyActionbarTitle(currentWeekTS)
    }

    fun getNewEventDayCode() = Formatter.getDayCodeFromTS(currentWeekTS)

    override fun scrollTo(y: Int) {
        weekHolder!!.week_view_hours_scrollview.scrollY = y
        weekScrollY = y
    }

    override fun updateHoursTopMargin(margin: Int) {
        weekHolder?.week_view_hours_divider?.layoutParams?.height = margin
        weekHolder?.week_view_hours_scrollview?.requestLayout()
    }

    override fun getCurrScrollY() = weekScrollY
}
