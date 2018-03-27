package com.time.cat.ui.modules.main.listener;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

/**
 * @author dlink
 * @date 2018/1/25
 * @discription
 */
public interface OnPlanViewClickListener {
    void onViewSortClick();
    void initSearchView(Menu menu, AppCompatActivity activity);
}
