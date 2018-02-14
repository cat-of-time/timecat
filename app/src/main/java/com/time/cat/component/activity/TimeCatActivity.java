package com.time.cat.component.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shang.commonjar.contentProvider.SPHelper;
import com.time.cat.NetworkSystem.RetrofitHelper;
import com.time.cat.R;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.mvp.presenter.ActivityPresenter;
import com.time.cat.mvp.view.TimeCatLayout;
import com.time.cat.mvp.view.TimeCatLayoutWrapper;
import com.time.cat.util.ClipboardUtils;
import com.time.cat.util.ColorUtil;
import com.time.cat.util.ConstantUtil;
import com.time.cat.util.LogUtil;
import com.time.cat.util.RegexUtil;
import com.time.cat.util.SearchEngineUtil;
import com.time.cat.util.SharedIntentHelper;
import com.time.cat.util.ToastUtil;
import com.time.cat.util.UrlCountUtil;
import com.time.cat.util.ViewUtil;
import com.time.cat.util.onestep.AppsAdapter;
import com.time.cat.util.onestep.ResolveInfoWrap;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author dlink
 * @date 2018/2/2
 */
public class TimeCatActivity extends BaseActivity implements ActivityPresenter, TimeCatLayoutWrapper.ActionListener {
    public static final String TO_SPLIT_STR = "to_split_str";
    private static final String DEVIDER = "__DEVIDER___DEVIDER__";
    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码-----------------------------------
    private TimeCatLayout timeCatLayout;
    private TimeCatLayoutWrapper timeCatLayoutWrapper;
    private ContentLoadingProgressBar loading;
    //</生命周期>-------------------------------------------------------------------------------------
    private SwipeMenuRecyclerView mAppsRecyclerView;
    private View mAppsRecyclerViewLL;
    private EditText toTrans;
    private EditText transResult;
    private RelativeLayout transRl;
    private EditText add_task_et_title;
    private EditText add_task_et_content;
    private RelativeLayout add_task_rl;
    private int alpha;
    private int lastPickedColor;
    private boolean remainSymbol = true;
    private String originString;
    private String mSelectText;
    private List<String> netWordSegments;

    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //<功能归类分区方法，必须调用>----------------------------------------------------------------
        initView();
        initData();
        initEvent();
        //</功能归类分区方法，必须调用>----------------------------------------------------------------
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        netWordSegments = null;
        originString = null;
        initView();
    }

    @Override
    public void onBackPressed() {
        if (timeCatLayoutWrapper != null && timeCatLayoutWrapper.getVisibility() == View.GONE) {
            boolean stickSharebar = SPHelper.getBoolean(ConstantUtil.IS_STICK_SHAREBAR, false);
            if (mAppsRecyclerViewLL != null) {
                mAppsRecyclerViewLL.setVisibility(stickSharebar ? View.VISIBLE : View.GONE);
            }
            timeCatLayoutWrapper.setVisibility(View.VISIBLE);
            if (transRl != null) {
                transRl.setVisibility(View.GONE);
            }
            if (add_task_rl != null) {
                add_task_rl.setVisibility(View.GONE);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void initView() {//必须调用
        boolean fullScreen = SPHelper.getBoolean(ConstantUtil.IS_FULL_SCREEN, false);
        initContentView(fullScreen);
        initTextString();
        initTimeCatView(fullScreen);
    }

    private void initContentView(boolean fullScreen) {
        alpha = SPHelper.getInt(ConstantUtil.TIMECAT_ALPHA, 100);
        lastPickedColor = SPHelper.getInt(ConstantUtil.TIMECAT_DIY_BG_COLOR, Color.parseColor("#fff7ca"));
        int value = (int) ((alpha / 100.0f) * 255);

        RegexUtil.refreshSymbolSelection();
        if (fullScreen) {
            setTheme(R.style.PreSettingTheme);
            setContentView(R.layout.activity_time_cat);
            getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.timecat_activity_window_full));
            getWindow().getDecorView().setBackgroundColor(Color.argb(value, Color.red(lastPickedColor), Color.green(lastPickedColor), Color.blue(lastPickedColor)));
            showAppList4OneStep();
        } else {
            CardView cardView = new CardView(this);
            cardView.setRadius(ViewUtil.dp2px(10));
            cardView.setCardBackgroundColor(Color.argb(value, Color.red(lastPickedColor), Color.green(lastPickedColor), Color.blue(lastPickedColor)));
            View view = LayoutInflater.from(this).inflate(R.layout.activity_time_cat, null, false);
            cardView.addView(view);

            getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.trans));
            setContentView(cardView);
        }
    }

    private void initTextString() {
        Intent intent = getIntent();
        String str = null;
        try {
            str = intent.getStringExtra(TO_SPLIT_STR);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(str)) {
            String action = intent.getAction();
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                    str = sharedText;
                }
            }
        }

        if (TextUtils.isEmpty(str)) {
            finish();
            return;
        }

        mSelectText = str;
        originString = str.replaceAll("@", " @ ");
    }

    private void initTimeCatView(boolean fullScreen) {
        remainSymbol = SPHelper.getBoolean(ConstantUtil.REMAIN_SYMBOL, true);
        boolean stickHeader = SPHelper.getBoolean(ConstantUtil.IS_STICK_HEADER, false);
        int text = SPHelper.getInt(ConstantUtil.TEXT_SIZE, ConstantUtil.DEFAULT_TEXT_SIZE);
        int line = SPHelper.getInt(ConstantUtil.LINE_MARGIN, ConstantUtil.DEFAULT_LINE_MARGIN);
        int item = SPHelper.getInt(ConstantUtil.ITEM_MARGIN, ConstantUtil.DEFAULT_ITEM_MARGIN);
        int padding = SPHelper.getInt(ConstantUtil.ITEM_PADDING, ViewUtil.dp2px(ConstantUtil.DEFAULT_ITEM_PADDING));


        timeCatLayout = findViewById(R.id.timecat);
        loading = findViewById(R.id.loading);
        timeCatLayoutWrapper = findViewById(R.id.timecat_wrap);


        loading.show();
        timeCatLayout.reset();
        timeCatLayoutWrapper.setVisibility(View.GONE);
        if (fullScreen) {
            timeCatLayoutWrapper.setFullScreenMode(true);
        }
        timeCatLayoutWrapper.setStickHeader(stickHeader);

        timeCatLayout.setTextSize(text);
        timeCatLayout.setLineSpace(line);
        timeCatLayout.setItemSpace(item);
        timeCatLayout.setTextPadding(padding);

        timeCatLayoutWrapper.setShowSymbol(remainSymbol);
        timeCatLayoutWrapper.setShowSection(SPHelper.getBoolean(ConstantUtil.REMAIN_SECTION, false));
        timeCatLayoutWrapper.setActionListener(this);
        timeCatLayoutWrapper.onSwitchType(SPHelper.getBoolean(ConstantUtil.DEFAULT_LOCAL, false));
    }

    private void showAppList4OneStep() {
        mAppsRecyclerView = findViewById(R.id.app_list);
        mAppsRecyclerViewLL = findViewById(R.id.app_list_ll);
        if (SPHelper.getBoolean(ConstantUtil.IS_STICK_SHAREBAR, true)) {
            mAppsRecyclerViewLL.setVisibility(View.VISIBLE);
            mAppsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            List<ResolveInfoWrap> addedItems = SharedIntentHelper.listFilterIntents(this);
            mAppsRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
            mAppsRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
            AppsAdapter appsAdapter = new AppsAdapter(this);
            appsAdapter.setItems(addedItems);
            appsAdapter.setOnItemClickListener(new AppsAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(ResolveInfoWrap item) {
                    if (!TextUtils.isEmpty(mSelectText)) {
                        try {
                            SharedIntentHelper.share(TimeCatActivity.this, item, mSelectText);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtil.show("请选择文字");
                    }

                }
            });
            mAppsRecyclerView.setLongPressDragEnabled(true);// 开启拖拽，就这么简单一句话。
            mAppsRecyclerView.setOnItemMoveListener(new OnItemMoveListener() {
                @Override
                public boolean onItemMove(int fromPosition, int toPosition) {
                    // 当Item被拖拽的时候。
                    Collections.swap(addedItems, fromPosition, toPosition);
                    appsAdapter.notifyItemMoved(fromPosition, toPosition);
                    SharedIntentHelper.saveShareAppIndexs2Sp(addedItems, TimeCatActivity.this);

                    return true;// 返回true表示处理了，返回false表示你没有处理。
                }

                @Override
                public void onItemDismiss(int position) {

                }
            });
            mAppsRecyclerView.setAdapter(appsAdapter);

        } else {
            mAppsRecyclerViewLL.setVisibility(View.GONE);
        }

    }

    private void showSegment(boolean isLocal) {
        loading.show();
        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_SWITCH_TYPE);
        timeCatLayout.reset();
        if (!isLocal) {
            if (netWordSegments == null) {
                getSegment(originString);
            } else {
                for (String t : netWordSegments) {
                    timeCatLayout.addTextItem(t);
                }
                loading.hide();
                timeCatLayoutWrapper.setVisibility(View.VISIBLE);
            }
        } else {
            List<String> txts = getLocalSegments(originString);
            for (String t : txts) {
                timeCatLayout.addTextItem(t);
            }
            loading.hide();
            timeCatLayoutWrapper.setVisibility(View.VISIBLE);
        }
    }
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码----------------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-------------------------------------------
    @Override
    public void initData() {//必须调用
    }

    private void getSegment(String str) {
        RetrofitHelper.getWordSegmentService().getWordSegsList(str).compose(this.bindToLifecycle()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).timeout(5000, TimeUnit.MILLISECONDS).subscribe(recommendInfo -> {
            LogUtil.d(recommendInfo.toString());
            List<String> txts = recommendInfo.get(0).getWord();
            netWordSegments = txts;
            timeCatLayout.reset();
            for (String t : txts) {
                timeCatLayout.addTextItem(t);
            }
            loading.hide();
            timeCatLayoutWrapper.setVisibility(View.VISIBLE);

            if (!SPHelper.getBoolean(ConstantUtil.HAD_SHOW_LONG_PRESS_TOAST, false)) {
                ToastUtil.show(R.string.bb_long_press_toast);
                SPHelper.save(ConstantUtil.HAD_SHOW_LONG_PRESS_TOAST, true);
            }
        }, throwable -> {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d(throwable.toString());
                    ToastUtil.show(R.string.no_internet_for_fenci);

                    timeCatLayoutWrapper.onSwitchType(true);
                }
            });

        });
    }

    @NonNull
    private List<String> getLocalSegments(String str) {
        List<String> txts = new ArrayList<String>();
        String s = "";
        for (int i = 0; i < str.length(); i++) {
            char first = str.charAt(i);
            //当到达末尾的时候
            if (i + 1 >= str.length()) {
                s = s + first;
                break;
            }
            char next = str.charAt(i + 1);
            if ((RegexUtil.isChinese(first) && !RegexUtil.isChinese(next)) || (!RegexUtil.isChinese(first) && RegexUtil.isChinese(next)) || (Character.isLetter(first) && !Character.isLetter(next)) || (Character.isDigit(first) && !Character.isDigit(next))) {
                s = s + first + DEVIDER;
            } else if (RegexUtil.isSymbol(first)) {
                s = s + DEVIDER + first + DEVIDER;
            } else {
                s = s + first;
            }
        }
        str = s;
        str.replace("\n", DEVIDER + "\n" + DEVIDER);
        String[] texts = str.split(DEVIDER);
        for (String text : texts) {
            if (text.equals(DEVIDER)) continue;
            //当首字母是英文字母时，默认该字符为英文
            if (RegexUtil.isEnglish(text)) {
                txts.add(text);
                continue;
            }
            if (RegexUtil.isNumber(text)) {
                txts.add(text);
                continue;
            }
            for (int i = 0; i < text.length(); i++) {
                txts.add(text.charAt(i) + "");
            }
        }
        return txts;
    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-------------------------------------------


    //<Event事件区>---只要存在事件监听代码就是-----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用
    }


    //-//<TimeCatLayoutWrapper.ActionListener>------------------------------------------------------
    @Override
    public void onSelected(String text) {
        mSelectText = text;
    }

    @Override
    public void onSearch(String text) {
        if (TextUtils.isEmpty(text)) {
            text = originString;
        }
        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_SEARCH);
        boolean isUrl = false;
        Uri uri = null;
        try {
            Pattern p = Pattern.compile("^((https?|ftp|news):\\/\\/)?([a-z]([a-z0-9\\-]*[\\.。])+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)|(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))(\\/[a-z0-9_\\-\\.~]+)*(\\/([a-z0-9_\\-\\.]*)(\\?[a-z0-9+_\\-\\.%=&]*)?)?(#[a-z][a-z0-9_]*)?$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(text);
            if (!matcher.matches()) {
                uri = Uri.parse(SearchEngineUtil.getInstance().getSearchEngines().get(SPHelper.getInt(ConstantUtil.BROWSER_SELECTION, 0)).url + URLEncoder.encode(text, "utf-8"));
                isUrl = false;
            } else {
                uri = Uri.parse(text);
                if (!text.startsWith("http")) {
                    text = "http://" + text;
                }
                isUrl = true;
            }

            boolean t = SPHelper.getBoolean(ConstantUtil.USE_LOCAL_WEBVIEW, true);
            Intent intent;
            if (t) {
                intent = new Intent();
                if (isUrl) {
                    intent.putExtra("url", text);
                } else {
                    intent.putExtra("query", text);
                }
                intent.setClass(TimeCatActivity.this, WebActivity.class);
                startActivity(intent);
            } else {
                intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent();
            if (isUrl) {
                intent.putExtra("url", text);
            } else {
                intent.putExtra("query", text);
            }
            intent.setClass(TimeCatActivity.this, WebActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onShare(String text) {
        if (TextUtils.isEmpty(text)) {
            text = originString;
        }
        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_SHARAE);
        SharedIntentHelper.sendShareIntent(TimeCatActivity.this, text);
    }

    @Override
    public void onTrans(String text) {
        if (mAppsRecyclerView != null) {
            mAppsRecyclerViewLL.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(text)) {
            text = originString;
        }
        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_TRANSLATE);

        if (transRl == null) {
            ViewStub viewStub = findViewById(R.id.trans_view_stub);
            viewStub.inflate();
            transRl = findViewById(R.id.trans_rl);
            toTrans = findViewById(R.id.to_translate);
            transResult = findViewById(R.id.translate_result);
            TextView title = findViewById(R.id.title);

            title.setTextColor(ColorUtil.getPropertyTextColor(lastPickedColor, alpha));
            toTrans.setTextColor(ColorUtil.getPropertyTextColor(lastPickedColor, alpha));
            transResult.setTextColor(ColorUtil.getPropertyTextColor(lastPickedColor, alpha));
            EditText editText = new EditText(this);
//设置EditText的显示方式为多行文本输入
            transResult.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//文本显示的位置在EditText的最上方
            transResult.setGravity(Gravity.TOP);
//改变默认的单行模式
            transResult.setSingleLine(false);
//水平滚动设置为False
            transResult.setHorizontallyScrolling(false);

            findViewById(R.id.translate_iv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (timeCatLayoutWrapper != null && timeCatLayoutWrapper.getVisibility() == View.GONE) {
                        boolean stickSharebar = SPHelper.getBoolean(ConstantUtil.IS_STICK_SHAREBAR, false);
                        if (mAppsRecyclerViewLL != null) {
                            mAppsRecyclerViewLL.setVisibility(stickSharebar ? View.VISIBLE : View.GONE);
                        }
                        timeCatLayoutWrapper.setVisibility(View.VISIBLE);
                        if (transRl != null) {
                            transRl.setVisibility(View.GONE);
                        }
                    }
                }
            });
            findViewById(R.id.trans_again).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(toTrans.getText())) {
                        translate(toTrans.getText().toString());
                    }
                    ViewUtil.hideInputMethod(toTrans);
                }
            });


        }
        translate(text);
    }

    private void translate(String text) {
        if (TextUtils.isEmpty(text)) {
            transResult.setText("");
            return;
        }
        timeCatLayoutWrapper.setVisibility(View.GONE);
        transRl.setVisibility(View.VISIBLE);
        toTrans.setText(text);
        toTrans.setSelection(text.length());
        transResult.setText("正在翻译");
        RetrofitHelper.getTranslationService().getTranslationItem(text.replaceAll("\n", "")).compose(TimeCatActivity.this.bindToLifecycle()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(recommendInfo -> {
            List<String> transes = recommendInfo.getTranslation();
            if (transes.size() > 0) {
                String trans = transes.get(0);
                transResult.setText(trans);
            }
            LogUtil.d(recommendInfo.toString());
        }, throwable -> {
            LogUtil.d(throwable.toString());
        });
    }

    @Override
    public void onAddTask(String text) {
        if (mAppsRecyclerView != null) {
            mAppsRecyclerViewLL.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(text)) {
            text = originString;
        }
        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_ADDTASK);

        if (add_task_rl == null) {
            ViewStub viewStub = findViewById(R.id.task_view_stub);
            viewStub.inflate();
            add_task_rl = findViewById(R.id.add_task_rl);
            add_task_et_title = findViewById(R.id.add_task_et_title);
            add_task_et_content = findViewById(R.id.add_task_et_content);
            TextView title = findViewById(R.id.add_task_tv_title);

            title.setTextColor(ColorUtil.getPropertyTextColor(lastPickedColor, alpha));
            add_task_et_title.setTextColor(ColorUtil.getPropertyTextColor(lastPickedColor, alpha));
            add_task_et_content.setTextColor(ColorUtil.getPropertyTextColor(lastPickedColor, alpha));

            findViewById(R.id.add_task_iv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (timeCatLayoutWrapper != null && timeCatLayoutWrapper.getVisibility() == View.GONE) {
                        boolean stickSharebar = SPHelper.getBoolean(ConstantUtil.IS_STICK_SHAREBAR, false);
                        if (mAppsRecyclerViewLL != null) {
                            mAppsRecyclerViewLL.setVisibility(stickSharebar ? View.VISIBLE : View.GONE);
                        }
                        timeCatLayoutWrapper.setVisibility(View.VISIBLE);
                        if (add_task_rl != null) {
                            add_task_rl.setVisibility(View.GONE);
                        }
                    }
                }
            });
            findViewById(R.id.add_task_iv_success).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewUtil.hideInputMethod(add_task_et_title);
                    ViewUtil.hideInputMethod(add_task_et_content);

                    if (!TextUtils.isEmpty(add_task_et_title.getText())) {
                        String title = add_task_et_title.getText().toString();
                        String content = add_task_et_content.getText().toString();
                        addTask(title, content);
                    } else {
                        ToastUtil.show("任务标题必须非空！");
                    }
                }
            });
        }
        timeCatLayoutWrapper.setVisibility(View.GONE);
        add_task_rl.setVisibility(View.VISIBLE);

        add_task_et_title.setText(text);
        add_task_et_content.setText(text);
        add_task_et_content.setSelection(text.length());
    }

    private void addTask(String title, String content) {
        ToastUtil.show("添加任务成功");
        finish();
//        ToastUtil.show("添加任务失败");
    }

    @Override
    public void onCopy(String text) {
        if (TextUtils.isEmpty(text)) {
            text = originString;
        }
        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_COPY);

        Intent intent = new Intent(ConstantUtil.BROADCAST_SET_TO_CLIPBOARD);
        intent.putExtra(ConstantUtil.BROADCAST_SET_TO_CLIPBOARD_MSG, text);
        sendBroadcast(intent);
        String finalText = text;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ClipboardUtils.setText(getApplicationContext(), finalText);
                ToastUtil.show("已复制");
                finish();
            }
        }, 100);
    }

    @Override
    public void onDrag() {
        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_DRAG);
    }

    @Override
    public void onSwitchType(boolean isLocal) {
        showSegment(isLocal);
    }

    @Override
    public void onSwitchSymbol(boolean isShow) {
        SPHelper.save(ConstantUtil.REMAIN_SYMBOL, isShow);
        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_REMAIN_SYMBOL);
    }

    @Override
    public void onSwitchSection(boolean isShow) {
        SPHelper.save(ConstantUtil.REMAIN_SECTION, isShow);
        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_REMAIN_SECTION);
    }

    @Override
    public void onDragSelection() {
        UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_DRAG_SELECTION);

    }
    //-//</TimeCatLayoutWrapper.ActionListener>-----------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是----------------------------------------------------------
}
