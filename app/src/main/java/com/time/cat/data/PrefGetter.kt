package com.time.cat.data

import android.content.Context
import android.content.res.Resources
import android.media.RingtoneManager
import android.net.Uri
import android.support.annotation.IntDef

import com.time.cat.BuildConfig
import com.time.cat.R
import com.time.cat.TimeCatApp
import com.time.cat.util.string.StringUtil

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by Kosh on 10 Nov 2016, 3:43 PM
 */

object PrefGetter {

    val LIGHT = 1
    val DARK = 2
    val AMLOD = 3
    val BLUISH = 4
    val MID_NIGHT_BLUE = 5

    val RED = 1
    val PINK = 2
    val PURPLE = 3
    val DEEP_PURPLE = 4
    val INDIGO = 5
    val BLUE = 6
    val LIGHT_BLUE = 7
    val CYAN = 8
    val TEAL = 9
    val GREEN = 10
    val LIGHT_GREEN = 11
    val LIME = 12
    val YELLOW = 13
    val AMBER = 14
    val ORANGE = 15
    val DEEP_ORANGE = 16


    private val WHATS_NEW_VERSION = "whats_new"
    private val ADS = "enable_ads"
    private val TOKEN = "token"
    private val ENTERPRISE_TOKEN = "enterprise_token"
    private val USER_ICON_GUIDE = "user_icon_guide"
    private val RELEASE_GUIDE = "release_guide"
    private val FILE_OPTION_GUIDE = "file_option_guide"
    private val COMMENTS_GUIDE = "comments_guide"
    private val REPO_GUIDE = "repo_guide"
    private val MARKDOWNDOWN_GUIDE = "markdowndown_guide"
    private val HOME_BUTTON_GUIDE = "home_button_guide"
    private val NAV_DRAWER_GUIDE = "nav_drawer_guide"
    private val ACC_NAV_DRAWER_GUIDE = "acc_nav_drawer_guide"
    private val FAB_LONG_PRESS_REPO_GUIDE = "fab_long_press_repo_guide"
    private val WRAP_CODE = "wrap_code"
    private val OTP_CODE = "otp_code"
    private val ENTERPRISE_OTP_CODE = "enterprise_otp_code"
    private val APP_LANGUAGE = "app_language"
    private val SENT_VIA = "fasthub_signature"
    private val SENT_VIA_BOX = "sent_via_enabled"
    private val PROFILE_BACKGROUND_URL = "profile_background_url"
    private val AMLOD_THEME_ENABLED = "amlod_theme_enabled"
    private val MIDNIGHTBLUE_THEME_ENABLED = "midnightblue_theme_enabled"
    private val BLUISH_THEME_ENABLED = "bluish_theme_enabled"
    private val PRO_ITEMS = "fasth_pro_items"
    private val ENTERPRISE_ITEM = "enterprise_item"
    private val CODE_THEME = "code_theme"
    private val ENTERPRISE_URL = "enterprise_url"
    private val NOTIFICATION_SOUND_PATH = "notification_sound_path"
    private val DISABLE_AUTO_LOAD_IMAGE = "disable_auto_loading_image"
    private val PLAY_STORE_REVIEW_ACTIVITY = "play_store_review_activity"

    var token: String?
        get() = SharedPreferenceHelper.getString(TOKEN)
        set(token) = SharedPreferenceHelper.set(TOKEN, token)

    val enterpriseToken: String?
        get() = SharedPreferenceHelper.getString(ENTERPRISE_TOKEN)

    var enterpriseOtpCode: String?
        get() = SharedPreferenceHelper.getString(ENTERPRISE_OTP_CODE)
        set(otp) = SharedPreferenceHelper.set(ENTERPRISE_OTP_CODE, otp)

    var otpCode: String?
        get() = SharedPreferenceHelper.getString(OTP_CODE)
        set(otp) = SharedPreferenceHelper.set(OTP_CODE, otp)

    var isAdsEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(ADS)
        set(isEnabled) = SharedPreferenceHelper.set(ADS, isEnabled)

    val isUserIconGuideShowed: Boolean
        get() {
            val isShowed = SharedPreferenceHelper.getBoolean(USER_ICON_GUIDE)
            SharedPreferenceHelper.set(USER_ICON_GUIDE, true)
            return isShowed
        }

