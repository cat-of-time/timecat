package com.time.cat.ui.fragment.notes.presenter;

import com.time.cat.mvp.model.DBmodel.DBNote;
import com.time.cat.ui.fragment.notes.model.NotesDataManager;
import com.time.cat.ui.fragment.notes.model.NotesDataManagerAction;
import com.time.cat.ui.fragment.notes.view.NotesFragmentAction;

import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/28
 * @discription NotesFragment的View与Model的组装
 * @usage null
 */
public class NotesPresenter implements NotesPresenterAction, NotesDataManager.OnDataChangeListener {
    private NotesFragmentAction notesFragmentAction;
    private NotesDataManagerAction notesDataManagerAction;

    public NotesPresenter(NotesFragmentAction notesFragmentAction) {
        this.notesFragmentAction = notesFragmentAction;
        this.notesDataManagerAction = new NotesDataManager();
    }

    @Override
    public void refresh() {
        notesDataManagerAction.refreshData(this);
    }

    @Override
    public void onDataChange(List<DBNote> adapterDBNoteList) {
        notesFragmentAction.refreshView(adapterDBNoteList);
    }
}
