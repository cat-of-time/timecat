package com.time.cat.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.timecat.commonjar.contentProvider.SPHelper;
import com.timecat.utils.StatusBarCompat;
import com.time.cat.R;
import com.time.cat.ui.base.BaseActivity;
import com.time.cat.util.clipboard.ClipboardUtils;
import com.time.cat.data.Constants;
import com.time.cat.util.override.ToastUtil;
import com.time.cat.util.UrlCountUtil;

public class DiyOcrKeyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diy_ocr_key);

        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.colorPrimary);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.set_diy_ocr_key);

        findViewById(R.id.copy_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlCountUtil.onEvent(UrlCountUtil.CLICK_COPY_OCR_URL);
                ClipboardUtils.setText(DiyOcrKeyActivity.this, "https://www.microsoft.com/cognitive-services/");
                ToastUtil.ok(R.string.copyed);
            }
        });
        EditText keyInput = findViewById(R.id.ocr_diy_key_edit);
        keyInput.setText(SPHelper.getString(Constants.DIY_OCR_KEY, ""));

        findViewById(R.id.ocr_diy_key_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (keyInput.getText() != null) {
                    SPHelper.save(Constants.DIY_OCR_KEY, keyInput.getText().toString());
                    ToastUtil.ok(R.string.set_diy_ocr_key_ok);
                    finish();
                }
            }
        });

    }
}