    val isReleaseHintShow: Boolean
        get() {
            val isShowed = SharedPreferenceHelper.getBoolean(RELEASE_GUIDE)
            SharedPreferenceHelper.set(RELEASE_GUIDE, true)
            return isShowed
        }

    val isFileOptionHintShow: Boolean
        get() {
            val isShowed = SharedPreferenceHelper.getBoolean(FILE_OPTION_GUIDE)
            SharedPreferenceHelper.set(FILE_OPTION_GUIDE, true)
            return isShowed
        }

    val isCommentHintShowed: Boolean
        get() {
            val isShowed = SharedPreferenceHelper.getBoolean(COMMENTS_GUIDE)
            SharedPreferenceHelper.set(COMMENTS_GUIDE, true)
            return isShowed
        }

    val isHomeButoonHintShowed: Boolean
        get() {
            val isShowed = SharedPreferenceHelper.getBoolean(HOME_BUTTON_GUIDE)
            SharedPreferenceHelper.set(HOME_BUTTON_GUIDE, true)
            return isShowed
        }

    val isRepoGuideShowed: Boolean
        get() {
            val isShowed = SharedPreferenceHelper.getBoolean(REPO_GUIDE)
            SharedPreferenceHelper.set(REPO_GUIDE, true)
            return isShowed
        }

    val isEditorHintShowed: Boolean
        get() {
            val isShowed = SharedPreferenceHelper.getBoolean(MARKDOWNDOWN_GUIDE)
            SharedPreferenceHelper.set(MARKDOWNDOWN_GUIDE, true)
            return isShowed
        }

    val isNavDrawerHintShowed: Boolean
        get() {
            val isShowed = SharedPreferenceHelper.getBoolean(NAV_DRAWER_GUIDE)
            SharedPreferenceHelper.set(NAV_DRAWER_GUIDE, true)
            return isShowed
        }

    val isAccountNavDrawerHintShowed: Boolean
        get() {
            val isShowed = SharedPreferenceHelper.getBoolean(ACC_NAV_DRAWER_GUIDE)
            SharedPreferenceHelper.set(ACC_NAV_DRAWER_GUIDE, true)
            return isShowed
        }

    val isRepoFabHintShowed: Boolean
        get() {
            val isShowed = SharedPreferenceHelper.getBoolean(FAB_LONG_PRESS_REPO_GUIDE)
            SharedPreferenceHelper.set(FAB_LONG_PRESS_REPO_GUIDE, true)
            return isShowed
        }

    val isRVAnimationEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean("recylerViewAnimation")

