package com.time.cat.component.base;


import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.time.cat.ThemeSystem.utils.ThemeUtils;
import com.time.cat.TimeCatApp;

/**
 * @author dlink
 * @date 2018/1/24
 * @discription 基础Fragment类
 */
public class BaseFragment extends Fragment {

    private Activity activity;

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
