package com.yumingchuan.rsqmonthcalendar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yumingchuan.rsqmonthcalendar.utils.EmptyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumingchuan on 2017/11/23.
 */

/**
 * @param <T> 万能适配器
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseViewHolder> {

    private final List<T> dataList;

    public BaseRecyclerViewAdapter() {
        dataList = new ArrayList<>();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(onCreateView(parent, viewType));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.getItemView().setTag(position);
        holder.getItemView().setOnClickListener(onClickListener);
        bindViewData(holder.getItemView(), (dataList != null && dataList.size() > position) ? dataList.get(position) : null, position);
    }


    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!EmptyUtils.isEmpty(onItemClickListener)) {
                int position = (int) view.getTag();
                if (position < dataList.size()) {
                    onItemClickListener.onItemClick(dataList.get(position), position);
                }
            }
        }
    };

    public static class BaseViewHolder extends RecyclerView.ViewHolder {

        private final View itemView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public View getItemView() {
            return itemView;
        }
    }

    public abstract View onCreateView(ViewGroup parent, int viewType);

    public abstract void bindViewData(View itemView, T t, int position);

    public void reloadData(List<T> list) {
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Object object, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}


