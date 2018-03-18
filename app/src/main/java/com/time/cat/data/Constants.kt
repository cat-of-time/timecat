package com.time.cat.data

import com.time.cat.R
import com.time.cat.TimeCatApp

object Constants {

    const val CONTENT = "content://"
    const val AUTHORITY = "com.time.cat"
    const val SEPARATOR = "/"
    const val CONTENT_URI = CONTENT + AUTHORITY

    const val BROADCAST_RELOAD_SETTING = "broadcast_reload_setting"
    const val BROADCAST_TIMECAT_MONITOR_SERVICE_MODIFIED = "broadcast_timecat_monitor_service_modified"

    const val BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED = "broadcast_clipboard_listen_service_modified"

    const val BROADCAST_SET_TO_CLIPBOARD = "broadcast_set_to_clipboard"
    const val BROADCAST_SET_TO_CLIPBOARD_MSG = "broadcast_set_to_clipboard_msg"


    //shareCard
    const val HAD_SHARED = "had_shared"
    const val SETTING_OPEN_TIMES = "setting_open_times"

    //FunctionSettingCard
    const val MONITOR_CLIP_BOARD = "monitor_clip_board"
    const val MONITOR_CLICK = "monitor_click"
    const val TOTAL_SWITCH = "total_switch"
    const val SHOW_FLOAT_VIEW = "show_float_view"
    const val REMAIN_SYMBOL = "remain_symbol"
    const val REMAIN_SECTION = "remain_section"
    const val DEFAULT_LOCAL = "default_local"


    const val AUTO_OPEN_SETTING = "auto_open_setting"

    //floatview
    const val FLOAT_SWITCH_STATE = "float_switch_state"
    const val FLOAT_VIEW_LAND_X = "float_view_land_x"
    const val FLOAT_VIEW_LAND_Y = "float_view_land_Y"
    const val FLOAT_VIEW_PORT_X = "float_view_port_x"
    const val FLOAT_VIEW_PORT_Y = "float_view_port_y"


    const val IS_SHOW_NOTIFY = "is_show_notify"
    const val NOTIFY_DISABLED_IGNORE = "notify_disabled_ignore"


    //FeedBackAndUpdateCard

    //MonitorSettingCard
    const val TEXT_ONLY = "text_only"
    const val QQ_SELECTION = "qq_selection"
    const val WEIXIN_SELECTION = "weixin_selection"
    const val OTHER_SELECTION = "other_selection"

    const val BROWSER_SELECTION = "browser_selection"


    const val Setting_content_Changes = "tencent_contents_change"
    const val SHOW_TENCENT_SETTINGS = "tencent_settings"


    const val ONLINE_CONFIG_OPEN_UPDATE = "online_config_open_update"
    const val DOUBLE_CLICK_INTERVAL = "double_click_interval"
    const val DEFAULT_DOUBLE_CLICK_INTERVAL = 1000


    //SettingTimeCatActivity
    const val TEXT_SIZE = "text_size"
    const val LINE_MARGIN = "line_margin"
    const val ITEM_MARGIN = "item_margin"
    const val ITEM_PADDING = "item_padding"
    const val TIMECAT_ALPHA = "timecat_alpha"
    const val USE_LOCAL_WEBVIEW = "use_local_webview"
    const val USE_FLOAT_VIEW_TRIGGER = "use_float_view_trigger"
    const val TIMECAT_DIY_BG_COLOR = "timecat_diy_bg_color"
    const val IS_FULL_SCREEN = "is_full_screen"
    const val IS_STICK_HEADER = "is_stick_header"
    const val IS_STICK_SHAREBAR = "is_stick_sharebar"
    const val AUTO_ADD_BLANKS = "auto_add_blanks"
    const val TREAT_BLANKS_AS_SYMBOL = "treat_blanks_as_symbol"


    const val FLOATVIEW_SIZE = "floatview_size_"
    const val FLOATVIEW_ALPHA = "floatview_alpha"
    const val FLOATVIEW_DIY_BG_COLOR = "floatview_diy_bg_color"
    const val FLOATVIEW_IS_STICK = "floatview_is_stick"


    const val DEFAULT_TEXT_SIZE = 14
    const val DEFAULT_LINE_MARGIN = 8
    const val DEFAULT_ITEM_MARGIN = 0
    const val DEFAULT_ITEM_PADDING = 10


    //whiteListActivity
    const val WHITE_LIST_COUNT = "white_list_count"
    const val WHITE_LIST = "white_list"
    const val REFRESH_WHITE_LIST_BROADCAST = "refresh_white_list_broadcast"


    //Float whiteList Activity
    const val FLOAT_WHITE_LIST_COUNT = "float_white_list_count"
    const val FLOAT_WHITE_LIST = "float_white_list_"
    const val FLOAT_REFRESH_WHITE_LIST_BROADCAST = "float_refresh_white_list_broadcast"

    const val HAS_ADDED_LAUNCHER_AS_WHITE_LIST = "has_added_launcher_as_white_list"


