package com.time.cat.ui.modules.notes;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.api.ScrollBoundaryDecider;
import com.time.cat.R;
import com.time.cat.ui.base.BaseFragment;
import com.time.cat.ui.base.mvp.presenter.FragmentPresenter;
import com.time.cat.ui.modules.main.listener.OnNoteViewClickListener;
import com.time.cat.ui.modules.notes.list_view.NoteListFragment;
import com.time.cat.ui.modules.notes.markdown_view.FileListFragment;
import com.time.cat.ui.modules.notes.timeline_view.TimeLineFragment;
import com.time.cat.util.override.LogUtil;
import com.timecat.commonjar.contentProvider.SPHelper;

import java.util.ArrayList;
import java.util.List;

import static com.time.cat.data.Constants.NOTES_VIEW_TYPE;

/**
 * @author dlink
 * @date 2018/1/25
 * @discription 笔记fragment，只与view有关，业务逻辑下放给presenter
 */
public class NotesFragment extends BaseFragment implements FragmentPresenter, OnNoteViewClickListener {

    //<editor-fold desc="生命周期">
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
    //</editor-fold desc="生命周期">



    //<editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码">
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    private List<Fragment> fragmentList;
    RefreshLayout mRefreshLayout;
    NoteListFragment noteListFragment;
    TimeLineFragment timeLineFragment;
    FileListFragment fileListFragment;


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
        switch(SPHelper.getInt(NOTES_VIEW_TYPE, 0)) {
            case 0:
                noteListFragment = new NoteListFragment();
                fragmentList.add(noteListFragment);
                setOnScrollBoundaryDecider(noteListFragment);
                noteListFragment.setUserVisibleHint(true);
                getChildFragmentManager().beginTransaction().add(R.id.fragment_container, noteListFragment).commitNow();
                if (onFragmentChanged != null) {
                    onFragmentChanged.adjustMenu(0);
                }
                break;
            case 1:
                timeLineFragment = new TimeLineFragment();
                fragmentList.add(timeLineFragment);
                setOnScrollBoundaryDecider(timeLineFragment);
                timeLineFragment.setUserVisibleHint(true);
                getChildFragmentManager().beginTransaction().add(R.id.fragment_container, timeLineFragment).commitNow();
                if (onFragmentChanged != null) {
                    onFragmentChanged.adjustMenu(1);
                }
                break;
            case 2:
                fileListFragment = new FileListFragment();
                fragmentList.add(fileListFragment);
                setOnScrollBoundaryDecider(fileListFragment);
                fileListFragment.setUserVisibleHint(true);
                getChildFragmentManager().beginTransaction().add(R.id.fragment_container, fileListFragment).commitNow();
                if (onFragmentChanged != null) {
                    onFragmentChanged.adjustMenu(2);
                }
                break;
        }
    }
    //</editor-fold desc="UI显示区--操作UI，但不存在数据获取或处理代码，也不存在事件监听代码">)>

    //<editor-fold desc="Data数据区--存在数据获取或处理代码，但不存在事件监听代码">
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
    //</editor-fold desc="Data数据区--存在数据获取或处理代码，但不存在事件监听代码">

    //<editor-fold desc="Event事件区--只要存在事件监听代码就是">
    @Override
    public void initEvent() {//必须调用

    }

    @Override
    public void notifyDataChanged() {
        refreshData();
    }

    //-//<Listener>------------------------------------------------------------------------------
    //-//</Listener>-----------------------------------------------------------------------------

    //-//<用户强制刷新>
    @Override
    public void onViewNoteRefreshClick() {
        refreshData();
    }

    @Override
    public void onViewSortClick() {
        if (fileListFragment != null) fileListFragment.onViewSortClick();
    }

    @Override
    public void initSearchView(Menu menu, AppCompatActivity activity) {
        if (fileListFragment != null) fileListFragment.initSearchView(menu, activity);
    }
    //-//</用户强制刷新>

    //</editor-fold desc="Event事件区--只要存在事件监听代码就是">



    //<内部类>---尽量少用----------------------------------------------------------------------------
    OnScrollBoundaryDecider onScrollBoundaryDecider;

    public void setOnScrollBoundaryDecider(OnScrollBoundaryDecider onScrollBoundaryDecider) {
        this.onScrollBoundaryDecider = onScrollBoundaryDecider;
    }

    public interface OnScrollBoundaryDecider {
        boolean canRefresh();
        boolean canLoadMore();
    }

    OnFragmentChanged onFragmentChanged;

    public void setOnFragmentChanged(OnFragmentChanged onFragmentChanged) {
        this.onFragmentChanged = onFragmentChanged;
    }

    public interface OnFragmentChanged {
        /**
         * 给MainActivity实现
         * @param currentFragmentLabel 0->list view; 1->card view; 2->timeline view
         */
        void adjustMenu(int currentFragmentLabel);
    }
    //</内部类>---尽量少用---------------------------------------------------------------------------

}