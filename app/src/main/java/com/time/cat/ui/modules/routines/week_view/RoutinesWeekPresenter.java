package com.time.cat.ui.modules.routines.week_view;

import com.time.cat.ui.base.mvp.BaseLazyLoadMVP;
import com.time.cat.ui.base.mvp.BaseLazyLoadPresenter;
import com.time.cat.util.override.LogUtil;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/22
 * @discription null
 * @usage null
 */
public class RoutinesWeekPresenter<V extends BaseLazyLoadMVP.View> extends BaseLazyLoadPresenter<V> {
    @Override
    public void lazyLoadData() {
        // do something to load data
        // ...
        // after load
        LogUtil.e("hide progress");
        sendToView(V::hideProgress);
    }
}
