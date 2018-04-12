package com.time.cat.ui.modules.plans.card_view;

import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.ui.base.mvp.BaseLazyLoadMVP;

import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/28
 * @discription null
 * @usage null
 */
public class PlanCardMVP {
    interface View extends BaseLazyLoadMVP.View {
        void refreshView(List<DBPlan> adapterDBNoteList);
    }

    interface Presenter extends BaseLazyLoadMVP.Presenter {
        void refresh();
    }
}
