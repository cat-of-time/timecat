package com.time.cat.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.time.cat.R;
import com.time.cat.ui.activity.addtask.DialogActivity;
import com.time.cat.ui.activity.main.listener.OnNoteViewClickListener;
import com.time.cat.ui.base.BaseFragment;
import com.time.cat.database.DB;
import com.time.cat.mvp.model.DBmodel.DBNote;
import com.time.cat.mvp.model.DBmodel.DBUser;
import com.time.cat.mvp.presenter.FragmentPresenter;
import com.time.cat.mvp.view.card_stack_view.CardStackView;
import com.time.cat.mvp.view.card_stack_view.StackAdapter;
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
public class NotesFragment extends BaseFragment implements FragmentPresenter,
                                                           OnNoteViewClickListener,
                                                           CardStackView.ItemExpendListener {
    @SuppressWarnings("unused")
    private static final String TAG = "NotesFragment";

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

        mStackView = view.findViewById(R.id.notes_csv);
        cardStackViewAdapter = new CardStackViewAdapter(context);

        //<功能归类分区方法，必须调用>-----------------------------------------------------------------
        initData();
        initView();
        initEvent();
        //</功能归类分区方法，必须调用>----------------------------------------------------------------

        return view;
    }
    //</生命周期>------------------------------------------------------------------------------------


    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------
    private CardStackView mStackView;
    private CardStackViewAdapter cardStackViewAdapter;

    @Override
    public void initView() {//必须调用

    }
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>-----------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-----------------------------------------
    @Override
    public void initData() {//必须调用
        refreshData();
        mStackView.setAdapter(cardStackViewAdapter);
    }

    @Override
    public void notifyDataChanged() {
        refreshData();
    }

    public void refreshData() {
        List<DBNote> dbNoteList = DB.notes().findAll();
        if (dbNoteList == null || dbNoteList.size() <= 0) {
            return;
        }
        List<DBNote> adapterDBNoteList = new ArrayList<>();

        List<Integer> CardStackViewDataList = new ArrayList<>();
        int[] CardStackViewData = getResources().getIntArray(R.array.card_stack_view_data);
        for (int aCardStackViewData : CardStackViewData) {
            CardStackViewDataList.add(aCardStackViewData);
        }
        if (context != null) {
            DBUser dbUser = DB.users().getActive(context);

            for (int i = 0; i < dbNoteList.size(); i++) {
                if ((dbNoteList.get(i).getOwner().equals(ModelUtil.getOwnerUrl(dbUser)))) {
                    adapterDBNoteList.add(dbNoteList.get(i));
                }
            }
            if (adapterDBNoteList.size() > 0) {
                cardStackViewAdapter.updateData(adapterDBNoteList);
            }
        }
    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------


    //<Event事件区>---只要存在事件监听代码就是----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用
        mStackView.setItemExpendListener(this);
    }


    //-//<Listener>------------------------------------------------------------------------------
    @Override
    public void onViewNoteRefreshClick() {
        refreshData();
    }
    //-//</Listener>-----------------------------------------------------------------------------



    //-//<CardStackView.ItemExpendListener>------------------------------------------------------------------------------
    @Override
    public void onItemExpend(boolean expend) {
//        mActionButtonContainer.setVisibility(expend ? View.VISIBLE : View.GONE);
    }
    //-//</CardStackView.ItemExpendListener>-----------------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------
    public class CardStackViewAdapter extends StackAdapter<DBNote> {

//        private final List<DBNote> list = new ArrayList<>();

        public CardStackViewAdapter(Context context) {
            super(context);
        }

        @Override
        public void bindView(DBNote data, int position, CardStackView.ViewHolder holder) {
            if (holder instanceof ColorItemViewHolder) {
                ColorItemViewHolder h = (ColorItemViewHolder) holder;
                h.onBind(data, position);
            }
        }

        @Override
        protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                default:
                    view = getLayoutInflater().inflate(R.layout.item_list_card, parent, false);
                    return new ColorItemViewHolder(view);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.item_list_card;
        }

    }

    public class ColorItemViewHolder extends CardStackView.ViewHolder implements
                                                                      View.OnLongClickListener,
                                                                      View.OnClickListener {
        @BindView(R.id.frame_list_card_item)
        View mLayout;
        @BindView(R.id.container_list_content)
        View mContainerContent;
        @BindView(R.id.text_list_card_title)
        TextView mTextTitle;

        @BindView(R.id.notes_tv_title)
        TextView notes_tv_title;

        @BindView(R.id.notes_tv_content)
        TextView notes_tv_content;

        DBNote dbNote;

        public ColorItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onItemExpand(boolean b) {
            mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void onBind(DBNote data, int position) {
            mLayout.getBackground().setColorFilter(data.getColor(), PorterDuff.Mode.SRC_IN);

            mTextTitle.setText(String.valueOf(position));

            dbNote = data;
            notes_tv_title.setText(dbNote.getTitle());
            // 排版，开头空两格。使用sSpannableStringBuilder,隐藏掉前面两个字符，达到缩进的错觉
//            SpannableStringBuilder span = new SpannableStringBuilder("缩进"+note.getContent());
//            span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
//                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            notes_tv_content.setText(dbNote.getContent());
            notes_tv_content.setOnClickListener(this);
            notes_tv_title.setOnLongClickListener(this);
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
            if (v.getId() == R.id.notes_tv_content) {

                Intent intent2DialogActivity = new Intent(context, DialogActivity.class);
                intent2DialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2DialogActivity.putExtra(DialogActivity.TO_SAVE_STR, dbNote.getContent());
                Bundle bundle = new Bundle();
                bundle.putSerializable(DialogActivity.TO_UPDATE_NOTE, dbNote);
                intent2DialogActivity.putExtras(bundle);
                startActivity(intent2DialogActivity);
                ToastUtil.show("to modify a note");
            }
        }
    }
    //</内部类>---尽量少用---------------------------------------------------------------------------

}
