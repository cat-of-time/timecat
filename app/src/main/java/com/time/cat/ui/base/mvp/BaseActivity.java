package com.time.cat.ui.base.mvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.Constants;
import com.time.cat.helper.AppHelper;
import com.time.cat.ui.modules.setting.SettingActivity;
import com.time.cat.util.EasyPermissionsManager;

import net.grandcentrix.thirtyinch.TiActivity;

import java.util.List;

import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/11
 * @discription null
 * @usage null
 */
public abstract class BaseActivity<V extends BaseMVP.View, P extends BasePresenter<V>> extends TiActivity<P, V>
        implements BaseMVP.View,
                   EasyPermissionsManager.PermissionCallbacks {

    //<权限检查>---------------------------------------------------------------------------------<([{
    protected static final int RC_PERM = 123;
    protected static int reSting = R.string.ask_again;//默认提示语句
    private CheckPermListener mListener;

    public void checkPermission(CheckPermListener listener, int resString, String... mPerms) {
        mListener = listener;
        if (EasyPermissionsManager.hasPermissions(this, mPerms)) {
            if (mListener != null) mListener.grantPermission();
        } else {
            CharSequence text = Html.fromHtml("<font color=\"#000000\">" + getString(resString) + "</font>");
            EasyPermissionsManager.requestPermissions(this, text, RC_PERM, mPerms);
        }
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //同意了某些权限可能不是全部
        if (mListener != null) mListener.denyPermission();
    }

    @Override
    public void onPermissionsAllGranted() {
        if (mListener != null) mListener.grantPermission();//同意了全部权限的回调
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (!EasyPermissionsManager.checkDeniedPermissionsNeverAskAgain(this, getString(R.string.perm_tip), R.string.setting, R.string.cancel, null, perms)) {
            if (mListener != null) mListener.denyPermission();
        }
    }

    /**
     * 权限回调接口
     */
    public interface CheckPermListener {
        //权限通过后的回调方法
        void grantPermission();

        void denyPermission();
    }
    //</权限检查>--------------------------------------------------------------------------------}])>





    //<公共生命周期>------------------------------------------------------------------------------<([{
    @State
    Bundle presenterStateBundle = new Bundle();

    @LayoutRes
    protected abstract int layout();
    protected abstract boolean isTransparent();
    protected abstract boolean canBack();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);
        getPresenter().onSaveInstanceState(presenterStateBundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppHelper.updateAppLanguage(this);
        super.onCreate(savedInstanceState);
        if (layout() != 0) {
            setContentView(layout());
            ButterKnife.bind(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (canBack()) {
            if (item.getItemId() == android.R.id.home) {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    //</公共生命周期>-----------------------------------------------------------------------------}])>





    //<BaseMVP.View>------------------------------------------------------------------------------
    private Toast toast;
    @State
    boolean isProgressShowing;

    @Override
    public void showMessage(@StringRes int titleRes, @StringRes int msgRes) {
        showMessage(getString(titleRes), getString(msgRes));
    }

    @Override
    public void showMessage(@NonNull String titleRes, @NonNull String msgRes) {
        hideProgress();
        if (toast != null) toast.cancel();
        Context context = TimeCatApp.getInstance(); // WindowManager$BadTokenException
        toast = titleRes.equals(context.getString(R.string.error)) ? Toasty.error(context, msgRes, Toast.LENGTH_LONG) : Toasty.info(context, msgRes, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void showErrorMessage(@NonNull String msgRes) {
        showMessage(getString(R.string.error), msgRes);
    }


    @Override
    public void showProgress(@StringRes int resId) {
        showProgress(resId, true);
    }

    @Override
    public void showBlockingProgress(int resId) {
        showProgress(resId, false);
    }

    @Override
    public void hideProgress() {
//        ProgressDialogFragment fragment = (ProgressDialogFragment) AppHelper.getFragmentByTag(getSupportFragmentManager(), ProgressDialogFragment.TAG);
//        if (fragment != null) {
//            isProgressShowing = false;
//            fragment.dismiss();
//        }
    }


    @Override
    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public void onRequireLogin() {
//        Toasty.warning(TimeCatApp.getInstance(), getString(R.string.unauthorized_user), Toast.LENGTH_LONG).show();
//        final Glide glide = Glide.get(TimeCatApp.getInstance());
//        getPresenter().manageViewDisposable(RxHelper.getObservable(Observable.fromCallable(() -> {
//            glide.clearDiskCache();
//            PrefGetter.setToken(null);
//            PrefGetter.setOtpCode(null);
//            PrefGetter.resetEnterprise();
//            Login.logout();
//            return true;
//        })).subscribe(aBoolean -> {
//            glide.clearMemory();
//            Intent intent = new Intent(this, LoginChooserActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finishAffinity();
//        }));
    }

    @Override
    public void onBackPressed() {
//        if (drawer != null && (drawer.isDrawerOpen(GravityCompat.START) || drawer.isDrawerOpen(GravityCompat.END))) {
//            closeDrawer();
//        } else {
//            boolean clickTwiceToExit = !PrefGetter.isTwiceBackButtonDisabled();
//            superOnBackPressed(clickTwiceToExit);
//        }
    }

    @Override
    public void onLogoutPressed() {
//        MessageDialogView.newInstance(getString(R.string.logout), getString(R.string.confirm_message), Bundler.start().put(BundleConstant.YES_NO_EXTRA, true).put("logout", true).end()).show(getSupportFragmentManager(), MessageDialogView.TAG);
    }

    @Override
    public void onThemeChanged() {
//        if (this instanceof MainActivity) {
//            recreate();
//        } else {
//            Intent intent = new Intent(this, MainActivity.class);
////            intent.putExtras(Bundler.start().put(BundleConstant.YES_NO_EXTRA, true).end());
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            finish();
//        }
    }

    @Override
    public void onOpenSettings() {
        startActivityForResult(new Intent(this, SettingActivity.class), Constants.REFRESH_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REFRESH_CODE) {
                onThemeChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EasyPermissionsManager.SETTINGS_REQ_CODE) {
            //设置返回
        }
    }

    @Override
    public boolean isEnterprise() {
        return getPresenter() != null && getPresenter().isEnterprise();
    }

    @Override
    public void onOpenUrlInBrowser() {
//        if (!InputHelper.isEmpty(schemeUrl)) {
//            ActivityHelper.startCustomTab(this, schemeUrl);
//            try {
//                finish();
//            } catch (Exception ignored) {
//            }// fragment might be committed and calling finish will crash the app.
//        }
    }

    private void showProgress(int resId, boolean cancelable) {
        String msg = getString(R.string.in_progress);
        if (resId != 0) {
            msg = getString(resId);
        }
        if (!isProgressShowing && !isFinishing()) {
//            ProgressDialogFragment fragment = (ProgressDialogFragment) AppHelper.getFragmentByTag(
//                    getSupportFragmentManager(), ProgressDialogFragment.TAG);
//            if (fragment == null) {
//                isProgressShowing = true;
//                fragment = ProgressDialogFragment.newInstance(msg, cancelable);
//                fragment.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);
//            }
        }
    }
    //</BaseMVP.View>------------------------------------------------------------------------------

}
