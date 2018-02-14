package com.time.cat.mvp.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.time.cat.R;
import com.time.cat.util.ViewUtil;

class TimeCatHeader extends ViewGroup implements View.OnClickListener {

    ImageView mSearch;
    ImageView mShare;
    ImageView mCopy;
    ImageView mTrans;
    ImageView mTask;

    Drawable mBorder;
    private int mActionGap;
    private int mContentPadding;
    private ActionListener mActionListener;
    private boolean dragMode = false;
    private boolean stickHeader = false;

    public TimeCatHeader(Context context) {
        this(context, null);
    }

    public TimeCatHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeCatHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSubViews();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimeCatHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSubViews();
    }

    private void initSubViews() {
        Context context = getContext();

        mBorder = ContextCompat.getDrawable(context, R.drawable.tiemcat_action_bar_bg);
        mBorder.setCallback(this);

        mSearch = new ImageView(context);
        mSearch.setImageResource(R.mipmap.timecat_action_search);
        mSearch.setOnClickListener(this);
        mSearch.setContentDescription(getContext().getString(R.string.search));

        mShare = new ImageView(context);
        mShare.setImageResource(R.mipmap.timecat_action_share);
        mShare.setOnClickListener(this);
        mShare.setContentDescription(getContext().getString(R.string.share_));

        mTrans = new ImageView(context);
        mTrans.setImageResource(R.mipmap.ic_compare_arrows_white_36dp);
        mTrans.setOnClickListener(this);
        mTrans.setContentDescription(getContext().getString(R.string.translate));

        mTask = new ImageView(context);
        mTask.setImageResource(R.mipmap.timecat_action_new_task);
        mTask.setOnClickListener(this);
        mTask.setContentDescription(getContext().getString(R.string.addTask));

        mCopy = new ImageView(context);
        mCopy.setImageResource(R.mipmap.timecat_action_copy);
        mCopy.setOnClickListener(this);
        mCopy.setContentDescription(getContext().getString(R.string.copy_));

        addView(mSearch, createLayoutParams());
        addView(mShare, createLayoutParams());
        addView(mTrans, createLayoutParams());
        addView(mTask, createLayoutParams());
        addView(mCopy, createLayoutParams());

        setWillNotDraw(false);

        mActionGap = ViewUtil.dp2px(5);
        mContentPadding = ViewUtil.dp2px(10);
    }

    private LayoutParams createLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        return params;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int measureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.measure(measureSpec, measureSpec);
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height + mContentPadding + mSearch.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        layoutSubView(mSearch, mActionGap, 0);
        layoutSubView(mShare, 2 * mActionGap + mSearch.getMeasuredWidth(), 0);
        layoutSubView(mTrans, 3 * mActionGap + mTrans.getMeasuredWidth() + mShare.getMeasuredWidth(), 0);
        layoutSubView(mTask, 4 * mActionGap + mTask.getMeasuredWidth() + mTrans.getMeasuredWidth() + mShare.getMeasuredWidth(), 0);
        layoutSubView(mCopy, width - mActionGap - mCopy.getMeasuredWidth(), 0);

        Rect oldBounds = mBorder.getBounds();
        Rect newBounds = new Rect(0, mSearch.getMeasuredHeight() / 2, width, height);

        if (!stickHeader && !oldBounds.equals(newBounds)) {
            ObjectAnimator.ofObject(new BoundWrapper(oldBounds), "bound", new RectEvaluator(), oldBounds, newBounds).setDuration(200).start();
        }
    }

    private void layoutSubView(View view, int l, int t) {
        view.layout(l, t, view.getMeasuredWidth() + l, view.getMeasuredHeight() + t);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!stickHeader) {
            mBorder.draw(canvas);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == mBorder;
    }

    public void setStickHeader(boolean stickHeader) {
        this.stickHeader = stickHeader;
    }

    public int getContentPadding() {
        return mContentPadding;
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public void onClick(View v) {
        if (mActionListener == null) {
            return;
        }
        if (v == mSearch) {
            mActionListener.onSearch();
        } else if (v == mShare) {
            mActionListener.onShare();
        } else if (v == mCopy) {
            mActionListener.onCopy();
        } else if (v == mTrans) {
            mActionListener.onTrans();
        } else if (v == mTask) {
            mActionListener.onAddTask();
        }
    }

    interface ActionListener {
        void onSearch();

        void onShare();

        void onCopy();

        void onTrans();

        void onAddTask();
    }

    private class BoundWrapper {
        Rect bound;

        public BoundWrapper(Rect bound) {
            this.bound = bound;
        }

        public Rect getBound() {
            return bound;
        }

        public void setBound(Rect bound) {
            this.bound = bound;
            mBorder.setBounds(bound);
            postInvalidate();
        }
    }
}
