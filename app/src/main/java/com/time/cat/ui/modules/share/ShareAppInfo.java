package com.time.cat.ui.activity.share;

import android.content.pm.ResolveInfo;

public class ShareAppInfo {

    public boolean enable;
    public ResolveInfo applicationInfo;
    public String packageName;
    public String appName;

    public ShareAppInfo(ResolveInfo applicationInfo, String appName, String packageName, boolean enable) {
        this.applicationInfo = applicationInfo;
        this.packageName = packageName;
        this.appName = appName;
        this.enable = enable;
    }
}
