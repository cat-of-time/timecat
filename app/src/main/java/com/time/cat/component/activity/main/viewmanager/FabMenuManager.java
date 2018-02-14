package com.time.cat.component.activity.main.viewmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.component.activity.main.MainActivity;
import com.time.cat.component.activity.main.schedules.SchedulesHelpActivity;
import com.time.cat.mvp.model.DBmodel.DBUser;
import com.time.cat.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper to manage the home screen floating action button behaviour
 */
public class FabMenuManager implements View.OnClickListener {


    LeftDrawerManager drawerMgr;
    FloatingActionsMenu fabMenu;
    FloatingActionButton fab;
    MainActivity activity;
    List<FloatingActionButton> scheduleActions;
    FloatingActionButton scanQrAction;

    private int currentPage = 0;


    public FabMenuManager(FloatingActionButton fab, FloatingActionsMenu fabMenu, LeftDrawerManager drawerMgr, MainActivity a) {
        this.fab = fab;
        this.fabMenu = fabMenu;
        this.activity = a;
        this.drawerMgr = drawerMgr;
        this.scheduleActions = getScheduleActions();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());

        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                if (!prefs.getBoolean("PREFERENCE_SCHEDULE_HELP_SHOWN", false)) {
                    activity.launchActivityDelayed(SchedulesHelpActivity.class, 600);
                }
            }

            @Override
            public void onMenuCollapsed() {

            }
        });

    }

    private List<FloatingActionButton> getScheduleActions() {
        ArrayList<FloatingActionButton> actions = new ArrayList<>();

        FloatingActionButton actionA = fabMenu.findViewById(R.id.action_a);
        FloatingActionButton actionB = fabMenu.findViewById(R.id.action_b);
        FloatingActionButton actionC = fabMenu.findViewById(R.id.action_c);

        actions.add(actionA);
        actions.add(actionB);
        actions.add(actionC);
        scanQrAction = fabMenu.findViewById(R.id.action_d);
        if (TimeCatApp.isPharmaModeEnabled(activity)) {
            scanQrAction.setVisibility(View.VISIBLE);
            actions.add(scanQrAction);
        } else {
            scanQrAction.setVisibility(View.GONE);
        }
        return actions;
    }


    public void init() {

        for (FloatingActionButton f : scheduleActions) {
            f.setOnClickListener(this);
        }

        fab.setOnClickListener(this);
        fab.setIconDrawable(new IconicsDrawable(activity).icon(GoogleMaterial.Icon.gmd_plus).paddingDp(5).sizeDp(24).color(Color.parseColor("#263238")));

        onViewPagerItemChange(0);
    }

    public void onViewPagerItemChange(int currentPage) {

        this.currentPage = currentPage;

        fab.setColorNormal(Color.parseColor("#ffffff"));
        fab.setColorPressed(ScreenUtils.equivalentNoAlpha(Color.parseColor("#ffffff"), 0.5f));

//        fab.setColorNormalResId(getFabColor(currentPage));
//        fab.setColorPressedResId(getFabPressedColor(currentPage));

        switch (currentPage) {

            case 0:
                for (FloatingActionButton f : scheduleActions) {
                    f.setVisibility(View.VISIBLE);
                }
                fab.setVisibility(View.GONE);
                fabMenu.setVisibility(View.VISIBLE);
                fabMenu.bringToFront();
                break;
            case 1:
            case 2:
                for (FloatingActionButton f : scheduleActions) {
                    f.setVisibility(View.GONE);
                }
                fabMenu.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                fab.bringToFront();
                break;
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.add_button:
                onClickAdd();
                break;

            // schedules
            case R.id.action_a:
//                startSchedulesActivity(ScheduleTypeFragment.TYPE_ROUTINES);
                break;
            case R.id.action_b:
//                startSchedulesActivity(ScheduleTypeFragment.TYPE_HOURLY);
                break;
            case R.id.action_c:
//                startSchedulesActivity(ScheduleTypeFragment.TYPE_PERIOD);
                break;
            case R.id.action_d:
                startScanActivity();
                break;

        }


    }

    private void onClickAdd() {
        switch (currentPage) {
            case 0:
                return;
            case 1:
//                launchActivity(RoutinesActivity.class);
                break;
            case 2:
//                launchActivity(PlansActivity.class);
                break;
        }
    }

    private void launchActivity(Class<?> type) {
        activity.startActivity(new Intent(activity, type));
        activity.overridePendingTransition(0, 0);
    }

    private void startSchedulesActivity(int scheduleType) {
//        Intent i = new Intent(activity, ScheduleCreationActivity.class);
//        i.putExtra("scheduleType", scheduleType);
//        activity.startActivity(i);
//        activity.overridePendingTransition(0, 0);
//        fabMenu.collapse();
    }

    private void startScanActivity() {
//        Intent i = new Intent(activity, ScanActivity.class);
//        i.putExtra("after_scan_pkg", activity.getPackageName());
//        i.putExtra("after_scan_cls", ConfirmSchedulesActivity.class.getName());
//        activity.startActivity(i);
//        activity.overridePendingTransition(0, 0);
//        fabMenu.collapse();
    }

    public int getFabColor(int page) {
        switch (page) {
            case 1:
                return R.color.android_orange;
            case 2:
                return R.color.android_blue_darker;
            case 3:
                return R.color.android_green;
            default:
                return R.color.android_blue_darker;
        }
    }

    public int getFabPressedColor(int page) {
        switch (page) {
            case 1:
                return R.color.android_orange_dark;
            case 2:
                return R.color.android_blue_dark;
            case 3:
                return R.color.android_green_dark;
            default:
                return R.color.android_blue_dark;
        }
    }

    public void onUserUpdate(DBUser u) {
        for (FloatingActionButton f : scheduleActions) {
            f.setColorNormal(u.color());
            f.setColorPressed(ScreenUtils.equivalentNoAlpha(u.color(), 0.5f));
        }
        fabMenu.invalidate();
    }

    public void onPharmacyModeChanged(boolean enabled) {
        if (enabled && !scheduleActions.contains(scanQrAction)) {
            scheduleActions.add(scanQrAction);
            scanQrAction.setVisibility(View.VISIBLE);
        } else if (!enabled && scheduleActions.contains(scanQrAction)) {
            scanQrAction.setVisibility(View.GONE);
            scheduleActions.remove(scanQrAction);
        }
        fabMenu.invalidate();
        onViewPagerItemChange(currentPage);
    }

}
