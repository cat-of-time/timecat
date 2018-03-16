package com.time.cat.ui.base.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.time.cat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/16
 * @discription 支持懒加载
 * @usage null
 */
public abstract class BaseLazyLoadFragment<V extends BaseLazyLoadMVP.View, P extends BaseLazyLoadPresenter<V>>
        extends BaseFragment<V, P> implements BaseLazyLoadMVP.View {

    protected View view; // fragment view object

    @BindView(R.id.progress_bar)
    protected ProgressBar progressBar;


    /**
     * 是否可见状态 为了避免和{@link BaseLazyLoadFragment#isVisible()}冲突 换个名字
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
     * 故使用 {@link BaseLazyLoadFragment#setForceLoad(boolean)}来让Fragment下次执行initData
     * </pre>
     */
    private boolean forceLoad = false;

    @Override
    protected int fragmentLayout() {
        return 0;
    }





    public abstract View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);






    //<生命周期>---------------------------------------------------------------------------------<([{
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // 若 viewpager 不设置 setOffscreenPageLimit 或设置数量不够
        // 销毁的Fragment onCreateView 每次都会执行(但实体类没有从内存销毁)
        // 导致initData反复执行,所以这里注释掉
        // isFirstLoad = true;

        // 取消 isFirstLoad = true的注释 , 因为上述的initData本身就是应该执行的
        // onCreateView执行 证明被移出过FragmentManager initData确实要执行.
        // 如果这里有数据累加的Bug 请在initViews方法里初始化您的数据 比如 list.clear();
        isFirstLoad = true;
        view = initViews(inflater, container, savedInstanceState);
        assert view != null;
        ButterKnife.bind(this, view);
        isPrepared = true;
        lazyLoad();

        return view;
    }

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
    //</生命周期>--------------------------------------------------------------------------------}])>






    //<lazy load>-------------------------------------------------------------------------------<([{
    @Override
    public void showProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onVisible() {
        isFragmentVisible = true;
        lazyLoad();
    }

    @Override
    public void onInvisible() {
        isFragmentVisible = false;
    }

    /**
     * 要实现延迟加载Fragment内容,需要在 onCreateView
     * isPrepared = true;
     */
    @Override
    public void lazyLoad() {
        if (isPrepared() && isFragmentVisible()) {
            if (forceLoad || isFirstLoad()) {
                forceLoad = false;
                isFirstLoad = false;
                getPresenter().lazyLoadData();
            }
        }
    }

    @Override
    public void notifyDataChanged() {
        getPresenter().refreshData();
    }

    /**
     * 忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
     */
    @Override
    public void setForceLoad(boolean forceLoad) {
        this.forceLoad = forceLoad;
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

    @Override
    public boolean isFirstLoad() {
        return isFirstLoad;
    }

    @Override
    public boolean isFragmentVisible() {
        return isFragmentVisible;
    }
    //<lazy load>-------------------------------------------------------------------------------}])>

}
