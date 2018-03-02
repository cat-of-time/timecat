package com.time.cat.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.ui.activity.DiyOcrKeyActivity;
import com.time.cat.ui.activity.about.AboutActivity;
import com.time.cat.ui.activity.about.DonateActivity;
import com.time.cat.ui.base.baseCard.AbsCard;
import com.time.cat.mvp.view.dialog.Dialog;
import com.time.cat.mvp.view.dialog.DialogFragment;
import com.time.cat.mvp.view.dialog.SimpleDialog;
import com.time.cat.util.CountLinkMovementMethod;
import com.time.cat.util.UrlCountUtil;

public class AboutCard extends AbsCard {
    private TextView about;
    private TextView share;
    private OnClickListener myOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.about:
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_ABOUT);
                    Intent intent2About = new Intent(mContext, AboutActivity.class);
                    mContext.startActivity(intent2About);
                    break;
                case R.id.share:
                    ShareCard.shareToWeChat(v, mContext);
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_SHARE);
                    break;
                case R.id.donate:
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_SETTINGS_DONATE);
                    toDonate();
                    break;
                case R.id.diy_ocr_key:
                    UrlCountUtil.onEvent(UrlCountUtil.CLICK_DIY_OCR_KEY);
                    Intent intent = new Intent(mContext, DiyOcrKeyActivity.class);
                    mContext.startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    public AboutCard(Context context) {
        super(context);
        initView(context);
    }

    public AboutCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    public AboutCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    protected void initView(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.card_about, this);
        about = findViewById(R.id.about);
        share = findViewById(R.id.share);

        about.setOnClickListener(myOnClickListener);
        share.setOnClickListener(myOnClickListener);
//        if (ChanelHandler.is360SDK(context)){
//            feedback.setVisibility(View.GONE);
//        }
        findViewById(R.id.donate).setOnClickListener(myOnClickListener);

        findViewById(R.id.diy_ocr_key).setOnClickListener(myOnClickListener);

    }

    private void toDonate() {
        Intent intent = new Intent();
        intent.setClass(mContext, DonateActivity.class);
        mContext.startActivity(intent);
    }


    private void showAboutDialog() {
        PackageManager manager = mContext.getPackageManager();
        PackageInfo info = null;
        String version = "1.3.0";
        try {
            info = manager.getPackageInfo(mContext.getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            protected void onBuildDone(Dialog dialog) {
                ((SimpleDialog) dialog).getMessageTextView().setMovementMethod(CountLinkMovementMethod.getInstance());
                super.onBuildDone(dialog);
            }
        };
        ((SimpleDialog.Builder) builder).
                message(Html.fromHtml(String.format(mContext.getString(R.string.about_content), version).replaceAll("\n", "<br />") + "<br/><a href='" + "https://github.com/l465659833/Bigbang" + "'>" + "Github: https://github.com/l465659833/Bigbang" + "</a>"

                )).title(mContext.getString(R.string.about)).positiveAction(mContext.getString(R.string.confirm));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), null);
    }

}
