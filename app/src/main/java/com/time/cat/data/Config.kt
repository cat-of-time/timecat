package com.time.cat.data

import android.Manifest.permission.VIBRATE
import android.content.Context
import android.content.res.Resources
import android.media.RingtoneManager
import android.net.Uri
import android.support.annotation.IntDef
import android.text.format.DateFormat
import com.simplemobiletools.commons.helpers.BaseConfig
import com.time.cat.BuildConfig
import com.time.cat.R
import com.time.cat.TimeCatApp
import com.time.cat.data.Constants.CALDAV_SYNCED_CALENDAR_IDS
import com.time.cat.data.Constants.DISPLAY_EVENT_TYPES
import com.time.cat.data.Constants.DISPLAY_PAST_EVENTS
import com.time.cat.data.Constants.END_WEEKLY_AT
import com.time.cat.data.Constants.FONT_SIZE
import com.time.cat.data.Constants.FONT_SIZE_MEDIUM
import com.time.cat.data.Constants.FONT_SIZE_SMALL
import com.time.cat.data.Constants.LAST_USED_CALDAV_CALENDAR
import com.time.cat.data.Constants.MONTHLY_VIEW
import com.time.cat.data.Constants.REMINDER_MINUTES
import com.time.cat.data.Constants.REMINDER_MINUTES_2
import com.time.cat.data.Constants.REMINDER_MINUTES_3
import com.time.cat.data.Constants.REMINDER_OFF
import com.time.cat.data.Constants.REMINDER_SOUND
import com.time.cat.data.Constants.REPLACE_DESCRIPTION
import com.time.cat.data.Constants.SNOOZE_DELAY
import com.time.cat.data.Constants.START_WEEKLY_AT
import com.time.cat.data.Constants.SUNDAY_FIRST
import com.time.cat.data.Constants.USE_24_HOUR_FORMAT
import com.time.cat.data.Constants.USE_SAME_SNOOZE
import com.time.cat.data.Constants.VIEW
import com.time.cat.data.Constants.WEEK_NUMBERS
import com.time.cat.data.Constants.WEEK_VIEW_BACKGROUND
import com.time.cat.data.Constants.WEEK_VIEW_SUPPRESS_COLOR
import com.time.cat.data.Constants.WEEK_VIEW_TEXT_COLOR
import com.time.cat.util.string.StringUtil
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)

        const val LIGHT = 1
        const val DARK = 2
        const val AMLOD = 3
        const val BLUISH = 4
        const val MID_NIGHT_BLUE = 5

        const val RED = 1
        const val PINK = 2
        const val PURPLE = 3
        const val DEEP_PURPLE = 4
        const val INDIGO = 5
        const val BLUE = 6
        const val LIGHT_BLUE = 7
        const val CYAN = 8
        const val TEAL = 9
        const val GREEN = 10
        const val LIGHT_GREEN = 11
        const val LIME = 12
        const val YELLOW = 13
        const val AMBER = 14
        const val ORANGE = 15
        const val DEEP_ORANGE = 16
    }

    fun save(name : String, value: Boolean) = prefs.edit().putBoolean(name, value).apply()
    fun save(name : String, value: Long) = prefs.edit().putLong(name, value).apply()
    fun save(name : String, value: Int) = prefs.edit().putInt(name, value).apply()
    fun save(name : String, value: String) = prefs.edit().putString(name, value).apply()
    fun save(name : String, value: Set<String>) = prefs.edit().putStringSet(name, value).apply()

    fun getBoolean(name : String, defaultValue : Boolean) = prefs.getBoolean(name, defaultValue)
    fun getInt(name : String, defaultValue : Int) = prefs.getInt(name, defaultValue)
    fun getLong(name : String, defaultValue : Long) = prefs.getLong(name, defaultValue)
    fun getString(name : String, defaultValue : String) = prefs.getString(name, defaultValue)
    fun getStringSet(name : String, defaultValue : Set<String>) = prefs.getStringSet(name, defaultValue)


    //*****************************************************************
    // 星期视图
    //*****************************************************************


    var isSundayFirst: Boolean
        get() {
            val isSundayFirst = Calendar.getInstance(Locale.getDefault()).firstDayOfWeek == Calendar.SUNDAY
            return getBoolean(SUNDAY_FIRST, isSundayFirst)
        }
        set(sundayFirst) = save(SUNDAY_FIRST, sundayFirst)

    var use24hourFormat: Boolean
        get() {
            val use24hourFormat = DateFormat.is24HourFormat(context)
            return getBoolean(USE_24_HOUR_FORMAT, use24hourFormat)
        }
        set(use24hourFormat) = save(USE_24_HOUR_FORMAT, use24hourFormat)

    var displayWeekNumbers: Boolean
        get() = getBoolean(WEEK_NUMBERS, false)
        set(displayWeekNumbers) = save(WEEK_NUMBERS, displayWeekNumbers)

    var startWeeklyAt: Int
        get() = getInt(START_WEEKLY_AT, 5)
        set(startWeeklyAt) = save(START_WEEKLY_AT, startWeeklyAt)

    var endWeeklyAt: Int
        get() = getInt(END_WEEKLY_AT, 24)
        set(endWeeklyAt) = save(END_WEEKLY_AT, endWeeklyAt)

    var weekViewBackground: Int
        get() = getInt(WEEK_VIEW_BACKGROUND , context.resources.getColor(R.color.white))
        set(weekViewBackground) = save(WEEK_VIEW_BACKGROUND , weekViewBackground)

    var weekViewTextColor: Int
        get() = prefs.getInt(WEEK_VIEW_TEXT_COLOR, context.resources.getColor(R.color.gray))
        set(textColor) = prefs.edit().putInt(WEEK_VIEW_TEXT_COLOR, textColor).apply()

    var weekViewSuppressColor: Int
        get() = prefs.getInt(WEEK_VIEW_SUPPRESS_COLOR, context.resources.getColor(R.color.colorPrimary))
        set(textColor) = prefs.edit().putInt(WEEK_VIEW_SUPPRESS_COLOR, textColor).apply()

    var vibrateOnReminder: Boolean
        get() = getBoolean(VIBRATE, false)
        set(vibrate) = save(VIBRATE, vibrate)

    var reminderSound: String
        get() = getString(REMINDER_SOUND, getDefaultNotificationSound())
        set(path) = save(REMINDER_SOUND, path)

    var storedView: Int
        get() = getInt(VIEW, MONTHLY_VIEW)
        set(view) = save(VIEW, view)

    var defaultReminderMinutes: Int
        get() = getInt(REMINDER_MINUTES, 10)
        set(defaultReminderMinutes) = save(REMINDER_MINUTES, defaultReminderMinutes)

    var defaultReminderMinutes2: Int
        get() = getInt(REMINDER_MINUTES_2, REMINDER_OFF)
        set(defaultReminderMinutes2) = save(REMINDER_MINUTES_2, defaultReminderMinutes2)

    var defaultReminderMinutes3: Int
        get() = getInt(REMINDER_MINUTES_3, REMINDER_OFF)
        set(defaultReminderMinutes3) = save(REMINDER_MINUTES_3, defaultReminderMinutes3)

    var useSameSnooze: Boolean
        get() = getBoolean(USE_SAME_SNOOZE, false)
        set(useSameSnooze) = save(USE_SAME_SNOOZE, useSameSnooze)

    var snoozeDelay: Int
        get() = getInt(SNOOZE_DELAY, 10)
        set(snoozeDelay) = save(SNOOZE_DELAY, snoozeDelay)

    var displayPastEvents: Int
        get() = getInt(DISPLAY_PAST_EVENTS, 0)
        set(displayPastEvents) = save(DISPLAY_PAST_EVENTS, displayPastEvents)

    var displayEventTypes: Set<String>
        get() = getStringSet(DISPLAY_EVENT_TYPES, HashSet<String>())
        set(displayEventTypes) = prefs.edit().remove(DISPLAY_EVENT_TYPES).putStringSet(DISPLAY_EVENT_TYPES, displayEventTypes).apply()

    var fontSize: Int
        get() = getInt(FONT_SIZE, FONT_SIZE_MEDIUM)
        set(size) = save(FONT_SIZE, size)

    var caldavSyncedCalendarIDs: String
        get() = getString(CALDAV_SYNCED_CALENDAR_IDS, "")
        set(calendarIDs) = save(CALDAV_SYNCED_CALENDAR_IDS, calendarIDs)

    var lastUsedCaldavCalendar: Int
        get() = getInt(LAST_USED_CALDAV_CALENDAR, getSyncedCalendarIdsAsList().first().toInt())
        set(calendarId) = save(LAST_USED_CALDAV_CALENDAR, calendarId)

    var replaceDescription: Boolean
        get() = getBoolean(REPLACE_DESCRIPTION, false)
        set(replaceDescription) = save(REPLACE_DESCRIPTION, replaceDescription)

    //***************************
    //
    //***************************

    var token: String?
        get() = SharedPreferenceHelper.getString(Constants.TOKEN)
        set(token) = SharedPreferenceHelper.set(Constants.TOKEN, token)

    val enterpriseToken: String?
        get() = SharedPreferenceHelper.getString(Constants.ENTERPRISE_TOKEN)

    var enterpriseOtpCode: String?
        get() = SharedPreferenceHelper.getString(Constants.ENTERPRISE_OTP_CODE)
        set(otp) = SharedPreferenceHelper.set(Constants.ENTERPRISE_OTP_CODE, otp)

    var otpCode: String?
        get() = SharedPreferenceHelper.getString(Constants.OTP_CODE)
        set(otp) = SharedPreferenceHelper.set(Constants.OTP_CODE, otp)

    var isAdsEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(Constants.ADS)
        set(isEnabled) = SharedPreferenceHelper.set(Constants.ADS, isEnabled)

    val isUserIconGuideShowed: Boolean
        get() {
            SharedPreferenceHelper.set(Constants.USER_ICON_GUIDE, true)
            return SharedPreferenceHelper.getBoolean(Constants.USER_ICON_GUIDE)
        }

    val isReleaseHintShow: Boolean
        get() {
            SharedPreferenceHelper.set(Constants.RELEASE_GUIDE, true)
            return SharedPreferenceHelper.getBoolean(Constants.RELEASE_GUIDE)
        }

    val isFileOptionHintShow: Boolean
        get() {
            SharedPreferenceHelper.set(Constants.FILE_OPTION_GUIDE, true)
            return SharedPreferenceHelper.getBoolean(Constants.FILE_OPTION_GUIDE)
        }

    val isCommentHintShowed: Boolean
        get() {
            SharedPreferenceHelper.set(Constants.COMMENTS_GUIDE, true)
            return SharedPreferenceHelper.getBoolean(Constants.COMMENTS_GUIDE)
        }

    val isHomeButoonHintShowed: Boolean
        get() {
            SharedPreferenceHelper.set(Constants.HOME_BUTTON_GUIDE, true)
            return SharedPreferenceHelper.getBoolean(Constants.HOME_BUTTON_GUIDE)
        }

    val isRepoGuideShowed: Boolean
        get() {
            SharedPreferenceHelper.set(Constants.REPO_GUIDE, true)
            return SharedPreferenceHelper.getBoolean(Constants.REPO_GUIDE)
        }

    val isEditorHintShowed: Boolean
        get() {
            SharedPreferenceHelper.set(Constants.MARKDOWNDOWN_GUIDE, true)
            return SharedPreferenceHelper.getBoolean(Constants.MARKDOWNDOWN_GUIDE)
        }

    val isNavDrawerHintShowed: Boolean
        get() {
            SharedPreferenceHelper.set(Constants.NAV_DRAWER_GUIDE, true)
            return SharedPreferenceHelper.getBoolean(Constants.NAV_DRAWER_GUIDE)
        }

    val isAccountNavDrawerHintShowed: Boolean
        get() {
            SharedPreferenceHelper.set(Constants.ACC_NAV_DRAWER_GUIDE, true)
            return SharedPreferenceHelper.getBoolean(Constants.ACC_NAV_DRAWER_GUIDE)
        }

    val isRepoFabHintShowed: Boolean
        get() {
            SharedPreferenceHelper.set(Constants.FAB_LONG_PRESS_REPO_GUIDE, true)
            return SharedPreferenceHelper.getBoolean(Constants.FAB_LONG_PRESS_REPO_GUIDE)
        }

    val isRVAnimationEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean("recylerViewAnimation")

    val notificationTaskDuration: Int
        get() {
            if (SharedPreferenceHelper.isExist("notificationEnabled")
                    && SharedPreferenceHelper.getBoolean("notificationEnabled")) {
                val prefValue = SharedPreferenceHelper.getString("notificationTime")
                if (prefValue != null) {
                    return notificationDurationMillis(prefValue)
                }
            }
            return -1
        }

    val isTwiceBackButtonDisabled: Boolean
        get() = SharedPreferenceHelper.getBoolean("back_button")

    val isRectAvatar: Boolean
        get() = SharedPreferenceHelper.getBoolean("rect_avatar")

    val isMarkAsReadEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean("markNotificationAsRead")

    val isWrapCode: Boolean
        get() = SharedPreferenceHelper.getBoolean(Constants.WRAP_CODE)

    val isSentViaEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(Constants.SENT_VIA)

    val isSentViaBoxEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(Constants.SENT_VIA_BOX)

    val themeType: Int
        @ThemeType get() = getThemeType(TimeCatApp.getInstance().resources)

    val appLanguage: String
        get() = SharedPreferenceHelper.getString(Constants.APP_LANGUAGE) ?: "en"


    var profileBackgroundUrl: String?
        get() = SharedPreferenceHelper.getString(Constants.PROFILE_BACKGROUND_URL)
        set(url) = if (url == null) {
            SharedPreferenceHelper.clearKey(Constants.PROFILE_BACKGROUND_URL)
        } else {
            SharedPreferenceHelper.set(Constants.PROFILE_BACKGROUND_URL, url)
        }

    val isNotificationSoundEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean("notificationSound")

    val isAmlodEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(Constants.AMLOD_THEME_ENABLED)

    val isMidNightBlueThemeEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(Constants.MIDNIGHTBLUE_THEME_ENABLED)

    val isBluishEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(Constants.BLUISH_THEME_ENABLED)

    val isEnterpriseEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(Constants.ENTERPRISE_ITEM)

    val isAllFeaturesUnlocked: Boolean
        get() = isProEnabled && isEnterprise

    val isProEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(Constants.PRO_ITEMS)

    var codeTheme: String?
        get() = SharedPreferenceHelper.getString(Constants.CODE_THEME)
        set(theme) = SharedPreferenceHelper.set(Constants.CODE_THEME, theme)

    var enterpriseUrl: String?
        get() = SharedPreferenceHelper.getString(Constants.ENTERPRISE_URL)
        set(value) = SharedPreferenceHelper.set(Constants.ENTERPRISE_URL, value)

    val isEnterprise: Boolean
        get() = !StringUtil.isEmpty(enterpriseUrl)

    val isNavBarTintingDisabled: Boolean
        get() = SharedPreferenceHelper.getBoolean("navigation_color")

    var notificationSound: Uri?
        get() {
            val nsp = SharedPreferenceHelper.getString(Constants.NOTIFICATION_SOUND_PATH)
            return if (!StringUtil.isEmpty(nsp)) Uri.parse(nsp) else RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        set(uri) = SharedPreferenceHelper.set(Constants.NOTIFICATION_SOUND_PATH, uri.toString())

    val isAutoImageDisabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(Constants.DISABLE_AUTO_LOAD_IMAGE) && AppHelper.isDataPlan()

    val isAppAnimationDisabled: Boolean
        get() = SharedPreferenceHelper.getBoolean("app_animation")

    val isPlayStoreWarningShowed: Boolean
        get() = SharedPreferenceHelper.getBoolean(Constants.PLAY_STORE_REVIEW_ACTIVITY)

    val isFeedsHintShowed: Boolean
        get() {
            val isFeedsHitShowed = SharedPreferenceHelper.getBoolean("feeds_hint")
            if (!isFeedsHitShowed) {
                SharedPreferenceHelper.set("feeds_hint", true)
            }
            return isFeedsHitShowed
        }

    val isIssuesLongPressHintShowed: Boolean
        get() {
            val isIssuesLongPressHintShowed = SharedPreferenceHelper.getBoolean("issues_long_press_hint")
            if (!isIssuesLongPressHintShowed) {
                SharedPreferenceHelper.set("issues_long_press_hint", true)
            }
            return isIssuesLongPressHintShowed
        }

    val isPRLongPressHintShowed: Boolean
        get() {
            val isPRLongPressHintShowed = SharedPreferenceHelper.getBoolean("pr_long_press_hint")
            if (!isPRLongPressHintShowed) {
                SharedPreferenceHelper.set("pr_long_press_hint", true)
            }
            return isPRLongPressHintShowed
        }

    //*************
    // 主题
    //*************

    @IntDef(LIGHT, DARK, AMLOD, MID_NIGHT_BLUE, BLUISH)
    @Retention(RetentionPolicy.SOURCE)
    annotation class ThemeType

    @IntDef(RED, PINK, PURPLE, DEEP_PURPLE, INDIGO, BLUE, LIGHT_BLUE, CYAN, TEAL, GREEN, LIGHT_GREEN, LIME, YELLOW, AMBER, ORANGE, DEEP_ORANGE)
    @Retention(RetentionPolicy.SOURCE)
    annotation class ThemeColor

    fun setTokenEnterprise(token: String?) = SharedPreferenceHelper.set(Constants.ENTERPRISE_TOKEN, token)

    fun clear() = SharedPreferenceHelper.clearPrefs()

    fun notificationDurationMillis(prefValue: String): Int {
        if (!StringUtil.isEmpty(prefValue)) {
            when (prefValue) {
                "1" -> return 60
                "5" -> return 5 * 60
                "10" -> return 10 * 60
                "20" -> return 20 * 60
                "30" -> return 30 * 60
                "60" -> return 60 * 60 // 1 hour
                "120" -> return 60 * 2 * 60 // 2 hours
                "180" -> return 60 * 3 * 60 // 3 hours
            }
        }
        return -1
    }

    @ThemeType
    fun getThemeType(context: Context): Int = getThemeType(context.resources)

    @ThemeColor
    fun getThemeColor(context: Context): Int = getThemeColor(context.resources)

    @ThemeType
    fun getThemeType(resources: Resources): Int {
        val appTheme = SharedPreferenceHelper.getString("appTheme")
        if (!StringUtil.isEmpty(appTheme)) {
            if (appTheme!!.equals(resources.getString(R.string.dark_theme_mode), ignoreCase = true)) {
                return DARK
            } else if (appTheme.equals(resources.getString(R.string.light_theme_mode), ignoreCase = true)) {
                return LIGHT
            } else if (appTheme.equals(resources.getString(R.string.amlod_theme_mode), ignoreCase = true)) {
                return AMLOD
            } else if (appTheme.equals(resources.getString(R.string.mid_night_blue_theme_mode), ignoreCase = true)) {
                return MID_NIGHT_BLUE
            } else if (appTheme.equals(resources.getString(R.string.bluish_theme), ignoreCase = true)) {
                return BLUISH
            }
        }
        return LIGHT
    }

    @ThemeColor
    fun getThemeColor(resources: Resources): Int = getThemeColor(resources, SharedPreferenceHelper.getString("appColor"))


    // used for color picker to get the index of the color (enum) from the name of the color
    fun getThemeColor(resources: Resources, appColor: String?): Int {
        if (!StringUtil.isEmpty(appColor)) {
            if (appColor!!.equals(resources.getString(R.string.red_theme_mode), ignoreCase = true))
                return RED
            if (appColor.equals(resources.getString(R.string.pink_theme_mode), ignoreCase = true))
                return PINK
            if (appColor.equals(resources.getString(R.string.purple_theme_mode), ignoreCase = true))
                return PURPLE
            if (appColor.equals(resources.getString(R.string.deep_purple_theme_mode), ignoreCase = true))
                return DEEP_PURPLE
            if (appColor.equals(resources.getString(R.string.indigo_theme_mode), ignoreCase = true))
                return INDIGO
            if (appColor.equals(resources.getString(R.string.blue_theme_mode), ignoreCase = true))
                return BLUE
            if (appColor.equals(resources.getString(R.string.light_blue_theme_mode), ignoreCase = true))
                return LIGHT_BLUE
            if (appColor.equals(resources.getString(R.string.cyan_theme_mode), ignoreCase = true))
                return CYAN
            if (appColor.equals(resources.getString(R.string.teal_theme_mode), ignoreCase = true))
                return TEAL
            if (appColor.equals(resources.getString(R.string.green_theme_mode), ignoreCase = true))
                return GREEN
            if (appColor.equals(resources.getString(R.string.light_green_theme_mode), ignoreCase = true))
                return LIGHT_GREEN
            if (appColor.equals(resources.getString(R.string.lime_theme_mode), ignoreCase = true))
                return LIME
            if (appColor.equals(resources.getString(R.string.yellow_theme_mode), ignoreCase = true))
                return YELLOW
            if (appColor.equals(resources.getString(R.string.amber_theme_mode), ignoreCase = true))
                return AMBER
            if (appColor.equals(resources.getString(R.string.orange_theme_mode), ignoreCase = true))
                return ORANGE
            if (appColor.equals(resources.getString(R.string.deep_orange_theme_mode), ignoreCase = true))
                return DEEP_ORANGE
        }
        return BLUE
    }

    fun setAppLangauge(language: String?) = SharedPreferenceHelper.set(Constants.APP_LANGUAGE, language ?: "en")

    fun setWhatsNewVersion() = SharedPreferenceHelper.set(Constants.WHATS_NEW_VERSION, BuildConfig.VERSION_CODE)

    fun showWhatsNew(): Boolean = SharedPreferenceHelper.getInt(Constants.WHATS_NEW_VERSION) != BuildConfig.VERSION_CODE

    fun enableAmlodTheme() = SharedPreferenceHelper.set(Constants.AMLOD_THEME_ENABLED, true)

    fun enableMidNightBlueTheme() = SharedPreferenceHelper.set(Constants.MIDNIGHTBLUE_THEME_ENABLED, true)

    fun enableBluishTheme() = SharedPreferenceHelper.set(Constants.BLUISH_THEME_ENABLED, true)


    fun setProItems() {
        SharedPreferenceHelper.set(Constants.PRO_ITEMS, true)
        enableAmlodTheme()
        enableBluishTheme()
        enableMidNightBlueTheme()
    }

    fun setEnterpriseItem() = SharedPreferenceHelper.set(Constants.ENTERPRISE_ITEM, true)


    fun hasSupported(): Boolean = isProEnabled || isAmlodEnabled || isBluishEnabled


    fun resetEnterprise() {
        setTokenEnterprise(null)
        enterpriseOtpCode = null
        enterpriseUrl = null
    }

    fun setPlayStoreWarningShowed() = SharedPreferenceHelper.set(Constants.PLAY_STORE_REVIEW_ACTIVITY, true)


    fun clearPurchases() {
        SharedPreferenceHelper.set(Constants.PRO_ITEMS, false)
        SharedPreferenceHelper.set(Constants.BLUISH_THEME_ENABLED, false)
        SharedPreferenceHelper.set(Constants.AMLOD_THEME_ENABLED, false)
        enterpriseUrl = null
    }

    fun getSyncedCalendarIdsAsList() = caldavSyncedCalendarIDs.split(",").filter { it.trim().isNotEmpty() } as ArrayList<String>

    fun addDisplayEventType(type: String) = addDisplayEventTypes(HashSet<String>(Arrays.asList(type)))
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
