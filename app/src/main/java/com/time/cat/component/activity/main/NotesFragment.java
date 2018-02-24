package com.time.cat.component.activity.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;
import com.time.cat.R;
import com.time.cat.component.activity.addtask.DialogActivity;
import com.time.cat.component.activity.main.listener.OnNoteViewClickListener;
import com.time.cat.component.base.BaseFragment;
import com.time.cat.database.DB;
import com.time.cat.mvp.model.DBmodel.DBNote;
import com.time.cat.mvp.model.DBmodel.DBUser;
import com.time.cat.mvp.presenter.FragmentPresenter;
import com.time.cat.util.ModelUtil;
import com.time.cat.util.override.ToastUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author dlink
 * @date 2018/1/25
 * @discription 笔记fragment
 */
public class NotesFragment extends BaseFragment implements FragmentPresenter, OnNoteViewClickListener {
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
        recyclerView.setNestedScrollingEnabled(false);

        //<功能归类分区方法，必须调用>-----------------------------------------------------------------
        initData();
        initView();
        initEvent();
        //</功能归类分区方法，必须调用>----------------------------------------------------------------

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
        refreshData();
    }

    @Override
    public void notifyDataChanged() {
        refreshData();
    }

    public void refreshData() {
        List<DBNote> dbNoteList = DB.notes().findAll();
        List<DBNote> adapterDBNoteList = new ArrayList<>();
        if (context != null) {
            DBUser dbUser = DB.users().getActive(context);
            for (int i = 0; i < dbNoteList.size(); i++) {
                if ((dbNoteList.get(i).getOwner().equals(ModelUtil.getOwnerUrl(dbUser)))) {
                    adapterDBNoteList.add(dbNoteList.get(i));
                }
            }
            adapter.setItems(adapterDBNoteList);
        }
    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------


    //<Event事件区>---只要存在事件监听代码就是----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用

    }


    //-//<Listener>------------------------------------------------------------------------------
    @Override
    public void onViewNoteRefreshClick() {
        refreshData();
    }
    //-//</Listener>-----------------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {

        private final List<DBNote> list = new ArrayList<>();

        private final ExpansionLayoutCollection expansionsCollection = new ExpansionLayoutCollection();

        public RecyclerAdapter() {
            expansionsCollection.openOnlyOne(true);
        }

        @Override
        public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerHolder(LayoutInflater.from(getContext()).inflate(R.layout.recyclerview_card_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, int position) {
            holder.bind(list, position);

            expansionsCollection.add(holder.getExpansionLayout());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void setItems(List<DBNote> items) {
            this.list.clear();
            this.list.addAll(items);
            notifyDataSetChanged();
        }
        public void addItems(List<DBNote> items) {
            this.list.addAll(items);
            notifyDataSetChanged();
        }

    }

    public class RecyclerHolder extends RecyclerView.ViewHolder implements
                                                                View.OnLongClickListener,
                                                                View.OnClickListener {
        @BindView(R.id.expansionLayout)
        ExpansionLayout expansionLayout;

        @BindView(R.id.notes_tv_title)
        TextView notes_tv_title;

        @BindView(R.id.notes_et_content)
        TextView notes_tv_content;

        List<DBNote> list;
        int position;

        public RecyclerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(List<DBNote> list, int position) {
            expansionLayout.collapse(false);
            this.list = list;
            this.position = position;
            DBNote note = list.get(position);
            notes_tv_title.setText(note.getTitle());
            // 排版，开头空两格。使用sSpannableStringBuilder,隐藏掉前面两个字符，达到缩进的错觉
//            SpannableStringBuilder span = new SpannableStringBuilder("缩进"+note.getContent());
//            span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
//                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            notes_tv_content.setText(note.getContent());
            notes_tv_title.setOnClickListener(this);
            notes_tv_title.setOnLongClickListener(this);
        }

        public ExpansionLayout getExpansionLayout() {
            return expansionLayout;
        }

        @Override
        public boolean onLongClick(View v) {
            if (v.getId() == R.id.notes_tv_title) {
                // 长按显示删除按钮
                new MaterialDialog.Builder(getActivity())
                        .content("确定删除这个任务吗？")
                        .positiveText("删除")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                DBNote dbNote = list.get(position);
                                Log.e(TAG, "dbNote == " + dbNote.toString());
                                try {
                                    DB.notes().delete(dbNote);
                                    ToastUtil.show("已删除");
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    ToastUtil.show("删除失败");
                                }
                                notifyDataChanged();
                            }
                        })
                        .negativeText("取消")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();
                return true;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.notes_tv_title) {

                DBNote note = list.get(position);
                Intent intent2DialogActivity = new Intent(context, DialogActivity.class);
                intent2DialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2DialogActivity.putExtra(DialogActivity.TO_SAVE_STR, note.getContent());
                Bundle bundle = new Bundle();
                bundle.putSerializable(DialogActivity.TO_UPDATE_NOTE, note);
                intent2DialogActivity.putExtras(bundle);
                startActivity(intent2DialogActivity);
                ToastUtil.show("to modify a note");
            }
        }
    }
    //</内部类>---尽量少用---------------------------------------------------------------------------

}
