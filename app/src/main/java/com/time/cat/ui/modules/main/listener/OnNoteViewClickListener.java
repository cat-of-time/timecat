package com.time.cat.ui.modules.main.listener;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/23
 * @discription null
 * @usage null
 */
public interface OnNoteViewClickListener {
    void onViewNoteRefreshClick();
    void onViewSortClick();
    void initSearchView(Menu menu, AppCompatActivity activity);
}
