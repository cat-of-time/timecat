package com.time.cat.ui.modules.setting.card;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.Config;
import com.time.cat.ui.base.baseCard.AbsCard;
import com.time.cat.util.override.ToastUtil;

public class RoutineCard extends AbsCard {

    public RoutineCard(Context context) {
        super(context);
        initView(context);
    }

    public RoutineCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RoutineCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    protected void initView(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.card_routine, this);

        // 是否24小时制
        SwitchCompat settings_hour_format = findViewById(R.id.settings_hour_format);
        settings_hour_format.setChecked(new Config(TimeCatApp.getInstance()).getUse24hourFormat());
        settings_hour_format.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            new Config(TimeCatApp.getInstance()).setUse24hourFormat(isChecked);
        });

        //是否星期天为一周的开始
        SwitchCompat settings_sunday_first = findViewById(R.id.settings_sunday_first);
        settings_sunday_first.setChecked(new Config(TimeCatApp.getInstance()).isSundayFirst());
        settings_sunday_first.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            new Config(TimeCatApp.getInstance()).setSundayFirst(isChecked);
        });

        // setting start
        TextView settings_start_weekly_at = findViewById(R.id.settings_start_weekly_at);
        settings_start_weekly_at.setText(getHoursString(new Config(TimeCatApp.getInstance()).getStartWeeklyAt()));
        RelativeLayout settings_start_weekly_at_holder = findViewById(R.id.settings_start_weekly_at_holder);
        settings_start_weekly_at_holder.setOnClickListener(v -> new MaterialDialog.Builder(mContext).items(R.array.weekly_start_end).itemsCallbackSingleChoice(new Config(TimeCatApp.getInstance()).getStartWeeklyAt(), (dialog, view, which, text) -> {
            int start = which + 1;
            int end = new Config(TimeCatApp.getInstance()).getEndWeeklyAt();
            if (start >= end) {
                ToastUtil.e("The day cannot end earlier than it starts！");
            } else {
                new Config(TimeCatApp.getInstance()).setStartWeeklyAt(start);
                settings_start_weekly_at.setText(getHoursString(start));
                ToastUtil.ok("设置成功！");
                dialog.dismiss();
            }
            return true;
        }).show());

        // setting end
        TextView settings_end_weekly_at = findViewById(R.id.settings_end_weekly_at);
        settings_end_weekly_at.setText(getHoursString(new Config(TimeCatApp.getInstance()).getEndWeeklyAt()));
        RelativeLayout settings_end_weekly_at_holder = findViewById(R.id.settings_end_weekly_at_holder);
        settings_end_weekly_at_holder.setOnClickListener(v -> new MaterialDialog.Builder(mContext).items(R.array.weekly_start_end).itemsCallbackSingleChoice(new Config(TimeCatApp.getInstance()).getEndWeeklyAt(), (dialog, view, which, text) -> {
            int end = which + 1;
            int start = new Config(TimeCatApp.getInstance()).getStartWeeklyAt();
            if (end <= start) {
                ToastUtil.e("The day cannot end earlier than it starts！");
            } else {
                new Config(TimeCatApp.getInstance()).setEndWeeklyAt(end);
                settings_end_weekly_at.setText(getHoursString(end));
                ToastUtil.ok("设置成功！");
                dialog.dismiss();
            }
            return true;
        }).show());

        // setting pre-filled weeks
        TextView settings_pre_filled_weeks = findViewById(R.id.settings_pre_filled_weeks);
        settings_pre_filled_weeks.setText(String.valueOf(new Config(TimeCatApp.getInstance()).getPreFilledWeeks()));
        RelativeLayout settings_pre_filled_weeks_holder = findViewById(R.id.settings_pre_filled_weeks_holder);
        settings_pre_filled_weeks_holder.setOnClickListener(v -> new MaterialDialog.Builder(mContext).items(R.array.pre_filled_weeks).itemsCallbackSingleChoice(getPositionFrom(new Config(TimeCatApp.getInstance()).getPreFilledWeeks()), (dialog, view, which, text) -> {
            new Config(TimeCatApp.getInstance()).setPreFilledWeeks(getPreFilledWeeks(which));
            settings_pre_filled_weeks.setText(String.valueOf(getPreFilledWeeks(which)));
            ToastUtil.ok("设置成功！");
            dialog.dismiss();
            return true;
        }).show());
    }


    private String getHoursString(int hours) {
        return (hours < 10?  "0"+hours : hours) + ":00";
    }

    private int getPreFilledWeeks(int position) {
        switch (position) {
            case 0: return 1;
            case 1: return 3;
            case 2: return 5;
            case 3: return 7;
            case 4: return 9;
            case 5: return 11;
            case 6: return 13;
            case 7: return 31;
            case 8: return 61;
            case 9: return 91;
            default:return 1;
        }
    }

    private int getPositionFrom(int preFilledWeeks) {
        switch (preFilledWeeks) {
            case 1 :return 0;
            case 3 :return 1;
            case 5 :return 2;
            case 7 :return 3;
            case 9 :return 4;
            case 11:return 5;
            case 13:return 6;
            case 31:return 7;
            case 61:return 8;
            case 91:return 9;
            default:return 0;
        }
    }
}
