package com.time.cat.ui.base.mvp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evernote.android.state.StateSaver;
import com.time.cat.TimeCatApp;

import net.grandcentrix.thirtyinch.TiFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Kosh on 27 May 2016, 7:54 PM
 */

public abstract class BaseFragment<V extends BaseMVP.View, P extends BasePresenter<V>> extends TiFragment<P, V>
        implements BaseMVP.View {

    protected BaseMVP.View callback;

    @Nullable
    private Unbinder unbinder;
    Context context;
    private Activity activity;

    @LayoutRes
    protected abstract int fragmentLayout();



    //<公共生命周期>------------------------------------------------------------------------------<([{
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseMVP.View) {
            callback = (BaseMVP.View) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);
        getPresenter().onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            StateSaver.restoreInstanceState(this, savedInstanceState);
            getPresenter().onRestoreInstanceState(savedInstanceState);
        }
        getPresenter().setEnterprise(isEnterprise());
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        activity = getActivity();
        if (fragmentLayout() != 0) {
            final Context contextThemeWrapper = new ContextThemeWrapper(getContext(), getContext().getTheme());
            LayoutInflater themeAwareInflater = inflater.cloneInContext(contextThemeWrapper);
            View view = themeAwareInflater.inflate(fragmentLayout(), container, false);
            unbinder = ButterKnife.bind(this, view);
            return view;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public Context getContext() {
        if (activity == null) {
            return TimeCatApp.getInstance();
        }
        return activity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }
    //<公共生命周期>------------------------------------------------------------------------------}])>






    //<BaseMVP.View>----------------------------------------------------------------------------<([{
    @Override
    public void showProgress(@StringRes int resId) {
        callback.showProgress(resId);
    }

    @Override
    public void showProgress(int resId, boolean cancelable) {
        callback.showProgress(resId, cancelable);
    }

    @Override
    public void showBlockingProgress(int resId) {
        callback.showBlockingProgress(resId);
    }

    @Override
    public void hideProgress() {
        if (callback != null) callback.hideProgress();
    }

    @Override
    public void showMessage(@StringRes int titleRes, @StringRes int msgRes) {
        callback.showMessage(titleRes, msgRes);
    }

    @Override
    public void showMessage(@NonNull String titleRes, @NonNull String msgRes) {
        callback.showMessage(titleRes, msgRes);
    }

    @Override
    public void showErrorMessage(@NonNull String msgRes) {
        callback.showErrorMessage(msgRes);
    }

    @Override
    public boolean isLoggedIn() {
        return callback.isLoggedIn();
    }

    @Override
    public void onRequireLogin() {
        callback.onRequireLogin();
    }

    @Override
    public void onLogoutPressed() {
        callback.onLogoutPressed();
    }

    @Override
    public void onThemeChanged() {
        callback.onThemeChanged();
    }

    @Override
    public void onOpenSettings() {
        callback.onOpenSettings();
    }

    @Override
    public boolean isEnterprise() {
        return callback != null && callback.isEnterprise();
    }

    @Override
    public void onOpenUrlInBrowser() {
        callback.onOpenUrlInBrowser();
    }

    protected boolean isSafe() {
        return getView() != null && getActivity() != null && !getActivity().isFinishing();
    }
    //<BaseMVP.View>-------------------------------------------------------------------------------}])>
}
