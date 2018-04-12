package com.time.cat.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/28
 * @discription null
 * @usage null
 */
public abstract class BaseTransitionLayout extends FrameLayout {

    public BaseTransitionLayout(Context context) {
        this(context, null);
    }

    public BaseTransitionLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseTransitionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addViewWhenFinishInflate();
    }

    public abstract void addViewWhenFinishInflate();

    public abstract void firstInit(String info);

    public void onAnimationEnd(){
        super.onAnimationEnd();
    }

    public abstract void duringAnimation(float rate);

    public abstract void saveNextPosition(int position, String info);
}
