package com.time.cat.ui.modules.plans.card_view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.ui.modules.plans.detail.PlanDetailActivity;
import com.time.cat.ui.widgets.DragLayout;
import com.time.cat.util.string.TimeUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/29
 * @discription null
 * @usage null
 */
public class CardItemFragment extends Fragment implements DragLayout.GotoDetailListener, View.OnClickListener {
    @BindView(R.id.drag_layout)
    DragLayout dragLayout;
    @BindView(R.id.image)
    ImageView imageView;
    @BindView(R.id.star)
    ImageView star;
    @BindView(R.id.create_at)
    TextView create_at;
    @BindView(R.id.update_at)
    TextView update_at;
    @BindView(R.id.plan_title)
    TextView title;
    @BindView(R.id.content)
    TextView content;

    private DBPlan dbPlan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_item, null);
        ButterKnife.bind(this, rootView);
        if (dbPlan != null) {
            Glide.with(TimeCatApp.getInstance()).load(dbPlan.getCoverImageUrl()).into(imageView);
            Date date = TimeUtil.formatGMTDateStr(dbPlan.getCreated_datetime());
            if (date != null) {
                String s = (date.getYear()+1900) + "." + (date.getMonth() + 1) + "." + date.getDate();
                create_at.setText(s);
            }
            date = TimeUtil.formatGMTDateStr(dbPlan.getUpdate_datetime());
            if (date != null) {
                String s = (date.getYear()+1900) + "." + (date.getMonth() + 1) + "." + date.getDate();
                update_at.setText(s);
            }
            star.setImageResource(dbPlan.getIs_star() > 0 ? R.drawable.ic_star_on : R.drawable.ic_star_off);
            title.setText(dbPlan.getTitle());
            content.setText(dbPlan.getContent());
        }
        star.setOnClickListener(this);
        dragLayout.setGotoDetailListener(this);
        return rootView;
    }

    @Override
    public void gotoDetail() {
        Activity activity = (Activity) getContext();
        if (activity != null) {
//转场动画无效，不知道为啥
//            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation
//                    (activity, new Pair(imageView,PlanDetailActivity.IMAGE_TRANSITION_NAME));
//            Intent intent = new Intent(activity, PlanDetailActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("DBPlan", dbPlan);
//            intent.putExtras(bundle);
//            intent.putExtra(PlanDetailActivity.EXTRA_IMAGE_URL, imageUrl);
//            ActivityCompat.startActivity(activity, intent, options.toBundle());
            Intent intent = new Intent(activity, PlanDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("DBPlan", dbPlan);
            intent.putExtras(bundle);
            activity.startActivity(intent);
        }
    }

    public void bindData(DBPlan dbPlan) {
        this.dbPlan = dbPlan;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.star:
                dbPlan.setIs_star(dbPlan.getIs_star() > 0 ? 0 : 1);
                star.setImageResource(dbPlan.getIs_star() > 0 ? R.drawable.ic_star_on : R.drawable.ic_star_off);
                break;
        }
    }
}
