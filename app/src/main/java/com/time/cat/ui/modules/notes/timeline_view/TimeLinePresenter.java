package com.time.cat.ui.modules.notes.timeline_view;

import com.time.cat.data.database.DB;
import com.time.cat.data.model.APImodel.Note;
import com.time.cat.data.model.Converter;
import com.time.cat.data.model.DBmodel.DBNote;
import com.time.cat.data.model.DBmodel.DBUser;
import com.time.cat.data.network.RetrofitHelper;
import com.time.cat.ui.base.mvp.BaseLazyLoadPresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/28
 * @discription null
 * @usage null
 */
public class TimeLinePresenter extends BaseLazyLoadPresenter<TimeLineMVP.View> implements TimeLineMVP.Presenter{
    @Override
    public void lazyLoadData() {
        refreshData();
    }

    @Override
    public void refresh() {
        refreshData();
    }

    public void refreshData() {
        RetrofitHelper.getNoteService().getNotesAll() //获取Observable对象
                .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .doOnNext(new Action1<ArrayList<Note>>() {
                    @Override
                    public void call(ArrayList<Note> noteList) {
                        if (noteList == null || noteList.size() <= 0)
                            return;
                        for (Note note : noteList) {
                            DB.notes().safeSaveNote(note);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(new Subscriber<ArrayList<Note>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //请求失败
//                        ToastUtil.show("同步[ 笔记 ]到本地出现错误");
//                        LogUtil.e("同步[ 笔记 ]到本地出现错误" + e.toString());
                    }

                    @Override
                    public void onNext(ArrayList<Note> task) {
                        //请求成功
//                        ToastUtil.show("成功同步[ 笔记 ]");
//                        LogUtil.e("成功同步[ 笔记 ]: " + task.toString());
                    }
                });


        List<DBNote> dbNoteList = DB.notes().findAll();
        if (dbNoteList == null || dbNoteList.size() <= 0) {
            return;
        }
        List<DBNote> adapterDBNoteList = new ArrayList<>();
        DBUser dbUser = DB.users().getActive();
        for (int i = dbNoteList.size()-1; i >= 0; i--) {
            if ((dbNoteList.get(i).getOwner().equals(Converter.getOwnerUrl(dbUser)))) {
                adapterDBNoteList.add(dbNoteList.get(i));
            }
        }

        if (adapterDBNoteList.size() >= 0) {
            onDataChange(adapterDBNoteList);
        }
    }

    //<NotesDataManager.OnDataChangeListener>-----data层返回数据的回调接口-----------------------------
    private void onDataChange(List<DBNote> adapterDBNoteList) {
        sendToView(view -> {
            view.refreshView(adapterDBNoteList);
        });
    }
    //</NotesDataManager.OnDataChangeListener>------------------------------------------------------
}
