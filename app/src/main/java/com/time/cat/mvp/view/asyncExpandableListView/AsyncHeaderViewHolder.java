package com.time.cat.mvp.view.asyncExpandableListView;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author dlink
 * @date 2018/1/26
 * @discription AsyncHeaderViewHolder for AsyncExpandableListView
 */
public abstract class AsyncHeaderViewHolder extends RecyclerView.ViewHolder implements AsyncExpandableListView.OnGroupStateChangeListener {
    private final int mGroupOrdinal;
    private final AsyncExpandableListView mAsyncExpandableListView;

    public AsyncHeaderViewHolder(View itemView, int groupOrdinal, AsyncExpandableListView asyncExpandableListView) {
        super(itemView);
        mGroupOrdinal = groupOrdinal;
        mAsyncExpandableListView = asyncExpandableListView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mAsyncExpandableListView.onGroupClicked(mGroupOrdinal);
                onItemClick(v);
            }
        });
    }

    /**
     * triggered by onClick event, to be overrode.
     *
     * @param view item
     */
    public void onItemClick(View view) {
    }


}
