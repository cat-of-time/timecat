package com.zhangsiqi.dragboarddemo.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhangsiqi.dragboarddemo.MainActivity;
import com.zhangsiqi.dragboarddemo.R;
import com.zhangsiqi.dragboarddemo.data.Item;

import java.util.Collections;
import java.util.List;

/**
 * Created by zhangsiqi on 2016/9/11.
 */
public class RecyclerViewVerticalDataAdapter extends RecyclerView.Adapter<RecyclerViewVerticalDataAdapter.ViewHolder> {

    private Activity mContext;
    private List<Item> mData;
    private LayoutInflater mInflater;

    private int mDragPosition;//正在拖动的 View 的 position
    private boolean mHideDragItem; // 是否隐藏正在拖动的 position

    public RecyclerViewVerticalDataAdapter(Activity context, List<Item> data) {
        this.mContext = context;
        this.mContext = context;
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = mInflater.inflate(R.layout.recyclerview_item_item, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Item item = mData.get(position);
        holder.tv_name.setText(item.getItemName());
        holder.tv_id.setText(item.getItemId());
        holder.tv_info.setText(item.getInfo());

        if (position == mDragPosition && mHideDragItem) {
            holder.itemView.setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setTag(item);
                ((MainActivity)mContext).getDragHelper().drag(v, position);
                return true;
            }
        });
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
        Item task = (Item) tag;
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

        TextView tv_name, tv_id, tv_info;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_id = (TextView) itemView.findViewById(R.id.tv_id);
            tv_info = (TextView) itemView.findViewById(R.id.tv_info);
        }
    }
}