    const val UNIVERSAL_COPY_BROADCAST = "universal_copy_broadcast"
    const val UNIVERSAL_COPY_BROADCAST_DELAY = "universal_copy_broadcast_delay"
    const val SCREEN_CAPTURE_OVER_BROADCAST = "screen_capture_over_broadcast"


    const val TOTAL_SWITCH_BROADCAST = "total_switch_broadcast"
    const val MONITOR_CLICK_BROADCAST = "monitor_click_broadcast"
    const val MONITOR_CLIPBOARD_BROADCAST = "monitor_clipboard_broadcast"

    const val NOTIFY_UNIVERSAL_COPY_BROADCAST = "notify_universal_copy_broadcast"
    const val NOTIFY_SCREEN_CAPTURE_OVER_BROADCAST = "notify_screen_capture_over_broadcast"

    const val OCR_TIME = "ocr_time"
    const val OCR_TIME_TO_ALERT = 5
    const val SHOULD_SHOW_DIY_OCR = "should_show_diy_ocr"

    const val DIY_OCR_KEY = "diy_ocr_key"


    const val EFFECT_AFTER_REBOOT_BROADCAST = "effect_after_reboot_broadcast"

    const val LONG_PRESS_KEY_INDEX = "long_press_key_index"

    const val SHARE_APPS_DIS = "share_app_dis"
    const val HAD_ENTER_INTRO = "had_enter_intro"
    const val SHARE_APP_INDEX = "share_app_index"

    const val HAD_SHOW_LONG_PRESS_TOAST = "had_show_long_press_toast"

    //copyActivity
    const val IS_FULL_SCREEN_COPY = "is_full_screen_copy"

    //xp全局复制
    const val UNIVERSAL_COPY_BROADCAST_XP = "universal_copy_broadcast_xp"

    // InfoOperationActivity
    const val UNIVERSAL_SAVE_COTENT = "universal_save_content"

    val ALI_APP_KEY = TimeCatApp.getInstance().getString(R.string.ali_feedback_key)
    val ALI_APP_SECRET = TimeCatApp.getInstance().getString(R.string.ali_feedback_key_secret)


    const val LOW_ALPHA = .3f
    const val MEDIUM_ALPHA = .6f
    const val STORED_LOCALLY_ONLY = 0

    const val DAY_CODE = "day_code"
    const val YEAR_LABEL = "year"
    const val EVENT_ID = "event_id"
    const val EVENT_OCCURRENCE_TS = "event_occurrence_ts"
    const val NEW_EVENT_START_TS = "new_event_start_ts"
    const val WEEK_START_TIMESTAMP = "week_start_timestamp"
    const val NEW_EVENT_SET_HOUR_DURATION = "new_event_set_hour_duration"
    const val WEEK_START_DATE_TIME = "week_start_date_time"
    const val CALDAV = "Caldav"
    const val OPEN_MONTH = "open_month"

    const val MONTHLY_VIEW = 1
    const val YEARLY_VIEW = 2
    const val EVENTS_LIST_VIEW = 3
    const val WEEKLY_VIEW = 4
    const val DAILY_VIEW = 5

    const val REMINDER_OFF = -1

    const val DAY = 86400
    const val DAY_MILLI_SECONDS = 86400 * 1000

    const val WEEK = 604800
    const val MONTH = 2592001    // exact value not taken into account, Joda is used for adding months and years
    const val YEAR = 31536000

    const val DAY_MINUTES = 24 * 60
    const val DAY_SECONDS = 24 * 60 * 60
    const val WEEK_SECONDS = 7 * DAY_SECONDS
    const val WEEK_MILLI_SECONDS = WEEK_SECONDS * 1000
    // Shared Preferences
    const val USE_24_HOUR_FORMAT = "use_24_hour_format"
    const val SUNDAY_FIRST = "sunday_first"
    const val WEEK_NUMBERS = "week_numbers"
    const val START_WEEKLY_AT = "start_weekly_at"
    const val END_WEEKLY_AT = "end_weekly_at"
    const val VIBRATE = "vibrate"
    const val REMINDER_SOUND = "reminder_sound"
    const val VIEW = "view"
    const val REMINDER_MINUTES = "reminder_minutes"
    const val REMINDER_MINUTES_2 = "reminder_minutes_2"
    const val REMINDER_MINUTES_3 = "reminder_minutes_3"
    const val DISPLAY_EVENT_TYPES = "display_event_types"
    const val FONT_SIZE = "font_size"
    const val CALDAV_SYNC = "caldav_sync"
    const val CALDAV_SYNCED_CALENDAR_IDS = "caldav_synced_calendar_ids"
    const val LAST_USED_CALDAV_CALENDAR = "last_used_caldav_calendar"
    const val SNOOZE_DELAY = "snooze_delay"
    const val DISPLAY_PAST_EVENTS = "display_past_events"
    const val REPLACE_DESCRIPTION = "replace_description"
    const val USE_SAME_SNOOZE = "use_same_snooze"

    val letterIDs = intArrayOf(R.string.sunday_letter, R.string.monday_letter, R.string.tuesday_letter, R.string.wednesday_letter,
            R.string.thursday_letter, R.string.friday_letter, R.string.saturday_letter)

