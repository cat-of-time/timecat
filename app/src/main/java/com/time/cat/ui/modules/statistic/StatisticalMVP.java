package com.time.cat.ui.modules.statistic;

import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.ui.base.mvp.BaseActivityMVP;

import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/24
 * @discription null
 * @usage null
 */
public class StatisticalMVP {
    interface View extends BaseActivityMVP.View {
        void refreshView(List<DBNote> adapterDBNoteList);
    }

    interface Presenter extends BaseActivityMVP.Presenter {
        void refresh();
    }
}
