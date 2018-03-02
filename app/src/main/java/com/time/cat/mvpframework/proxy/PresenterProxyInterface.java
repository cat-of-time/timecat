package com.time.cat.mvpframework.proxy;


import com.time.cat.mvpframework.factory.PresenterMvpFactory;
import com.time.cat.mvpframework.presenter.BaseMvpPresenter;
import com.time.cat.mvpframework.view.BaseMvpView;

/**
 * @author 刘镓旗
 * @date 2017/11/20
 * @description 代理接口
 */
public interface PresenterProxyInterface<V extends BaseMvpView,P extends BaseMvpPresenter<V>> {


    /**
     * 设置创建Presenter的工厂
     * @param presenterFactory PresenterFactory类型
     */
    void setPresenterFactory(PresenterMvpFactory<V, P> presenterFactory);

    /**
     * 获取Presenter的工厂类
     * @return 返回PresenterMvpFactory类型
     */
    PresenterMvpFactory<V,P> getPresenterFactory();


    /**
     * 获取创建的Presenter
     * @return 指定类型的Presenter
     */
    P getMvpPresenter();


}
