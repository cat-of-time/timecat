package com.time.cat.ui.activity.setting;

import com.time.cat.R;
import com.time.cat.ui.base.BaseActivity;

public class OthersFragment extends BaseRecyclerFragment {
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            BaseActivity baseActivity = ((BaseActivity) getActivity());
            baseActivity.getSupportActionBar().setTitle(R.string.fragment_other);
        }
    }

    @Override
    protected void prepareCardView() {
        cardViews.add(new SLSettingCard(getActivity()));
        cardViews.add(new FeedBackAndUpdateCard(getActivity()));
        cardViews.add(new AboutCard(getActivity()));
    }

}
