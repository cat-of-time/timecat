package com.time.cat.ui.fragment.notes.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.time.cat.R;
import com.time.cat.database.DB;
import com.time.cat.mvp.model.DBmodel.DBNote;
import com.time.cat.mvp.view.card_stack_view.CardStackView;
import com.time.cat.mvpframework.factory.CreatePresenter;
import com.time.cat.ui.activity.addtask.DialogActivity;
import com.time.cat.ui.activity.main.listener.OnNoteViewClickListener;
import com.time.cat.ui.base.BaseFragment;
import com.time.cat.ui.fragment.notes.presenter.NotesPresenter;
import com.time.cat.util.override.ToastUtil;

import java.sql.SQLException;
import java.util.List;

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
        implements OnNoteViewClickListener, NotesFragmentAction,
                   ColorItemViewHolder.ColorItemViewHolderAction,
                   CardStackViewAdapter.CardStackViewAdapterAction,
                   CardStackView.ItemExpendListener {
    @SuppressWarnings("unused")
    private static final String TAG = "NotesFragment";

    Context context;
    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();

        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        ButterKnife.bind(this, view);
        setupStackView();

        return view;
    }
    //</生命周期>------------------------------------------------------------------------------------





    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码-----------------------------------
    @BindView(R.id.notes_csv)
    CardStackView mStackView;

    private CardStackViewAdapter cardStackViewAdapter;

    private void setupStackView() {
        cardStackViewAdapter = new CardStackViewAdapter(context);
        cardStackViewAdapter.setCardStackViewAdapterAction(this);
        getMvpPresenter().onAttachMvpView(this);
        getMvpPresenter().refresh();
        // 等adapter和数据准备完毕再setAdapter
        mStackView.setAdapter(cardStackViewAdapter);
        mStackView.setItemExpendListener(this);
    }
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>---------------------------------





    //<Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //-//<Activity自动刷新>---------------------------------------------------------------------------
    @Override
    public void notifyDataChanged() {
        getMvpPresenter().refresh();
    }
    //-//</Activity自动刷新>--------------------------------------------------------------------------


    //-//<用户强制刷新>------------------------------------------------------------------------------
    @Override
    public void onViewNoteRefreshClick() {
        getMvpPresenter().refresh();
    }
    //-//</用户强制刷新>-----------------------------------------------------------------------------


    //-//<NotesFragmentAction>----------------------------------------------------------------------
    @Override
    public void refreshView(List<DBNote> adapterDBNoteList) {
        cardStackViewAdapter.updateData(adapterDBNoteList);
    }
    //-//</NotesFragmentAction>---------------------------------------------------------------------


    //-//<CardStackView>----------------------------------------------------------------------------
    @Override
    public void onTitleLongClick(DBNote dbNote) {
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
    }

    @Override
    public void onContentClick(DBNote dbNote) {
        Intent intent2DialogActivity = new Intent(context, DialogActivity.class);
        intent2DialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2DialogActivity.putExtra(DialogActivity.TO_SAVE_STR, dbNote.getContent());
        Bundle bundle = new Bundle();
        bundle.putSerializable(DialogActivity.TO_UPDATE_NOTE, dbNote);
        intent2DialogActivity.putExtras(bundle);
        startActivity(intent2DialogActivity);
        ToastUtil.show("to modify a note");
    }

    @Override
    public CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_list_card, parent, false);
        ColorItemViewHolder colorItemViewHolder = new ColorItemViewHolder(view);
        colorItemViewHolder.setColorItemViewHolderAction(this);
        return colorItemViewHolder;
    }

    @Override
    public void onItemExpend(boolean expend) {}
    //-//</CardStackView>---------------------------------------------------------------------------


    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------

}
