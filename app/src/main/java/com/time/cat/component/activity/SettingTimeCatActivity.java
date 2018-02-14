package com.time.cat.component.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.shang.commonjar.contentProvider.SPHelper;
import com.shang.utils.StatusBarCompat;
import com.time.cat.R;
import com.time.cat.component.activity.share.ShareAppManagerActivity;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.component.base.baseCard.DividerItemDecoration;
import com.time.cat.mvp.view.TimeCatLayout;
import com.time.cat.mvp.view.TimeCatLayoutWrapper;
import com.time.cat.util.ConstantUtil;
import com.time.cat.util.UrlCountUtil;
import com.time.cat.util.ViewUtil;

/**
 * Created by penglu on 2016/11/9.
 */

public class SettingTimeCatActivity extends BaseActivity {
    private static final int TIMECAT_BACKGROUND_COLOR_ARRAY_RES = R.array.timecat_background_color;

    private static final int MIN_TEXT_SIZE = 8;
    private static final int MAX_TEXT_SIZE = 25;

    private static final int MIN_LINE_MARGIN = (int) ViewUtil.dp2px(0);
    private static final int MAX_LINE_MARGIN = (int) ViewUtil.dp2px(25);


    private static final int MIN_ITEM_MARGIN = (int) ViewUtil.dp2px(0);
    private static final int MAX_ITEM_MARGIN = (int) ViewUtil.dp2px(20);


    private static final int MIN_ITEM_PADDING = (int) ViewUtil.dp2px(2);
    private static final int MAX_ITEM_PADDING = (int) ViewUtil.dp2px(25);

    private CardView cardView;
    private TimeCatLayoutWrapper mTimeCatLayoutWrap;
    private TimeCatLayout mTimeCatLayout;
    TimeCatLayoutWrapper.ActionListener timeCatActionListener = new TimeCatLayoutWrapper.ActionListener() {

        @Override
        public void onSelected(String text) {
        }

        @Override
        public void onSearch(String text) {
        }

        @Override
        public void onShare(String text) {
        }

        @Override
        public void onCopy(String text) {
        }

        @Override
        public void onTrans(String text) {
        }

        @Override
        public void onAddTask(String text) {
        }

        @Override
        public void onDrag() {
            UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_DRAG);
        }

