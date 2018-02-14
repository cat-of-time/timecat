package com.time.cat.component.activity;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shang.commonjar.contentProvider.SPHelper;
import com.time.cat.R;
import com.time.cat.component.activity.setting.SettingActivity;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.mvp.view.GuideView;
import com.time.cat.mvp.view.TimeCatLayoutWrapper;
import com.time.cat.util.ClipboardUtils;
import com.time.cat.util.SnackBarUtil;
import com.time.cat.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.time.cat.component.activity.SplashActivity.KEY;

public class IntroActivity extends BaseActivity {
    private static final String INTRODUCED = "introduced";
    public int clickTimes = 0;
    private TextView mIntro, mJumpBtn;
    private TextView mFunctionIntroTV;
    private GuideView guideView;
    private TimeCatLayoutWrapper mTimeCatLayout;
    private CardView mTimeCatWraper;
    private Button mEnterBtn;
    private Handler handler;
    private String[] txts_cloud;
    private String[] txts_local;
    TimeCatLayoutWrapper.ActionListener timeCatActionListener = new TimeCatLayoutWrapper.ActionListener() {

        private boolean firstSelected = true, firstSearch = true, firstShare = true, firstCopy = true, firstTrans = true, firstDrag = true, firstAddTask = true;

        @Override
        public void onSelected(String text) {
            if (firstSelected) {
                guideView.performClick();
                firstSelected = false;
            }
        }

        @Override
        public void onSearch(String text) {
            if (firstSearch) {
                mFunctionIntroTV.setScaleY(0);
                mFunctionIntroTV.setScaleX(0);
                mFunctionIntroTV.setText(R.string.search_mode_help);
                mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
            } else {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com/s?wd=" + URLEncoder.encode(text, "utf-8")));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            clickTimes++;
            showEnterBtn();


        }

        @Override
        public void onShare(String text) {
            if (firstShare) {
                mFunctionIntroTV.setScaleY(0);
                mFunctionIntroTV.setScaleX(0);
                mFunctionIntroTV.setText(R.string.share_mode_help);
                mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
            } else {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(sharingIntent);

            }
            clickTimes++;

            showEnterBtn();
        }

        @Override
        public void onCopy(String text) {
            if (firstCopy) {
                mFunctionIntroTV.setScaleY(0);
                mFunctionIntroTV.setScaleX(0);
                mFunctionIntroTV.setText(R.string.copy_mode_help);
                mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
            } else {
                if (!TextUtils.isEmpty(text)) {
                    ClipboardUtils.setText(getApplicationContext(), text);
                    ToastUtil.show(R.string.copyed);
                }
            }

            clickTimes++;
            showEnterBtn();

        }

        @Override
        public void onTrans(String text) {
            if (firstTrans) {
                mFunctionIntroTV.setScaleY(0);
                mFunctionIntroTV.setScaleX(0);
                mFunctionIntroTV.setText(R.string.translate_mode_help);
                mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
            } else {
                if (!TextUtils.isEmpty(text)) {
                    SnackBarUtil.show(mIntro, R.string.open_timecat_for_translate);
                }
            }
            clickTimes++;
            showEnterBtn();
        }

        @Override
        public void onAddTask(String text) {
            if (firstAddTask) {
                mFunctionIntroTV.setScaleY(0);
                mFunctionIntroTV.setScaleX(0);
                mFunctionIntroTV.setText(R.string.add_task_mode_help);
                mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
            } else {
                if (!TextUtils.isEmpty(text)) {
                    SnackBarUtil.show(mIntro, getString(R.string.open_timecat_for_task));
                }
            }
            clickTimes++;
            showEnterBtn();
        }

        @Override
        public void onDrag() {
            if (firstDrag) {
                mFunctionIntroTV.setText(R.string.sort_mode_help);
            } else {
                mFunctionIntroTV.setText(R.string.choose_sentences_mode);
            }
            firstDrag = !firstDrag;
            mFunctionIntroTV.setScaleY(0);
            mFunctionIntroTV.setScaleX(0);
            mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
            clickTimes++;
            showEnterBtn();
        }

        @Override
        public void onSwitchType(boolean isLocal) {
            mTimeCatLayout.reset();
            if (isLocal) {
                for (String text : txts_local) {
                    mTimeCatLayout.addTextItem(text);
                }
                mFunctionIntroTV.setText(R.string.word_type_local);
            } else {
                for (String text : txts_cloud) {
                    mTimeCatLayout.addTextItem(text);
                }
                mFunctionIntroTV.setText(R.string.word_type_cloud);
            }
            firstDrag = !firstDrag;
            mFunctionIntroTV.setScaleY(0);
            mFunctionIntroTV.setScaleX(0);
            mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
            clickTimes++;
            showEnterBtn();
        }

        @Override
        public void onSwitchSymbol(boolean isShow) {
            mFunctionIntroTV.setText(R.string.show_symbol);
            mFunctionIntroTV.setScaleY(0);
            mFunctionIntroTV.setScaleX(0);
            mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
            clickTimes++;
            showEnterBtn();
        }

        @Override
        public void onSwitchSection(boolean isShow) {
            mFunctionIntroTV.setText(R.string.show_section);
            mFunctionIntroTV.setScaleY(0);
            mFunctionIntroTV.setScaleX(0);
            mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
            clickTimes++;
            showEnterBtn();
        }

        @Override
        public void onDragSelection() {
            mFunctionIntroTV.setText(R.string.show_drag_selection);
            mFunctionIntroTV.setScaleY(0);
            mFunctionIntroTV.setScaleX(0);
            mFunctionIntroTV.animate().scaleY(1).scaleX(1).start();
            clickTimes++;
            showEnterBtn();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        handler = new Handler();
        initView();
        showClickIntro();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showClickIntro() {

        TextView tv = new TextView(this);
        tv.setText(R.string.try_long_click_text);
        tv.setTextColor(getResources().getColor(R.color.white));
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.hand_down);
        Animation animation = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.click_here_anim);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageView.startAnimation(animation);
                    }
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        guideView = new GuideView.Builder(this).setTargetView(mIntro)//设置目标
                .setCustomGuideView(tv).setCenterView(imageView).setDirction(GuideView.Direction.BOTTOM).setShape(GuideView.MyShape.CIRCULAR)   // 设置圆形显示区域，
                .setOffset(0, mIntro.getMeasuredHeight() + 100).setBgColor(getResources().getColor(R.color.shadow)).setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        animation.cancel();
                        guideView.hide();
                        mIntro.setVisibility(View.GONE);
                        mTimeCatWraper.setVisibility(View.VISIBLE);
                        mTimeCatWraper.setScaleX(0);
                        mTimeCatWraper.setScaleY(0);
                        mTimeCatWraper.animate().scaleY(1).scaleX(1).setInterpolator(new AnticipateOvershootInterpolator()).setDuration(200).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showTimeCatIntro();
                                    }
                                }, 300);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();

                    }
                }).setOnViewAddedListener(view -> {
                    view.setAnimation(animation);
                    animation.start();
                }).build();
        guideView.setClickable(false);
        guideView.setLongClickable(false);
        guideView.setFocusable(false);
        guideView.show();
    }

    private void initView() {
        mIntro = findViewById(R.id.intro);
        mFunctionIntroTV = findViewById(R.id.enter_timecat_intro);
        mTimeCatLayout = findViewById(R.id.timecat_wrap);
        mTimeCatLayout.setActionListener(timeCatActionListener);

        mTimeCatWraper = findViewById(R.id.timecat_wraper);
        txts_cloud = new String[]{"TimeCat", "是", "您", "的", "快捷", "助手", "。", "\n", "您", "可以", "在", "任意", "app", "中", "对", "文字", "进行", "编辑", "，", "包括", "分词", "，", "翻译", "，", "复制", "以及", "动态", "调整", "。", "\n", "希望", "您", "能", "在", "日常", "生活", "中", "获得", "便利"};
        txts_local = new String[]{"TimeCat", "是", "您", "的", "快", "捷", "助", "手", "。", "\n", "您", "可", "以", "在", "任", "意", "app", "中", "对", "文", "字", "进", "行", "编", "辑", "，", "包", "括", "分", "词", "，", "翻", "译", "，", "复", "制", "以", "及", "动", "态", "调", "整", "。", "\n", "希", "望", "您", "能", "在", "日", "常", "生", "活", "中", "获", "得", "便", "利"};
        for (String t : txts_cloud) {
            mTimeCatLayout.addTextItem(t);
        }

        mIntro.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                guideView.performClick();
                return true;
            }
        });
        mEnterBtn = findViewById(R.id.enter_timecat);
        mEnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IntroActivity.this, PreSettingActivity.class);
                startActivity(intent);
                SPHelper.save(KEY, true);
                SPHelper.save(INTRODUCED, true);
                finish();
            }
        });
        mJumpBtn = findViewById(R.id.jump);
        mJumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(R.string.jump_toast);
                Intent intent = new Intent();
                intent.setClass(IntroActivity.this, SettingActivity.class);
                startActivity(intent);
                SPHelper.save(KEY, true);
                SPHelper.save(INTRODUCED, true);
                finish();
            }
        });

        if (SPHelper.getBoolean(INTRODUCED, false)) {
            mJumpBtn.setVisibility(View.VISIBLE);
        } else {
            mJumpBtn.setVisibility(View.GONE);
        }
    }

    private void showTimeCatIntro() {
        TextView tv = new TextView(this);
        tv.setText(R.string.try_click_text);
        tv.setTextColor(getResources().getColor(R.color.white));

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.hand_swipe);
        Animation animation = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.swipe_here_anim);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageView.startAnimation(animation);
                    }
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        guideView = new GuideView.Builder(this).setTargetView(mTimeCatWraper)//设置目标
                .setCustomGuideView(tv).setCenterView(imageView).setDirction(GuideView.Direction.BOTTOM).setShape(GuideView.MyShape.RECTANGULAR)   // 设置圆形显示区域，
                .setRadius(5).setOffset(0, mTimeCatWraper.getMeasuredHeight() / 2 + 100).setBgColor(getResources().getColor(R.color.shadow)).setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        animation.cancel();
                        guideView.hide();
                        showEnterBtn();
                        mFunctionIntroTV.setVisibility(View.VISIBLE);
                    }
                }).setOnViewAddedListener(view -> {
                    view.setAnimation(animation);
                    animation.start();
                }).build();
        guideView.setClickable(false);
        guideView.setLongClickable(false);
        guideView.setFocusable(false);
        guideView.show();
    }

    private void showEnterBtn() {
        if (clickTimes >= 5 || SPHelper.getBoolean(KEY, false) || SPHelper.getBoolean(INTRODUCED, false)) {
            if (mEnterBtn.getVisibility() != View.VISIBLE) {
                mEnterBtn.setVisibility(View.VISIBLE);
                mEnterBtn.setScaleY(0);
                mEnterBtn.setScaleX(0);
                mEnterBtn.setAlpha(0);
                mEnterBtn.animate().scaleX(1).scaleY(1).alpha(1).setInterpolator(new AnticipateOvershootInterpolator()).setStartDelay(500).start();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (guideView != null && guideView.isShown()) {
            guideView.hide();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (guideView != null) guideView.hide();
        guideView = null;
    }
}
