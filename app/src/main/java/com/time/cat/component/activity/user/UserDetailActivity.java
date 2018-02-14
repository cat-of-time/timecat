package com.time.cat.component.activity.user;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.nispok.snackbar.Snackbar;
import com.time.cat.R;
import com.time.cat.ThemeSystem.manager.ThemeManager;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.database.DB;
import com.time.cat.mvp.model.DBmodel.DBUser;
import com.time.cat.mvp.presenter.ActivityPresenter;
import com.time.cat.util.AvatarMgr;
import com.time.cat.util.ScreenUtils;
import com.time.cat.util.Snack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dlink
 * @date 2018/2/2
 * @discription 用户信息Activity
 */
public class UserDetailActivity extends BaseActivity implements ActivityPresenter, GridView.OnItemClickListener {
    public static final String[] COLORS = new String[]{"#1abc9c", "#16a085", "#f1c40f", "#f39c12", "#2ecc71", "#27ae60", "#e67e22", "#d35400", "#c0392b", "#e74c3c", "#2980b9", "#3498db", "#9b59b6", "#8e44ad", "#2c3e50", "#34495e"};
    private static final String TAG = "UserDetailActivity";
    //</生命周期>------------------------------------------------------------------------------------
    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码-----------------------------------
    GridView avatarGrid;
    BaseAdapter adapter;
    DBUser user;

    ImageView userAvatar;
    View userAvatarBg;
    RelativeLayout gridContainer;

    View top;
    View bg;
    EditText userName;
    List<String> avatars = new ArrayList<>(AvatarMgr.avatars.keySet());

