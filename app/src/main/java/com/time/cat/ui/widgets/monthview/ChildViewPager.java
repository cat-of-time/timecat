package com.yumingchuan.rsqmonthcalendar.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yumingchuan.rsqmonthcalendar.utils.LogUtils;


/**
 * Created by yumingchuan on 2017/6/19.
 */

public class ChildViewPager extends ViewPager {


    private static final String TAG = "xujun";
    private float downX;

    public ChildViewPager(Context context) {
        super(context);
    }

    public ChildViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        LogUtils.i("arg0arg0", "MyViewPager dispatchTouchEvent" + super.dispatchTouchEvent(ev));

        int curPosition;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                downX = ev.getX();

                getParent().getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                curPosition = this.getCurrentItem();
                int count = this.getAdapter().getCount();
//                全部由孩子拦截触摸事件
/*                getParent().requestDisallowInterceptTouchEvent(true);*/
                // 当当前页面在最后一页和0页的时候，由父亲拦截触摸事件
                if (getVisibility() == View.GONE) {
                    getParent().getParent().requestDisallowInterceptTouchEvent(false);
                } else if (ev.getX() - downX > 0 && curPosition == 0 ) {
                    getParent().getParent().requestDisallowInterceptTouchEvent(false);
                } else if (ev.getX() - downX < 0 && curPosition == count - 1) {
                    getParent().getParent().requestDisallowInterceptTouchEvent(false);
                } else {//其他情况，由孩子拦截触摸事件
                    getParent().getParent().requestDisallowInterceptTouchEvent(true);
                }
        }
        return super.dispatchTouchEvent(ev);
    }


}
