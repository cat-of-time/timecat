package com.time.cat.ui.modules.routines;

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
import com.time.cat.ui.modules.main.listener.OnRoutineViewClickListener;
import com.time.cat.ui.modules.week_view.RoutinesWeekFragment;
import com.time.cat.util.override.LogUtil;
import com.timecat.commonjar.contentProvider.SPHelper;

import java.util.ArrayList;
import java.util.List;

import static com.time.cat.data.Constants.ROUTINES_VIEW_TYPE;

/**
 * @author dlink
 * @date 2018/1/25
 * @discription 生物钟fragment
 */
public class RoutinesFragment extends BaseFragment implements FragmentPresenter, OnRoutineViewClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = "RoutinesFragment";


    //<生命周期>-------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentConfig(false, true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_routines;
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
    RoutinesWeekFragment routinesWeekFragment;
    RoutinesListFragment routinesListFragment;

    @Override
    public void initView() {//必须调用
        super.initView();
        fragmentList = new ArrayList<>();
//        routinesWeekFragment = new RoutinesWeekFragment();//week_top_holder
//        setOnScrollBoundaryDecider(routinesWeekFragment);
//        fragmentList.add(routinesWeekFragment);

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
        if (SPHelper.getInt(ROUTINES_VIEW_TYPE, 0) == 0) {
            routinesWeekFragment = new RoutinesWeekFragment();//week_top_holder
            fragmentList.add(routinesWeekFragment);
            setOnScrollBoundaryDecider(routinesWeekFragment);
            routinesWeekFragment.setUserVisibleHint(true);
            getChildFragmentManager().beginTransaction().add(R.id.fragment_container, routinesWeekFragment).commitNow();
        } else {
            routinesListFragment = new RoutinesListFragment();
            fragmentList.add(routinesListFragment);
            setOnScrollBoundaryDecider(routinesListFragment);
            routinesListFragment.setUserVisibleHint(true);
            getChildFragmentManager().beginTransaction().add(R.id.fragment_container, routinesListFragment).commitNow();
        }
    }
    //</editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码">)>-----------------------------


    //<editor-fold desc="Data数据区--存在数据获取或处理代码，但不存在事件监听代码">-----------------------------------------
    @Override
    public void initData() {//必须调用

        if (!isPrepared()) {
            LogUtil.w("initData", "目标已被回收");
            return;
        }
        updateViewPager();

        frameLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
        }, 2000);
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
    public void onViewRefreshClick() {
        refreshData();
    }

    //-//<Listener>------------------------------------------------------------------------------
    //-//</Listener>-----------------------------------------------------------------------------

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
