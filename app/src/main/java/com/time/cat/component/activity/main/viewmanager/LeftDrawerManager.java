package com.time.cat.component.activity.main.viewmanager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.time.cat.AnimationSystem.ViewHelper;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.component.activity.AboutActivity;
import com.time.cat.component.activity.main.MainActivity;
import com.time.cat.component.activity.setting.SettingActivity;
import com.time.cat.component.activity.user.LoginActivity;
import com.time.cat.component.activity.user.UserDetailActivity;
import com.time.cat.component.dialog.DialogThemeFragment;
import com.time.cat.database.DB;
import com.time.cat.mvp.model.DBmodel.DBUser;
import com.time.cat.util.AvatarMgr;
import com.time.cat.util.IconUtils;
import com.time.cat.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dlink
 * @date 2018/2/3
 * @discription 侧滑栏
 */
public class LeftDrawerManager implements Drawer.OnDrawerItemClickListener, AccountHeader.OnAccountHeaderListener {
    public static final int HOME = -1;
    public static final int USER_ADD = -2;
    public static final int SCHEDULES = 0;
    public static final int ROUTINES = 1;
    public static final int PLANS = 2;
    public static final int MEDICINES = 3;
    public static final int USERS = 4;
    public static final int THEME = 5;
    public static final int SETTINGS = 6;
    public static final int ABOUT = 7;
    public static final int PHARMACIES = 8;
    public static final int CALENDAR = 9;
    private static final String TAG = "LeftDrawerManager";
    //-//<Drawer.OnDrawerItemClickListener>---------------------------------------------------------
    private static final int REQUEST_LOGIN = 0;
    private AccountHeader headerResult = null;
    private Drawer drawer = null;
    private Toolbar toolbar;
    private MainActivity mainActivity;
    private DBUser currentUser;

    public LeftDrawerManager(MainActivity activity, Toolbar toolbar) {
        this.toolbar = toolbar;
        this.mainActivity = activity;
    }

    public void init(Bundle savedInstanceState) {

        boolean isPharmaEnabled = TimeCatApp.isPharmaModeEnabled(mainActivity);

        ArrayList<IProfile> profiles = new ArrayList<>();
        profiles.add(new ProfileSettingDrawerItem().withName("添加用户").withDescription("管理他人的指导方针").withIcon(new IconicsDrawable(mainActivity, GoogleMaterial.Icon.gmd_account_add).sizeDp(24).paddingDp(5).colorRes(R.color.dark_grey_home)).withIdentifier(USER_ADD));
        for (DBUser p : DB.users().findAll()) {
            Log.d("LeftDrawer", "Adding user to getDrawer: " + p.name());
            profiles.add(new ProfileDrawerItem().withIdentifier(p.id().intValue()).withName(p.name()).withEmail(p.getEmail()).withIcon(AvatarMgr.res(p.avatar())));
        }
//        DBUser u = new DBUser();
//        u.setAvatar(AvatarMgr.AVATAR_2);
//        u.setColor(Color.YELLOW);
//        u.setName("测试");
//        u.setId((long) 5);
//        Log.e(TAG, "Adding user to getDrawer: " + u.name());
//        profiles.add(new ProfileDrawerItem()
//                .withIdentifier(u.id().intValue())
//                .withName(u.name())
//                .withEmail(u.name() + "@timecat")
//                .withIcon(AvatarMgr.res(u.avatar())));

        headerResult = new AccountHeaderBuilder().withActivity(mainActivity).withHeaderBackground(R.drawable.drawer_header).withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP).withCompactStyle(false).withProfiles(profiles).withAlternativeProfileHeaderSwitching(true).withThreeSmallProfileImages(true).withOnAccountHeaderListener(this).withSavedInstance(savedInstanceState).build();

