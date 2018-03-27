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
        sendToView(v->{
            v.showProgress();

            if (v.isFragmentVisible()) {
                lazyLoadData();
            } else {
                v.setForceLoad(true);
            }
        });
    }

    public abstract void lazyLoadData();
}
