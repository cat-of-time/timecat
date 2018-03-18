package com.time.cat.data;

import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.time.cat.BuildConfig;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.helper.DeviceNameGetter;
import com.time.cat.util.string.StringUtil;

import java.util.Locale;

import es.dmoral.toasty.Toasty;

/**
 * Created by kosh20111 on 18 Oct 2016, 9:29 PM
 */

public class AppHelper {

    @Nullable
    public static Fragment getFragmentByTag(@NonNull FragmentManager fragmentManager, @NonNull String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }

    public static void cancelNotification(@NonNull Context context) {
        cancelNotification(context, Constants.REQUEST_CODE);
    }

    public static void cancelNotification(@NonNull Context context, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(id);
        }
    }

    public static void cancelAllNotifications(@NonNull Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    public static void copyToClipboard(@NonNull Context context, @NonNull String uri) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getString(R.string.app_name), uri);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toasty.success(TimeCatApp.getInstance(), context.getString(R.string.success_copied)).show();
        }
    }

    public static boolean isNightMode(@NonNull Resources resources) {
        @Config.ThemeType int themeType = new Config(TimeCatApp.getInstance()).getThemeType(resources);
        return themeType != Config.LIGHT;
    }

    public static void updateAppLanguage(@NonNull Context context) {
        String lang = new Config(context).getAppLanguage();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, lang);
        }
        updateResourcesLegacy(context, lang);
    }

    private static void updateResources(Context context, String language) {
        Locale locale = getLocale(language);
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static void updateResourcesLegacy(Context context, String language) {
        Locale locale = getLocale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    @NonNull
    private static Locale getLocale(String language) {
        Locale locale = null;
        if (language.equalsIgnoreCase("zh-rCN")) {
            locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language.equalsIgnoreCase("zh-rTW")) {
            locale = Locale.TRADITIONAL_CHINESE;
        }
        if (locale != null) return locale;
        String[] split = language.split("-");
        if (split.length > 1) {
            locale = new Locale(split[0], split[1]);
        } else {
            locale = new Locale(language);
        }
        return locale;
    }

    public static String getDeviceName() {
        if (isEmulator()) {
            return "Android Emulator";
        }
        return DeviceNameGetter.getInstance().getDeviceName();
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains("Android SDK built for x86") || Build.MANUFACTURER.contains("Genymotion") || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) || "google_sdk".equals(Build.PRODUCT);
    }

    private static boolean isInstalledFromPlaySore(@NonNull Context context) {
        final String ipn = context.getPackageManager().getInstallerPackageName(BuildConfig.APPLICATION_ID);
        return !StringUtil.isEmpty(ipn);
    }

//    public static boolean isGoogleAvailable(@NonNull Context context) {
//        ApplicationInfo applicationInfo = null;
//        try {
//            applicationInfo = context.getPackageManager().getApplicationInfo("com.google.android.gms", 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return applicationInfo != null && applicationInfo.enabled &&
//                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
//    }

    public static boolean isDeviceAnimationEnabled(@NonNull Context context) {
        float duration = Settings.Global.getFloat(context.getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1);
        float transition = Settings.Global.getFloat(context.getContentResolver(), Settings.Global.TRANSITION_ANIMATION_SCALE, 1);
        return (duration != 0 && transition != 0);
    }

    public static boolean isDataPlan() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) TimeCatApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            final NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return mobile.isConnectedOrConnecting();
        }
        return false;
    }

}