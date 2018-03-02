package com.time.cat.util.onestep;

import android.content.pm.ResolveInfo;


public class ResolveInfoWrap {
    public ResolveInfo resolveInfo;
    public int type;

    public ResolveInfoWrap(ResolveInfo localResolveInfo, int typeUrl) {
        resolveInfo = localResolveInfo;
        type = typeUrl;
    }
}
