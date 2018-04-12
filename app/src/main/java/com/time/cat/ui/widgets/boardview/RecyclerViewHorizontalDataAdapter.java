package com.time.cat.ui.widgets.boardview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.time.cat.R;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.Converter;
import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.data.model.DBmodel.DBSubPlan;
import com.time.cat.data.model.DBmodel.DBTask;
import com.time.cat.ui.modules.operate.InfoOperationActivity;
import com.time.cat.ui.widgets.FlipView.FlipMenuItem;
import com.time.cat.ui.widgets.FlipView.FlipMenuView;
import com.time.cat.util.override.ToastUtil;
import com.time.cat.util.string.StringUtil;
import com.time.cat.util.string.TimeUtil;
import com.time.cat.util.view.ImageUtil;

import java.util.Date;
import java.util.List;

public class RecyclerViewHorizontalDataAdapter extends RecyclerView.Adapter<RecyclerViewHorizontalDataAdapter.ViewHolder>{

    public static final int TYPE_CONTENT = 0;
    public static final int TYPE_FOOTER = 1;

    private Activity mContext;
    private List<DBSubPlan> mData;
    private LayoutInflater mInflater;

    private View mFooterView;
    private DBPlan dbPlan;

    public void setFooterView(View view) {
        mFooterView = view;
        notifyItemInserted(getItemCount() - 1);
    }

    public RecyclerViewHorizontalDataAdapter(Activity context, DBPlan dbPlan) {
        this.mContext = context;
        this.dbPlan = dbPlan;
        this.mData = DB.subPlans().findAll(dbPlan);
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return new ViewHolder(mFooterView, TYPE_FOOTER);
        }
        View convertView = mInflater.inflate(R.layout.recyclerview_item_entry, parent, false);
        return new ViewHolder(convertView, TYPE_CONTENT);
    }

    boolean isDeleteAble = true;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_CONTENT:
                DBSubPlan dbSubPlan = mData.get(position);
                holder.title_icon.setOnClickListener(v -> {
                    FlipMenuView share = new FlipMenuView.Builder(mContext, holder.title_icon)
                            .addItem(new FlipMenuItem("编辑", Color.WHITE, 0xff4CAF50, ImageUtil.getBitmapFromVectorDrawable(mContext, R.drawable.ic_sort_white_24dp)))
                            .addItem(new FlipMenuItem("刷新", Color.WHITE, 0xff4999F0, ImageUtil.getBitmapFromVectorDrawable(mContext, R.drawable.ic_autorenew_white_24dp)))
                            .addItem(new FlipMenuItem("删除", Color.RED, 0xff000000, ImageUtil.getBitmapFromVectorDrawable(mContext, R.drawable.ic_cancel_red_24dp)))
                            .create();

                    share.setOnFlipClickListener(new FlipMenuView.OnFlipClickListener() {
                        @Override
                        public void onItemClick(int position1) {
                            switch (position1) {
                                case 0:
                                    new MaterialDialog.Builder(mContext).title("编辑列表名称").inputType(InputType.TYPE_CLASS_TEXT)
                                            .input("列表名称", "", (dialog, input) -> {
                                                // Do something
                                                dbSubPlan.setTitle("" + input);
                                                DB.subPlans().safeSaveDBSubPlanAndFireEvent(dbSubPlan);
                                                ToastUtil.ok("编辑成功：" + dbSubPlan.getTitle());
                                                notifyItemChanged(position);
                                            }).show();
                                    break;
                                case 1:
                                    notifyItemChanged(position);
                                    break;
                                case 2:
                                    new MaterialDialog.Builder(mContext).content("确定删除这个任务吗？").positiveText("删除").onPositive((dialog, which) -> {
                                        if (!isDeleteAble) return;
                                        new Handler().postDelayed(() -> isDeleteAble = true, 250);
                                        for (DBTask dbTask : DB.schedules().findAll(dbSubPlan)) {
                                            DB.schedules().deleteAndFireEvent(dbTask);
                                        }
                                        DB.subPlans().deleteAndFireEvent(dbSubPlan);
                                        notifyItemRemoved(position);
                                        notifyDataSetChanged();
                                    }).negativeText("取消").onNegative((dialog, which) -> dialog.dismiss()).show();
                                    break;
                            }
                        }

                        @Override
                        public void dismiss() {

                        }
                    });
                });
                holder.tv_title.setText(dbSubPlan.getTitle());
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                holder.rv_item.setLayoutManager(layoutManager);
                RecyclerViewVerticalDataAdapter itemAdapter = new RecyclerViewVerticalDataAdapter(mContext, dbSubPlan);
                holder.rv_item.setAdapter(itemAdapter);
                holder.add_task.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, InfoOperationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(InfoOperationActivity.TO_ATTACH_SUBPLAN, dbSubPlan);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                });
                break;
            case TYPE_FOOTER:
                holder.add_subPlan.setOnClickListener(v -> {
                    holder.add_subPlan.setVisibility(View.GONE);
                    holder.edit_sub_plan.setVisibility(View.VISIBLE);
                });
                holder.btn_cancel.setOnClickListener(v -> {
                    holder.add_subPlan.setVisibility(View.VISIBLE);
                    holder.edit_sub_plan.setVisibility(View.GONE);
                });
                holder.btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = holder.editText.getText().toString();
                        if (StringUtil.isEmpty(title)) {
                            ToastUtil.e("请输入名称！");
                            return;
                        }
                        DBSubPlan dbSubPlan = new DBSubPlan();
                        dbSubPlan.setTitle(title);
                        dbSubPlan.setCreated_datetime(TimeUtil.formatGMTDate(new Date()));
                        dbSubPlan.setUpdate_datetime(dbSubPlan.getCreated_datetime());
                        dbSubPlan.setOwner(Converter.getOwnerUrl(DB.users().getActive()));
                        dbSubPlan.setPlan(dbPlan);
                        dbSubPlan.setActiveUser();
                        DB.subPlans().safeSaveDBSubPlanAndFireEvent(dbSubPlan);

                        mData.add(dbSubPlan);
                        notifyItemInserted(getItemCount() - 1);
                        ToastUtil.ok("添加子[计划]："+title);
                    }
                });

                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mFooterView == null)
            return mData.size();
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mFooterView == null)
            return TYPE_CONTENT;
        if (position == getItemCount() - 1)
            return TYPE_FOOTER;
        return TYPE_CONTENT;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView title_icon;
        TextView tv_title;
        RecyclerView rv_item;
        RelativeLayout add_task;

        RelativeLayout add_subPlan;
        RelativeLayout edit_sub_plan;
        Button btn_cancel;
        com.rey.material.widget.Button btn_ok;
        EditText editText;

        public ViewHolder(View convertView, int itemType) {
            super(convertView);
            if (itemType == TYPE_CONTENT) {
                title_icon = convertView.findViewById(R.id.title_icon);
                tv_title = convertView.findViewById(R.id.tv_title);
                rv_item = convertView.findViewById(R.id.rv);
                add_task = convertView.findViewById(R.id.add);
            } else {
                add_subPlan = convertView.findViewById(R.id.add_sub_plan);
                edit_sub_plan = convertView.findViewById(R.id.edit_sub_plan);
                btn_cancel = convertView.findViewById(R.id.add_cancel);
                btn_ok = convertView.findViewById(R.id.add_ok);
                editText = convertView.findViewById(R.id.add_et);
            }
        }

        public RecyclerView getRecyclerView() {
            return rv_item;
        }
    }
}
