package com.time.cat.helper

import android.Manifest.permission.VIBRATE
import android.content.Context
import android.media.RingtoneManager
import android.text.format.DateFormat
import com.simplemobiletools.commons.helpers.BaseConfig
import com.time.cat.R
import com.time.cat.data.*
import java.util.*

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)
    }

    fun save(name : String, value: Boolean) = prefs.edit().putBoolean(name, value).apply()
    fun save(name : String, value: Long) = prefs.edit().putLong(name, value).apply()
    fun save(name : String, value: Int) = prefs.edit().putInt(name, value).apply()
    fun save(name : String, value: String) = prefs.edit().putString(name, value).apply()
    fun save(name : String, value: Set<String>) = prefs.edit().putStringSet(name, value).apply()


    var isSundayFirst: Boolean
        get() {
            val isSundayFirst = Calendar.getInstance(Locale.getDefault()).firstDayOfWeek == Calendar.SUNDAY
            return prefs.getBoolean(SUNDAY_FIRST, isSundayFirst)
        }
        set(sundayFirst) = save(SUNDAY_FIRST, sundayFirst)

    var use24hourFormat: Boolean
        get() {
            val use24hourFormat = DateFormat.is24HourFormat(context)
            return prefs.getBoolean(USE_24_HOUR_FORMAT, use24hourFormat)
        }
        set(use24hourFormat) = save(USE_24_HOUR_FORMAT, use24hourFormat)

    var displayWeekNumbers: Boolean
        get() = prefs.getBoolean(WEEK_NUMBERS, false)
        set(displayWeekNumbers) = save(WEEK_NUMBERS, displayWeekNumbers)

    var startWeeklyAt: Int
        get() = prefs.getInt(START_WEEKLY_AT, 5)
        set(startWeeklyAt) = save(START_WEEKLY_AT, startWeeklyAt)

    var endWeeklyAt: Int
        get() = prefs.getInt(END_WEEKLY_AT, 24)
        set(endWeeklyAt) = save(END_WEEKLY_AT, endWeeklyAt)

    var vibrateOnReminder: Boolean
        get() = prefs.getBoolean(VIBRATE, false)
        set(vibrate) = save(VIBRATE, vibrate)

    var reminderSound: String
        get() = prefs.getString(REMINDER_SOUND, getDefaultNotificationSound())
        set(path) = prefs.edit().putString(REMINDER_SOUND, path)

    var storedView: Int
        get() = prefs.getInt(VIEW, MONTHLY_VIEW)
        set(view) = save(VIEW, view)

    var defaultReminderMinutes: Int
        get() = prefs.getInt(REMINDER_MINUTES, 10)
        set(defaultReminderMinutes) = save(REMINDER_MINUTES, defaultReminderMinutes)

    var defaultReminderMinutes2: Int
        get() = prefs.getInt(REMINDER_MINUTES_2, REMINDER_OFF)
        set(defaultReminderMinutes2) = save(REMINDER_MINUTES_2, defaultReminderMinutes2)

    var defaultReminderMinutes3: Int
        get() = prefs.getInt(REMINDER_MINUTES_3, REMINDER_OFF)
        set(defaultReminderMinutes3) = save(REMINDER_MINUTES_3, defaultReminderMinutes3)

    var useSameSnooze: Boolean
        get() = prefs.getBoolean(USE_SAME_SNOOZE, false)
        set(useSameSnooze) = save(USE_SAME_SNOOZE, useSameSnooze)

    var snoozeDelay: Int
        get() = prefs.getInt(SNOOZE_DELAY, 10)
        set(snoozeDelay) = save(SNOOZE_DELAY, snoozeDelay)

    var displayPastEvents: Int
        get() = prefs.getInt(DISPLAY_PAST_EVENTS, 0)
        set(displayPastEvents) = save(DISPLAY_PAST_EVENTS, displayPastEvents)

    var displayEventTypes: Set<String>
        get() = prefs.getStringSet(DISPLAY_EVENT_TYPES, HashSet<String>())
        set(displayEventTypes) = prefs.edit().remove(DISPLAY_EVENT_TYPES).putStringSet(DISPLAY_EVENT_TYPES, displayEventTypes)

    var fontSize: Int
        get() = prefs.getInt(FONT_SIZE, FONT_SIZE_MEDIUM)
        set(size) = save(FONT_SIZE, size)

//    var caldavSync: Boolean
//        get() = prefs.getBoolean(CALDAV_SYNC, false)
//        set(caldavSync) {
//            context.scheduleCalDAVSync(caldavSync)
//            save(CALDAV_SYNC, caldavSync)
//        }

    var caldavSyncedCalendarIDs: String
        get() = prefs.getString(CALDAV_SYNCED_CALENDAR_IDS, "")
        set(calendarIDs) = prefs.edit().putString(CALDAV_SYNCED_CALENDAR_IDS, calendarIDs)

    var lastUsedCaldavCalendar: Int
        get() = prefs.getInt(LAST_USED_CALDAV_CALENDAR, getSyncedCalendarIdsAsList().first().toInt())
        set(calendarId) = save(LAST_USED_CALDAV_CALENDAR, calendarId)

    var replaceDescription: Boolean
        get() = prefs.getBoolean(REPLACE_DESCRIPTION, false)
        set(replaceDescription) = save(REPLACE_DESCRIPTION, replaceDescription)

    fun getSyncedCalendarIdsAsList() = caldavSyncedCalendarIDs.split(",").filter { it.trim().isNotEmpty() } as ArrayList<String>

    fun addDisplayEventType(type: String) {
        addDisplayEventTypes(HashSet<String>(Arrays.asList(type)))
    }

    private fun addDisplayEventTypes(types: Set<String>) {
        val currDisplayEventTypes = HashSet<String>(displayEventTypes)
        currDisplayEventTypes.addAll(types)
        displayEventTypes = currDisplayEventTypes
    }

    fun removeDisplayEventTypes(types: Set<String>) {
        val currDisplayEventTypes = HashSet<String>(displayEventTypes)
        currDisplayEventTypes.removeAll(types)
        displayEventTypes = currDisplayEventTypes
    }

    private fun getDefaultNotificationSound(): String {
        return try {
            RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION)?.toString() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun getFontSize() = when (fontSize) {
        FONT_SIZE_SMALL -> getSmallFontSize()
        FONT_SIZE_MEDIUM -> getMediumFontSize()
        else -> getLargeFontSize()
    }

    private fun getSmallFontSize() = getMediumFontSize() - 3f
    private fun getMediumFontSize() = context.resources.getDimension(R.dimen.day_text_size) / context.resources.displayMetrics.density
    private fun getLargeFontSize() = getMediumFontSize() + 3f
}
