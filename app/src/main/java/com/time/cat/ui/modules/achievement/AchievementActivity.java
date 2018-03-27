package com.time.cat.ui.modules.achievement;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.ui.adapter.AchievementAdapter;
import com.time.cat.ui.base.mvp.BaseActivity;
import com.time.cat.ui.widgets.RippleView;
import com.time.cat.util.source.AssetManager;

import butterknife.BindView;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/24
 * @discription 成就页面
 * @usage null
 */
public class AchievementActivity extends BaseActivity<AchievementMVP.View, AchievementPresenter> implements View.OnClickListener {
    @Override
    protected int layout() {
        return R.layout.activity_achiecement;
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
    public AchievementPresenter providePresenter() {
        return new AchievementPresenter();
    }

    @BindView(R.id.grid_view) GridView gridView;
    @BindView(R.id.back_btn) RippleView rippleViewBack;
    @BindView(R.id.top_bar_right_btn) RelativeLayout rippleViewRight;
    @BindView(R.id.top_bar_right_btn_text) TextView textView;
    @BindView(R.id.top_bar_text) TextView title;

    SparseArray sparseArray;
    AchievementAdapter achievementAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title.setText(R.string.my_achievement_text);
        rippleViewRight.setOnClickListener(this);
        textView.setText(R.string.share_);
        textView.setVisibility(View.VISIBLE);
        rippleViewBack.setOnClickListener(this);

        sparseArray = AssetManager.getAchievementImgAssets(this);
        achievementAdapter = new AchievementAdapter(this, sparseArray, false);
        gridView.setAdapter(achievementAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                return;
            case R.id.top_bar_right_btn:
//                d = new AchievementAdapter(this, this.a.e, true);
//                c = new s(this.a);
//                c cVar = new c(this.a);
//                c.b();
//                cVar.execute(new Void[0]);
                return;
            default:
        }
    }
}
