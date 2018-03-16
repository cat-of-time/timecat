package com.time.cat.ui.widgets.navigation;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.time.cat.R;

import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;

/**
 * 自定义一个只有图标的Item
 */
public class OnlyIconItemView extends BaseTabItem {

    private ImageView mIcon;

    private int mDefaultDrawable;

    public OnlyIconItemView(Context context) {
        this(context,null);
    }

    public OnlyIconItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public OnlyIconItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.item_only_icon, this, true);

        mIcon = (ImageView) findViewById(R.id.icon);
    }

    public void initialize(@DrawableRes int drawableRes) {
        mDefaultDrawable = drawableRes;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        mIcon.setOnLongClickListener(onLongClickListener);
    }

    @Override
    public void setChecked(boolean checked) {
        mIcon.setImageResource(mDefaultDrawable);
    }

    @Override
    public void setMessageNumber(int number) {
        //不需要就不用管
    }

    @Override
    public void setHasMessage(boolean hasMessage) {
        //不需要就不用管
    }

    @Override
    public String getTitle() {
        return "no title";
    }
}