package com.time.cat.ui.widgets.boardview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.DBmodel.DBSubPlan;
import com.time.cat.data.model.DBmodel.DBTask;
import com.time.cat.ui.modules.operate.InfoOperationActivity;
import com.time.cat.ui.modules.plans.detail.PlanDetailActivity;
import com.time.cat.ui.widgets.SmoothCheckBox;
import com.time.cat.util.string.TimeUtil;
import com.time.cat.util.view.ViewUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RecyclerViewVerticalDataAdapter extends RecyclerView.Adapter<RecyclerViewVerticalDataAdapter.ViewHolder> {

    private Activity mContext;
    private DBSubPlan dbSubPlan;
    private List<DBTask> mData;
    private LayoutInflater mInflater;

    private int mDragPosition;//正在拖动的 View 的 position
    private boolean mHideDragItem; // 是否隐藏正在拖动的 position
    private boolean onBine = false;

    public RecyclerViewVerticalDataAdapter(Activity context, DBSubPlan dbSubPlan) {
        this.mContext = context;
        this.dbSubPlan = dbSubPlan;
        this.mData = DB.schedules().findAll(dbSubPlan);
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = mInflater.inflate(R.layout.recyclerview_item_item, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        onBine = true;
        final DBTask item = mData.get(holder.getAdapterPosition());
        holder.item_title.setText(item.getTitle());

        if (holder.getAdapterPosition() == mDragPosition && mHideDragItem) {
            holder.itemView.setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setTag(item);
                ((PlanDetailActivity)mContext).getDragHelper().drag(v, position);
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2DialogActivity = new Intent(mContext, InfoOperationActivity.class);
                intent2DialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2DialogActivity.putExtra(InfoOperationActivity.TO_SAVE_STR, item.getContent());
                Bundle bundle = new Bundle();
                bundle.putSerializable(InfoOperationActivity.TO_UPDATE_TASK, item);
                intent2DialogActivity.putExtras(bundle);
                mContext.startActivity(intent2DialogActivity);
            }
        });

//        holder.item_title.setTextColor(item.getIsFinish() ? Color.GRAY : Color.BLACK);
        holder.item_checkBox.setChecked(item.getIsFinish());
        if (item.getIsFinish()) {
            holder.item_title.setTextColor(Color.GRAY);
            ViewUtil.addClearCenterLine(holder.item_title);
        } else {
            holder.item_title.setTextColor(Color.BLACK);
            ViewUtil.removeLine(holder.item_title);
        }
        holder.item_checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                if (onBine) return;
                if (isChecked) {
                    ViewUtil.addClearCenterLine(holder.item_title);
                    holder.item_title.setTextColor(Color.GRAY);
                    item.setIsFinish(true);
                    if (item.getFinished_datetime() == null) {
                        item.setFinished_datetime(TimeUtil.formatGMTDate(new Date()));
                    }
                } else {
                    ViewUtil.removeLine(holder.item_title);
                    holder.item_title.setTextColor(Color.BLACK);
                    item.setIsFinish(false);
                    item.setFinished_datetime(null);
                }
                DB.schedules().safeSaveDBTask(item);
            }
        });
        onBine = false;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void onDrag(int position) {
        mDragPosition = position;
        mHideDragItem = true;
        notifyItemChanged(position);
    }

    public void onDrop(int page, int position, Object tag) {
        mHideDragItem = false;
        DBTask task = (DBTask) tag;
        List<DBSubPlan> dbSubPlanList = DB.subPlans().findAll( dbSubPlan.getPlan());
        task.setSubplan(dbSubPlanList.get(page));
        DB.schedules().safeSaveDBTask(task);
        notifyItemChanged(position);
    }

    public void onDragOut() {
        if (mDragPosition >= 0 && mDragPosition < mData.size()) {
            mData.remove(mDragPosition);
            notifyDataSetChanged();// 此处如果用 notifyItemRemove 下一次选定时的 position 是错的
            mDragPosition = -1;
        }
    }

    public void onDragIn(int position, Object tag) {
        DBTask task = (DBTask) tag;
        if (position > mData.size()) {// 如果拖进来时候的 position 比当前 列表的长度大，就添加到列表末端
            position = mData.size();
        }
        mData.add(position, task);
        notifyItemInserted(position);
        mDragPosition = position;
        mHideDragItem = true;
    }

    public void updateDragItemVisibility(int position) {
        if (mDragPosition >= 0 && mDragPosition < mData.size() && position < mData.size() && mDragPosition != position) {
            if (Math.abs(mDragPosition - position) == 1) {
                notifyItemChanged(mDragPosition);
                Collections.swap(mData, mDragPosition, position);
                mDragPosition = position;
                notifyItemChanged(position);
            } else {
                notifyItemChanged(mDragPosition);
                if (mDragPosition > position) {
                    for (int i = mDragPosition; i > position; i--) {
                        Collections.swap(mData, i, i - 1);
                        notifyItemChanged(i);
                    }
                } else {
                    for (int i = mDragPosition; i < position; i++) {
                        Collections.swap(mData, i, i + 1);
                        notifyItemChanged(i);
                    }
                }
                mDragPosition = position;
                notifyItemChanged(position);
            }
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_title;
        SmoothCheckBox item_checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            item_title = itemView.findViewById(R.id.item_title);
            item_checkBox = itemView.findViewById(R.id.item_checkBox);
        }
    }
}
