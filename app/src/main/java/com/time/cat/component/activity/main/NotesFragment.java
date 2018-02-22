package com.time.cat.component.activity.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.github.florent37.expansionpanel.ExpansionLayout;
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;
import com.time.cat.R;
import com.time.cat.component.activity.main.listener.OnViewClickListener;
import com.time.cat.component.base.BaseFragment;
import com.time.cat.database.DB;
import com.time.cat.mvp.model.DBmodel.DBNote;
import com.time.cat.mvp.model.DBmodel.DBUser;
import com.time.cat.mvp.presenter.FragmentPresenter;
import com.time.cat.util.ModelUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author dlink
 * @date 2018/1/25
 * @discription 笔记fragment
 */
public class NotesFragment extends BaseFragment implements FragmentPresenter, OnViewClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = "NotesFragment";


    @BindView(R.id.notes_rv)
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    Context context;

    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        recyclerView = view.findViewById(R.id.notes_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);

        //<功能归类分区方法，必须调用>-----------------------------------------------------------------
        initData();
        initView();
        initEvent();
        //</功能归类分区方法，必须调用>----------------------------------------------------------------

        List<DBNote> dbNoteList = DB.notes().findAll();
        List<DBNote> adapterDBNoteList = new ArrayList<>();
        DBUser dbUser = DB.users().getActive(getContext());
        for (int i = 0; i < dbNoteList.size(); i++) {
            if (dbNoteList.get(i).getOwner() == ModelUtil.getOwnerUrl(dbUser)) {
                adapterDBNoteList.add(dbNoteList.get(i));
            }
        }
        adapter.setItems(adapterDBNoteList);
        return view;
    }
    //</生命周期>------------------------------------------------------------------------------------


    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------
    @Override
    public void initView() {//必须调用

    }
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>-----------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-----------------------------------------
    @Override
    public void initData() {//必须调用

    }

    @Override
    public void notifyDataChanged() {
        refreshData();
    }

    public void refreshData() {
        List<DBNote> dbNoteList = DB.notes().findAll();
        List<DBNote> adapterDBNoteList = new ArrayList<>();
        DBUser dbUser = DB.users().getActive(context);
        for (int i = 0; i < dbNoteList.size(); i++) {
            if ((dbNoteList.get(i).getOwner().equals(ModelUtil.getOwnerUrl(dbUser)))) {
                adapterDBNoteList.add(dbNoteList.get(i));
            }
        }
        adapter.setItems(adapterDBNoteList);
        adapter.notifyDataSetChanged();
    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------


    //<Event事件区>---只要存在事件监听代码就是----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用

    }

    @Override
    public void onViewTodayClick() {

    }

    @Override
    public void onViewRefreshClick() {

    }

    @Override
    public void onViewNoteRefreshClick() {
        refreshData();
    }

    @Override
    public void onViewChangeMarkThemeClick() {

    }


    //-//<Listener>------------------------------------------------------------------------------
    //-//</Listener>-----------------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------
    public final static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {

        private final List<DBNote> list = new ArrayList<>();

        private final ExpansionLayoutCollection expansionsCollection = new ExpansionLayoutCollection();

        public RecyclerAdapter() {
            expansionsCollection.openOnlyOne(true);
        }

        @Override
        public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return RecyclerHolder.buildFor(parent);
        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, int position) {
            holder.bind(list.get(position));

            expansionsCollection.add(holder.getExpansionLayout());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void setItems(List<DBNote> items) {
            this.list.addAll(items);
            notifyDataSetChanged();
        }

        public final static class RecyclerHolder extends RecyclerView.ViewHolder {

            private static final int LAYOUT = R.layout.recyclerview_card_item;

            @BindView(R.id.expansionLayout)
            ExpansionLayout expansionLayout;

            @BindView(R.id.notes_tv_title)
            TextView notes_tv_title;

            @BindView(R.id.notes_et_content)
            EditText notes_et_content;

            public static RecyclerHolder buildFor(ViewGroup viewGroup){
                return new RecyclerHolder(LayoutInflater.from(viewGroup.getContext()).inflate(LAYOUT, viewGroup, false));
            }

            public RecyclerHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(DBNote note){
                expansionLayout.collapse(false);
                notes_tv_title.setText(note.getTitle());
                notes_et_content.setText(note.getContent());
            }

            public ExpansionLayout getExpansionLayout() {
                return expansionLayout;
            }
        }
    }
    //</内部类>---尽量少用---------------------------------------------------------------------------

}
