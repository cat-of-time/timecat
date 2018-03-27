package com.time.cat.ui.modules.statistic;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.ui.base.mvp.BaseActivity;
import com.time.cat.ui.widgets.RippleView;

import butterknife.BindView;
import lecho.lib.hellocharts.view.BubbleChartView;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/24
 * @discription null
 * @usage null
 */
public class StatisticalActivity extends BaseActivity<StatisticalMVP.View, StatisticalPresenter> implements View.OnClickListener {
    @Override
    protected int layout() {
        return R.layout.activity_statistical;
    }

    @Override
    protected boolean canBack() {
        return true;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @NonNull
    @Override
    public StatisticalPresenter providePresenter() {
        return new StatisticalPresenter();
    }

    @BindView(R.id.level_img)
    LinearLayout A;
    @BindView(R.id.back_btn)
    RippleView k;
    @BindView(R.id.top_bar_right_btn)
    RippleView l;
    @BindView(R.id.today_finish_num)
    TextView m;
    @BindView(R.id.today_work_time)
    TextView n;
    @BindView(R.id.today_fail_tomato)
    TextView o;
    @BindView(R.id.history_finish_num)
    TextView p;
    @BindView(R.id.history_work_time)
    TextView q;
    @BindView(R.id.history_fail_tomato)
    TextView r;
    @BindView(R.id.today_distribute_btn)
    TextView s;
    @BindView(R.id.seven_day_distribute_btn)
    TextView t;
    @BindView(R.id.day_num_btn)
    TextView u;
    @BindView(R.id.week_num_btn)
    TextView v;
    @BindView(R.id.month_num_btn)
    TextView w;
    @BindView(R.id.tomato_num_chart)
    LineChartView x;
    @BindView(R.id.tomato_distribute_chart)
    BubbleChartView y;
    @BindView(R.id.bubble_chat_layout)
    RelativeLayout z;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = findViewById(R.id.top_bar_text);
        TextView textView2 = findViewById(R.id.top_bar_right_btn_text);

        textView.setText(R.string.statistical_text);
        textView2.setVisibility(View.VISIBLE);
        textView2.setText(R.string.my_achievement_text);
        l.setOnClickListener(this);
        s.setOnClickListener(this);
        t.setOnClickListener(this);
        u.setOnClickListener(this);
        v.setOnClickListener(this);
        w.setOnClickListener(this);
        k.setOnClickListener(this);
        A.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
