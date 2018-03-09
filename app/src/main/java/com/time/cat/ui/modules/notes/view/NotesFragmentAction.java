package com.time.cat.ui.modules.notes.view;

import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.ui.base.mvpframework.view.BaseMvpView;

import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/28
 * @discription null
 * @usage null
 */
public interface NotesFragmentAction extends BaseMvpView {
    void refreshView(List<DBNote> adapterDBNoteList);
}
