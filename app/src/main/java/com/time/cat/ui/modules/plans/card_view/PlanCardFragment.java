package com.time.cat.ui.modules.plans.card_view;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.data.model.events.PersistenceEvents;
import com.time.cat.ui.base.mvp.BaseLazyLoadFragment;
import com.time.cat.ui.modules.plans.PlansFragment;
import com.time.cat.ui.widgets.CustPagerTransformer;
import com.time.cat.ui.widgets.FadeTransitionImageView;
import com.time.cat.util.override.LogUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/28
 * @discription null
 * @usage null
 */
public class PlanCardFragment extends BaseLazyLoadFragment<PlanCardMVP.View, PlanCardPresenter>
        implements PlansFragment.OnScrollBoundaryDecider, PlanCardMVP.View {
    private List<DBPlan> dataList;

    @BindView(R.id.indicator_tv)
    TextView indicatorTv;
    @BindView(R.id.plan_card_viewpager)
    ViewPager viewPager;
    @BindView(R.id.bottomImageView)
    FadeTransitionImageView bottomView;
    FragmentStatePagerAdapter fragmentStatePagerAdapter;
    private Queue<Object> pendingEvents = new LinkedList<>();

    private List<Fragment> fragments = new ArrayList<>(); // 供ViewPager使用
    private final String[] imageArray = {
            "http://img.hb.aicdn.com/3f04db36f22e2bf56d252a3bc1eacdd2a0416d75221a7c-rpihP1_fw658",
            "http://img.hb.aicdn.com/10dd7b6eb9ca02a55e915a068924058e72f7b3353a40d-ZkO3ko_fw658",
            "http://img.hb.aicdn.com/a3a995b26bd7d58ccc164eafc6ab902601984728a3101-S2H0lQ_fw658",
            "http://img.hb.aicdn.com/09302f8c939c76bb920af0aa8ea0a7ea8ae286b8a6de-1Teqka_fw658",
            "http://pic4.nipic.com/20091124/3789537_153149003980_2.jpg"
    };


    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    public int getLayout() {
        return R.layout.fragment_plan_cardview;
    }

    @Override
    public void initView() {
        initDataList();
        fillViewPager();
//        if (dataList != null && dataList.get(0) != null) {
//            bottomView.firstInit(dataList.get(0).getCoverImageUrl());
//        }
        TimeCatApp.eventBus().register(this);
        new Handler().postDelayed(()-> getPresenter().refreshData(), 500);
    }

    @Override
    public View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        while (!pendingEvents.isEmpty()) {
            LogUtil.e("Processing pending event...");
            onEvent(pendingEvents.poll());
        }
    }

    //</生命周期>------------------------------------------------------------------------------------


    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------
    /**
     * 填充ViewPager
     */
    private void fillViewPager() {

        // 1. viewPager添加parallax效果，使用PageTransformer就足够了
        viewPager.setPageTransformer(false, new CustPagerTransformer(getActivity()));

        // 2. viewPager添加adapter
        for (int i = 0; i < 10; i++) {
            // 预先准备10个fragment
            fragments.add(new CardItemFragment());
        }
        fragmentStatePagerAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == getCount()-1) {
                    return new CardLastFragment();
                }
                CardItemFragment fragment = (CardItemFragment) fragments.get(position % 10);
                fragment.bindData(dataList.get(position % dataList.size()));
                return fragment;
            }

            @Override
            public int getCount() {
                return dataList.size() + 1;
            }
        };
        viewPager.setAdapter(fragmentStatePagerAdapter);


        // 3. viewPager滑动时，调整指示器
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position < dataList.size()) updateIndicatorTv();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        updateIndicatorTv();
    }

    /**
     * 更新指示器
     */
    private void updateIndicatorTv() {
        int totalNum = fragmentStatePagerAdapter.getCount();
        int currentItem = viewPager.getCurrentItem()+1;
        indicatorTv.setText(Html.fromHtml("<font color='#12edf0'>" + currentItem + "</font>  /  " + (totalNum-1)));
        if (dataList.size() > 0) {
            bottomView.saveNextPosition(currentItem, dataList.get(currentItem - 1).getCoverImageUrl());
        }
    }
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>-----------------------------




    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-----------------------------------------
    private void initDataList() {
        dataList = DB.plans().findAll();
    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------


    //<Event事件区>---只要存在事件监听代码就是----------------------------------------------------------
    @NonNull
    @Override
    public PlanCardPresenter providePresenter() {
        return new PlanCardPresenter();
    }

    @Override
    public boolean canRefresh() {
        return false;
    }

    @Override
    public boolean canLoadMore() {
        return false;
    }

    public void onEvent(final Object evt) {
        new Handler().post(() -> {
            if (evt instanceof PersistenceEvents.ModelCreateOrUpdateEvent) {
                notifyDataChanged();
            } else if (evt instanceof PersistenceEvents.PlanCreateEvent) {
                notifyDataChanged();
            } else if (evt instanceof PersistenceEvents.PlanUpdateEvent) {
                notifyDataChanged();
            } else if (evt instanceof PersistenceEvents.PlanDeleteEvent) {
                notifyDataChanged();
            }
        });
    }

    //-//<PlanCardMVP.View>
    @Override
    public void refreshView(List<DBPlan> adapterDBPlanList) {
        dataList = adapterDBPlanList;
        fragmentStatePagerAdapter.notifyDataSetChanged();

    }
    //-//</PlanCardMVP.View>
    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------

}
