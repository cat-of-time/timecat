package com.time.cat.ui.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.time.cat.R;
import com.time.cat.data.model.entity.Achievement;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/24
 * @discription null
 * @usage null
 */
public class AchievementAdapter extends BaseAdapter {
    private SparseArray sparseArray;
    private Context context;
    private boolean gotAchievement = false;
    private ImageView imageView;
    private TextView textView;

    public AchievementAdapter(Context context, SparseArray sparseArray, boolean z) {
        this.context = context;
        this.sparseArray = sparseArray;
        this.gotAchievement = z;
    }

    @Override
    public int getCount() {
        return sparseArray.size();
    }

    @Override
    public Object getItem(int position) {
        return sparseArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_achievement, null);
            imageView = view.findViewById(R.id.big_icon);
            textView = view.findViewById(R.id.achievement_name);
        }
        Achievement achievement = (Achievement) sparseArray.valueAt(position);

        textView.setText(achievement.getName());


        if (!this.gotAchievement || achievement.getComplete() <= 0) {
            imageView.setImageResource(achievement.getDefaultImgRes());
        } else {
            imageView.setImageResource(achievement.getCompleteImgRes());
        }

        imageView.setOnClickListener(v -> {
            View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_achievement, null);
            ImageView dialog_picture = dialog.findViewById(R.id.dialog_picture);
            RelativeLayout share_btn = dialog.findViewById(R.id.share_btn);
            if (achievement.getComplete() <= 0) {
                dialog_picture.setImageResource(achievement.getDefaultImgRes());
            } else {
                share_btn.setVisibility(View.VISIBLE);
                share_btn.setOnClickListener(v1 -> {
                    // click share TODO
                });
                dialog_picture.setImageResource(achievement.getCompleteImgRes());
            }
            TextView title = dialog.findViewById(R.id.dialog_name);
            title.setText(achievement.getName());
            TextView dialog_describe = dialog.findViewById(R.id.dialog_describe);
            dialog_describe.setText(achievement.getDescribe());
            new MaterialDialog.Builder(context).customView(dialog, false).show();
        });
        return view;
    }
}
