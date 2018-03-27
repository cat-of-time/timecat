package com.time.cat.ui.modules.setting;

import com.time.cat.R;
import com.time.cat.ui.base.BaseActivity;
import com.time.cat.ui.modules.setting.card.FloatAndNotifySettingCard;
import com.time.cat.ui.modules.setting.card.TimeCatSettingCard;

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
