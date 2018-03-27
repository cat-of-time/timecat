package com.time.cat.ui.modules.pomodoro;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.time.cat.R;
import com.time.cat.ui.base.mvp.BaseActivity;
import com.time.cat.ui.widgets.RoundProgressBar;
import com.time.cat.util.override.ToastUtil;

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

    @BindView(R.id.progressView)
    RoundProgressBar roundProgressBar;
    @BindView(R.id.pomodoro_container)
    RelativeLayout pomodoroContainer;

    boolean isWorking = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        roundProgressBar.setCountdownTime(45 * 60);
        isWorking = true;
        roundProgressBar.setOnCountListener(new RoundProgressBar.OnCountListener() {

            @Override
            public void countDownFinished() {
                ToastUtil.i("倒计时结束");
                isWorking = !isWorking;
                refresh(isWorking);
            }

            @Override
            public void counting(int remainTime) {

            }

            @Override
            public void onCountingClick() {
                new MaterialDialog.Builder(getActivity()).backgroundColor(Color.WHITE)
                        .title("放弃番茄").titleColor(Color.BLACK)
                        .content("确定要放弃此次番茄吗？").contentColor(Color.GRAY)
                        .positiveText("确定").positiveColor(Color.RED).onPositive((dialog, which) -> {
                            roundProgressBar.setForceStop(true);
                        }).negativeText("取消").onNegative((dialog, which) -> {
                            dialog.dismiss();
                        }).show();
            }

        });
        roundProgressBar.setOnClickListener(v -> {
            refresh(isWorking);
            roundProgressBar.startCountdown();
        });
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
        pomodoroContainer.setBackgroundColor(workColor);
        roundProgressBar.setMiddleLayerBgColor(workColor);
        roundProgressBar.setShadowLayerInnerColor(workColor);
        roundProgressBar.setCountdownTime(30 * 60);
    }


    private void relax() {
        int workColor = getResources().getColor(R.color.relax_background_color);
        pomodoroContainer.setBackgroundColor(workColor);
        roundProgressBar.setMiddleLayerBgColor(workColor);
        roundProgressBar.setShadowLayerInnerColor(workColor);
        roundProgressBar.setCountdownTime(10 * 60);
    }


}
