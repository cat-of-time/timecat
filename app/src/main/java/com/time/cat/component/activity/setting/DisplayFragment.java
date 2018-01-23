package com.time.cat.component.activity.setting;

import com.time.cat.R;
import com.time.cat.component.base.BaseActivity;

/**
 * Created by penglu on 2016/12/10.
 */

public class DisplayFragment extends BaseRecyclerFragment {

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            BaseActivity baseActivity = ((BaseActivity) getActivity());
            baseActivity.getSupportActionBar().setTitle(R.string.fragment_display);
        }
    }

    @Override
    protected void prepareCardView() {
        cardViews.add(new TimeCatSettingCard(getActivity()));
        cardViews.add(new FloatAndNotifySettingCard(getActivity()));
    }
}
