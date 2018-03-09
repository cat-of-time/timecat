package com.time.cat.ui.modules.notes.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.ui.base.mvpframework.presenter.BaseMvpPresenter;
import com.time.cat.ui.modules.notes.model.NotesDataManager;
import com.time.cat.ui.modules.notes.view.NotesFragmentAction;

import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/28
 * @discription NotesFragment的View与Model的组装
 * @usage null
 */
public class NotesPresenter extends BaseMvpPresenter<NotesFragmentAction> implements NotesDataManager.OnDataChangeListener {
    private static final String TAG = "MVP-NotesPresenter";
    private NotesDataManager notesDataManagerAction;

    public NotesPresenter() {
        this.notesDataManagerAction = new NotesDataManager();
    }

    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    public void onCreatePresenter(@Nullable Bundle savedState) {
        super.onCreatePresenter(savedState);
        if(savedState != null){
            Log.e(TAG,"RequestPresenter5  onCreatePresenter 测试  = " + savedState.getString("test2") );
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Log.e("perfect-mvp","RequestPresenter5  onSaveInstanceState 测试 " );
        outState.putString("test2","test_save2");
    }

    @Override
    public void onDestroyPresenter() {
        super.onDestroyPresenter();
    }
    //</生命周期>------------------------------------------------------------------------------------



    //<业务处理>-----外部只允许调用业务处理函数-----------------------------------------------------------
    public void refresh() {
        notesDataManagerAction.refreshData(this);
    }
    //</业务处理>-----外部只允许调用业务处理函数-----------------------------------------------------------



    //<NotesDataManager.OnDataChangeListener>-----data层返回数据的回调接口-----------------------------
    @Override
    public void onDataChange(List<DBNote> adapterDBNoteList) {
        if(getMvpView() != null) {
            getMvpView().refreshView(adapterDBNoteList);
        }
    }
    //</NotesDataManager.OnDataChangeListener>------------------------------------------------------
}
