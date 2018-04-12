package com.time.cat.ui.modules.notes.timeline_view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.data.model.events.PersistenceEvents;
import com.time.cat.ui.adapter.TimeLineNotesAdapter;
import com.time.cat.ui.base.mvp.BaseLazyLoadFragment;
import com.time.cat.ui.modules.main.listener.OnNoteViewClickListener;
import com.time.cat.ui.modules.notes.NotesFragment;
import com.time.cat.util.override.LogUtil;
import com.time.cat.util.source.AvatarManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/28
 * @discription null
 * @usage null
 */
public class TimeLineFragment extends BaseLazyLoadFragment<TimeLineMVP.View, TimeLinePresenter>
        implements NotesFragment.OnScrollBoundaryDecider, OnNoteViewClickListener, TimeLineMVP.View {

    @Override
    public int getLayout() {
        return 0;
    }

    @Override
    public void initView() {}

    @Override
    public View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        handler = new Handler();
        View view = inflater.inflate(R.layout.fragment_notes_timelineview, container, false);
        ButterKnife.bind(this, view);
        mAdapter = new TimeLineNotesAdapter();
        View headerView = inflater.inflate(R.layout.item_notes_list_header, container, false);
        ImageView avatar = headerView.findViewById(R.id.avatar);
        avatar.setImageResource(AvatarManager.res(DB.users().getActive().avatar()));
        mAdapter.addHeaderView(headerView);
        mAdapter.addFooterView(inflater.inflate(R.layout.item_notes_list_footer, container, false));
        mAdapter.openLoadAnimation();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);
        getPresenter().refresh();
        mRefreshLayout.setOnRefreshListener(refreshLayout -> refreshLayout.getLayout().postDelayed(() -> {
            getPresenter().refresh();
            refreshLayout.finishRefresh();
        }, 2000));
        TimeCatApp.eventBus().register(this);
        return view;
    }

    //<editor-fold desc="Field">

    Context context;
    Handler handler;
    private TimeLineNotesAdapter mAdapter;
    private Queue<Object> pendingEvents = new LinkedList<>();

    //</editor-fold desc="Field">

    //<editor-fold desc="生命周期">


    @Override
    public void onResume() {
        super.onResume();
        while (!pendingEvents.isEmpty()) {
            LogUtil.e("Processing pending event...");
            onEvent(pendingEvents.poll());
        }
    }
    //</editor-fold desc="生命周期">

    //<editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)">
    @BindView(R.id.refreshLayout)
    RefreshLayout mRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    //</editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)">

    //<editor-fold desc="Event事件区--只要存在事件监听代码就是">

    //-//<Activity自动刷新>
    @Override
    public void notifyDataChanged() {
        getPresenter().refresh();
    }
    //-//</Activity自动刷新>


    //-//<用户强制刷新>
    @Override
    public void onViewNoteRefreshClick() {
        getPresenter().refresh();
    }

    @Override
    public void onViewSortClick() {}

    @Override
    public void initSearchView(Menu menu, AppCompatActivity activity) {}
    //-//</用户强制刷新>


    //-//<NotesFragmentAction>
    @Override
    public void refreshView(List<DBNote> adapterDBNoteList) {
        mAdapter.replaceData(adapterDBNoteList);
    }
    //-//</NotesFragmentAction>


    public void onEvent(final Object evt) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (evt instanceof PersistenceEvents.ModelCreateOrUpdateEvent) {
                    notifyDataChanged();
                } else if (evt instanceof PersistenceEvents.NoteCreateEvent) {
                    notifyDataChanged();
                } else if (evt instanceof PersistenceEvents.NoteUpdateEvent) {
                    notifyDataChanged();
                } else if (evt instanceof PersistenceEvents.NoteDeleteEvent) {
                    notifyDataChanged();
                }
            }
        });
    }

    @NonNull
    @Override
    public TimeLinePresenter providePresenter() {
        return new TimeLinePresenter();
    }

    @Override
    public boolean canRefresh() {
        return false;
    }

    @Override
    public boolean canLoadMore() {
        return false;
    }
    //</editor-fold desc="Event事件区--只要存在事件监听代码就是">


}
