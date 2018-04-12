package com.time.cat.ui.modules.setting.card;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.time.cat.R;
import com.time.cat.ui.base.baseCard.AbsCard;

public class PomodoroCard extends AbsCard {

    public PomodoroCard(Context context) {
        super(context);
        initView(context);
    }

    public PomodoroCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PomodoroCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    protected void initView(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.card_about, this);

    }

}
