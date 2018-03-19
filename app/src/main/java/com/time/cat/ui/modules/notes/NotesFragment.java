package com.time.cat.ui.modules.notes;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.data.model.events.PersistenceEvents;
import com.time.cat.ui.activity.main.listener.OnNoteViewClickListener;
import com.time.cat.ui.adapter.TimeLineNotesAdapter;
import com.time.cat.ui.base.BaseFragment;
import com.time.cat.ui.base.mvpframework.factory.CreatePresenter;
import com.time.cat.util.override.LogUtil;
import com.time.cat.util.source.AvatarManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author dlink
 * @date 2018/1/25
 * @discription 笔记fragment，只与view有关，业务逻辑下放给presenter
 */
@CreatePresenter(NotesPresenter.class)
public class NotesFragment
        extends BaseFragment<NotesFragmentAction, NotesPresenter>
        implements OnNoteViewClickListener, NotesFragmentAction {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        handler = new Handler();
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        ButterKnife.bind(this, view);
        avatar.setImageResource(AvatarManager.res(DB.users().getActive().avatar()));
        mAdapter = new TimeLineNotesAdapter();
        mAdapter.addFooterView(inflater.inflate(R.layout.item_notes_list_footer, container, false));
        mAdapter.openLoadAnimation();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);
        getMvpPresenter().onAttachMvpView(this);
        getMvpPresenter().refresh();
        mRefreshLayout.setOnRefreshListener(refreshLayout -> refreshLayout.getLayout().postDelayed(() -> {
            getMvpPresenter().refresh();
            refreshLayout.finishRefresh();
        }, 2000));
        TimeCatApp.eventBus().register(this);

        return view;
    }
    //</editor-fold desc="生命周期">

    //<editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)">
    @BindView(R.id.refreshLayout)
    RefreshLayout mRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.avatar)
    ImageView avatar;
    //</editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)">

    //<editor-fold desc="Event事件区--只要存在事件监听代码就是">

    //-//<Activity自动刷新>-
    @Override
    public void notifyDataChanged() {
        getMvpPresenter().refresh();
    }
    //-//</Activity自动刷新>


    //-//<用户强制刷新>
    @Override
    public void onViewNoteRefreshClick() {
        getMvpPresenter().refresh();
    }
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
    //</editor-fold desc="Event事件区--只要存在事件监听代码就是">

}
