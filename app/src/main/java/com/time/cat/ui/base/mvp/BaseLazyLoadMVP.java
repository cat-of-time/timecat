package com.time.cat.ui.base.mvp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/16
 * @discription null
 * @usage null
 */
public interface BaseLazyLoadMVP {

    interface View extends BaseMVP.View {
        boolean isPrepared();
        boolean isFirstLoad();
        boolean isFragmentVisible();
        void onVisible();
        void onInvisible();
        void lazyLoad();
        void setForceLoad(boolean forceLoad);


        //以下自己
        void notifyDataChanged();
        @CallOnMainThread void showProgress();
    }

    interface Presenter extends BaseMVP.Presenter {
        void lazyLoadData();
        void refreshData();
    }

}
