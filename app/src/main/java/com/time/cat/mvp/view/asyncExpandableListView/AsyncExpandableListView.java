package com.time.cat.mvp.view.asyncExpandableListView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * @author dlink
 * @date 2018/1/26
 * @discription AsyncExpandableListView
 */
public class AsyncExpandableListView<T1, T2> extends CollectionView<T1, T2> {

    protected Map<OnGroupStateChangeListener, Integer> mOnGroupStateChangeListeners = new WeakHashMap<>();
    protected int expandedGroupOrdinal = -1;
    private AsyncExpandableListViewCallbacks<T1, T2> mCallbacks;


    public AsyncExpandableListView(Context context) {
        super(context);
    }

    public AsyncExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AsyncExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected CollectionView.RowInformation<T1, T2> populatRoweData(RecyclerView.ViewHolder holder, int position) {
        CollectionView.RowInformation<T1, T2> rowInfo = super.populatRoweData(holder, position);
        if (rowInfo.isHeader()) {
            mOnGroupStateChangeListeners.put((AsyncHeaderViewHolder) holder, rowInfo.getGroupOrdinal());
        }

        return rowInfo;
    }

    public void onGroupClicked(int groupOrdinal) {
        if (groupOrdinal != expandedGroupOrdinal) {
            onStartExpandingGroup(groupOrdinal);
        } else {
            collapseGroup(groupOrdinal);
        }

    }

    public void setCallbacks(final AsyncExpandableListViewCallbacks<T1, T2> callbacks) {
        CollectionViewCallbacks<T1, T2> collectionViewCallbacks = new CollectionViewCallbacks<T1, T2>() {
            @Override
            public ViewHolder newCollectionHeaderView(Context context, int groupOrdinal, ViewGroup parent) {
                return callbacks.newCollectionHeaderView(context, groupOrdinal, parent);
            }

            @Override
            public ViewHolder newCollectionItemView(Context context, int groupOrdinal, ViewGroup parent) {
                return callbacks.newCollectionItemView(context, groupOrdinal, parent);
            }

            @Override
            public void bindCollectionHeaderView(Context context, ViewHolder holder, int groupOrdinal, T1 headerItem) {
                callbacks.bindCollectionHeaderView(context, (AsyncHeaderViewHolder) holder, groupOrdinal, headerItem);
            }

            @Override
            public void bindCollectionItemView(Context context, ViewHolder holder, int groupOrdinal, T2 item) {
                callbacks.bindCollectionItemView(context, holder, groupOrdinal, item);
            }
        };
        setCollectionCallbacks(collectionViewCallbacks);
        mCallbacks = callbacks;
    }

    protected void collapseGroup(int groupOrdinal) {
        expandedGroupOrdinal = -1;
        removeAllItemsInGroup(groupOrdinal);
        for (OnGroupStateChangeListener onGroupStateChangeListener : mOnGroupStateChangeListeners.keySet()) {
            if (mOnGroupStateChangeListeners.get(onGroupStateChangeListener) == groupOrdinal) {
                onGroupStateChangeListener.onGroupCollapsed();
            }
        }
    }

    protected void onStartExpandingGroup(int groupOrdinal) {
        int ordinal = 0;
        for (int i = 0; i < mInventory.getGroups().size(); i++) {
            ordinal = mInventory.getGroups().keyAt(i);
            if (ordinal != groupOrdinal) {
                collapseGroup(ordinal);
            }
        }

        expandedGroupOrdinal = groupOrdinal;
        mCallbacks.onStartLoadingGroup(groupOrdinal);
        for (OnGroupStateChangeListener onGroupStateChangeListener : mOnGroupStateChangeListeners.keySet()) {
            if (mOnGroupStateChangeListeners.get(onGroupStateChangeListener) == groupOrdinal) {
                onGroupStateChangeListener.onGroupStartExpending();
            }
        }
    }

    public boolean onFinishLoadingGroup(int groupOrdinal, List<T2> items) {
        if (expandedGroupOrdinal < 0 || groupOrdinal != expandedGroupOrdinal) {
            return false;
        }

        addItemsInGroup(expandedGroupOrdinal, items);
        for (OnGroupStateChangeListener onGroupStateChangeListener : mOnGroupStateChangeListeners.keySet()) {
            if (mOnGroupStateChangeListeners.get(onGroupStateChangeListener) == expandedGroupOrdinal) {
                onGroupStateChangeListener.onGroupExpanded();
            }
        }

        return true;
    }


    public interface OnGroupStateChangeListener {

        void onGroupStartExpending();

        void onGroupExpanded();

        void onGroupCollapsed();
    }

}
