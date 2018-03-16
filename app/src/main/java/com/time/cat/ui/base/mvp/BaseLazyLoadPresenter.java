package com.time.cat.ui.base.mvp;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/16
 * @discription null
 * @usage null
 */
public abstract class BaseLazyLoadPresenter<V extends BaseLazyLoadMVP.View>
        extends BasePresenter<V> implements BaseLazyLoadMVP.Presenter {
    @Override
    public void refreshData() {
        getViewOrThrow().showProgress();

        if (getViewOrThrow().isFragmentVisible()) {
            lazyLoadData();
        } else {
            getViewOrThrow().setForceLoad(true);
        }
    }

    public abstract void lazyLoadData();
}
