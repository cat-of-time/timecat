package com.time.cat.ui.modules.week_view

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.time.cat.R
import com.time.cat.TimeCatApp
import com.time.cat.config
import com.time.cat.data.Constants.WEEK_MILLI_SECONDS
import com.time.cat.data.Constants.WEEK_START_DATE_TIME
import com.time.cat.data.model.events.PersistenceEvents
import com.time.cat.helper.Formatter
import com.time.cat.helper.seconds
import com.time.cat.ui.base.mvp.BaseLazyLoadFragment
import com.time.cat.ui.modules.main.MainActivity
import com.time.cat.ui.modules.routines.RoutinesFragment
import com.time.cat.ui.modules.week_view.listener.WeekFragmentListener
import com.time.cat.ui.widgets.weekview.MyScrollView
import com.time.cat.util.override.LogUtil
import kotlinx.android.synthetic.main.fragment_schedules_weekview_holder.*
import kotlinx.android.synthetic.main.fragment_schedules_weekview_holder.view.*
import org.joda.time.DateTime
import java.util.*

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/15
 * @description 生物钟页面的星期视图，用来管理课程、吃药时间、定期总结、定期运动、生物钟等
 * @usage null
 */
class RoutinesWeekFragment : BaseLazyLoadFragment<RoutinesWeekMVP.View, RoutinesWeekPresenter<RoutinesWeekMVP.View>>(), WeekFragmentListener, RoutinesFragment.OnScrollBoundaryDecider {

    override fun canRefresh(): Boolean = weekScrollY <= minScrollY

    override fun canLoadMore(): Boolean = weekScrollY >= maxScrollY

    override fun providePresenter(): RoutinesWeekPresenter<RoutinesWeekMVP.View> {
        return RoutinesWeekPresenter()
    }

    override fun initViews(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        weekHolder = inflater.inflate(R.layout.fragment_schedules_weekview_holder, container, false) as ViewGroup
        weekHolder!!.background = ColorDrawable(context!!.config.weekViewBackground)
        setupFragment()
        Handler().postDelayed({ presenter.refreshData() }, 500)
        TimeCatApp.eventBus().register(this)
        return weekHolder as ViewGroup
    }

    override fun hideProgress() {
        super.hideProgress()
        if (progressBar != null) progressBar.visibility = View.GONE
    }

    private val PREFILLED_WEEKS = 35
    private var mRowHeight = 0
    private var minScrollY = -1
    private var maxScrollY = -1
    private var weekHolder: ViewGroup? = null
    private var defaultWeeklyPage = 0
    private var thisWeekTS = 0L
    private var currentWeekTS = 0L
    private var isGoToTodayVisible = false
    private var weekScrollY = 0
    private val pendingEvents = LinkedList<Any>()
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dateTimeString = arguments?.getString(WEEK_START_DATE_TIME) ?: getThisWeekDateTime()
        currentWeekTS = (DateTime.parse(dateTimeString) ?: DateTime()).seconds()
        thisWeekTS = currentWeekTS
        handler = Handler()
        mRowHeight = (context!!.resources.getDimension(R.dimen.weekly_view_row_height)).toInt()
        minScrollY = mRowHeight * context!!.config.startWeeklyAt
        maxScrollY = mRowHeight * context!!.config.endWeeklyAt
    }

    override fun onResume() {
        super.onResume()
        while (!pendingEvents.isEmpty()) {
            LogUtil.e("Processing pending event...")
            onEvent(pendingEvents.poll())
        }
    }

    private fun setupFragment() {
        val weekTSs = getWeekTimestamps(currentWeekTS)
        val weeklyAdapter = WeekPagerAdapter(activity!!.supportFragmentManager, weekTSs, this)

        val textColor = context!!.config.weekViewTextColor
        weekHolder!!.week_view_hours_holder.removeAllViews()
        val hourDateTime = DateTime().withDate(2000, 1, 1).withTime(0, 0, 0, 0)
        for (i in 1..23) {
            val formattedHours = Formatter.getHours(context!!, hourDateTime.withHourOfDay(i))
            (layoutInflater.inflate(R.layout.week_view_hour_textview, null, false) as TextView).apply {
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

//                    setupWeeklyActionbarTitle(weekTSs[position])
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
        weekHolder!!.week_view_hours_scrollview.setOnTouchListener { _, _ -> true }
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
        (viewPager?.adapter as? WeekPagerAdapter)?.updateCalendars(viewPager.currentItem)
    }

    fun shouldGoToTodayBeVisible() = currentWeekTS != thisWeekTS

    fun updateActionBarTitle() {
//        setupWeeklyActionbarTitle(currentWeekTS)
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


    fun onEvent(evt: Any) {
        handler.post({
            if (evt is PersistenceEvents.ModelCreateOrUpdateEvent) {
                notifyDataChanged()
                refreshEvents()
            } else if (evt is PersistenceEvents.RoutineCreateEvent) {
                notifyDataChanged()
                refreshEvents()
            } else if (evt is PersistenceEvents.RoutineUpdateEvent) {
                notifyDataChanged()
                refreshEvents()
            } else if (evt is PersistenceEvents.RoutineDeleteEvent) {
                notifyDataChanged()
                refreshEvents()
            }
        })
    }
}
