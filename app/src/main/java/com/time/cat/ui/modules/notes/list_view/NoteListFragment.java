package com.time.cat.ui.modules.plans.list_view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.time.cat.R;
import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.ui.adapter.PlanListAdapter;
import com.time.cat.ui.base.mvp.BaseLazyLoadFragment;
import com.time.cat.ui.modules.plans.PlansFragment;

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
 * @discription 列表视图
 * @usage null
 */
public class PlanListFragment extends BaseLazyLoadFragment<PlanListMVP.View, PlanListPresenter> implements PlansFragment.OnScrollBoundaryDecider{
    private List<DBPlan> dataList;
    @BindView(R.id.plan_rv)
    RecyclerView recyclerView;

    @Override
    public int getLayout() {
        return R.layout.fragment_plan_list;
    }

    @Override
    public void initView() {
        initDataList();
        PlanListAdapter adapter = new PlanListAdapter(dataList, (Activity) getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    //<生命周期>-------------------------------------------------------------------------------------

    //</生命周期>------------------------------------------------------------------------------------
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

    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------

    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>-----------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-----------------------------------------

    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------


    //<Event事件区>---只要存在事件监听代码就是----------------------------------------------------------

    @NonNull
    @Override
    public PlanListPresenter providePresenter() {
        return new PlanListPresenter();
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