        @Override
        public void onSwitchType(boolean isLocal) {
            //
            UrlCountUtil.onEvent(UrlCountUtil.CLICK_TIMECAT_SWITCH_TYPE);
            mTimeCatLayout.reset();
            if (isLocal) {
                String[] txts = new String[]{"TimeCat", "可", "以", "进", "行", "文", "本", "分", "词", "。", "\n", "也", "支", "持", "复", "制", "翻", "译", "和", "调", "整", "…"};

                for (String t : txts) {
                    mTimeCatLayout.addTextItem(t);
                }
            } else {
                String[] txts = new String[]{"TimeCat", "可以", "进行", "文本", "分词", "。", "\n", "也", "支持", "复制", "翻译", "和", "调整", "…"};

                for (String t : txts) {
                    mTimeCatLayout.addTextItem(t);
                }
            }
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

        }
    };
    private SeekBar mTextSizeSeekBar;
    private SeekBar mLineMarginSeekBar;
    private SeekBar mItemMarginSeekBar;
    private SeekBar mItemPaddingSeekBar;
    private TextView textSize, lineMargin, itemMargin, itemPadding;
    private TextView timecatAlpha;
    private SeekBar mTimecatAlphaSeekBar;
    private CheckBox isFullScreen;
    private CheckBox isStickHeader;
    private CheckBox isDefaultLocal;
    private CheckBox autoAddBlanks;
    private CheckBox isBlankSymbol;
    private RecyclerView backgroundRV;
    private int[] timecatBackgroungColors;
    private int lastPickedColor;//只存rgb
    private int alpha;//只存alpha，0-100
    private CheckBox isStickSharebar;
    private RecyclerView.Adapter backgroundColorAdapter = new RecyclerView.Adapter() {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ColorVIewHolder(new TextView(SettingTimeCatActivity.this));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView;
            view.setMinimumHeight(ViewUtil.dp2px(40));
            if (position == timecatBackgroungColors.length) {
                view.setBackgroundColor(getResources().getColor(R.color.white));
                view.setText(R.string.set_background_myself);
                view.setTextColor(getResources().getColor(R.color.black));
                view.setTextSize(14);
                view.setGravity(Gravity.CENTER);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showColorPickDialog();
                        UrlCountUtil.onEvent(UrlCountUtil.CLICK_SET_BB_BGCOLOR_DIY);
                    }
                });
            } else {
                view.setText("");
                view.setBackgroundColor(timecatBackgroungColors[position]);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (timecatBackgroungColors.length > position) {
                            applyColor(timecatBackgroungColors[position]);
                            lastPickedColor = timecatBackgroungColors[position];
                            SPHelper.save(ConstantUtil.TIMECAT_DIY_BG_COLOR, timecatBackgroungColors[position]);
                            UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_BB_BGCOLOR, lastPickedColor + "");
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return timecatBackgroungColors.length + 1;
        }

        class ColorVIewHolder extends RecyclerView.ViewHolder {
            public ColorVIewHolder(View itemView) {
                super(itemView);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_timecat);

        StatusBarCompat.setupStatusBarView(this, (ViewGroup) getWindow().getDecorView(), true, R.color.colorPrimary);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.setting_timecat);


        mTimeCatLayout = findViewById(R.id.timecat);
        mTimeCatLayoutWrap = findViewById(R.id.timecat_wrap);
        cardView = findViewById(R.id.timecat_wraper);

        mTextSizeSeekBar = findViewById(R.id.set_text_size);
        mLineMarginSeekBar = findViewById(R.id.set_line_margin);
        mItemMarginSeekBar = findViewById(R.id.set_item_margin);
        mItemPaddingSeekBar = findViewById(R.id.set_item_padding);
        mTimecatAlphaSeekBar = findViewById(R.id.set_timecat_alpha);

        textSize = findViewById(R.id.text_size);
        lineMargin = findViewById(R.id.line_margin);
        itemMargin = findViewById(R.id.item_margin);
        itemPadding = findViewById(R.id.item_padding);
        timecatAlpha = findViewById(R.id.timecat_alpha);

        backgroundRV = findViewById(R.id.timecat_background);


        isFullScreen = findViewById(R.id.is_full_screen);
        isStickHeader = findViewById(R.id.is_stick_header);
        isStickSharebar = findViewById(R.id.is_stick_sharebar);
        isDefaultLocal = findViewById(R.id.is_default_local);
        autoAddBlanks = findViewById(R.id.auto_add_blanks);
        isBlankSymbol = findViewById(R.id.is_blank_symbol);


        mTextSizeSeekBar.setMax(MAX_TEXT_SIZE - MIN_TEXT_SIZE);
        mLineMarginSeekBar.setMax(MAX_LINE_MARGIN - MIN_LINE_MARGIN);
        mItemMarginSeekBar.setMax(MAX_ITEM_MARGIN - MIN_ITEM_MARGIN);
        mItemPaddingSeekBar.setMax(MAX_ITEM_PADDING - MIN_ITEM_PADDING);
        mItemMarginSeekBar.setMax(100);
        findViewById(R.id.setting_share_apps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SettingTimeCatActivity.this, ShareAppManagerActivity.class);
                startActivity(intent);
            }
        });

        mTextSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = MIN_TEXT_SIZE + progress;
                mTimeCatLayout.setTextSize(value);
                textSize.setText(getString(R.string.setting_text_size) + value);
                SPHelper.save(ConstantUtil.TEXT_SIZE, value);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_BB_TEXT_SIZE, value + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mLineMarginSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = MIN_LINE_MARGIN + progress;
                mTimeCatLayout.setLineSpace(value);
                lineMargin.setText(getString(R.string.setting_line_margin) + value);
                SPHelper.save(ConstantUtil.LINE_MARGIN, value);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_BB_LINE_MARGIN, value + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mItemMarginSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = MIN_ITEM_MARGIN + progress;
                mTimeCatLayout.setItemSpace(value);
                itemMargin.setText(getString(R.string.setting_item_margin) + value);
                SPHelper.save(ConstantUtil.ITEM_MARGIN, value);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_BB_ITEM_MARGIN, value + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mItemPaddingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = MIN_ITEM_PADDING + progress;
                mTimeCatLayout.setTextPadding(value);
                itemPadding.setText(getString(R.string.setting_item_padding) + value);
                SPHelper.save(ConstantUtil.ITEM_PADDING, value);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_BB_ITEM_PADDING, value + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTimecatAlphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress;
