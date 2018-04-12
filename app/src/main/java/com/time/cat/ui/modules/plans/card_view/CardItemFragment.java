package com.time.cat.ui.modules.plans.card_view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.data.model.DBmodel.DBSubPlan;
import com.time.cat.data.model.DBmodel.DBTask;
import com.time.cat.data.model.events.PersistenceEvents;
import com.time.cat.ui.modules.plans.detail.PlanDetailActivity;
import com.time.cat.ui.widgets.DragLayout;
import com.time.cat.ui.widgets.FlipView.FlipMenuItem;
import com.time.cat.ui.widgets.FlipView.FlipMenuView;
import com.time.cat.util.override.ToastUtil;
import com.time.cat.util.string.TimeUtil;
import com.time.cat.util.view.ImageUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.time.cat.util.UrlCountUtil.mContext;

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
    @BindView(R.id.card_item_more)
    ImageView card_item_more;

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
        card_item_more.setOnClickListener(this);
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

    boolean isDeleteAble = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.star:
                dbPlan.setIs_star(dbPlan.getIs_star() > 0 ? 0 : 1);
                DB.plans().safeSaveDBPlan(dbPlan);
                star.setImageResource(dbPlan.getIs_star() > 0 ? R.drawable.ic_star_on : R.drawable.ic_star_off);
                break;
            case R.id.card_item_more:
                FlipMenuView share = new FlipMenuView.Builder(getActivity(), card_item_more)
                        .addItem(new FlipMenuItem("删除", Color.RED, 0xff000000, ImageUtil.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_cancel_red_24dp)))
                        .addItem(new FlipMenuItem("刷新", Color.WHITE, 0xff4999F0, ImageUtil.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_autorenew_white_24dp)))
                        .addItem(new FlipMenuItem("编辑", Color.WHITE, 0xff4CAF50, ImageUtil.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_sort_white_24dp)))
                        .create();

                share.setOnFlipClickListener(new FlipMenuView.OnFlipClickListener() {
                    @Override
                    public void onItemClick(int position1) {
                        switch (position1) {
                            case 0:
                                new MaterialDialog.Builder(mContext).content("确定删除这个任务吗？").positiveText("删除").onPositive((dialog, which) -> {
                                    if (!isDeleteAble) return;
                                    isDeleteAble = false;
                                    new Handler().postDelayed(() -> isDeleteAble = true, 250);
                                    for (DBSubPlan dbSubPlan : DB.subPlans().findAll(dbPlan)) {
                                        for (DBTask dbTask : DB.schedules().findAll(dbSubPlan)) {
                                            DB.schedules().deleteAndFireEvent(dbTask);
                                        }
                                        DB.subPlans().deleteAndFireEvent(dbSubPlan);
                                    }
                                    DB.plans().deleteAndFireEvent(dbPlan);
                                }).negativeText("取消").onNegative((dialog, which) -> dialog.dismiss()).show();
                                break;
                            case 1:
                                TimeCatApp.eventBus().post(new PersistenceEvents.PlanUpdateEvent(dbPlan));
                                break;
                            case 2:
                                new MaterialDialog.Builder(TimeCatApp.getInstance()).title("编辑列表名称").inputType(InputType.TYPE_CLASS_TEXT).input("列表名称", "", (dialog, input) -> {
                                    // Do something
                                    dbPlan.setTitle("" + input);
                                    DB.plans().safeSaveDBPlanAndFireEvent(dbPlan);
                                    ToastUtil.ok("编辑成功：" + dbPlan.getTitle());
                                }).show();
                                break;
                        }
                    }

                    @Override
                    public void dismiss() {

                    }
                });
                break;
        }
    }
}
