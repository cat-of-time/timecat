package com.time.cat.data.async;

import android.os.AsyncTask;

import com.time.cat.data.model.DBmodel.DBTask;
import com.time.cat.ui.widgets.asyncExpandableListView.AsyncExpandableListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/16
 * @discription for schedules fragment
 * @usage null
 */
public class LoadContentDataTask extends AsyncTask<Void, Void, List<DBTask>> {

    private final int mGroupOrdinal;
    private WeakReference<AsyncExpandableListView<DBTask, DBTask>> listviewRef = null;

    public LoadContentDataTask(int groupOrdinal, AsyncExpandableListView<DBTask, DBTask> listview) {
        mGroupOrdinal = groupOrdinal;
        listviewRef = new WeakReference<>(listview);
    }

    @Override
    protected List<DBTask> doInBackground(Void... params) {
        List<DBTask> items = new ArrayList<>();
        items.add(listviewRef.get().getHeader(mGroupOrdinal));
        return items;
    }


    @Override
    protected void onPostExecute(List<DBTask> tasks) {
        if (listviewRef.get() != null && tasks != null) {
            listviewRef.get().onFinishLoadingGroup(mGroupOrdinal, tasks);
        }
    }

}