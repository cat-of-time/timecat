package com.time.cat.component.activity.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.time.cat.R;

/**
 * @author dlink
 * @date 2018/1/23
 * @discription
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context context;
    private String[] titles;

    public CalendarAdapter(Context context) {
        titles = context.getResources().getStringArray(R.array.titles);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.view_calendar_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.calendar_item_content.setText(titles[position]);
        holder.calendar_item_delay.setText("顺延"+ position +"天");
    }

    @Override
    public int getItemCount() {
        return titles == null ? 0 : titles.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView calendar_item_content;
        CheckBox calendar_item_checkBox;
        TextView calendar_item_delay;
        ViewHolder(View view) {
            super(view);

            calendar_item_content = view.findViewById(R.id.calendar_item_content);
            calendar_item_checkBox = view.findViewById(R.id.calendar_item_checkBox);
            calendar_item_delay = view.findViewById(R.id.calendar_item_delay);

            calendar_item_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("ViewHolder", "onClick--> position = " + getPosition());
                }
            });
//            ColorStateList sl = new ColorStateList(
//                    new int[][] {
//                            new int[] { -android.R.attr.state_checked },
//                            new int[] { android.R.attr.state_checked }
//                    },
//                    new int[] {
//                            ColorUtil.resolveColor(calendar_item_checkBox.getContext(), R.attr.colorControlNormal),
//                            R.color.red
//                    });
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                calendar_item_checkBox.setButtonTintList(sl);
//            } else {
//                Drawable checkDrawable = ContextCompat.getDrawable(
//                        calendar_item_checkBox.getContext(),
//                        R.drawable.abc_btn_check_material);
//                Drawable drawable = DrawableCompat.wrap(checkDrawable);
//                DrawableCompat.setTintList(drawable, sl);
//                calendar_item_checkBox.setButtonDrawable(drawable);
//            }


//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                calendar_item_checkBox.setButtonTintList();
//            }
        }
    }
}
