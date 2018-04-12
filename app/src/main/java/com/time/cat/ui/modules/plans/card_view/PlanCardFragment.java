package com.time.cat.ui.modules.plans.card_view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.time.cat.R;
import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.ui.base.mvp.BaseLazyLoadFragment;
import com.time.cat.ui.modules.plans.PlansFragment;
import com.time.cat.ui.widgets.FadeTransitionImageView;
import com.time.cat.ui.widgets.HorizontalTransitionLayout;
import com.time.cat.ui.widgets.PileLayout;
import com.time.cat.ui.widgets.VerticalTransitionLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/28
 * @discription null
 * @usage null
 */
public class PileFragment extends BaseLazyLoadFragment<PileMVP.View, PilePresenter> implements PlansFragment.OnScrollBoundaryDecider{
    private List<DBPlan> dataList;

    @BindView(R.id.descriptionView)
    TextView content;

    @Override
    public int getLayout() {
        return R.layout.fragment_plan_listview;
    }

    @Override
    public void initView() {
        animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                title.onAnimationEnd();
                bottomView.onAnimationEnd();
                timeView.onAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        };

        // 3. PileLayout绑定Adapter
        initDataList();
        pileLayout.setAdapter(new PileLayout.Adapter() {
            @Override
            public int getLayoutId() {
                return R.layout.item_card_layout;
            }

            @Override
            public void bindView(View view, int position) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                if (viewHolder == null) {
                    viewHolder = new ViewHolder();
                    viewHolder.imageView = view.findViewById(R.id.imageView);
                    view.setTag(viewHolder);
                }

                Glide.with(getActivity()).load(dataList.get(position).getOwner()).into(viewHolder.imageView);
            }

            @Override
            public int getItemCount() {
                return dataList.size();
            }

            @Override
            public void displaying(int position) {
                content.setText(dataList.get(position).getContent() + " Since the world is so beautiful, You have to believe me, and this index is " + position);
                if (lastDisplay < 0) {
                    initSecene(position);
                    lastDisplay = 0;
                } else if (lastDisplay != position) {
                    transitionSecene(position);
                    lastDisplay = position;
                }
            }

            @Override
            public void onItemClick(View view, int position) {
                super.onItemClick(view, position);
            }
        });
    }

    @Override
    public View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    //<生命周期>-------------------------------------------------------------------------------------

    //</生命周期>------------------------------------------------------------------------------------

    private void initSecene(int position) {
        title.firstInit(dataList.get(position).getTitle());
        bottomView.firstInit(dataList.get(position).getUrl());
        timeView.firstInit(dataList.get(position).getCreated_datetime());
    }

    private void transitionSecene(int position) {
        if (transitionAnimator != null) {
            transitionAnimator.cancel();
        }

        title.saveNextPosition(position, dataList.get(position).getTitle());
        bottomView.saveNextPosition(position, dataList.get(position).getUrl());
        timeView.saveNextPosition(position, dataList.get(position).getCreated_datetime());

        transitionAnimator = ObjectAnimator.ofFloat(this, "transitionValue", 0.0f, 1.0f);
        transitionAnimator.setDuration(300);
        transitionAnimator.start();
        transitionAnimator.addListener(animatorListener);

    }

    /**
     * 从asset读取文件json数据
     */
    private void initDataList() {
        dataList = new ArrayList<>();
        try {
            InputStream in = getContext().getAssets().open("preset.config");
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            String jsonStr = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.optJSONArray("result");
            if (null != jsonArray) {
                int len = jsonArray.length();
                for (int j = 0; j < 3; j++) {
                    for (int i = 0; i < len; i++) {
                        JSONObject itemJsonObject = jsonArray.getJSONObject(i);
                        DBPlan itemEntity = new DBPlan(itemJsonObject);
                        dataList.add(itemEntity);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 属性动画
     */
    public void setTransitionValue(float transitionValue) {
        this.transitionValue = transitionValue;
        title.duringAnimation(transitionValue);
        bottomView.duringAnimation(transitionValue);
        timeView.duringAnimation(transitionValue);
    }

    public float getTransitionValue() {
        return transitionValue;
    }

    class ViewHolder {
        ImageView imageView;
    }

    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------

    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>-----------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-----------------------------------------

    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------


    //<Event事件区>---只要存在事件监听代码就是----------------------------------------------------------

    @NonNull
    @Override
    public PilePresenter providePresenter() {
        return new PilePresenter();
    }

    @Override
    public boolean canRefresh() {
        return false;
    }

    @Override
    public boolean canLoadMore() {
        return false;
    }


    //-//<Listener>------------------------------------------------------------------------------
    //-//</Listener>-----------------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------

    //</内部类>---尽量少用---------------------------------------------------------------------------

}
