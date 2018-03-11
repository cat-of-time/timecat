package com.time.cat.ui.base.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import net.grandcentrix.thirtyinch.TiView;
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/10
 * @discription null
 * @usage null
 */
public interface BaseMvp {

    interface FAView extends TiView, OnScrollTopListener {

        @CallOnMainThread void showProgress(@StringRes int resId);

        @CallOnMainThread void showBlockingProgress(@StringRes int resId);

        @CallOnMainThread void hideProgress();

        @CallOnMainThread void showMessage(@StringRes int titleRes, @StringRes int msgRes);

        @CallOnMainThread void showMessage(@NonNull String titleRes, @NonNull String msgRes);

        @CallOnMainThread void showErrorMessage(@NonNull String msgRes);

        boolean isLoggedIn();

        void onRequireLogin();

        void onLogoutPressed();

        void onThemeChanged();

        void onOpenSettings();

        boolean isEnterprise();

        void onOpenUrlInBrowser();
    }

    interface FAPresenter {

        void onSaveInstanceState(Bundle outState);

        void onRestoreInstanceState(Bundle outState);

        void manageDisposable(@Nullable Disposable... disposables);

        <T> void manageObservable(@Nullable Observable<T> observable);

        void manageViewDisposable(@Nullable Disposable... disposables);

        boolean isApiCalled();

        void onSubscribed(boolean cancelable);

        void onError(@NonNull Throwable throwable);

        <T> void makeRestCall(@NonNull Observable<T> observable, @NonNull Consumer<T> onNext);

        <T> void makeRestCall(@NonNull Observable<T> observable, @NonNull Consumer<T> onNext, boolean cancelable);

        void onCheckGitHubStatus();
    }

    interface PaginationListener<P> {
        int getCurrentPage();

        int getPreviousTotal();

        void setCurrentPage(int page);

        void setPreviousTotal(int previousTotal);

        boolean onCallApi(int page, @Nullable P parameter);
    }

    interface OnScrollTopListener {
        void onScrollTop(int index);
    }
}
