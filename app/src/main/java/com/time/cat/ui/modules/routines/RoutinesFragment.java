package com.time.cat.ui.modules.routines;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.time.cat.R;
import com.time.cat.ui.activity.main.listener.OnRoutineViewClickListener;
import com.time.cat.ui.base.BaseFragment;
import com.time.cat.ui.base.mvp.presenter.FragmentPresenter;
import com.time.cat.ui.modules.schedules_weekview.RoutinesWeekFragment;
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

    @Override
    public void initView() {//必须调用
        super.initView();
        fragmentList = new ArrayList<>();
    }

    private void updateViewPager() {
        if (fragmentList == null) return;
        Fragment fragment = getFragments();
        for (Fragment fragment1:fragmentList) {
            getChildFragmentManager().beginTransaction().remove(fragment1).commitNow();
        }
        fragmentList.clear();
        fragmentList.add(fragment);
        getChildFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commitNow();
    }


    private Fragment getFragments() {
        if (SPHelper.getInt(ROUTINES_VIEW_TYPE, 0) == 0) {
            return new RoutinesWeekFragment();
        } else {
            return new RoutinesListFragment();
        }
    }
    //</editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码">)>-----------------------------


    //<editor-fold desc="Data数据区--存在数据获取或处理代码，但不存在事件监听代码">-----------------------------------------
    @Override
    public void initData() {//必须调用

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
        if (!isPrepared()) {
            Log.w("initData", "目标已被回收");
            return;
        }
        updateViewPager();

        frameLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
//            }
//        }, 500);
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

    //</内部类>---尽量少用---------------------------------------------------------------------------

}
