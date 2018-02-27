package com.time.cat.ui.activity.main.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.mvp.model.DBmodel.DBNote;
import com.time.cat.mvp.view.card_stack_view.CardStackView;
import com.time.cat.mvp.view.card_stack_view.StackAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardStackViewAdapter extends StackAdapter<Integer> {

    private final List<DBNote> list = new ArrayList<>();

    public CardStackViewAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(Integer data, int position, CardStackView.ViewHolder holder) {
        if (holder instanceof ColorItemViewHolder) {
            ColorItemViewHolder h = (ColorItemViewHolder) holder;
            h.onBind(list, data, position);
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

    static class ColorItemViewHolder extends CardStackView.ViewHolder implements
                                                                      View.OnLongClickListener,
                                                                      View.OnClickListener {
        View mLayout;
        View mContainerContent;
        TextView mTextTitle;

        @BindView(R.id.notes_tv_title)
        TextView notes_tv_title;

        @BindView(R.id.notes_et_content)
        TextView notes_tv_content;
        List<DBNote> list;
        int position;

        public ColorItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mTextTitle = view.findViewById(R.id.text_list_card_title);
        }

        @Override
        public void onItemExpand(boolean b) {
            mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void onBind(List<DBNote> list, Integer data, int position) {
            this.list = list;
            this.position = position;
            mLayout.getBackground().setColorFilter(data, PorterDuff.Mode.SRC_IN);

            mTextTitle.setText(String.valueOf(position));

            DBNote note = list.get(position);
            notes_tv_title.setText(note.getTitle());
            // 排版，开头空两格。使用sSpannableStringBuilder,隐藏掉前面两个字符，达到缩进的错觉
//            SpannableStringBuilder span = new SpannableStringBuilder("缩进"+note.getContent());
//            span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
//                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            notes_tv_content.setText(note.getContent());
            notes_tv_content.setOnClickListener(this);
            notes_tv_title.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (v.getId() == R.id.notes_tv_title) {
                // 长按显示删除按钮
//                new MaterialDialog.Builder(getActivity())
//                        .content("确定删除这个任务吗？")
//                        .positiveText("删除")
//                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(MaterialDialog dialog, DialogAction which) {
//                                DBNote dbNote = list.get(position);
//                                Log.e(TAG, "dbNote == " + dbNote.toString());
//                                try {
//                                    DB.notes().delete(dbNote);
//                                    ToastUtil.show("已删除");
//                                } catch (SQLException e) {
//                                    e.printStackTrace();
//                                    ToastUtil.show("删除失败");
//                                }
//                                notifyDataChanged();
//                            }
//                        })
//                        .negativeText("取消")
//                        .onNegative(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                dialog.dismiss();
//                            }
//                        }).show();
                return true;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.notes_tv_content) {

//                DBNote note = list.get(position);
//                Intent intent2DialogActivity = new Intent(context, DialogActivity.class);
//                intent2DialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent2DialogActivity.putExtra(DialogActivity.TO_SAVE_STR, note.getContent());
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(DialogActivity.TO_UPDATE_NOTE, note);
//                intent2DialogActivity.putExtras(bundle);
//                startActivity(intent2DialogActivity);
//                ToastUtil.show("to modify a note");
            }
        }
    }

}
