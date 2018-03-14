package com.time.cat.ui.base.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.evernote.android.state.StateSaver;
import com.time.cat.R;
import com.time.cat.helper.RxHelper;

import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.rx2.RxTiPresenterDisposableHandler;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.HttpException;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/10
 * @discription null
 * @usage null
 */
public class BasePresenter<V extends BaseMVP.View> extends TiPresenter<V> implements BaseMVP.Presenter {
    @com.evernote.android.state.State
    boolean enterprise;

    private boolean apiCalled;
    private final RxTiPresenterDisposableHandler subscriptionHandler = new RxTiPresenterDisposableHandler(this);

    @Override
    public void onSaveInstanceState(Bundle outState) {
        StateSaver.saveInstanceState(this, outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle outState) {
        if (outState != null) StateSaver.restoreInstanceState(this, outState);
    }

    @Override
    public void manageDisposable(@Nullable Disposable... disposables) {
        if (disposables != null) {
            subscriptionHandler.manageDisposables(disposables);
        }
    }

    @Override
    public <T> void manageObservable(@Nullable Observable<T> observable) {
        if (observable != null) {
            manageDisposable(RxHelper.getObservable(observable).subscribe(t -> {/**/}, Throwable::printStackTrace));
        }
    }

    @Override
    public void manageViewDisposable(@Nullable Disposable... disposables) {
        if (disposables != null) {
            if (isViewAttached()) {
                subscriptionHandler.manageViewDisposables(disposables);
            } else {
                sendToView(v -> manageViewDisposable(disposables));
            }
        }
    }

    @Override
    public boolean isApiCalled() {
        return apiCalled;
    }

    @Override
    public void onSubscribed(boolean cancelable) {
        sendToView(v -> {
            if (cancelable) {
                v.showProgress(R.string.in_progress);
            } else {
                v.showBlockingProgress(R.string.in_progress);
            }
        });
    }

    @Override
    public void onError(@NonNull Throwable throwable) {
        apiCalled = true;
        throwable.printStackTrace();
//        int code = RestProvider.getErrorCode(throwable);
//        if (code == 401) {
//            sendToView(BaseMVP.View::onRequireLogin);
//            return;
//        }
//        GitHubErrorResponse errorResponse = RestProvider.getErrorResponse(throwable);
//        if (errorResponse != null && errorResponse.getMessage() != null) {
//            sendToView(v -> v.showErrorMessage(errorResponse.getMessage()));
//        } else {
//            sendToView(v -> v.showMessage(R.string.error, getPrettifiedErrorMessage(throwable)));
//        }
    }

    @Override
    public <T> void makeRestCall(@NonNull Observable<T> observable, @NonNull Consumer<T> onNext) {
        makeRestCall(observable, onNext, true);
    }

    @Override
    public <T> void makeRestCall(@NonNull Observable<T> observable, @NonNull Consumer<T> onNext, boolean cancelable) {
        manageDisposable(RxHelper.getObservable(observable).doOnSubscribe(disposable -> onSubscribed(cancelable)).subscribe(onNext, this::onError, () -> apiCalled = true));
    }

    @StringRes
    private int getPrettifiedErrorMessage(@Nullable Throwable throwable) {
        int resId = R.string.network_error;
        if (throwable instanceof HttpException) {
            resId = R.string.network_error;
        } else if (throwable instanceof IOException) {
            resId = R.string.request_error;
        } else if (throwable instanceof TimeoutException) {
            resId = R.string.unexpected_error;
        }
        return resId;
    }

    public boolean isEnterprise() {
        return enterprise;
    }

    public void setEnterprise(boolean enterprise) {
        this.enterprise = enterprise;
    }
}