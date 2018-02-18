package com.time.cat.util.onestep;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.List;


public class Utils {
    public static List<ResolveInfo> getAllAppsInfo(Context context) {
        return context.getPackageManager().queryIntentActivities(Intent.makeMainActivity(null), 0);
    }

}