        //Create the getDrawer
        drawer = new DrawerBuilder().withActivity(mainActivity).withFullscreen(true).withToolbar(toolbar).withAccountHeader(headerResult).addDrawerItems(new PrimaryDrawerItem().withName(R.string.title_activity_home).withIcon(IconUtils.icon(mainActivity, GoogleMaterial.Icon.gmd_home, R.color.black).alpha(110)).withIdentifier(HOME), new PrimaryDrawerItem().withName(R.string.title_activity_users).withIcon(IconUtils.icon(mainActivity, CommunityMaterial.Icon.cmd_account_multiple, R.color.black).alpha(110)).withIdentifier(USERS), new DividerDrawerItem(), new PrimaryDrawerItem().withName(R.string.title_activity_schedules).withIcon(IconUtils.icon(mainActivity, GoogleMaterial.Icon.gmd_calendar, R.color.black).alpha(110)).withIdentifier(SCHEDULES), new PrimaryDrawerItem().withName(R.string.title_activity_routines).withIcon(IconUtils.icon(mainActivity, GoogleMaterial.Icon.gmd_alarm, R.color.black).alpha(110)).withIdentifier(ROUTINES), new PrimaryDrawerItem().withName(R.string.title_activity_plans).withIcon(IconUtils.icon(mainActivity, GoogleMaterial.Icon.gmd_airplanemode_active, R.color.black).alpha(110)).withIdentifier(PLANS), new PrimaryDrawerItem().withName(R.string.title_activity_medicines).withEnabled(false).withIcon(IconUtils.icon(mainActivity, CommunityMaterial.Icon.cmd_pill, R.color.black).alpha(38)).withIdentifier(MEDICINES), new PrimaryDrawerItem().withName(R.string.home_menu_pharmacies).withIcon(IconUtils.icon(mainActivity, CommunityMaterial.Icon.cmd_map_marker_multiple, R.color.black).alpha(38)).withEnabled(false).withIdentifier(PHARMACIES), new DividerDrawerItem(), new PrimaryDrawerItem().withName(R.string.drawer_theme_option).withIcon(IconUtils.icon(mainActivity, GoogleMaterial.Icon.gmd_pin_assistant, R.color.black).alpha(110)).withIdentifier(THEME), new PrimaryDrawerItem().withName(R.string.drawer_settings_option).withIcon(IconUtils.icon(mainActivity, CommunityMaterial.Icon.cmd_settings, R.color.black).alpha(130)).withIdentifier(SETTINGS), new DividerDrawerItem(), new PrimaryDrawerItem().withName(R.string.drawer_about_option).withIcon(IconUtils.icon(mainActivity, CommunityMaterial.Icon.cmd_information, R.color.black).alpha(110)).withIdentifier(ABOUT)).withOnDrawerItemClickListener(this).withOnDrawerListener(new Drawer.OnDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = drawer.getDrawerLayout().getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
//                        float rightScale = 0.8f + scale * 0.2f;
//                        float leftScale = 1 - 0.3f * scale;
//        Log.e(TAG, "\nscale:" + scale + "\nleftScale:" + leftScale + "\nrightScale:" + rightScale);

//                        ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                ViewHelper.setTranslationX(mContent, mMenu.getMeasuredWidth() * (1 - scale));

                mContent.invalidate();
            }
        }).withDelayOnDrawerClose(0).withStickyFooterShadow(true).withScrollToTopAfterClick(true).withSliderBackgroundColor(Color.TRANSPARENT).withSavedInstance(savedInstanceState).build();

        DBUser u = DB.users().getActive(mainActivity);
        headerResult.setActiveProfile(u.id().intValue(), false);
        updateHeaderBackground(u);
        drawer.setStatusBarColor(u.color());
        drawer.getDrawerLayout().setClipToPadding(true);
        drawer.getDrawerLayout().setFitsSystemWindows(false);

        onPharmacyModeChanged(isPharmaEnabled);

    }

    @Override
    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {

        int identifier = iDrawerItem.getIdentifier();

        switch (identifier) {
            case HOME:
//                mainActivity.showPagerItem(0, false);
                break;
            case USERS:
                Intent intent = new Intent(mainActivity, LoginActivity.class);
                mainActivity.startActivityForResult(intent, REQUEST_LOGIN);
                mainActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                launchActivity(new Intent(mainActivity, UsersActivity.class));
                drawer.setSelection(HOME, false);
                break;

            case SCHEDULES:
                mainActivity.showPagerItem(0, false);
                break;
            case ROUTINES:
                mainActivity.showPagerItem(1, false);
                break;
            case PLANS:
                mainActivity.showPagerItem(2, false);
                break;
            case MEDICINES:
//                mainActivity.showPagerItem(2, false);
                break;
            case CALENDAR:
//                launchActivity(new Intent(mainActivity, CalendarActivity.class));
                drawer.setSelection(HOME, false);
                break;

            case THEME:
                //mainActivity.showTutorial();
//                launchActivity(new Intent(mainActivity, MaterialIntroActivity.class));
                DialogThemeFragment themeDialog = new DialogThemeFragment();
                themeDialog.setClickListener(mainActivity);
                themeDialog.show(mainActivity.getSupportFragmentManager(), "theme");
                drawer.setSelection(HOME, false);
                break;
            case SETTINGS:
                launchActivity(new Intent(mainActivity, SettingActivity.class));
                drawer.setSelection(HOME, false);
                break;

            case ABOUT:
                launchActivity(new Intent(mainActivity, AboutActivity.class));
                drawer.setSelection(HOME, false);
                break;
            default:
                return false;
        }
        drawer.closeDrawer();
        return true;
    }
    //-//</Drawer.OnDrawerItemClickListener>--------------------------------------------------------


    public void onPharmacyModeChanged(boolean enabled) {
        PrimaryDrawerItem item = (PrimaryDrawerItem) drawer.getDrawerItem(PHARMACIES);
        BadgeStyle bs = new BadgeStyle();
        if (enabled) {
            addCalendarItem();
            Drawable bg = new IconicsDrawable(mainActivity).icon(GoogleMaterial.Icon.gmd_check).color(mainActivity.getResources().getColor(R.color.dark_grey_text)).sizeDp(18);
            bs.withBadgeBackground(bg);
        } else {
            drawer.removeItem(CALENDAR);
            bs.withBadgeBackground(new ColorDrawable(Color.TRANSPARENT));
        }
        item.withBadgeStyle(bs);
        item.withBadge(" ");
        drawer.updateItem(item);
    }

    public void onPagerPositionChange(int pagerPosition) {
        Log.d("LeftDrawer", "onPagerPositionChange: " + pagerPosition);
        switch (pagerPosition) {
            case 0:
                drawer.setSelection(SCHEDULES, false);
                break;
            case 1:
                drawer.setSelection(ROUTINES, false);
                break;
            case 2:
                drawer.setSelection(PLANS, false);
                break;
        }
    }

    @Override
    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
