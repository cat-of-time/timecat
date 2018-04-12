package com.time.cat.ui.modules.notes;

import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.ui.base.mvp.BaseMVP;

import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/11
 * @discription null
 * @usage null
 */
public interface NotesMVP {

    interface View extends BaseMVP.View {
        void refreshView(List<DBNote> adapterDBNoteList);
    }

    interface Presenter extends BaseMVP.Presenter {
        void refresh();
    }
}
