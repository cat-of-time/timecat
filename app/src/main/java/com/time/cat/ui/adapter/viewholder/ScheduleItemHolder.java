package com.time.cat.ui.adapter.viewholder;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.time.cat.R;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/16
 * @discription for schedules fragment
 * @usage null
 */
public class ScheduleItemHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "ScheduleItemHolder";

    private final TextView tvContent;
    private final TextView schedule_task_tv_label;
    private final TextView schedule_task_tv_date;
    private final TextView schedule_task_tv_time;
    private final TextView schedule_task_tv_tag;
    String[] text_color_set = new String[]{
            "#f44336", "#ff9800", "#2196f3", "#4caf50"
    };
    String[] background_color_set = new String[]{
            "#50f44336", "#50ff9800", "#502196f3", "#504caf50"
    };
    String[] label_str_set = new String[] {
            "重要且紧急", "重要不紧急", "紧急不重要", "不重要不紧急",
    };
    public ScheduleItemHolder(View v) {
        super(v);
        tvContent = v.findViewById(R.id.schedule_task_tv_content);
        schedule_task_tv_label = v.findViewById(R.id.schedule_task_tv_label);
        schedule_task_tv_date = v.findViewById(R.id.schedule_task_tv_date);
        schedule_task_tv_time = v.findViewById(R.id.schedule_task_tv_time);
        schedule_task_tv_tag = v.findViewById(R.id.schedule_task_tv_tag);
    }

    public TextView getTextViewContent() {
        return tvContent;
    }

    public TextView getScheduleTaskTv_date() {
        return schedule_task_tv_date;
    }

    public TextView getScheduleTaskTv_label() {
        return schedule_task_tv_label;
    }

    public TextView getScheduleTaskTv_time() {
        return schedule_task_tv_time;
    }

    public TextView getScheduleTaskTv_tag() {
        return schedule_task_tv_tag;
    }

    public void setLabel(int label) {
        schedule_task_tv_label.setText(label_str_set[label]);
        schedule_task_tv_label.setTextColor(Color.parseColor(text_color_set[label]));
        schedule_task_tv_label.setBackgroundColor(Color.parseColor(background_color_set[label]));
    }
}