package com.time.cat.ui.base;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.ui.base.mvpframework.presenter.BaseMvpPresenter;
import com.time.cat.ui.base.mvpframework.view.AbstractFragment;
import com.time.cat.ui.base.mvpframework.view.BaseMvpView;
import com.time.cat.ui.widgets.theme.utils.ThemeUtils;

import butterknife.ButterKnife;

/**
 * @author dlink
 * @date 2018/1/24
 * @discription 基础Fragment类,基类BaseFragment中的传递参数args可以供子类选择性使用
 */
public class BaseFragment<V extends BaseMvpView, P extends BaseMvpPresenter<V>> extends AbstractFragment<V, P> implements BaseMvpView{

    private Activity activity;
    protected AppCompatActivity appCompatActivity; // context object
    Context context;
    protected View view; // fragment view object
    //传递过来的参数Bundle，供子类使用
    protected Bundle args;


    //<公共生命周期>------------------------------------------------------------------------------<([{
    /**
     * 创建fragment的静态方法，方便传递参数
     * @param args 传递的参数
     * @return
     */
    public static <T extends Fragment>T newInstance(Class clazz,Bundle args) {
        T mFragment=null;
        try {
            mFragment= (T) clazz.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mFragment.setArguments(args);
        return mFragment;
    }

    /**
     * 初始创建Fragment对象时调用
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();
        if (args != null && args.size() > 0) {
            initVariables(args);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity = getActivity();
    }

    @Override
    public void onResume() {
        ThemeUtils.refreshUI(getActivity(), null);
        super.onResume();
    }

    public Context getContext() {
        if (activity == null) {
            return TimeCatApp.getInstance();
        }
        return activity;
    }

    /**
     * 被ViewPager移出的Fragment 下次显示时会从getArguments()中重新获取数据
     * 所以若需要刷新被移除Fragment内的数据需要重新put数据 eg:
     * Bundle args = getArguments();
     *      if (args != null) {
     *      args.putParcelable(KEY, info);
     * }
     * 可以不用重载
     */
    public void initVariables(Bundle bundle) {}
    //</公共生命周期>-----------------------------------------------------------------------------}])>





    //<配置变量，有两种：懒加载、自带标题>------------------------------------------------------------<([{
    public void FragmentConfig(boolean hasToolBar, boolean lazyLoad){
        this.hasToolBar = hasToolBar;
        this.lazyLoad = lazyLoad;
    }


    boolean hasToolBar = false;
    /**
     * Fragment title
     */
    public String fragmentTitle;
    protected Toolbar toolbar;
    protected String toolbarTitle;
    /**
     * If true, set back arrow in toolbar.
     */
    protected boolean setDisplayHomeAsUpEnabled = true;


    boolean lazyLoad = false;
    /**
     * 是否可见状态 为了避免和{@link Fragment#isVisible()}冲突 换个名字
     */
    private boolean isFragmentVisible;
    /**
     * 标志位，View已经初始化完成。
     * isPrepared还是准一些,isAdded有可能出现onCreateView没走完但是isAdded了
     */
    private boolean isPrepared;
    /**
     * 是否第一次加载
     */
    private boolean isFirstLoad = true;
    /**
     * <pre>
     * 忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
     * 一般用于PagerAdapter需要刷新各个子Fragment的场景
     * 不要new 新的 PagerAdapter 而采取reset数据的方式
     * 所以要求Fragment重新走initData方法
     * 故使用 {@link BaseFragment#setForceLoad(boolean)}来让Fragment下次执行initData
     * </pre>
     */
    private boolean forceLoad = false;
    //<配置变量，有两种：懒加载、自带标题>------------------------------------------------------------}])>




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        appCompatActivity = (AppCompatActivity) getActivity();
        if (lazyLoad && !hasToolBar) {
            // 若 viewpager 不设置 setOffscreenPageLimit 或设置数量不够
            // 销毁的Fragment onCreateView 每次都会执行(但实体类没有从内存销毁)
            // 导致initData反复执行,所以这里注释掉
            // isFirstLoad = true;

            // 取消 isFirstLoad = true的注释 , 因为上述的initData本身就是应该执行的
            // onCreateView执行 证明被移出过FragmentManager initData确实要执行.
            // 如果这里有数据累加的Bug 请在initViews方法里初始化您的数据 比如 list.clear();
            isFirstLoad = true;
            view = initViews(inflater, container, savedInstanceState);
            isPrepared = true;
            lazyLoad();
        } else {
            view = inflater.inflate(getLayoutId(), container, false);
            ButterKnife.bind(this, view);
            initView();
        }
        return view;
    }




