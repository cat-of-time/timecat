package com.time.cat.ui.modules.setting;

import com.time.cat.R;
import com.time.cat.ui.base.BaseActivity;
import com.time.cat.ui.base.BaseRecyclerFragment;
import com.time.cat.ui.modules.setting.card.PomodoroCard;
import com.time.cat.ui.modules.setting.card.RoutineCard;

public class ClockFragment extends BaseRecyclerFragment {

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            BaseActivity baseActivity = ((BaseActivity) getActivity());
            baseActivity.getSupportActionBar().setTitle(R.string.fragment_clock);
        }
    }

    @Override
    protected void prepareCardView() {
        cardViews.add(new PomodoroCard(getActivity()));
        cardViews.add(new RoutineCard(getActivity()));
    }
}