//
        if (profile instanceof ProfileSettingDrawerItem) {
            Intent intent = new Intent(mainActivity, UserDetailActivity.class);
            launchActivity(intent);
            return true;
        } else {
            Long id = (long) profile.getIdentifier();
            DBUser user = DB.users().findById(id);
            boolean isActive = DB.users().isActive(user, mainActivity);
            if (isActive) {
                Intent intent = new Intent(mainActivity, UserDetailActivity.class);
                intent.putExtra("user_id", id);
                launchActivity(intent);
            } else {
                DB.users().setActive(user, mainActivity);
                updateHeaderBackground(user);
            }
        }
        return false;
    }

    public void updateHeaderBackground(DBUser u) {
        currentUser = u;
        LayerDrawable layers = (LayerDrawable) headerResult.getHeaderBackgroundView().getDrawable();
        ColorDrawable color = (ColorDrawable) layers.findDrawableByLayerId(R.id.color_layer);
        color.setColor(ScreenUtils.equivalentNoAlpha(u.color(), 1f));
    }

    public Drawer getDrawer() {
        return drawer;
    }

    public AccountHeader header() {
        return headerResult;
    }

    public void onActivityResume(DBUser u) {

        currentUser = u;

        List<DBUser> users = DB.users().findAll();
        ArrayList<IProfile> profiles = headerResult.getProfiles();
        ArrayList<IProfile> toRemove = new ArrayList<>();
        if (users.size() != profiles.size()) {
            for (IProfile pr : profiles) {
                Long id = Long.valueOf(pr.getIdentifier());
                boolean remove = true;
                for (DBUser pat : users) {
                    if (pat.id().equals(id)) {
                        remove = false;
                        break;
                    }
                }
                if (remove) {
                    toRemove.add(pr);
                }
            }
            for (IProfile pr : toRemove) {
                headerResult.removeProfile(pr);
            }
        }

        headerResult.setActiveProfile(u.id().intValue(), false);

        if (u != null && !u.equals(currentUser) || header().getActiveProfile().getIcon().getIconRes() != AvatarMgr.res(u.avatar())) {
            headerResult.setActiveProfile(u.id().intValue(), false);
            IProfile profile = headerResult.getActiveProfile();
            profile.withIcon(AvatarMgr.res(u.avatar()));
            headerResult.updateProfile(profile);
        }
        updateHeaderBackground(u);
    }

    public void onUserCreated(DBUser u) {

        IProfile profile = genProfile(u);
        headerResult.addProfiles(profile);
    }

    public void onUserUpdated(DBUser u) {
        IProfile profile = genProfile(u);
        headerResult.updateProfile(profile);

    }

    private void addCalendarItem() {
        drawer.addItemAtPosition(new PrimaryDrawerItem().withName("Dispensación").withIcon(IconUtils.icon(mainActivity, CommunityMaterial.Icon.cmd_calendar_check, R.color.black).alpha(110)).withEnabled(true).withIdentifier(CALENDAR), 7);
    }

    private void launchActivity(Intent i) {
        mainActivity.startActivity(i);
        mainActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private IProfile genProfile(DBUser u) {
        return new ProfileDrawerItem().withIdentifier(u.id().intValue()).withName(u.name()).withEmail(u.getEmail()).withIcon(AvatarMgr.res(u.avatar()));
    }
}