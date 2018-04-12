package com.time.cat.ui.modules.plans;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.api.ScrollBoundaryDecider;
import com.time.cat.R;
import com.time.cat.ui.base.BaseFragment;
import com.time.cat.ui.base.mvp.presenter.FragmentPresenter;
import com.time.cat.ui.modules.main.listener.OnPlanViewClickListener;
import com.time.cat.ui.modules.plans.card_view.PlanCardFragment;
import com.time.cat.ui.modules.plans.list_view.PlanListFragment;
import com.time.cat.ui.modules.plans.pile_view.PileFragment;
import com.time.cat.util.override.LogUtil;
import com.timecat.commonjar.contentProvider.SPHelper;

import java.util.ArrayList;
import java.util.List;

import static com.time.cat.data.Constants.PLANS_VIEW_TYPE;

/**
 * @author dlink
 * @date 2018/1/25
 * @discription 生物钟fragment
 */
public class PlansFragment extends BaseFragment implements FragmentPresenter, OnPlanViewClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = "PlansFragment";


    //<生命周期>-------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentConfig(false, true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.base_refresh_layout;
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_routines, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        frameLayout = view.findViewById(R.id.fragment_container);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        initView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
    //</生命周期>------------------------------------------------------------------------------------


    //<editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码">--------------------------------
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    private List<Fragment> fragmentList;
    RefreshLayout mRefreshLayout;
    PileFragment pileFragment;
    PlanListFragment planListFragment;
    PlanCardFragment planCardFragment;

    @Override
    public void initView() {//必须调用
        super.initView();
        fragmentList = new ArrayList<>();

        mRefreshLayout.setScrollBoundaryDecider(new ScrollBoundaryDecider() {
            @Override
            public boolean canRefresh(View content) {
                return onScrollBoundaryDecider != null && onScrollBoundaryDecider.canRefresh();
            }

            @Override
            public boolean canLoadMore(View content) {
                return onScrollBoundaryDecider != null && onScrollBoundaryDecider.canLoadMore();
            }
        });
    }

    private void updateViewPager() {
        if (fragmentList == null) return;
        for (Fragment f : fragmentList) {
            getChildFragmentManager().beginTransaction().remove(f).commitNow();
        }
        fragmentList.clear();
        switch(SPHelper.getInt(PLANS_VIEW_TYPE, 0)) {
            case 0:
                planListFragment = new PlanListFragment();
                fragmentList.add(planListFragment);
                setOnScrollBoundaryDecider(planListFragment);
                planListFragment.setUserVisibleHint(true);
                getChildFragmentManager().beginTransaction().add(R.id.fragment_container, planListFragment).commitNow();
                break;
            case 1:
                planCardFragment = new PlanCardFragment();
                fragmentList.add(planCardFragment);
                setOnScrollBoundaryDecider(planCardFragment);
                planCardFragment.setUserVisibleHint(true);
                getChildFragmentManager().beginTransaction().add(R.id.fragment_container, planCardFragment).commitNow();
                break;
            case 2:
                pileFragment = new PileFragment();
                fragmentList.add(pileFragment);
                setOnScrollBoundaryDecider(pileFragment);
                pileFragment.setUserVisibleHint(true);
                getChildFragmentManager().beginTransaction().add(R.id.fragment_container, pileFragment).commitNow();
                break;
        }
    }
    //</editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码">)>-----------------------------

    //<editor-fold desc="Data数据区--存在数据获取或处理代码，但不存在事件监听代码">-----------------------------------------
    @Override
    public void initData() {//必须调用
        new Handler().postDelayed(() -> {
            if (!isPrepared()) {
                LogUtil.w("initData", "目标已被回收");
                return;
            }
            updateViewPager();
            frameLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }, 500);
    }

    public void refreshData() {

        if (frameLayout != null) {
            frameLayout.setVisibility(View.GONE);
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        if (isFragmentVisible()) {
            initData();
        } else {
            setForceLoad(true);
        }
    }
    //</editor-fold desc="Data数据区--存在数据获取或处理代码，但不存在事件监听代码">----------------------------------------

    //<editor-fold desc="Event事件区--只要存在事件监听代码就是">----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用

    }

    @Override
    public void notifyDataChanged() {
        refreshData();
    }

    @Override
    public void onViewPlanRefreshClick() {
        refreshData();
    }
    //</editor-fold desc="Event事件区--只要存在事件监听代码就是">---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------
    OnScrollBoundaryDecider onScrollBoundaryDecider;

    public void setOnScrollBoundaryDecider(OnScrollBoundaryDecider onScrollBoundaryDecider) {
        this.onScrollBoundaryDecider = onScrollBoundaryDecider;
    }

    public interface OnScrollBoundaryDecider {
        boolean canRefresh();
        boolean canLoadMore();
    }
    //</内部类>---尽量少用---------------------------------------------------------------------------

}
