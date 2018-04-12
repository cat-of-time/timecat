package com.time.cat.ui.modules.activity;

import android.content.Intent;
import android.os.Bundle;

import com.time.cat.ui.base.BaseActivity;
import com.time.cat.data.Constants;

public class UniversalCopyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(new Intent(Constants.UNIVERSAL_COPY_BROADCAST_DELAY));
        finish();
        overridePendingTransition(0, 0);
    }
}
