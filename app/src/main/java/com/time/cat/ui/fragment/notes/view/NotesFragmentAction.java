package com.time.cat.ui.fragment.notes.view;

import com.time.cat.mvp.model.DBmodel.DBNote;

import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/28
 * @discription null
 * @usage null
 */
public interface NotesFragmentAction {
    void refreshView(List<DBNote> adapterDBNoteList);
}
