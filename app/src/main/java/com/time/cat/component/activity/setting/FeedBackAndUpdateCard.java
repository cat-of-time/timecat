package com.time.cat.component.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.component.activity.FeedbackActivity;
import com.time.cat.component.activity.howtouse.HowToUseActivity;
import com.time.cat.component.base.baseCard.AbsCard;
import com.time.cat.util.NetWorkUtil;
import com.time.cat.util.SnackBarUtil;
import com.time.cat.util.UrlCountUtil;

/**
 * Created by penglu on 2015/11/23.
 */
public class FeedBackAndUpdateCard extends AbsCard {
    private TextView feedback;
    private TextView checkUpdate;
    private TextView introduction;
    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.check_update:
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_CHECK_FOR_UPDATE);
                    if (!NetWorkUtil.isConnected(mContext)) {
                        SnackBarUtil.show(v, R.string.snackbar_net_error);
                        return;
                    }
                    SnackBarUtil.show(v, R.string.check_update_close);

                    break;
                case R.id.feedback:
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_FEEDBACK);
                    startFeedBack();
                    break;
                case R.id.introduction:
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_HOW_TO_USE);
                    showIntro();
                    break;
                default:
                    break;
            }
        }
    };

    public FeedBackAndUpdateCard(Context context) {
        super(context);
        initView(context);
    }

    public FeedBackAndUpdateCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    public FeedBackAndUpdateCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    protected void initView(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.card_feedback_update, this);
        checkUpdate = findViewById(R.id.check_update);
        feedback = findViewById(R.id.feedback);
        introduction = findViewById(R.id.introduction);

        checkUpdate.setOnClickListener(myOnClickListener);
        feedback.setOnClickListener(myOnClickListener);
        introduction.setOnClickListener(myOnClickListener);
//        if (ChanelHandler.is360SDK(context)){
//            feedback.setVisibility(View.GONE);
//        }
    }

    private void showIntro() {
        Intent intent = new Intent();
        intent.setClass(mContext, HowToUseActivity.class);
        mContext.startActivity(intent);
    }

    protected void startFeedBack() {
        Intent intent = new Intent();
        intent.setClass(mContext, FeedbackActivity.class);
        mContext.startActivity(intent);
    }

}
