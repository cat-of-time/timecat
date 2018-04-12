package com.time.cat.ui.modules.notes.timeline_view;

import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.ui.base.mvp.BaseLazyLoadMVP;

import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/28
 * @discription null
 * @usage null
 */
public class TimeLineMVP {
    interface View extends BaseLazyLoadMVP.View {
        void refreshView(List<DBNote> adapterDBNoteList);
    }

    interface Presenter extends BaseLazyLoadMVP.Presenter {
        void refresh();
    }
}