//                mTimeCatLayoutWrap.setBackgroundColorWithAlpha(lastPickedColor,value);
                cardView.setCardBackgroundColor(Color.argb((int) ((alpha / 100.0f) * 255), Color.red(lastPickedColor), Color.green(lastPickedColor), Color.blue(lastPickedColor)));
                timecatAlpha.setText(getString(R.string.setting_alpha_percent) + value + "%");
                SPHelper.save(ConstantUtil.TIMECAT_ALPHA, value);
                alpha = value;
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_BB_ALPHA, value + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        isFullScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPHelper.save(ConstantUtil.IS_FULL_SCREEN, isChecked);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_BB_FULL_SCREEN, isChecked + "");
            }
        });

        isStickHeader.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPHelper.save(ConstantUtil.IS_STICK_HEADER, isChecked);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_BB_STICK_HEAD, isChecked + "");
                mTimeCatLayoutWrap.setStickHeader(isChecked);
            }
        });

        isDefaultLocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPHelper.save(ConstantUtil.DEFAULT_LOCAL, isChecked);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_BB_DEFAULT_LOCAL, isChecked + "");
            }
        });
        autoAddBlanks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPHelper.save(ConstantUtil.AUTO_ADD_BLANKS, isChecked);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_BB_ADD_BLANKS, isChecked + "");
            }
        });
        isBlankSymbol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPHelper.save(ConstantUtil.TREAT_BLANKS_AS_SYMBOL, isChecked);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_BB_BLANKS_IS_SYMBOL, isChecked + "");
            }
        });
        isStickSharebar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPHelper.save(ConstantUtil.IS_STICK_SHAREBAR, isChecked);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_BB_STICK_SHAREBAR, isChecked + "");
            }
        });


        int text = SPHelper.getInt(ConstantUtil.TEXT_SIZE, ConstantUtil.DEFAULT_TEXT_SIZE);
        int line = SPHelper.getInt(ConstantUtil.LINE_MARGIN, ConstantUtil.DEFAULT_LINE_MARGIN);
        int item = SPHelper.getInt(ConstantUtil.ITEM_MARGIN, ConstantUtil.DEFAULT_ITEM_MARGIN);
        int padding = SPHelper.getInt(ConstantUtil.ITEM_PADDING, ViewUtil.dp2px(ConstantUtil.DEFAULT_ITEM_PADDING));
        alpha = SPHelper.getInt(ConstantUtil.TIMECAT_ALPHA, 100);
        lastPickedColor = SPHelper.getInt(ConstantUtil.TIMECAT_DIY_BG_COLOR, Color.parseColor("#fff7ca"));
        boolean fullScreen = SPHelper.getBoolean(ConstantUtil.IS_FULL_SCREEN, false);
        isFullScreen.setChecked(fullScreen);
        boolean stickHeader = SPHelper.getBoolean(ConstantUtil.IS_STICK_HEADER, false);
        isStickHeader.setChecked(stickHeader);
        boolean stickSharebar = SPHelper.getBoolean(ConstantUtil.IS_STICK_SHAREBAR, true);
        isStickSharebar.setChecked(stickSharebar);
        boolean remainSymbol = SPHelper.getBoolean(ConstantUtil.REMAIN_SYMBOL, true);


        boolean defaultLocal = SPHelper.getBoolean(ConstantUtil.DEFAULT_LOCAL, false);
        boolean addBlanks = SPHelper.getBoolean(ConstantUtil.AUTO_ADD_BLANKS, false);
        boolean blankIsSymbol = SPHelper.getBoolean(ConstantUtil.TREAT_BLANKS_AS_SYMBOL, true);


        mTextSizeSeekBar.setProgress((MIN_TEXT_SIZE));
        mLineMarginSeekBar.setProgress((MIN_LINE_MARGIN));
        mItemMarginSeekBar.setProgress((MIN_ITEM_MARGIN));
        mItemPaddingSeekBar.setProgress((MIN_ITEM_PADDING));

        mTextSizeSeekBar.setProgress((MAX_TEXT_SIZE));
        mLineMarginSeekBar.setProgress((MAX_LINE_MARGIN));
        mItemMarginSeekBar.setProgress((MAX_ITEM_MARGIN));
        mItemPaddingSeekBar.setProgress((MIN_ITEM_PADDING));

        mTextSizeSeekBar.setProgress((text - MIN_TEXT_SIZE));
        mLineMarginSeekBar.setProgress((line - MIN_LINE_MARGIN));
        mItemMarginSeekBar.setProgress((item - MIN_ITEM_MARGIN));
        mItemPaddingSeekBar.setProgress((padding - MIN_ITEM_PADDING));

        timecatAlpha.setText(getString(R.string.setting_alpha_percent) + alpha + "%");