    //<lazy load>-------------------------------------------------------------------------------<([{
    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     *
     * @param isVisibleToUser 是否显示出来了
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     *
     * @param hidden hidden True if the fragment is now hidden, false if it is not
     * visible.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isPrepared = false;
    }

    protected void onVisible() {
        isFragmentVisible = true;
        lazyLoad();
    }

    protected void onInvisible() {
        isFragmentVisible = false;
    }

    /**
     * 要实现延迟加载Fragment内容,需要在 onCreateView
     * isPrepared = true;
     */
    protected void lazyLoad() {
        if (isPrepared() && isFragmentVisible()) {
            if (forceLoad || isFirstLoad()) {
                forceLoad = false;
                isFirstLoad = false;
                initData();
            }
        }
    }

    /**
     * 忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
     */
    public void setForceLoad(boolean forceLoad) {
        this.forceLoad = forceLoad;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public boolean isFirstLoad() {
        return isFirstLoad;
    }

    public boolean isFragmentVisible() {
        return isFragmentVisible;
    }

    /**
     * 如果使用lazy load，必须重载
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState savedInstanceState
     * @return view
     */
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    /**
     * 如果使用lazy load，必须重载
     */
    protected void initData() {}

    /**
     * 如果使用lazy load，必须重载
     */
    public void notifyDataChanged() {}
    //<lazy load>-------------------------------------------------------------------------------}])>






    //<自带标题>---------------------------------------------------------------------------------<([{
    public String getTitle() {
        if (null == fragmentTitle) {
            setDefaultFragmentTitle(null);
        }
        return TextUtils.isEmpty(fragmentTitle) ? "" : fragmentTitle;
    }

    public void setTitle(String title) {
        fragmentTitle = title;
    }

    /**
     * 设置fragment的Title直接调用 {@link BaseFragment#setTitle(String)},若不显示该title 可以不做处理
     *
     * @param title 一般用于显示在TabLayout的标题
     */
    protected void setDefaultFragmentTitle(String title) {
        setTitle(title);
    }

    /**
     * 如果自带标题，必须重载
     * Get resource id of layout
     * @return resource id
     */
    public int getLayoutId(){
        return R.layout.wait_for_update;
    }

    /**
     * 如果自带标题，必须重载并super.initView()调用
     * Init view component
     */
    public void initView() {
        toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            if (toolbarTitle != null) {
                toolbar.setTitle(toolbarTitle);
                toolbar.setTitleTextColor(Color.WHITE);
            }
            appCompatActivity.setSupportActionBar(toolbar);
            if (setDisplayHomeAsUpEnabled) {
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }
    //<自带标题>---------------------------------------------------------------------------------}])>





    //<刷新menu>--------------------------------------------------------------------------------<([{
    protected MenuItem refreshItem;

    @SuppressLint("NewApi")
    public void showRefreshAnimation(MenuItem item) {
        hideRefreshAnimation();

        refreshItem = item;

        //这里使用一个ImageView设置成MenuItem的ActionView，这样我们就可以使用这个ImageView显示旋转动画了
        ImageView refreshActionView = (ImageView) getLayoutInflater().inflate(R.layout.action_view, null);
        refreshActionView.setImageResource(R.drawable.ic_autorenew_white_24dp);
        refreshItem.setActionView(refreshActionView);

        //显示刷新动画
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.refresh);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(1);
        refreshActionView.startAnimation(animation);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideRefreshAnimation();
            }
        }, 1000);
    }

    @SuppressLint("NewApi")
    private void hideRefreshAnimation() {
        if (refreshItem != null) {
            View view = refreshItem.getActionView();
            if (view != null) {
                view.clearAnimation();
                refreshItem.setActionView(null);
            }
        }
    }
    //<刷新menu>--------------------------------------------------------------------------------}])>

}
