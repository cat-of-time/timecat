package com.time.cat.component.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.time.cat.util.ConstantUtil;

public class TotalSwitchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(new Intent(ConstantUtil.TOTAL_SWITCH_BROADCAST));
        finish();
        overridePendingTransition(0, 0);
        return;
    }
}
