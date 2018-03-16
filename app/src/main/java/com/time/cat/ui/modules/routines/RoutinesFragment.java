package com.time.cat.ui.modules.routines;

import android.support.v4.app.Fragment;

import com.timecat.commonjar.contentProvider.SPHelper;
import com.time.cat.R;
import com.time.cat.ui.base.BaseFragment;
import com.time.cat.ui.base.mvp.presenter.FragmentPresenter;
import com.time.cat.ui.modules.schedules_weekview.RoutinesWeekFragment;

import java.util.ArrayList;
import java.util.List;

import static com.time.cat.data.Constants.ROUTINES_VIEW_TYPE;

/**
 * @author dlink
 * @date 2018/1/25
 * @discription 生物钟fragment
 */
public class RoutinesFragment extends BaseFragment implements FragmentPresenter {
    @SuppressWarnings("unused")
    private static final String TAG = "RoutinesFragment";


    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    public int getLayoutId() {
        return R.layout.fragment_routines;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateViewPager();
    }
    //</生命周期>------------------------------------------------------------------------------------


    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------
    private List<Fragment> fragmentList;

    @Override
    public void initView() {//必须调用
        super.initView();
        fragmentList = new ArrayList<>();
        fragmentList.add(new RoutinesWeekFragment());
        updateViewPager();
    }

    private void updateViewPager() {
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
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>-----------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-----------------------------------------
    @Override
    public void initData() {//必须调用

    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------


    //<Event事件区>---只要存在事件监听代码就是----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用

    }

    @Override
    public void notifyDataChanged() {
        updateViewPager();
    }

//-//<Listener>------------------------------------------------------------------------------
    //-//</Listener>-----------------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------

    //</内部类>---尽量少用---------------------------------------------------------------------------

}
