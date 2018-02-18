package com.time.cat.component.base;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.time.cat.ThemeSystem.utils.ThemeUtils;
import com.time.cat.TimeCatApp;

/**
 * @author dlink
 * @date 2018/1/24
 * @discription 基础Fragment类,基类BaseFragment中的传递参数args可以供子类选择性使用
 */
public class BaseFragment extends Fragment {

    private Activity activity;
    //传递过来的参数Bundle，供子类使用
    protected Bundle args;

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
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();
    }

    public Context getContext() {
        if (activity == null) {
            return TimeCatApp.getInstance();
        }
        return activity;
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

}
