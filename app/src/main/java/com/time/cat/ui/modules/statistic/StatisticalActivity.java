package com.time.cat.ui.modules.statistic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.DBmodel.DBTask;
import com.time.cat.ui.base.mvp.BaseActivity;
import com.time.cat.ui.modules.achievement.AchievementActivity;
import com.time.cat.ui.modules.main.MainActivity;
import com.time.cat.ui.widgets.RippleView;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.view.BubbleChartView;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/24
 * @discription null
 * @usage null
 */
public class StatisticalActivity extends BaseActivity<StatisticalMVP.View, StatisticalPresenter>
        implements View.OnClickListener {
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
    LinearLayout level_img;
    @BindView(R.id.back_btn)
    RippleView toBack;
    @BindView(R.id.top_bar_right_btn)
    RippleView toAchievement;

    // today data block
    @BindView(R.id.today_finish_num)
    TextView today_finish_num;
    @BindView(R.id.today_work_time)
    TextView today_work_time;
    @BindView(R.id.today_fail_tomato)
    TextView today_fail_tomato;

    @BindView(R.id.today_finish_task_num)
    TextView today_finish_task_num;
    @BindView(R.id.today_create_num)
    TextView today_create_num;
    @BindView(R.id.today_remain_task)
    TextView today_remain_task;

    @BindView(R.id.today_note_num)
    TextView today_note_num;
    @BindView(R.id.today_plan_num)
    TextView today_plan_num;
    @BindView(R.id.today_subplan_num)
    TextView today_subplan_num;

    // history data block
    @BindView(R.id.history_total_tasks_num)
    TextView history_total_tasks_num;
    @BindView(R.id.history_unfinished_task_num)
    TextView history_unfinished_task_num;

    @BindView(R.id.history_total_routine_num)
    TextView history_total_routine_num;

    @BindView(R.id.history_total_notes_num)
    TextView history_total_notes_num;

    @BindView(R.id.history_total_plans_num)
    TextView history_total_plans_num;
    @BindView(R.id.history_total_subplans_num)
    TextView history_total_subplans_num;

    @BindView(R.id.history_total_pomodoro_num)
    TextView history_total_pomodoro_num;
    @BindView(R.id.history_work_time)
    TextView history_work_time;
    @BindView(R.id.history_fail_tomato)
    TextView history_fail_tomato;



    // chart
    @BindView(R.id.today_distribute_btn)
    TextView today_distribute_btn;
    @BindView(R.id.seven_day_distribute_btn)
    TextView seven_day_distribute_btn;
    @BindView(R.id.day_num_btn)
    TextView day_num_btn;
    @BindView(R.id.week_num_btn)
    TextView week_num_btn;
    @BindView(R.id.month_num_btn)
    TextView month_num_btn;
    @BindView(R.id.tomato_num_chart)
    LineChartView tomato_num_chart;
    @BindView(R.id.tomato_distribute_chart)
    BubbleChartView tomato_distribute_chart;
    @BindView(R.id.bubble_chat_layout)
    RelativeLayout bubble_chat_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = findViewById(R.id.top_bar_text);
        TextView textView2 = findViewById(R.id.top_bar_right_btn_text);
        textView.setText(R.string.statistical_text);
        textView2.setVisibility(View.VISIBLE);
        textView2.setText(R.string.my_achievement_text);
        ButterKnife.bind(this);

        new Handler().postDelayed(() -> {
            DateTime today = new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
            DateTime tomorrow = today.plusDays(1);
            int createSize = DB.schedules().findBetween(today.toDate(), tomorrow.toDate()).size();
            int finishedSize = DB.schedules().findFinishedBetween(today.toDate(), tomorrow.toDate()).size();
            today_finish_task_num.setText("" + finishedSize);
            today_create_num.setText("" + createSize);
            today_remain_task.setText("" + (createSize - finishedSize));
            today_note_num.setText("" + DB.notes().findBetween(today.toDate(), tomorrow.toDate()).size());
            today_plan_num.setText("" + DB.plans().findBetween(today.toDate(), tomorrow.toDate()).size());
            today_subplan_num.setText("" + DB.subPlans().findBetween(today.toDate(), tomorrow.toDate()).size());
            today_finish_num.setText("" + DB.pomodoros().findFinishedBetween(today.toDate(), tomorrow.toDate()).size());
            today_work_time.setText("" + ((double)((int)((double)DB.pomodoros().getTodayWorkTime() / 60 * 1000)) / 1000));//保留3位有效数字,猥琐
            today_fail_tomato.setText("" + DB.pomodoros().findUnfinishedBetween(today.toDate(), tomorrow.toDate()).size());

            history_total_tasks_num.setText("" + DB.schedules().findAllForActiveUser().size());
            history_unfinished_task_num.setText("" + DB.schedules().findBy(DBTask.COLUMN_IS_FINISHED, false).size());
            history_total_routine_num.setText("" + DB.routines().findAllForActiveUser().size());
            history_total_notes_num.setText("" + DB.notes().findAllForActiveUser().size());
            history_total_plans_num.setText("" + DB.plans().findAllForActiveUser().size());
            history_total_subplans_num.setText("" + DB.subPlans().findAllForActiveUser().size());
            history_total_pomodoro_num.setText("" + DB.pomodoros().findAllForActiveUser().size());
            history_work_time.setText("" + ((double)((int)((double)DB.pomodoros().getTotalWorkTime() / 60 * 1000)) / 1000));//保留3位有效数字,猥琐
            history_fail_tomato.setText("" + DB.pomodoros().findTotalUnfinished().size());
        }, 1000);

        toBack.setOnClickListener(this);
        toAchievement.setOnClickListener(this);

        //图表按钮
        today_distribute_btn.setOnClickListener(this);
        seven_day_distribute_btn.setOnClickListener(this);

        //图表按钮
        day_num_btn.setOnClickListener(this);
        week_num_btn.setOnClickListener(this);
        month_num_btn.setOnClickListener(this);
        level_img.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_bar_right_btn:
                launchActivity(new Intent(this, AchievementActivity.class));
                break;
            case R.id.back_btn:
                launchActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
    }
}
