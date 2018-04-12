package com.zhangsiqi.dragboarddemo.adapter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhangsiqi.dragboarddemo.R;
import com.zhangsiqi.dragboarddemo.data.Entry;
import com.zhangsiqi.dragboarddemo.data.Item;

import java.util.List;

/**
 * Created by zhangsiqi on 2016/9/11.
 */
public class RecyclerViewHorizontalDataAdapter extends RecyclerView.Adapter<RecyclerViewHorizontalDataAdapter.ViewHolder>{

    public static final int TYPE_CONTENT = 0;
    public static final int TYPE_FOOTER = 1;

    private Activity mContext;
    private List<Entry> mData;
    private LayoutInflater mInflater;

    private View mFooterView;

    public void setFooterView(View view) {
        mFooterView = view;
        notifyItemInserted(getItemCount() - 1);
    }

    public RecyclerViewHorizontalDataAdapter(Activity context, List<Entry> data) {
        this.mContext = context;
        this.mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mFooterView != null && viewType == TYPE_FOOTER)
            return new ViewHolder(mFooterView, TYPE_FOOTER);
        View convertView = mInflater.inflate(R.layout.recyclerview_item_entry, parent, false);
        return new ViewHolder(convertView, TYPE_CONTENT);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_CONTENT:
                Entry entry = mData.get(position);
                holder.tv_title.setText(entry.getId());
                List<Item> itemList = entry.getItemList();
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                holder.rv_item.setLayoutManager(layoutManager);
                RecyclerViewVerticalDataAdapter itemAdapter = new RecyclerViewVerticalDataAdapter(mContext, itemList);
                holder.rv_item.setAdapter(itemAdapter);
                break;
            case TYPE_FOOTER:
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

        TextView tv_title;
        RecyclerView rv_item;

        public ViewHolder(View convertView, int itemType) {
            super(convertView);
            if (itemType == TYPE_CONTENT) {
                tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                rv_item = (RecyclerView) convertView.findViewById(R.id.rv);
            }
        }

        public RecyclerView getRecyclerView() {
            return rv_item;
        }
    }
}
