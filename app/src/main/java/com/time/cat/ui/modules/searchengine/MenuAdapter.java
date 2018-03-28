/*
 * Copyright 2016 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.time.cat.ui.activity.searchengine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timecat.commonjar.contentProvider.SPHelper;
import com.time.cat.R;
import com.time.cat.ui.activity.searchengine.listener.OnItemClickListener;
import com.time.cat.data.model.APImodel.SearchEngine;
import com.time.cat.data.Constants;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

public class MenuAdapter extends SwipeMenuAdapter<MenuAdapter.DefaultViewHolder> {

    private List<SearchEngine> searchEngines;

    private OnItemClickListener mOnItemClickListener;

    public MenuAdapter(List<SearchEngine> titles) {
        this.searchEngines = titles;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return searchEngines == null ? 0 : searchEngines.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_engine, parent, false);
    }

    @Override
    public MenuAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new DefaultViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(MenuAdapter.DefaultViewHolder holder, int position) {
        holder.setData(searchEngines.get(position), position);
        holder.setOnItemClickListener(mOnItemClickListener);
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final View backGround;
        TextView tvUrl;
        TextView tvTitle;
        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvUrl = itemView.findViewById(R.id.tv_url);
            backGround = itemView.findViewById(R.id.background);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public void setData(SearchEngine searchEngine, int position) {
            this.tvTitle.setText(searchEngine.title);
            this.tvUrl.setText(searchEngine.url);
            if (SPHelper.getInt(Constants.BROWSER_SELECTION, 0) == position) {
                backGround.setBackgroundResource(R.drawable.borders);
            } else {
                backGround.setBackgroundResource(R.drawable.transpate);
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
