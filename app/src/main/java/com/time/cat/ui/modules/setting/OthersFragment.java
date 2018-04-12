package com.time.cat.ui.modules.setting;

import com.time.cat.R;
import com.time.cat.ui.base.BaseActivity;
import com.time.cat.ui.base.BaseRecyclerFragment;
import com.time.cat.ui.modules.setting.card.AboutCard;
import com.time.cat.ui.modules.setting.card.FeedBackAndUpdateCard;
import com.time.cat.ui.modules.setting.card.SLSettingCard;

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
