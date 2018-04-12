package com.time.cat.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.time.cat.R;
import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.ui.adapter.viewholder.ColorItemViewHolder;
import com.time.cat.ui.widgets.card_stack_view.CardStackView;
import com.time.cat.ui.widgets.card_stack_view.StackAdapter;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/28
 * @discription null
 * @usage null
 */
public class CardStackViewAdapter extends StackAdapter<DBNote> {

    public CardStackViewAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(DBNote data, int position, CardStackView.ViewHolder holder) {
        if (holder instanceof ColorItemViewHolder) {
            ColorItemViewHolder h = (ColorItemViewHolder) holder;
            h.onBind(data, getItemCount() - position);
        }
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        if (cardStackViewAdapterAction != null) {
            return cardStackViewAdapterAction.onCreateView(parent, viewType);
        } else {
            View view = getLayoutInflater().inflate(R.layout.item_list_card, parent, false);
            return new ColorItemViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_list_card;
    }

    private CardStackViewAdapterAction cardStackViewAdapterAction;

    public void setCardStackViewAdapterAction(CardStackViewAdapterAction cardStackViewAdapterAction) {
        this.cardStackViewAdapterAction = cardStackViewAdapterAction;
    }

    public interface CardStackViewAdapterAction {
        CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType);
    }
}