    TextView userEmail;
    FloatingActionButton fab;
    int color1;
    int color2;
    Drawable iconClose;
    Drawable iconSwitch;
    LinearLayout colorList;
    HorizontalScrollView colorScroll;
    Button linkButton;
    String token = null;
    long userId;
    private Menu menu;
    private int avatarBackgroundColor;

    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        //<功能归类分区方法，必须调用>-----------------------------------------------------------------
        initView();
        initData();
        initEvent();
        //</功能归类分区方法，必须调用>----------------------------------------------------------------
    }

    @Override
    public void initView() {//必须调用
        setStatusBarFullTransparent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ThemeManager.getTheme(this));
            Log.e(TAG, "setStatusBarColor");
        }

        top = findViewById(R.id.top);
        bg = findViewById(R.id.bg);
        userAvatar = findViewById(R.id.user_avatar);
        userAvatarBg = findViewById(R.id.user_avatar_bg);
        gridContainer = findViewById(R.id.grid_container);

        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_detail_email);

        fab = findViewById(R.id.avatar_change);
        avatarGrid = findViewById(R.id.grid);
        colorScroll = findViewById(R.id.colorScroll);
        linkButton = findViewById(R.id.linkButton);

        avatarGrid.setVisibility(View.VISIBLE);
        gridContainer.setVisibility(View.GONE);

        iconClose = new IconicsDrawable(this, CommunityMaterial.Icon.cmd_close).sizeDp(24).paddingDp(5).colorRes(R.color.dark_grey_home);

        iconSwitch = new IconicsDrawable(this, CommunityMaterial.Icon.cmd_account_switch).sizeDp(24).paddingDp(2).colorRes(R.color.fab_light_normal);

        getUser();
        setSwitchFab();
        setupToolbar(user.name(), Color.TRANSPARENT);
        setupStatusBar(Color.TRANSPARENT);
        setupAvatarList();
        setupColorChooser();
        loadUser();
    }

    private void getUser() {
        userId = getIntent().getLongExtra("user_id", -1);
        lookForQrData(getIntent());

        if (userId != -1) {
            user = DB.users().findById(userId);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            token = prefs.getString("remote_token" + userId, null);
            if (token != null) {
                linkButton.setVisibility(View.VISIBLE);
                linkButton.setText("取消链接");
                linkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showUnlinkUserDialog();
                    }
                });

            } else {
                linkButton.setVisibility(View.GONE);
            }


        } else {
            linkButton.setVisibility(View.GONE);
            user = new DBUser();
        }
    }

    private void lookForQrData(Intent i) {
        String qrData = i.getStringExtra("qr_data");
        if (qrData != null) {
            UserLinkWrapper userLinkWrapper = new Gson().fromJson(qrData, UserLinkWrapper.class);
            Snack.show("用户关联成功！!", this, Snackbar.SnackbarDuration.LENGTH_LONG);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(UserDetailActivity.this);
            prefs.edit().putString("remote_token" + userId, userLinkWrapper.token).commit();
            Log.d("PatDetail", userLinkWrapper.toString());
        }
    }

    private void showUnlinkUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Si desvinculas este usuario se interrumpirá el seguimiento. Estás seguro de que deseas continuar?").setCancelable(true).setTitle("Ten cuidado").setPositiveButton("Si, desvincular", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                token = null;
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(UserDetailActivity.this);
                prefs.edit().remove("remote_token" + userId).commit();
                linkButton.setText("Vincular");
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setupColorChooser() {

        colorList = findViewById(R.id.color_chooser);
        colorList.removeAllViews();

        for (final String hex : COLORS) {
            ImageView colorView = (ImageView) getLayoutInflater().inflate(R.layout.item_color_chooser, null);
            final int color = Color.parseColor(hex);
            colorView.setBackgroundColor(color);
            colorView.setPadding(2, 2, 2, 2);
            if (color == user.color()) {
                colorView.setImageDrawable(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_checkbox_marked_circle).paddingDp(30).color(Color.WHITE).sizeDp(80));
            } else {
                colorView.setImageDrawable(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_checkbox_marked_circle).paddingDp(30).color(Color.TRANSPARENT).sizeDp(80));
            }
            colorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.setColor(color);
                    setupColorChooser();
                    int x = (int) view.getX() + view.getWidth() / 2 - colorScroll.getScrollX();
                    updateAvatar(user.avatar(), 1, 400, x);
                    colorList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollToColor(color);
                        }
                    }, 300);
                    //Toast.makeText(getBaseContext(), "Color: " + hex, Toast.LENGTH_SHORT).show();
                }
            });
            colorList.addView(colorView);
        }
    }

    private void scrollToColor(int color) {
        int index = 0;
        for (int i = 0; i < COLORS.length; i++) {
            if (color == Color.parseColor(COLORS[i])) {
                index = i;
                break;
            }
        }
        int width = colorList.getChildAt(0).getWidth();
        colorScroll.smoothScrollTo(width * index + width / 2 - colorScroll.getWidth() / 2, 0);
    }

    private void setSwitchFab() {
        fab.setColorNormalResId(R.color.fab_dark_normal);
        fab.setColorPressedResId(R.color.fab_dark_pressed);
        fab.setIconDrawable(iconSwitch);
    }

    private void setCloseFab() {
        fab.setColorNormalResId(R.color.fab_light_normal);
        fab.setColorPressedResId(R.color.fab_light_pressed);
        fab.setIconDrawable(iconClose);
    }

    private void setupAvatarList() {
        adapter = new UserAvatarsAdapter(this);
        avatarGrid.setAdapter(adapter);
        avatarGrid.setOnItemClickListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gridContainer.getVisibility() == View.VISIBLE) hideAvatarSelector();
                else showAvatarSelector();
            }
        });
    }

    private void hideAvatarSelector() {
        animateAvatarSelectorHide(200);
    }

    private void showAvatarSelector() {
        setCloseFab();
        animateAvatarSelectorShow(300);
    }

    private void animateAvatarSelectorShow(int duration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gridContainer.setVisibility(View.INVISIBLE);
            // get the center for the clipping circle
            int cx = (int) fab.getX() + fab.getWidth() / 2;
            int cy = 0;
            // get the final radius for the clipping circle
            int finalRadius = (int) Math.hypot(userAvatarBg.getWidth(), bg.getHeight() - userAvatarBg.getHeight());
            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(gridContainer, cx, cy, 0, finalRadius);
            anim.setInterpolator(new DecelerateInterpolator());
            // make the view visible and start the animation
            gridContainer.setVisibility(View.VISIBLE);
            anim.setDuration(duration).start();
            gridContainer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollToColor(user.color());
                }
            }, duration);
        }
    }

    private void animateAvatarSelectorHide(int duration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            int cx = (int) fab.getX() + fab.getWidth() / 2;
            int cy = 0;
            // get the final radius for the clipping circle
            int finalRadius = (int) Math.hypot(userAvatarBg.getWidth(), bg.getHeight() - userAvatarBg.getHeight());
            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(gridContainer, cx, cy, finalRadius, 0);
            // make the view visible and start the animation
            anim.setInterpolator(new AccelerateInterpolator());
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    gridContainer.setVisibility(View.GONE);
                    setSwitchFab();
                    super.onAnimationEnd(animation);
                }
            });
            anim.setDuration(duration).start();
        }
    }

    private void animateAvatarBg(int duration, int x, Animator.AnimatorListener cb) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            userAvatarBg.setVisibility(View.INVISIBLE);
            // get the center for the clipping circle
            int cx = (userAvatarBg.getLeft() + userAvatarBg.getRight()) / 2;
            int cy = userAvatarBg.getBottom();

            // get the final radius for the clipping circle
            int finalRadius = (int) Math.hypot(userAvatarBg.getWidth(), userAvatarBg.getHeight());

            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(userAvatarBg, x, cy, ScreenUtils.dpToPx(getResources(), 100f), finalRadius);
            // make the view visible and start the animation
            userAvatarBg.setVisibility(View.VISIBLE);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(duration);
            if (cb != null) {
                anim.addListener(cb);
            }
            anim.start();
        }
    }

    private void loadUser() {
        userName.setText(user.name());
        userEmail.setText(user.getEmail());
        top.setBackgroundColor(user.color());
        updateAvatar(user.avatar(), 400, 400, userAvatar.getWidth() / 2);
    }

    private void updateAvatar(String avatar, int delay, final int duration, final int x) {
        userAvatar.setImageResource(AvatarMgr.res(avatar));
        color1 = user.color();
        color2 = ScreenUtils.equivalentNoAlpha(color1, 0.7f);
        avatarBackgroundColor = color1;
        gridContainer.setBackgroundColor(getResources().getColor(R.color.dark_grey_home));
        ScreenUtils.setStatusBarColor(this, avatarBackgroundColor);
        Log.e(TAG, "setStatusBarColor");

        if (delay > 0) {
            userAvatarBg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    userAvatarBg.setBackgroundColor(avatarBackgroundColor);
                    animateAvatarBg(duration, x, new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            top.setBackgroundColor(avatarBackgroundColor);
                        }
                    });
                }
            }, delay);
        } else {
            userAvatarBg.setBackgroundColor(avatarBackgroundColor);
        }

    }
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>--------------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码--------------------------------------------
    @Override
    public void initData() {//必须调用

    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-------------------------------------------


    //<Event事件区>---只要存在事件监听代码就是-----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用
    }

    //-//<Activity>---------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        userAvatarBg.setVisibility(View.INVISIBLE);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_detail, menu);
        this.menu = menu;

        //if(token != null){
        this.menu.getItem(0).setVisible(false);
        //}

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                userAvatarBg.setVisibility(View.INVISIBLE);
                supportFinishAfterTransition();
                return true;

            case R.id.action_done:

                String text = userName.getText().toString().trim();

                if (!TextUtils.isEmpty(text) && !text.equals(user.name())) {
                    user.setName(text);
                }

                if (!TextUtils.isEmpty(user.name())) {
                    DB.users().saveAndFireEvent(user);
                    refreshTheme(this, user.color());
                    supportFinishAfterTransition();
                } else {
                    Snack.show("fail! plz try again!", this);
                }
                return true;

            case R.id.action_link_qr:
                if (token == null) {
                    startScanActivity();
                } else {
                    showUnlinkUserDialog();
                }
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void startScanActivity() {
//        Intent i = new Intent(this, ScanActivity.class);
//        i.putExtra("after_scan_pkg", getPackageName());
//        i.putExtra("after_scan_cls", UserDetailActivity.class.getName());
//        i.putExtra("user_id", userId);
//        this.startActivity(i);
//        this.overridePendingTransition(0, 0);
//        finish();
    }
    //-//</Activity>--------------------------------------------------------------------------------

    //-//</GridView.OnItemClickListener>------------------------------------------------------------
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String avatar = avatars.get(position);
        user.setAvatar(avatar);
        updateAvatar(avatar, 0, 0, userAvatar.getWidth() / 2);
        adapter.notifyDataSetChanged();

    }
    //-//</GridView.OnItemClickListener>------------------------------------------------------------


    //</Event事件区>---只要存在事件监听代码就是----------------------------------------------------------


    //<回调接口>-------------------------------------------------------------------------------------
    //</回调接口>------------------------------------------------------------------------------------


    //<内部类>---尽量少用-----------------------------------------------------------------------------
    private class UserAvatarsAdapter extends BaseAdapter {

        private Context context;

        public UserAvatarsAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return avatars.size();
        }

        @Override
        public Object getItem(int position) {
            return avatars.get(position);
        }

        @Override
        public long getItemId(int position) {
            return avatars.get(position).hashCode();
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ImageView v;
            String avatar = avatars.get(position);
            int resource = AvatarMgr.res(avatar);
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_avatar_list, viewGroup, false);
            }

            v = view.findViewById(R.id.imageView);
            v.setImageResource(resource);

            if (avatar.equals(user.avatar())) {
                v.setBackgroundResource(R.drawable.avatar_list_item_bg);
            } else {
                v.setBackgroundResource(R.color.transparent);
            }
            return view;
        }
    }

    public class UserLinkWrapper {
        public String name;
        public String id;
        public String token;

        @Override
        public String toString() {
            return "UserLinkWrapper{" + "name='" + name + '\'' + ", id='" + id + '\'' + ", token='" + token + '\'' + '}';
        }
    }
    //</内部类>---尽量少用----------------------------------------------------------------------------
}
