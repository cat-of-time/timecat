package com.time.cat.ui.fragment.notes.model;

import com.time.cat.database.DB;
import com.time.cat.mvp.model.DBmodel.DBNote;
import com.time.cat.mvp.model.DBmodel.DBUser;
import com.time.cat.util.ModelUtil;
import com.time.cat.util.override.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/28
 * @discription model层，只与数据有关
 * @usage null
 */
public class NotesDataManager {

    public void refreshData(OnDataChangeListener onDataChangeListener) {
        List<DBNote> dbNoteList = DB.notes().findAll();
        if (dbNoteList == null || dbNoteList.size() <= 0) {
            return;
        }
        List<DBNote> adapterDBNoteList = new ArrayList<>();
        DBUser dbUser = DB.users().getActive();
        LogUtil.e("active dbUser = " + dbUser.toString());
        LogUtil.e(dbNoteList);
        for (int i = dbNoteList.size()-1; i >= 0; i--) {
            if ((dbNoteList.get(i).getOwner().equals(ModelUtil.getOwnerUrl(dbUser)))) {
                adapterDBNoteList.add(dbNoteList.get(i));
            }
        }
        LogUtil.e(adapterDBNoteList);

        if (adapterDBNoteList.size() >= 0) {
            if (onDataChangeListener != null) {
                onDataChangeListener.onDataChange(adapterDBNoteList);
            }
        }
    }

    public interface OnDataChangeListener {
        void onDataChange(List<DBNote> adapterDBNoteList);
    }
}
