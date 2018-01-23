package com.time.cat.component.activity;

import android.content.Intent;
import android.os.Bundle;

import com.time.cat.component.base.BaseActivity;
import com.time.cat.util.ConstantUtil;

public class UniversalCopyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(new Intent(ConstantUtil.UNIVERSAL_COPY_BROADCAST_DELAY));
        finish();
        overridePendingTransition(0, 0);
        return;
    }
}