//        mTimeCatLayoutWrap.setBackgroundColorWithAlpha(lastPickedColor,alpha);
        cardView.setCardBackgroundColor(Color.argb((int) ((alpha / 100.0f) * 255), Color.red(lastPickedColor), Color.green(lastPickedColor), Color.blue(lastPickedColor)));
        mTimecatAlphaSeekBar.setProgress(alpha);

        mTimeCatLayoutWrap.setStickHeader(stickHeader);
        mTimeCatLayoutWrap.setShowSymbol(remainSymbol);
        mTimeCatLayoutWrap.setShowSection(SPHelper.getBoolean(ConstantUtil.REMAIN_SECTION, false));
        mTimeCatLayoutWrap.setActionListener(timeCatActionListener);

        String[] txts = new String[]{"TimeCat", "可以", "进行", "文本", "分词", "。", "\n", "也", "支持", "复制", "翻译", "和", "调整", "…"};

        for (String t : txts) {
            mTimeCatLayout.addTextItem(t);
        }
        applyColor(lastPickedColor);

        isDefaultLocal.setChecked(defaultLocal);
        autoAddBlanks.setChecked(addBlanks);
        isBlankSymbol.setChecked(blankIsSymbol);
        timecatBackgroungColors = getResources().getIntArray(TIMECAT_BACKGROUND_COLOR_ARRAY_RES);
        backgroundRV.setLayoutManager(new GridLayoutManager(this, 4));
        backgroundRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.GRID_LIST));
        backgroundRV.setAdapter(backgroundColorAdapter);

        mTimeCatLayoutWrap.onSwitchType(defaultLocal);
    }

    private void applyColor(int color) {
//        mTimeCatLayoutWrap.setBackgroundColorWithAlpha(color,alpha);
        cardView.setCardBackgroundColor(Color.argb((int) ((alpha / 100.0f) * 255), Color.red(color), Color.green(color), Color.blue(color)));
    }

    private void applyColor(int color, int alpha) {
//        mTimeCatLayoutWrap.setBackgroundColorWithAlpha(color,alpha);
        cardView.setCardBackgroundColor(Color.argb((int) ((alpha / 100.0f) * 255), Color.red(color), Color.green(color), Color.blue(color)));
    }

    private void showColorPickDialog() {
        ColorPickerDialogBuilder.with(this).setTitle(R.string.set_background_myself).initialColor(lastPickedColor).wheelType(ColorPickerView.WHEEL_TYPE.FLOWER).density(12).setOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int selectedColor) {
                applyColor(Color.rgb(Color.red(selectedColor), Color.green(selectedColor), Color.blue(selectedColor)), (int) (Color.alpha(selectedColor) * 100.0 / 255));
            }
        }).setPositiveButton(R.string.confirm, new ColorPickerClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                lastPickedColor = Color.rgb(Color.red(selectedColor), Color.green(selectedColor), Color.blue(selectedColor));
                alpha = (int) (Color.alpha(selectedColor) * 100.0 / 255);
                applyColor(Color.rgb(Color.red(selectedColor), Color.green(selectedColor), Color.blue(selectedColor)));
                SPHelper.save(ConstantUtil.TIMECAT_DIY_BG_COLOR, lastPickedColor);
                UrlCountUtil.onEvent(UrlCountUtil.STATUS_SET_BB_BGCOLOR, lastPickedColor + "");
                mTimecatAlphaSeekBar.setProgress(alpha);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                applyColor(lastPickedColor);
            }
        }).showColorEdit(true).setColorEditTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright)).build().show();
    }

}