    // repeat_rule for weekly repetition
    const val MONDAY = 1
    const val TUESDAY = 2
    const val WEDNESDAY = 4
    const val THURSDAY = 8
    const val FRIDAY = 16
    const val SATURDAY = 32
    const val SUNDAY = 64
    const val EVERY_DAY = 127

    // repeat_rule for monthly repetition
    const val REPEAT_MONTH_SAME_DAY = 1                   // ie 25th every month
    const val REPEAT_MONTH_ORDER_WEEKDAY_USE_LAST = 2     // ie every xth sunday. 4th if a month has 4 sundays, 5th if 5
    const val REPEAT_MONTH_LAST_DAY = 3                   // ie every last day of the month
    const val REPEAT_MONTH_ORDER_WEEKDAY = 4              // ie every 4th sunday, even if a month has 4 sundays only (will stay 4th even at months with 5)

    // special event flags
    const val FLAG_ALL_DAY = 1

    // this tag isn't a standard ICS tag, but there's no official way of adding a category color in an ics file
    const val CATEGORY_COLOR = "CATEGORY_COLOR:"

    const val DISPLAY = "DISPLAY"
    const val FREQ = "FREQ"
    const val UNTIL = "UNTIL"
    const val COUNT = "COUNT"
    const val INTERVAL = "INTERVAL"
    const val CONFIRMED = "CONFIRMED"
    const val VALUE = "VALUE"
    const val DATE = "DATE"

    const val DAILY = "DAILY"
    const val WEEKLY = "WEEKLY"
    const val MONTHLY = "MONTHLY"
    const val YEARLY = "YEARLY"

    const val MO = "MO"
    const val TU = "TU"
    const val WE = "WE"
    const val TH = "TH"
    const val FR = "FR"
    const val SA = "SA"
    const val SU = "SU"

    // font sizes
    const val FONT_SIZE_SMALL = 0
    const val FONT_SIZE_MEDIUM = 1
    const val FONT_SIZE_LARGE = 2

    const val SOURCE_SIMPLE_CALENDAR = "simple-calendar"
    const val SOURCE_IMPORTED_ICS = "imported-ics"
    const val SOURCE_CONTACT_BIRTHDAY = "contact-birthday"
    const val SOURCE_CONTACT_ANNIVERSARY = "contact-anniversary"

    const val BUNDLE_KEY_FROM_FILE = "FROM_FILE"
    const val BUNDLE_KEY_SAVED = "SAVED"
    const val BUNDLE_KEY_FILE_NAME = "FILE_NAME"
    const val BUNDLE_KEY_FILE_PATH = "FILE_PATH"

    const val REFRESH_CODE = 64
    const val REQUEST_CODE = 2016

    const val ROUTINES_VIEW_TYPE = "routines_view_type"
    const val SCHEDULES_VIEW_TYPE = "schedules_view_type"
    const val WEEK_VIEW_BACKGROUND = "week_view_background"
    const val WEEK_VIEW_TEXT_COLOR = "week_view_text_color"
    const val WEEK_VIEW_SUPPRESS_COLOR = "week_view_suppress_color"


    const val WHATS_NEW_VERSION = "whats_new"
    const val ADS = "enable_ads"
    const val TOKEN = "token"
    const val ENTERPRISE_TOKEN = "enterprise_token"
    const val USER_ICON_GUIDE = "user_icon_guide"
    const val RELEASE_GUIDE = "release_guide"
    const val FILE_OPTION_GUIDE = "file_option_guide"
    const val COMMENTS_GUIDE = "comments_guide"
    const val REPO_GUIDE = "repo_guide"
    const val MARKDOWNDOWN_GUIDE = "markdowndown_guide"
    const val HOME_BUTTON_GUIDE = "home_button_guide"
    const val NAV_DRAWER_GUIDE = "nav_drawer_guide"
    const val ACC_NAV_DRAWER_GUIDE = "acc_nav_drawer_guide"
    const val FAB_LONG_PRESS_REPO_GUIDE = "fab_long_press_repo_guide"
    const val WRAP_CODE = "wrap_code"
    const val OTP_CODE = "otp_code"
    const val ENTERPRISE_OTP_CODE = "enterprise_otp_code"
    const val APP_LANGUAGE = "app_language"
    const val SENT_VIA = "fasthub_signature"
    const val SENT_VIA_BOX = "sent_via_enabled"
    const val PROFILE_BACKGROUND_URL = "profile_background_url"
    const val AMLOD_THEME_ENABLED = "amlod_theme_enabled"
    const val MIDNIGHTBLUE_THEME_ENABLED = "midnightblue_theme_enabled"
    const val BLUISH_THEME_ENABLED = "bluish_theme_enabled"
    const val PRO_ITEMS = "fasth_pro_items"
    const val ENTERPRISE_ITEM = "enterprise_item"
    const val CODE_THEME = "code_theme"
    const val ENTERPRISE_URL = "enterprise_url"
    const val NOTIFICATION_SOUND_PATH = "notification_sound_path"
    const val DISABLE_AUTO_LOAD_IMAGE = "disable_auto_loading_image"
    const val PLAY_STORE_REVIEW_ACTIVITY = "play_store_review_activity"
}
