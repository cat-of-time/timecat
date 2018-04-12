package com.time.cat.ui.modules.pomodoro;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.ui.base.mvp.BaseActivity;
import com.time.cat.ui.modules.main.MainActivity;
import com.time.cat.ui.service.TickService;
import com.time.cat.ui.widgets.pomodoro.RippleWrapper;
import com.time.cat.ui.widgets.pomodoro.TickProgressBar;
import com.time.cat.util.TimeFormatUtil;

import butterknife.BindView;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/24
 * @discription null
 * @usage null
 */
public class PomodoroActivity extends BaseActivity<PomodoroMVP.View, PomodoroPresenter> {
    @Override
    protected int layout() {
        return R.layout.activity_pomodoro;
    }

    @Override
    protected boolean canBack() {
        return true;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @NonNull
    @Override
    public PomodoroPresenter providePresenter() {
        return new PomodoroPresenter();
    }

    private TimeCatApp mApplication;

    @BindView(R.id.btn_start)
    Button mBtnStart;
    @BindView(R.id.btn_pause)
    Button mBtnPause;
    @BindView(R.id.btn_resume)
    Button mBtnResume;
    @BindView(R.id.btn_stop)
    Button mBtnStop;
    @BindView(R.id.btn_skip)
    Button mBtnSkip;
    @BindView(R.id.text_count_down)
    TextView mTextCountDown;
    @BindView(R.id.text_time_title)
    TextView mTextTimeTile;
    @BindView(R.id.tick_progress_bar)
    TickProgressBar mProgressBar;
    @BindView(R.id.ripple_wrapper)
    RippleWrapper mRippleWrapper;

    private long mLastClickTime = 0;
    boolean isWorking = false;

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(findViewById(R.id.toolbar));

        mApplication = TimeCatApp.getInstance();
        initActions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        reload();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TickService.ACTION_COUNTDOWN_TIMER);
        registerReceiver(mIntentReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mIntentReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    
    public void refresh(boolean isWorking) {
        if (isWorking) {
            work();
        } else {
            relax();
        }
    }

    private void work() {
        int workColor = getResources().getColor(R.color.work_background_color);
    }
    
    private void relax() {
        int workColor = getResources().getColor(R.color.relax_background_color);
    }

    private void initActions() {
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = TickService.newIntent(getApplicationContext());
                i.setAction(TickService.ACTION_START);
                startService(i);

                mApplication.start();
                updateButtons();
                updateTitle();
                updateRipple();
            }
        });

        mBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = TickService.newIntent(getApplicationContext());
                i.setAction(TickService.ACTION_PAUSE);
                i.putExtra("time_left", (String) mTextCountDown.getText());
                startService(i);

                mApplication.pause();
                updateButtons();
                updateRipple();
            }
        });

        mBtnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = TickService.newIntent(getApplicationContext());
                i.setAction(TickService.ACTION_RESUME);
                startService(i);

                mApplication.resume();
                updateButtons();
                updateRipple();
            }
        });

        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = TickService.newIntent(getApplicationContext());
                i.setAction(TickService.ACTION_STOP);
                startService(i);

                mApplication.stop();
                reload();
            }
        });

        mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = TickService.newIntent(getApplicationContext());
                i.setAction(TickService.ACTION_STOP);
                startService(i);

                mApplication.skip();
                reload();
            }
        });

        mRippleWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - mLastClickTime < 500) {
                    boolean isSoundOn = getSharedPreferences()
                            .getBoolean("pref_key_tick_sound", true);

                    // 修改 SharedPreferences
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext()).edit();

                    if (isSoundOn) {
                        editor.putBoolean("pref_key_tick_sound", false);

                        Intent i = TickService.newIntent(getApplicationContext());
                        i.setAction(TickService.ACTION_TICK_SOUND_OFF);
                        startService(i);

                        Snackbar.make(view, getResources().getString(R.string.toast_tick_sound_off),
                                Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    } else {
                        editor.putBoolean("pref_key_tick_sound", true);

                        Intent i = TickService.newIntent(getApplicationContext());
                        i.setAction(TickService.ACTION_TICK_SOUND_ON);
                        startService(i);

                        Snackbar.make(view, getResources().getString(R.string.toast_tick_sound_on),
                                Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                    try {
                        editor.apply();
                    } catch (AbstractMethodError unused) {
                        editor.commit();
                    }

                    updateRipple();
                }

                mLastClickTime = clickTime;
            }
        });
    }
    
    private void reload() {
        mApplication.reload();

        mProgressBar.setMaxProgress(mApplication.getMillisInTotal() / 1000);
        mProgressBar.setProgress(mApplication.getMillisUntilFinished() / 1000);

        updateText(mApplication.getMillisUntilFinished());
        updateTitle();
        updateButtons();
        updateScene();
        updateRipple();
        updateAmount();

        if (getSharedPreferences().getBoolean("pref_key_screen_on", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void updateText(long millisUntilFinished) {
        mTextCountDown.setText(TimeFormatUtil.formatTime(millisUntilFinished));
    }

    private void updateTitle() {
        if (mApplication.getState() == TimeCatApp.STATE_FINISH) {
            String title;

            if (mApplication.getScene() == TimeCatApp.SCENE_WORK) {
                title = getResources().getString(R.string.scene_title_work);
            } else {
                title = getResources().getString(R.string.scene_title_break);
            }

            mTextTimeTile.setText(title);
            mTextTimeTile.setVisibility(View.VISIBLE);
            mTextCountDown.setVisibility(View.GONE);
        } else {
            mTextTimeTile.setVisibility(View.GONE);
            mTextCountDown.setVisibility(View.VISIBLE);
        }
    }

    private void updateButtons() {
        int state = mApplication.getState();
        int scene = mApplication.getScene();
        boolean isPomodoroMode = getSharedPreferences()
                .getBoolean("pref_key_pomodoro_mode", true);

        // 在番茄模式下不能暂停定时器
        mBtnStart.setVisibility(
                state == TimeCatApp.STATE_WAIT || state == TimeCatApp.STATE_FINISH ?
                        View.VISIBLE : View.GONE);

        if (isPomodoroMode) {
            mBtnPause.setVisibility(View.GONE);
            mBtnResume.setVisibility(View.GONE);
        } else {
            mBtnPause.setVisibility(state == TimeCatApp.STATE_RUNNING ?
                    View.VISIBLE : View.GONE);
            mBtnResume.setVisibility(state == TimeCatApp.STATE_PAUSE ?
                    View.VISIBLE : View.GONE);
        }

        if (scene == TimeCatApp.SCENE_WORK) {
            mBtnSkip.setVisibility(View.GONE);
            if (isPomodoroMode) {
                mBtnStop.setVisibility(!(state == TimeCatApp.STATE_WAIT ||
                        state == TimeCatApp.STATE_FINISH) ?
                        View.VISIBLE : View.GONE);
            } else {
                mBtnStop.setVisibility(state == TimeCatApp.STATE_PAUSE ?
                        View.VISIBLE : View.GONE);
            }

        } else {
            mBtnStop.setVisibility(View.GONE);
            if (isPomodoroMode) {
                mBtnSkip.setVisibility(!(state == TimeCatApp.STATE_WAIT ||
                        state == TimeCatApp.STATE_FINISH) ?
                        View.VISIBLE : View.GONE);
            } else {
                mBtnSkip.setVisibility(state == TimeCatApp.STATE_PAUSE ?
                        View.VISIBLE : View.GONE);
            }

        }
    }

    public void updateScene() {
        int scene = mApplication.getScene();

        int workLength = getSharedPreferences()
                .getInt("pref_key_work_length", TimeCatApp.DEFAULT_WORK_LENGTH);
        int shortBreak = getSharedPreferences()
                .getInt("pref_key_short_break", TimeCatApp.DEFAULT_SHORT_BREAK);
        int longBreak = getSharedPreferences()
                .getInt("pref_key_long_break", TimeCatApp.DEFAULT_LONG_BREAK);

        ((TextView)findViewById(R.id.stage_work_value))
                .setText(getResources().getString(R.string.stage_time_unit, workLength));
        ((TextView)findViewById(R.id.stage_short_break_value))
                .setText(getResources().getString(R.string.stage_time_unit, shortBreak));
        ((TextView)findViewById(R.id.stage_long_break_value))
                .setText(getResources().getString(R.string.stage_time_unit, longBreak));

        findViewById(R.id.stage_work).setAlpha(scene == TimeCatApp.SCENE_WORK ? 0.9f : 0.5f);
        findViewById(R.id.stage_short_break).setAlpha(scene == TimeCatApp.SCENE_SHORT_BREAK ? 0.9f : 0.5f);
        findViewById(R.id.stage_long_break).setAlpha(scene == TimeCatApp.SCENE_LONG_BREAK ? 0.9f : 0.5f);
    }

    private void updateRipple() {
        boolean isPlayOn = getSharedPreferences().getBoolean("pref_key_tick_sound", true);

        if (isPlayOn) {
            if (mApplication.getState() == TimeCatApp.STATE_RUNNING) {
                mRippleWrapper.start();
                return;
            }
        }

        mRippleWrapper.stop();
    }

    private void updateAmount() {
        long amount = getSharedPreferences().getLong("pref_key_amount_durations", 0);
        TextView textView = (TextView)findViewById(R.id.amount_durations);
        textView.setText(getResources().getString(R.string.amount_durations, amount));
    }

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TickService.ACTION_COUNTDOWN_TIMER)) {
                String requestAction = intent.getStringExtra(TickService.REQUEST_ACTION);

                switch (requestAction) {
                    case TickService.ACTION_TICK:
                        long millisUntilFinished = intent.getLongExtra(
                                TickService.MILLIS_UNTIL_FINISHED, 0);
                        mProgressBar.setProgress(millisUntilFinished / 1000);
                        updateText(millisUntilFinished);
                        break;
                    case TickService.ACTION_FINISH:
                    case TickService.ACTION_AUTO_START:
                        reload();
                        break;
                }
            }
        }
    };

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void exitApp() {
        stopService(TickService.newIntent(getApplicationContext()));
        mApplication.exit();
        finish();
    }
}
