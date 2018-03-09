package com.time.cat.ui.adapter.viewholder;

import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.ui.widgets.card_stack_view.CardStackView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/28
 * @discription null
 * @usage null
 */
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

    private DBNote dbNote;

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
            if (colorItemViewHolderAction != null) {
                colorItemViewHolderAction.onTitleLongClick(dbNote);
                Log.e("sdf", "colorItemViewHolderAction.onTitleLongClick(dbNote);");
            }
            Log.e("sdf", "onLongClick");
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.notes_tv_content) {
            if (colorItemViewHolderAction != null) {
                colorItemViewHolderAction.onContentClick(dbNote);
            }

        }
    }


    private ColorItemViewHolderAction colorItemViewHolderAction;

    public void setColorItemViewHolderAction(ColorItemViewHolderAction colorItemViewHolderAction) {
        this.colorItemViewHolderAction = colorItemViewHolderAction;
    }

    public interface ColorItemViewHolderAction {
        void onTitleLongClick(DBNote dbNote);
        void onContentClick(DBNote dbNote);
    }
}