package com.time.cat.ui.modules.routines.list_view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.data.database.DB;
import com.time.cat.data.model.DBmodel.DBRoutine;
import com.time.cat.ui.adapter.RoutineListAdapter;
import com.time.cat.ui.base.mvp.BaseLazyLoadFragment;
import com.time.cat.ui.modules.routines.RoutinesFragment;

import java.util.List;

import butterknife.BindView;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/28
 * @discription 列表视图
 * @usage null
 */
public class RoutinesListFragment extends BaseLazyLoadFragment<RoutinesListMVP.View, RoutinesListPresenter> implements RoutinesFragment.OnScrollBoundaryDecider{
    private List<DBRoutine> dataList;
    @BindView(R.id.routine_rv)
    RecyclerView recyclerView;

    @BindView(R.id.empty_view)
    FrameLayout empty_view;
    @BindView(R.id.empty_icon)
    ImageView empty_icon;
    @BindView(R.id.empty_head)
    TextView empty_head;
    @BindView(R.id.empty_attention)
    TextView empty_attention;
    @BindView(R.id.empty_long_press_routine)
    LinearLayout empty_long_press_routine;
    @BindView(R.id.empty_long_press_note)
    LinearLayout empty_long_press_note;

    @Override
    public int getLayout() {
        return R.layout.fragment_routines_list;
    }

    @Override
    public void initView() {
        dataList = DB.routines().findAll();
        empty_icon.setImageResource(R.drawable.ic_routines_grey_24dp);
        empty_head.setText(R.string.empty_routines_head);
        empty_attention.setText(R.string.empty_routines);
        empty_long_press_routine.setVisibility(View.VISIBLE);
        empty_long_press_note.setVisibility(View.INVISIBLE);
        if (dataList == null || dataList.isEmpty()) {
            empty_view.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            empty_view.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        RoutineListAdapter adapter = new RoutineListAdapter(dataList, (Activity) getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        new Handler().postDelayed(()-> getPresenter().refreshData(), 500);
    }

    @Override
    public View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    //<生命周期>-------------------------------------------------------------------------------------

    //</生命周期>------------------------------------------------------------------------------------


    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------

    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>-----------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-----------------------------------------

    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------


    //<Event事件区>---只要存在事件监听代码就是----------------------------------------------------------

    @NonNull
    @Override
    public RoutinesListPresenter providePresenter() {
        return new RoutinesListPresenter();
    }

    @Override
    public boolean canRefresh() {
        return !recyclerView.canScrollVertically(-1);
    }

    @Override
    public boolean canLoadMore() {
        return !recyclerView.canScrollVertically(1);
    }


    //-//<Listener>------------------------------------------------------------------------------
    //-//</Listener>-----------------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------

    //</内部类>---尽量少用---------------------------------------------------------------------------

}
