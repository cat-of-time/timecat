package com.time.cat.component.activity.howtouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shang.commonjar.contentProvider.SPHelper;
import com.shang.utils.StatusBarCompat;
import com.time.cat.R;
import com.time.cat.component.activity.IntroActivity;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.util.ConstantUtil;

public class HowToUseActivity extends BaseActivity {
    public static final String GO_TO_OPEN_FROM_OUTER = "go_to_open_from_outer";

    private View introMenu;
    private LinearLayout introContent;

    private TextView introTitle;
    private TextView introMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_use);

        SPHelper.save(ConstantUtil.HAD_ENTER_INTRO, true);
        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.colorPrimary);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.introduction);

        introMenu = findViewById(R.id.intro_menu);
        introContent = findViewById(R.id.intro_content);

        introTitle = findViewById(R.id.intro_title);
        introMsg = findViewById(R.id.intro_msg);

        findViewById(R.id.introduction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIntroActivity();
            }
        });


        findViewById(R.id.overall_intro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introMenu.setVisibility(View.GONE);
                introContent.setVisibility(View.VISIBLE);
                introTitle.setText(R.string.overall_intro);
                introMsg.setText(R.string.overall_intro_msg);
            }
        });

        findViewById(R.id.problems).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introMenu.setVisibility(View.GONE);
                introContent.setVisibility(View.VISIBLE);
                introTitle.setText(R.string.problems);
                introMsg.setText(R.string.problem_content);
            }
        });

        findViewById(R.id.how_to_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introMenu.setVisibility(View.GONE);
                introContent.setVisibility(View.VISIBLE);
                introTitle.setText(R.string.how_to_set_title);
                introMsg.setText(R.string.how_to_set_msg);
            }
        });


        findViewById(R.id.about_control).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introMenu.setVisibility(View.GONE);
                introContent.setVisibility(View.VISIBLE);
                introTitle.setText(R.string.about_control);
                introMsg.setText(R.string.about_control_msg);
            }
        });

        findViewById(R.id.about_accessibility).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introMenu.setVisibility(View.GONE);
                introContent.setVisibility(View.VISIBLE);
                introTitle.setText(R.string.about_accessibility);
                introMsg.setText(R.string.about_accessibility_msg);
            }
        });

        findViewById(R.id.about_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introMenu.setVisibility(View.GONE);
                introContent.setVisibility(View.VISIBLE);
                introTitle.setText(R.string.about_click);
                introMsg.setText(R.string.about_click_msg);
            }
        });

        findViewById(R.id.how_to_use_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introMenu.setVisibility(View.GONE);
                introContent.setVisibility(View.VISIBLE);
                introTitle.setText(R.string.how_to_use_copy);
                introMsg.setText(R.string.how_to_use_copy_msg);
            }
        });

        findViewById(R.id.about_ocr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introMenu.setVisibility(View.GONE);
                introContent.setVisibility(View.VISIBLE);
                introTitle.setText(R.string.about_ocr);
                introMsg.setText(R.string.about_ocr_msg);
            }
        });

        findViewById(R.id.about_universal_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introMenu.setVisibility(View.GONE);
                introContent.setVisibility(View.VISIBLE);
                introTitle.setText(R.string.about_universal_copy);
                introMsg.setText(R.string.about_universal_copy_msg);
            }
        });

        findViewById(R.id.open_from_outside).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introMenu.setVisibility(View.GONE);
                introContent.setVisibility(View.VISIBLE);
                introTitle.setText(R.string.open_from_outside);
                introMsg.setText(R.string.open_from_outside_msg);
            }
        });

        Intent intent = getIntent();
        boolean goToOpenFromOuter = intent.getBooleanExtra(GO_TO_OPEN_FROM_OUTER, false);
        if (goToOpenFromOuter) {
            findViewById(R.id.open_from_outside).performClick();
        }
    }

    private void showIntroActivity() {
        Intent intent = new Intent();
        intent.setClass(this, IntroActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (introMenu.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else {
            introMenu.setVisibility(View.VISIBLE);
            introContent.setVisibility(View.GONE);
        }
    }
}
