package com.time.cat.ui.modules.plans.card_view;

import com.time.cat.data.database.DB;
import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.ui.base.mvp.BaseLazyLoadPresenter;

import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/28
 * @discription null
 * @usage null
 */
public class PlanCardPresenter extends BaseLazyLoadPresenter<PlanCardMVP.View> implements PlanCardMVP.Presenter{
    @Override
    public void lazyLoadData() {
        List<DBPlan> adapterDBPlanList = DB.plans().findAll();
        onDataChange(adapterDBPlanList);
    }

    @Override
    public void refresh() {
        List<DBPlan> adapterDBPlanList = DB.plans().findAll();
        onDataChange(adapterDBPlanList);
    }

    //<NotesDataManager.OnDataChangeListener>-----data层返回数据的回调接口-----------------------------
    private void onDataChange(List<DBPlan> adapterDBPlanList) {
        sendToView(view -> {
            view.refreshView(adapterDBPlanList);
        });
    }
    //</NotesDataManager.OnDataChangeListener>------------------------------------------------------
}
