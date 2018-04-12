package com.time.cat.ui.modules.plans.detail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.model.DBmodel.DBPlan;
import com.time.cat.data.model.events.PersistenceEvents;
import com.time.cat.ui.base.BaseActivity;
import com.time.cat.ui.modules.main.MainActivity;
import com.time.cat.ui.widgets.FadeTransitionImageView;
import com.time.cat.ui.widgets.boardview.RecyclerViewHorizontalDataAdapter;
import com.time.cat.ui.widgets.boardview.drag.DragHelper;
import com.time.cat.ui.widgets.boardview.drag.DragLayout;
import com.time.cat.ui.widgets.boardview.pager.PagerRecyclerView;
import com.time.cat.util.override.LogUtil;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/3/29
 * @discription null
 * @usage null
 */
public class PlanDetailActivity extends BaseActivity {

    public static final String EXTRA_IMAGE_URL = "detailImageUrl";

    public static final String IMAGE_TRANSITION_NAME = "transitionImage";
    private Queue<Object> pendingEvents = new LinkedList<>();

    private DBPlan dbPlan;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        dbPlan = (DBPlan) getIntent().getSerializableExtra("DBPlan");
        setContentView(R.layout.activity_plan_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setTitle(dbPlan.getTitle());
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ali_feedback_icon_back_white);
        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(PlanDetailActivity.this, MainActivity.class));
            finish();
        });

        FadeTransitionImageView bottomView = findViewById(R.id.bottomImageView);
        bottomView.firstInit(dbPlan.getCoverImageUrl());
//        mLayout.getBackground().setColorFilter(data.getColor(), PorterDuff.Mode.SRC_IN);

        mLayoutMain = (DragLayout) findViewById(R.id.layout_main);
        mRecyclerView = (PagerRecyclerView) findViewById(R.id.rv_lists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFlingFactor(0.1f);

        mAdapter = new RecyclerViewHorizontalDataAdapter(mActivity, dbPlan);
        View footer = getLayoutInflater().inflate(R.layout.recyclerview_footer_addlist, null, false);
        mAdapter.setFooterView(footer);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnPageChangedListener(mOnPagerChangedListener);
        mRecyclerView.addOnLayoutChangeListener(mOnLayoutChangedListener);
        mRecyclerView.addOnScrollListener(mOnScrollListener);

        mDragHelper = new DragHelper(mActivity);
        mDragHelper.bindHorizontalRecyclerView(mRecyclerView);
        mLayoutMain.setDragHelper(mDragHelper);
        getDataAndRefreshView();
        TimeCatApp.eventBus().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        while (!pendingEvents.isEmpty()) {
            LogUtil.e("Processing pending event...");
            onEvent(pendingEvents.poll());
        }
    }

    public void onEvent(final Object evt) {
        new Handler().post(() -> {
            if (evt instanceof PersistenceEvents.ModelCreateOrUpdateEvent) {
                mAdapter.notifyDataSetChanged();
            } else if (evt instanceof PersistenceEvents.TaskCreateEvent) {
                mAdapter.notifyDataSetChanged();
            } else if (evt instanceof PersistenceEvents.TaskUpdateEvent) {
                mAdapter.notifyDataSetChanged();
            } else if (evt instanceof PersistenceEvents.TaskDeleteEvent) {
                mAdapter.notifyDataSetChanged();
            }
        });
    }
    private PagerRecyclerView mRecyclerView;
    private RecyclerViewHorizontalDataAdapter mAdapter;
    private DragLayout mLayoutMain;
    private DragHelper mDragHelper;

    private PlanDetailActivity mActivity;

    private void getDataAndRefreshView() {
        mAdapter.notifyDataSetChanged();
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int childCount = mRecyclerView.getChildCount();
            int width = mRecyclerView.getChildAt(0).getWidth();
            int padding = (mRecyclerView.getWidth() - width) / 2;

            for (int j = 0; j < childCount; j++) {
                View v = recyclerView.getChildAt(j);
                //往左 从 padding 到 -(v.getWidth()-padding) 的过程中，由大到小
                float rate = 0;
                if (v.getLeft() <= padding) {
                    if (v.getLeft() >= padding - v.getWidth()) {
                        rate = (padding - v.getLeft()) * 1f / v.getWidth();
                    } else {
                        rate = 1;
                    }
//                    v.setScaleY(1 - rate * 0.1f);
                    v.setScaleX(1 - rate * 0.1f);

                } else {
                    //往右 从 padding 到 recyclerView.getWidth()-padding 的过程中，由大到小
                    if (v.getLeft() <= recyclerView.getWidth() - padding) {
                        rate = (recyclerView.getWidth() - padding - v.getLeft()) * 1f / v.getWidth();
                    }
//                    v.setScaleY(0.9f + rate * 0.1f);
                    v.setScaleX(0.9f + rate * 0.1f);
                }
            }
        }
    };

    private View.OnLayoutChangeListener mOnLayoutChangedListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        }
    };

    private PagerRecyclerView.OnPageChangedListener mOnPagerChangedListener = new PagerRecyclerView.OnPageChangedListener() {
        @Override
        public void OnPageChanged(int oldPosition, int newPosition) {

        }
    };

    public DragHelper getDragHelper() {
        return mDragHelper;
    }

}