    val notificationTaskDuration: Int
        get() {
            if (SharedPreferenceHelper.isExist("notificationEnabled") && SharedPreferenceHelper.getBoolean("notificationEnabled")) {
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
        get() = SharedPreferenceHelper.getBoolean(WRAP_CODE)

    val isSentViaEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(SENT_VIA)

    val isSentViaBoxEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(SENT_VIA_BOX)

    val themeType: Int
        @ThemeType get() = getThemeType(TimeCatApp.getInstance().resources)

    val appLanguage: String
        get() {
            val appLanguage = SharedPreferenceHelper.getString(APP_LANGUAGE)
            return appLanguage ?: "en"
        }

    var profileBackgroundUrl: String?
        get() = SharedPreferenceHelper.getString(PROFILE_BACKGROUND_URL)
        set(url) = if (url == null) {
            SharedPreferenceHelper.clearKey(PROFILE_BACKGROUND_URL)
        } else {
            SharedPreferenceHelper.set(PROFILE_BACKGROUND_URL, url)
        }

    val isNotificationSoundEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean("notificationSound")

    val isAmlodEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(AMLOD_THEME_ENABLED)

    val isMidNightBlueThemeEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(MIDNIGHTBLUE_THEME_ENABLED)

    val isBluishEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(BLUISH_THEME_ENABLED)

    val isEnterpriseEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(ENTERPRISE_ITEM)

    val isAllFeaturesUnlocked: Boolean
        get() = isProEnabled && isEnterprise

    val isProEnabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(PRO_ITEMS)

    var codeTheme: String?
        get() = SharedPreferenceHelper.getString(CODE_THEME)
        set(theme) = SharedPreferenceHelper.set(CODE_THEME, theme)

    var enterpriseUrl: String?
        get() = SharedPreferenceHelper.getString(ENTERPRISE_URL)
        set(value) = SharedPreferenceHelper.set(ENTERPRISE_URL, value)

    val isEnterprise: Boolean
        get() = !StringUtil.isEmpty(enterpriseUrl)

    val isNavBarTintingDisabled: Boolean
        get() = SharedPreferenceHelper.getBoolean("navigation_color")

    var notificationSound: Uri?
        get() {
            val nsp = SharedPreferenceHelper.getString(NOTIFICATION_SOUND_PATH)
            return if (!StringUtil.isEmpty(nsp)) Uri.parse(nsp) else RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        set(uri) = SharedPreferenceHelper.set(NOTIFICATION_SOUND_PATH, uri.toString())

    val isAutoImageDisabled: Boolean
        get() = SharedPreferenceHelper.getBoolean(DISABLE_AUTO_LOAD_IMAGE) && AppHelper.isDataPlan()

    val isAppAnimationDisabled: Boolean
        get() = SharedPreferenceHelper.getBoolean("app_animation")

    val isPlayStoreWarningShowed: Boolean
        get() = SharedPreferenceHelper.getBoolean(PLAY_STORE_REVIEW_ACTIVITY)

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

    @IntDef(LIGHT, DARK, AMLOD, MID_NIGHT_BLUE, BLUISH)
    @Retention(RetentionPolicy.SOURCE)
    annotation class ThemeType

    @IntDef(RED, PINK, PURPLE, DEEP_PURPLE, INDIGO, BLUE, LIGHT_BLUE, CYAN, TEAL, GREEN, LIGHT_GREEN, LIME, YELLOW, AMBER, ORANGE, DEEP_ORANGE)
    @Retention(RetentionPolicy.SOURCE)
    internal annotation class ThemeColor

    fun setTokenEnterprise(token: String?) {
        SharedPreferenceHelper.set(ENTERPRISE_TOKEN, token)
    }

    fun clear() {
        SharedPreferenceHelper.clearPrefs()
    }

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
    fun getThemeType(context: Context): Int {
        return getThemeType(context.resources)
    }

    @ThemeColor
    fun getThemeColor(context: Context): Int {
        return getThemeColor(context.resources)
    }

    @ThemeType
    internal fun getThemeType(resources: Resources): Int {
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
    private fun getThemeColor(resources: Resources): Int {
        val appColor = SharedPreferenceHelper.getString("appColor")
        return getThemeColor(resources, appColor)
    }

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

    fun setAppLangauge(language: String?) {
        SharedPreferenceHelper.set(APP_LANGUAGE, language ?: "en")
    }

    fun setWhatsNewVersion() {
        SharedPreferenceHelper.set(WHATS_NEW_VERSION, BuildConfig.VERSION_CODE)
    }

    fun showWhatsNew(): Boolean {
        return SharedPreferenceHelper.getInt(WHATS_NEW_VERSION) != BuildConfig.VERSION_CODE
    }

    fun enableAmlodTheme() {
        SharedPreferenceHelper.set(AMLOD_THEME_ENABLED, true)
    }

    fun enableMidNightBlueTheme() {
        SharedPreferenceHelper.set(MIDNIGHTBLUE_THEME_ENABLED, true)
    }

    fun enableBluishTheme() {
        SharedPreferenceHelper.set(BLUISH_THEME_ENABLED, true)
    }

    fun setProItems() {
        SharedPreferenceHelper.set(PRO_ITEMS, true)
        enableAmlodTheme()
        enableBluishTheme()
        enableMidNightBlueTheme()
    }

    fun setEnterpriseItem() {
        SharedPreferenceHelper.set(ENTERPRISE_ITEM, true)
    }

    fun hasSupported(): Boolean {
        return isProEnabled || isAmlodEnabled || isBluishEnabled
    }

    fun resetEnterprise() {
        PrefGetter.setTokenEnterprise(null)
        PrefGetter.enterpriseOtpCode = null
        PrefGetter.enterpriseUrl = null
    }

    fun setPlayStoreWarningShowed() {
        SharedPreferenceHelper.set(PLAY_STORE_REVIEW_ACTIVITY, true)
    }

    fun clearPurchases() {
        SharedPreferenceHelper.set(PRO_ITEMS, false)
        SharedPreferenceHelper.set(BLUISH_THEME_ENABLED, false)
        SharedPreferenceHelper.set(AMLOD_THEME_ENABLED, false)
        enterpriseUrl = null
    }

}
