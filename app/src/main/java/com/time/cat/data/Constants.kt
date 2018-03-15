package com.time.cat.data

import com.time.cat.R
import com.time.cat.TimeCatApp

object ConstantUtil {

    val CONTENT = "content://"
    val AUTHORITY = "com.time.cat"
    val SEPARATOR = "/"
    val CONTENT_URI = CONTENT + AUTHORITY

    val BROADCAST_RELOAD_SETTING = "broadcast_reload_setting"
    val BROADCAST_TIMECAT_MONITOR_SERVICE_MODIFIED = "broadcast_timecat_monitor_service_modified"

    val BROADCAST_CLIPBOARD_LISTEN_SERVICE_MODIFIED = "broadcast_clipboard_listen_service_modified"

    val BROADCAST_SET_TO_CLIPBOARD = "broadcast_set_to_clipboard"
    val BROADCAST_SET_TO_CLIPBOARD_MSG = "broadcast_set_to_clipboard_msg"


    //shareCard
    val HAD_SHARED = "had_shared"
    val SETTING_OPEN_TIMES = "setting_open_times"

    //FunctionSettingCard
    val MONITOR_CLIP_BOARD = "monitor_clip_board"
    val MONITOR_CLICK = "monitor_click"
    val TOTAL_SWITCH = "total_switch"
    val SHOW_FLOAT_VIEW = "show_float_view"
    val REMAIN_SYMBOL = "remain_symbol"
    val REMAIN_SECTION = "remain_section"
    val DEFAULT_LOCAL = "default_local"


    val AUTO_OPEN_SETTING = "auto_open_setting"

    //floatview
    val FLOAT_SWITCH_STATE = "float_switch_state"
    val FLOAT_VIEW_LAND_X = "float_view_land_x"
    val FLOAT_VIEW_LAND_Y = "float_view_land_Y"
    val FLOAT_VIEW_PORT_X = "float_view_port_x"
    val FLOAT_VIEW_PORT_Y = "float_view_port_y"


    val IS_SHOW_NOTIFY = "is_show_notify"
    val NOTIFY_DISABLED_IGNORE = "notify_disabled_ignore"


    //FeedBackAndUpdateCard

    //MonitorSettingCard
    val TEXT_ONLY = "text_only"
    val QQ_SELECTION = "qq_selection"
    val WEIXIN_SELECTION = "weixin_selection"
    val OTHER_SELECTION = "other_selection"

    val BROWSER_SELECTION = "browser_selection"


    val Setting_content_Changes = "tencent_contents_change"
    val SHOW_TENCENT_SETTINGS = "tencent_settings"


    val ONLINE_CONFIG_OPEN_UPDATE = "online_config_open_update"
    val DOUBLE_CLICK_INTERVAL = "double_click_interval"
    val DEFAULT_DOUBLE_CLICK_INTERVAL = 1000


    //SettingTimeCatActivity
    val TEXT_SIZE = "text_size"
    val LINE_MARGIN = "line_margin"
    val ITEM_MARGIN = "item_margin"
    val ITEM_PADDING = "item_padding"
    val TIMECAT_ALPHA = "timecat_alpha"
    val USE_LOCAL_WEBVIEW = "use_local_webview"
    val USE_FLOAT_VIEW_TRIGGER = "use_float_view_trigger"
    val TIMECAT_DIY_BG_COLOR = "timecat_diy_bg_color"
    val IS_FULL_SCREEN = "is_full_screen"
    val IS_STICK_HEADER = "is_stick_header"
    val IS_STICK_SHAREBAR = "is_stick_sharebar"
    val AUTO_ADD_BLANKS = "auto_add_blanks"
    val TREAT_BLANKS_AS_SYMBOL = "treat_blanks_as_symbol"


    val FLOATVIEW_SIZE = "floatview_size_"
    val FLOATVIEW_ALPHA = "floatview_alpha"
    val FLOATVIEW_DIY_BG_COLOR = "floatview_diy_bg_color"
    val FLOATVIEW_IS_STICK = "floatview_is_stick"


    val DEFAULT_TEXT_SIZE = 14
    val DEFAULT_LINE_MARGIN = 8
    val DEFAULT_ITEM_MARGIN = 0
    val DEFAULT_ITEM_PADDING = 10


    //whiteListActivity
    val WHITE_LIST_COUNT = "white_list_count"
    val WHITE_LIST = "white_list"
    val REFRESH_WHITE_LIST_BROADCAST = "refresh_white_list_broadcast"


    //Float whiteList Activity
    val FLOAT_WHITE_LIST_COUNT = "float_white_list_count"
    val FLOAT_WHITE_LIST = "float_white_list_"
    val FLOAT_REFRESH_WHITE_LIST_BROADCAST = "float_refresh_white_list_broadcast"

    val HAS_ADDED_LAUNCHER_AS_WHITE_LIST = "has_added_launcher_as_white_list"


    val UNIVERSAL_COPY_BROADCAST = "universal_copy_broadcast"
    val UNIVERSAL_COPY_BROADCAST_DELAY = "universal_copy_broadcast_delay"
    val SCREEN_CAPTURE_OVER_BROADCAST = "screen_capture_over_broadcast"


    val TOTAL_SWITCH_BROADCAST = "total_switch_broadcast"
    val MONITOR_CLICK_BROADCAST = "monitor_click_broadcast"
    val MONITOR_CLIPBOARD_BROADCAST = "monitor_clipboard_broadcast"

    val NOTIFY_UNIVERSAL_COPY_BROADCAST = "notify_universal_copy_broadcast"
    val NOTIFY_SCREEN_CAPTURE_OVER_BROADCAST = "notify_screen_capture_over_broadcast"

    val OCR_TIME = "ocr_time"
    val OCR_TIME_TO_ALERT = 5
    val SHOULD_SHOW_DIY_OCR = "should_show_diy_ocr"

    val DIY_OCR_KEY = "diy_ocr_key"


    val EFFECT_AFTER_REBOOT_BROADCAST = "effect_after_reboot_broadcast"

    val LONG_PRESS_KEY_INDEX = "long_press_key_index"

    val SHARE_APPS_DIS = "share_app_dis"
    val HAD_ENTER_INTRO = "had_enter_intro"
    val SHARE_APP_INDEX = "share_app_index"

    val HAD_SHOW_LONG_PRESS_TOAST = "had_show_long_press_toast"

    //copyActivity
    val IS_FULL_SCREEN_COPY = "is_full_screen_copy"

    //xp全局复制
    val UNIVERSAL_COPY_BROADCAST_XP = "universal_copy_broadcast_xp"

    // InfoOperationActivity
    val UNIVERSAL_SAVE_COTENT = "universal_save_content"

    val ALI_APP_KEY = TimeCatApp.getInstance().getString(R.string.ali_feedback_key)
    val ALI_APP_SECRET = TimeCatApp.getInstance().getString(R.string.ali_feedback_key_secret)
}
