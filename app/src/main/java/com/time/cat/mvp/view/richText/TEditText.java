package com.time.cat.mvp.view.richText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.autofill.AutofillValue;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.textclassifier.TextClassifier;
import android.widget.Scroller;

import com.time.cat.R;
import com.time.cat.util.LogUtil;
import com.time.cat.util.ScreenUtil;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/17
 * @discription null
 * @usage null
 */
public class TEditText extends AppCompatEditText {

    private static final String TAG = TEditText.class.getSimpleName().trim();

    private ArrayList<String> mTopicList = new ArrayList<>();

    private ArrayList<ForegroundColorSpan> mColorSpans = new ArrayList<>();
    private ArrayList<AbsoluteSizeSpan> mSizeSpans = new ArrayList<>();

    private TInputConnection inputConnection;

    // 默认的正则表达式
    private final String DEFAULT_REGEX = "\\s#[^#]*?\\[.*?\\|话题]#\\s";// 获取 标签内容 ,如 #天气[1|话题]#

    // 话题标签的文字大小
    private int textSizeTopic = 16;
    // 话题文字颜色 未选中
    private int textColorNormalTopic = Color.parseColor("#FFAA31");
    // 话题文字颜色 选中
    private int textColorSelectTopic = Color.parseColor("#50ffaa31");

    // 标签匹配的正则表达式
    private String topicRegex;

    // 是否是删除操作
    private boolean isDel = false;

    private OnSelectionChanged selectionChanged;

    public TEditText(Context context) {
        this(context, null);
    }

    public TEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 获取自定义属性
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.TEditText);
        textSizeTopic = t.getDimensionPixelOffset(R.styleable.TEditText_text_size_topic, ScreenUtil.dip2px(context, textSizeTopic));
        textColorNormalTopic = t.getColor(R.styleable.TEditText_text_color_normal_topic, textColorNormalTopic);
        textColorSelectTopic = t.getColor(R.styleable.TEditText_text_color_select_topic, textColorSelectTopic);
        topicRegex = TextUtils.isEmpty(t.getString(R.styleable.TEditText_topic_regex)) ? DEFAULT_REGEX : t.getString(R.styleable.TEditText_topic_regex);

        // 用完后回收
        t.recycle();

        //设置选中颜色
        this.setHighlightColor(textColorSelectTopic);

        // 初始化退格键的监听
        inputConnection = new TInputConnection(null, true);

        initEvent();
    }

    private void initEvent() {

        /**
         * 监听删除键 <br/>
         * 1.光标在话题后面,将整个话题内容删除 <br/>
         * 2.光标在普通文字后面,删除一个字符
         */
        this.setBackSpaceListener(this::operateDelText);

        /**
         * 监听点击事件 <br/>
         * 注意：如果在页面中实现了该方法，则需要手动调用 {@link TEditText#changeSelectionPos()}方法
         */
        this.setOnClickListener(v -> changeSelectionPos());

        /**
         * 监听输入框内容改变 <br/>
         * 注意：如果在页面中实现了该方法，则需要在{@link TextWatcher#onTextChanged(CharSequence, int, int, int)}方法中手动调用
         * {@link TEditText#refreshUI(String)}方法
         */
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                refreshUI(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 光标改变时的回调监听，处理输入框长按拖拽光标的问题
     *
     * @param selStart start
     * @param selEnd   end
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (!isDel) {
            changeSelectionPos();
        }
        if (null != selectionChanged) {
            selectionChanged.curSelection(selStart, selEnd);
        }
    }

    /**
     * 改变输入框内的文字样式，在 {@link TextWatcher#onTextChanged(CharSequence, int, int, int)} 方法中调用
     *
     * @param content 传入CharSequence.toString()
     */
    private void refreshUI(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        // 查找话题
        mTopicList.clear();
        mTopicList.addAll(findTopic(content));

        // 查找到变色
        Editable editable = getText();

        // 刷新UI前先移除所有的样式
        for (int i = 0; i < mColorSpans.size(); i++) {
            editable.removeSpan(mColorSpans.get(i));
        }
        mColorSpans.clear();

        for (int i = 0; i < mSizeSpans.size(); i++) {
            editable.removeSpan(mSizeSpans.get(i));
        }
        mSizeSpans.clear();

        //为editable,中的话题加入colorSpan
        int findPos = 0;
        int size = mTopicList.size();
        for (int i = 0; i < size; i++) {
            //遍历话题
            String topic = mTopicList.get(i);
            findPos = content.indexOf(topic, findPos);
            if (findPos != -1) {
                // 改变话题标签文字颜色
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(textColorNormalTopic);
                editable.setSpan(colorSpan, findPos, findPos + topic.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                mColorSpans.add(colorSpan);

                // 改变话题标签文字大小
                AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(textSizeTopic, false);
                editable.setSpan(sizeSpan, findPos, findPos + topic.indexOf("["), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                mSizeSpans.add(sizeSpan);

                // 隐藏话题标签文字中的占位文字
                if (topic.contains("[") && topic.contains("]")) {
                    AbsoluteSizeSpan _sizeSpan = new AbsoluteSizeSpan(0, false);
                    editable.setSpan(_sizeSpan, findPos + topic.indexOf("["), findPos + topic.indexOf("]") + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                    mSizeSpans.add(_sizeSpan);

                    // 修改占位文字后面的文字大小
                    if (topic.length() >= topic.indexOf("]")) {
                        AbsoluteSizeSpan _sizeSpan1 = new AbsoluteSizeSpan(textSizeTopic, false);
                        editable.setSpan(_sizeSpan1, findPos + topic.indexOf("]") + 1, findPos + topic.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        mSizeSpans.add(_sizeSpan1);
                    }
                }
                findPos = findPos + topic.length();
            }
        }
    }

    /**********************改变光标位置，不可选中话题中间******************************************/
    private void changeSelectionPos() {
        setCursorVisible(true);
        if (null != mTopicList && mTopicList.size() > 0) {
            int selectionStart = getSelectionStart();
            int lastPos = 0;
            int size = mTopicList.size();
            for (int i = 0; i < size; i++) {
                String topic = mTopicList.get(i);
                lastPos = getText().toString().indexOf(topic, lastPos);
                if (lastPos != -1) {
                    if (selectionStart > lastPos && selectionStart <= (lastPos + topic.length())) {
                        //在这position 区间就移动光标
                        setSelection(lastPos + topic.length());
                    }
                }
                lastPos = lastPos + topic.length();
            }
        }
    }

    /**
     * 删除操作
     *
     * @return true or false
     */
    public boolean operateDelText() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        if (selectionStart != selectionEnd) {
            isDel = false;
            return false;
        }

        Editable editable = getText();
        String content = editable.toString();
        int lastPos = 0;
        int size = mTopicList.size();
        for (int i = 0; i < size; i++) {
            String topic = mTopicList.get(i);
            lastPos = content.indexOf(topic, lastPos);
            if (lastPos != -1) {
                if (selectionStart != 0 && selectionStart > lastPos && selectionStart <= (lastPos + topic.length())) {
                    setCursorVisible(false);
                    isDel = true;
                    //选中话题
                    setSelection(lastPos, lastPos + topic.length());
                    return true;
                }
            }
            lastPos += topic.length();
        }
        return false;
    }

    /**
     * 当输入法和EditText建立连接的时候会通过这个方法返回一个InputConnection。
     * 我们需要代理这个方法的父类方法生成的InputConnection并返回我们自己的代理类。
     */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        inputConnection.setTarget(super.onCreateInputConnection(outAttrs));
        return inputConnection;
    }

    /**
     * 匹配文本方法
     *
     * @param s 带匹配内容
     *
     * @return 匹配到的话题标签列表
     */
    public ArrayList<String> findTopic(String s) {
        if (TextUtils.isEmpty(topicRegex)) {
            Log.w(TAG, "you have not set the topicRegex.so we use the default regex. ");
            topicRegex = DEFAULT_REGEX;
            return new ArrayList<>();
        }
        Pattern p = Pattern.compile(topicRegex);
        Matcher m = p.matcher(s);
        ArrayList<String> list = new ArrayList<>();
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }

    public ArrayList<String> getTopicList() {
        return mTopicList;
    }

    /**
     * 插入话题内容
     *
     * @param topic 插入话题
     */
    public void insertTopic(String topic) {
        int selectStart = getSelectionStart();
        String con = getText().toString();
        String firstStr = con.substring(0, selectStart);
        String secondStr = con.substring(selectStart, con.length());
        setText(firstStr + topic + secondStr);
        setSelection(selectStart + topic.length());
    }

    /**
     * 是否删除操作的标记
     *
     * @param isDel true 删除操作
     */
    public void setDelFlag(boolean isDel) {
        this.isDel = isDel;
    }

    public boolean isDel() {
        return isDel;
    }

    /**
     * 光标改变
     *
     * @param selectionChanged 光标位置
     */
    public void setSelectionChangListener(OnSelectionChanged selectionChanged) {
        this.selectionChanged = selectionChanged;
    }

    public interface OnSelectionChanged {
        void curSelection(int start, int end);
    }

    /**
     * 设置软键盘删除键事件
     *
     * @param backSpaceListener 删除监听
     */
    public void setBackSpaceListener(TInputConnection.BackspaceListener backSpaceListener) {
        inputConnection.setBackspaceListener(backSpaceListener);
    }


    /*************************************************************************************************************/
    public TEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        LogUtil.i(TAG, "TEditText: ");
    }

    @Override
    public void setBackgroundResource(int resId) {
        super.setBackgroundResource(resId);
//        LogUtil.i(TAG, "setBackgroundResource: ");
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
//        LogUtil.i(TAG, "setBackgroundDrawable: ");
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setSupportBackgroundTintList(@Nullable ColorStateList tint) {
        super.setSupportBackgroundTintList(tint);
//        LogUtil.i(TAG, "setSupportBackgroundTintList: ");
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public ColorStateList getSupportBackgroundTintList() {
//        LogUtil.i(TAG, "getSupportBackgroundTintList: ");
        return super.getSupportBackgroundTintList();

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
//        LogUtil.i(TAG, "setSupportBackgroundTintMode: ");
        super.setSupportBackgroundTintMode(tintMode);

    }

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        LogUtil.i(TAG, "getSupportBackgroundTintMode: ");
        return super.getSupportBackgroundTintMode();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
//        LogUtil.i(TAG, "drawableStateChanged: " + getDrawableState());

    }

    @Override
    public void setTextAppearance(Context context, int resId) {
        super.setTextAppearance(context, resId);
//        LogUtil.i(TAG, "setTextAppearance: ");
    }

    @Override
    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
//        LogUtil.i(TAG, "onFilterTouchEventForSecurity: ");
        return super.onFilterTouchEventForSecurity(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        LogUtil.i(TAG, "onTouchEvent: ");
        return super.onTouchEvent(event);
    }

    @Override
    public void setAutoSizeTextTypeWithDefaults(int autoSizeTextType) {
        super.setAutoSizeTextTypeWithDefaults(autoSizeTextType);
    }

    @Override
    public void setAutoSizeTextTypeUniformWithConfiguration(int autoSizeMinTextSize, int autoSizeMaxTextSize, int autoSizeStepGranularity, int unit) {
        super.setAutoSizeTextTypeUniformWithConfiguration(autoSizeMinTextSize, autoSizeMaxTextSize, autoSizeStepGranularity, unit);
    }

    @Override
    public void setAutoSizeTextTypeUniformWithPresetSizes(@NonNull int[] presetSizes, int unit) {
        super.setAutoSizeTextTypeUniformWithPresetSizes(presetSizes, unit);
    }

    @Override
    public int getAutoSizeTextType() {
        return super.getAutoSizeTextType();
    }

    @Override
    public int getAutoSizeStepGranularity() {
        return super.getAutoSizeStepGranularity();
    }

    @Override
    public int getAutoSizeMinTextSize() {
        return super.getAutoSizeMinTextSize();
    }

    @Override
    public int getAutoSizeMaxTextSize() {
        return super.getAutoSizeMaxTextSize();
    }

    @Override
    public int[] getAutoSizeTextAvailableSizes() {
        return super.getAutoSizeTextAvailableSizes();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        super.setTypeface(tf, style);
    }

    @Override
    public int length() {
        return super.length();
    }

    @Override
    public Editable getEditableText() {
        return super.getEditableText();
    }

    @Override
    public int getLineHeight() {
        return super.getLineHeight();
    }

    @Override
    public void setKeyListener(KeyListener input) {
        super.setKeyListener(input);
    }

    @Override
    public int getCompoundPaddingTop() {
        return super.getCompoundPaddingTop();
    }

    @Override
    public int getCompoundPaddingBottom() {
        return super.getCompoundPaddingBottom();
    }

    @Override
    public int getCompoundPaddingLeft() {
        return super.getCompoundPaddingLeft();
    }

    @Override
    public int getCompoundPaddingRight() {
        return super.getCompoundPaddingRight();
    }

    @Override
    public int getCompoundPaddingStart() {
        return super.getCompoundPaddingStart();
    }

    @Override
    public int getCompoundPaddingEnd() {
        return super.getCompoundPaddingEnd();
    }

    @Override
    public int getExtendedPaddingTop() {
        return super.getExtendedPaddingTop();
    }

    @Override
    public int getExtendedPaddingBottom() {
        return super.getExtendedPaddingBottom();
    }

    @Override
    public int getTotalPaddingLeft() {
        return super.getTotalPaddingLeft();
    }

    @Override
    public int getTotalPaddingRight() {
        return super.getTotalPaddingRight();
    }

    @Override
    public int getTotalPaddingStart() {
        return super.getTotalPaddingStart();
    }

    @Override
    public int getTotalPaddingEnd() {
        return super.getTotalPaddingEnd();
    }

    @Override
    public int getTotalPaddingTop() {
        return super.getTotalPaddingTop();
    }

    @Override
    public int getTotalPaddingBottom() {
        return super.getTotalPaddingBottom();
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(int left, int top, int right, int bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    @Override
    public void setCompoundDrawablesRelative(@Nullable Drawable start, @Nullable Drawable top, @Nullable Drawable end, @Nullable Drawable bottom) {
        super.setCompoundDrawablesRelative(start, top, end, bottom);
    }

    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(int start, int top, int end, int bottom) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
    }

    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(@Nullable Drawable start, @Nullable Drawable top, @Nullable Drawable end, @Nullable Drawable bottom) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
    }

    @NonNull
    @Override
    public Drawable[] getCompoundDrawables() {
        return super.getCompoundDrawables();
    }

    @NonNull
    @Override
    public Drawable[] getCompoundDrawablesRelative() {
        return super.getCompoundDrawablesRelative();
    }

    @Override
    public void setCompoundDrawablePadding(int pad) {
        super.setCompoundDrawablePadding(pad);
    }

    @Override
    public int getCompoundDrawablePadding() {
        return super.getCompoundDrawablePadding();
    }

    @Override
    public void setCompoundDrawableTintList(@Nullable ColorStateList tint) {
        super.setCompoundDrawableTintList(tint);
    }

    @Override
    public ColorStateList getCompoundDrawableTintList() {
        return super.getCompoundDrawableTintList();
    }

    @Override
    public void setCompoundDrawableTintMode(@Nullable PorterDuff.Mode tintMode) {
        super.setCompoundDrawableTintMode(tintMode);
    }

    @Override
    public PorterDuff.Mode getCompoundDrawableTintMode() {
        return super.getCompoundDrawableTintMode();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
    }

    @Override
    public void setTextAppearance(int resId) {
        super.setTextAppearance(resId);
    }

    @NonNull
    @Override
    public Locale getTextLocale() {
        return super.getTextLocale();
    }

    @NonNull
    @Override
    public LocaleList getTextLocales() {
        return super.getTextLocales();
    }

    @Override
    public void setTextLocale(@NonNull Locale locale) {
        super.setTextLocale(locale);
    }

    @Override
    public void setTextLocales(@NonNull LocaleList locales) {
        super.setTextLocales(locales);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public float getTextSize() {
        return super.getTextSize();
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
    }

    @Override
    public float getTextScaleX() {
        return super.getTextScaleX();
    }

    @Override
    public void setTextScaleX(float size) {
        super.setTextScaleX(size);
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
    }

    @Override
    public Typeface getTypeface() {
        return super.getTypeface();
    }

    @Override
    public void setElegantTextHeight(boolean elegant) {
        super.setElegantTextHeight(elegant);
    }

    @Override
    public float getLetterSpacing() {
        return super.getLetterSpacing();
    }

    @Override
    public void setLetterSpacing(float letterSpacing) {
        super.setLetterSpacing(letterSpacing);
    }

    @Nullable
    @Override
    public String getFontFeatureSettings() {
        return super.getFontFeatureSettings();
    }

    @Nullable
    @Override
    public String getFontVariationSettings() {
        return super.getFontVariationSettings();
    }

    @Override
    public void setBreakStrategy(int breakStrategy) {
        super.setBreakStrategy(breakStrategy);
    }

    @Override
    public int getBreakStrategy() {
        return super.getBreakStrategy();
    }

    @Override
    public void setHyphenationFrequency(int hyphenationFrequency) {
        super.setHyphenationFrequency(hyphenationFrequency);
    }

    @Override
    public int getHyphenationFrequency() {
        return super.getHyphenationFrequency();
    }

    @Override
    public void setJustificationMode(int justificationMode) {
        super.setJustificationMode(justificationMode);
    }

    @Override
    public int getJustificationMode() {
        return super.getJustificationMode();
    }

    @Override
    public void setFontFeatureSettings(@Nullable String fontFeatureSettings) {
        super.setFontFeatureSettings(fontFeatureSettings);
    }

    @Override
    public boolean setFontVariationSettings(@Nullable String fontVariationSettings) {
        return super.setFontVariationSettings(fontVariationSettings);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    @Override
    public void setTextColor(ColorStateList colors) {
        super.setTextColor(colors);
    }

    @Override
    public void setHighlightColor(int color) {
        super.setHighlightColor(color);
    }

    @Override
    public int getHighlightColor() {
        return super.getHighlightColor();
    }

    @Override
    public void setShadowLayer(float radius, float dx, float dy, int color) {
        super.setShadowLayer(radius, dx, dy, color);
    }

    @Override
    public float getShadowRadius() {
        return super.getShadowRadius();
    }

    @Override
    public float getShadowDx() {
        return super.getShadowDx();
    }

    @Override
    public float getShadowDy() {
        return super.getShadowDy();
    }

    @Override
    public int getShadowColor() {
        return super.getShadowColor();
    }

    @Override
    public TextPaint getPaint() {
        return super.getPaint();
    }

    @Override
    public URLSpan[] getUrls() {
        return super.getUrls();
    }

    @Override
    public void setGravity(int gravity) {
        super.setGravity(gravity);
    }

    @Override
    public int getGravity() {
        return super.getGravity();
    }

    @Override
    public int getPaintFlags() {
        return super.getPaintFlags();
    }

    @Override
    public void setPaintFlags(int flags) {
        super.setPaintFlags(flags);
    }

    @Override
    public void setHorizontallyScrolling(boolean whether) {
        super.setHorizontallyScrolling(whether);
    }

    @Override
    public void setMinLines(int minLines) {
        super.setMinLines(minLines);
    }

    @Override
    public int getMinLines() {
        return super.getMinLines();
    }

    @Override
    public void setMinHeight(int minPixels) {
        super.setMinHeight(minPixels);
    }

    @Override
    public int getMinHeight() {
        return super.getMinHeight();
    }

    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
    }

    @Override
    public int getMaxLines() {
        return super.getMaxLines();
    }

    @Override
    public void setMaxHeight(int maxPixels) {
        super.setMaxHeight(maxPixels);
    }

    @Override
    public int getMaxHeight() {
        return super.getMaxHeight();
    }

    @Override
    public void setLines(int lines) {
        super.setLines(lines);
    }

    @Override
    public void setHeight(int pixels) {
        super.setHeight(pixels);
    }

    @Override
    public void setMinEms(int minEms) {
        super.setMinEms(minEms);
    }

    @Override
    public int getMinEms() {
        return super.getMinEms();
    }

    @Override
    public void setMinWidth(int minPixels) {
        super.setMinWidth(minPixels);
    }

    @Override
    public int getMinWidth() {
        return super.getMinWidth();
    }

    @Override
    public void setMaxEms(int maxEms) {
        super.setMaxEms(maxEms);
    }

    @Override
    public int getMaxEms() {
        return super.getMaxEms();
    }

    @Override
    public void setMaxWidth(int maxPixels) {
        super.setMaxWidth(maxPixels);
    }

    @Override
    public int getMaxWidth() {
        return super.getMaxWidth();
    }

    @Override
    public void setEms(int ems) {
        super.setEms(ems);
    }

    @Override
    public void setWidth(int pixels) {
        super.setWidth(pixels);
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
    }

    @Override
    public float getLineSpacingMultiplier() {
        return super.getLineSpacingMultiplier();
    }

    @Override
    public float getLineSpacingExtra() {
        return super.getLineSpacingExtra();
    }

    @Override
    public void append(CharSequence text, int start, int end) {
        super.append(text, start, end);
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    public void setFreezesText(boolean freezesText) {
        super.setFreezesText(freezesText);
    }

    @Override
    public CharSequence getHint() {
        return super.getHint();
    }

    @Override
    public void setInputType(int type) {
        super.setInputType(type);
    }

    @Override
    public void setRawInputType(int type) {
        super.setRawInputType(type);
    }

    @Override
    public int getInputType() {
        return super.getInputType();
    }

    @Override
    public void setImeOptions(int imeOptions) {
        super.setImeOptions(imeOptions);
    }

    @Override
    public int getImeOptions() {
        return super.getImeOptions();
    }

    @Override
    public void setImeActionLabel(CharSequence label, int actionId) {
        super.setImeActionLabel(label, actionId);
    }

    @Override
    public CharSequence getImeActionLabel() {
        return super.getImeActionLabel();
    }

    @Override
    public int getImeActionId() {
        return super.getImeActionId();
    }

    @Override
    public void setOnEditorActionListener(OnEditorActionListener l) {
        super.setOnEditorActionListener(l);
    }

    @Override
    public void onEditorAction(int actionCode) {
        super.onEditorAction(actionCode);
    }

    @Override
    public void setPrivateImeOptions(String type) {
        super.setPrivateImeOptions(type);
    }

    @Override
    public String getPrivateImeOptions() {
        return super.getPrivateImeOptions();
    }

    @Override
    public void setInputExtras(int xmlResId) throws XmlPullParserException, IOException {
        super.setInputExtras(xmlResId);
    }

    @Override
    public Bundle getInputExtras(boolean create) {
        return super.getInputExtras(create);
    }

    @Override
    public void setImeHintLocales(@Nullable LocaleList hintLocales) {
        super.setImeHintLocales(hintLocales);
    }

    @Nullable
    @Override
    public LocaleList getImeHintLocales() {
        return super.getImeHintLocales();
    }

    @Override
    public CharSequence getError() {
        return super.getError();
    }

    @Override
    public void setError(CharSequence error) {
        super.setError(error);
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        super.setError(error, icon);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        return super.setFrame(l, t, r, b);
    }

    @Override
    public void setFilters(InputFilter[] filters) {
        super.setFilters(filters);
    }

    @Override
    public InputFilter[] getFilters() {
        return super.getFilters();
    }

    @Override
    public boolean onPreDraw() {
        return super.onPreDraw();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
    }

    @Override
    protected boolean isPaddingOffsetRequired() {
        return super.isPaddingOffsetRequired();
    }

    @Override
    protected int getLeftPaddingOffset() {
        return super.getLeftPaddingOffset();
    }

    @Override
    protected int getTopPaddingOffset() {
        return super.getTopPaddingOffset();
    }

    @Override
    protected int getBottomPaddingOffset() {
        return super.getBottomPaddingOffset();
    }

    @Override
    protected int getRightPaddingOffset() {
        return super.getRightPaddingOffset();
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who);
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        super.invalidateDrawable(drawable);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return super.hasOverlappingRendering();
    }

    @Override
    public boolean isTextSelectable() {
        return super.isTextSelectable();
    }

    @Override
    public void setTextIsSelectable(boolean selectable) {
        super.setTextIsSelectable(selectable);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        return super.onCreateDrawableState(extraSpace);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void getFocusedRect(Rect r) {
        super.getFocusedRect(r);
    }

    @Override
    public int getLineCount() {
        return super.getLineCount();
    }

    @Override
    public int getLineBounds(int line, Rect bounds) {
        return super.getLineBounds(line, bounds);
    }

    @Override
    public int getBaseline() {
        return super.getBaseline();
    }

    @Override
    public PointerIcon onResolvePointerIcon(MotionEvent event, int pointerIndex) {
        return super.onResolvePointerIcon(event, pointerIndex);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return super.onCheckIsTextEditor();
    }

    @Override
    public boolean extractText(ExtractedTextRequest request, ExtractedText outText) {
        return super.extractText(request, outText);
    }

    @Override
    public void setExtractedText(ExtractedText text) {
        super.setExtractedText(text);
    }

    @Override
    public void onCommitCompletion(CompletionInfo text) {
        super.onCommitCompletion(text);
    }

    @Override
    public void onCommitCorrection(CorrectionInfo info) {
        super.onCommitCorrection(info);
    }

    @Override
    public void beginBatchEdit() {
        super.beginBatchEdit();
    }

    @Override
    public void endBatchEdit() {
        super.endBatchEdit();
    }

    @Override
    public void onBeginBatchEdit() {
        super.onBeginBatchEdit();
    }

    @Override
    public void onEndBatchEdit() {
        super.onEndBatchEdit();
    }

    @Override
    public boolean onPrivateIMECommand(String action, Bundle data) {
        return super.onPrivateIMECommand(action, data);
    }

    @Override
    public void setIncludeFontPadding(boolean includepad) {
        super.setIncludeFontPadding(includepad);
    }

    @Override
    public boolean getIncludeFontPadding() {
        return super.getIncludeFontPadding();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean bringPointIntoView(int offset) {
        return super.bringPointIntoView(offset);
    }

    @Override
    public boolean moveCursorToVisibleOffset() {
        return super.moveCursorToVisibleOffset();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
    }

    @Override
    public void debug(int depth) {
        super.debug(depth);
    }

    @Override
    public int getSelectionStart() {
        return super.getSelectionStart();
    }

    @Override
    public int getSelectionEnd() {
        return super.getSelectionEnd();
    }

    @Override
    public boolean hasSelection() {
        return super.hasSelection();
    }

    @Override
    public void setSingleLine() {
        super.setSingleLine();
    }

    @Override
    public void setAllCaps(boolean allCaps) {
        super.setAllCaps(allCaps);
    }

    @Override
    public void setSingleLine(boolean singleLine) {
        super.setSingleLine(singleLine);
    }

    @Override
    public void setMarqueeRepeatLimit(int marqueeLimit) {
        super.setMarqueeRepeatLimit(marqueeLimit);
    }

    @Override
    public int getMarqueeRepeatLimit() {
        return super.getMarqueeRepeatLimit();
    }

    @Override
    public TextUtils.TruncateAt getEllipsize() {
        return super.getEllipsize();
    }

    @Override
    public void setSelectAllOnFocus(boolean selectAllOnFocus) {
        super.setSelectAllOnFocus(selectAllOnFocus);
    }

    @Override
    public void setCursorVisible(boolean visible) {
        super.setCursorVisible(visible);
    }

    @Override
    public boolean isCursorVisible() {
        return super.isCursorVisible();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        super.addTextChangedListener(watcher);
    }

    @Override
    public void removeTextChangedListener(TextWatcher watcher) {
        super.removeTextChangedListener(watcher);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    public void clearComposingText() {
        super.clearComposingText();
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return super.onGenericMotionEvent(event);
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);
    }

    @Override
    public boolean showContextMenu() {
        return super.showContextMenu();
    }

    @Override
    public boolean showContextMenu(float x, float y) {
        return super.showContextMenu(x, y);
    }

    @Override
    public boolean didTouchFocusSelect() {
        return super.didTouchFocusSelect();
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        return super.onTrackballEvent(event);
    }

    @Override
    public void setScroller(Scroller s) {
        super.setScroller(s);
    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        return super.getLeftFadingEdgeStrength();
    }

    @Override
    protected float getRightFadingEdgeStrength() {
        return super.getRightFadingEdgeStrength();
    }

    @Override
    protected int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    @Override
    protected int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    protected int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @Override
    public void findViewsWithText(ArrayList<View> outViews, CharSequence searched, int flags) {
        super.findViewsWithText(outViews, searched, flags);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return super.onKeyShortcut(keyCode, event);
    }

    @Override
    public void onProvideStructure(ViewStructure structure) {
        super.onProvideStructure(structure);
    }

    @Override
    public void onProvideAutofillStructure(ViewStructure structure, int flags) {
        super.onProvideAutofillStructure(structure, flags);
    }

    @Override
    public void autofill(AutofillValue value) {
        super.autofill(value);
    }

    @Override
    public int getAutofillType() {
        return super.getAutofillType();
    }

    @Nullable
    @Override
    public AutofillValue getAutofillValue() {
        return super.getAutofillValue();
    }

    @Override
    public void addExtraDataToAccessibilityNodeInfo(AccessibilityNodeInfo info, String extraDataKey, Bundle arguments) {
        super.addExtraDataToAccessibilityNodeInfo(info, extraDataKey, arguments);
    }

    @Override
    public boolean isInputMethodTarget() {
        return super.isInputMethodTarget();
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        return super.onTextContextMenuItem(id);
    }

    @Override
    public boolean performLongClick() {
        return super.performLongClick();
    }

    @Override
    protected void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert);
    }

    @Override
    public boolean isSuggestionsEnabled() {
        return super.isSuggestionsEnabled();
    }

    @Override
    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        super.setCustomSelectionActionModeCallback(actionModeCallback);
    }

    @Override
    public ActionMode.Callback getCustomSelectionActionModeCallback() {
        return super.getCustomSelectionActionModeCallback();
    }

    @Override
    public void setCustomInsertionActionModeCallback(ActionMode.Callback actionModeCallback) {
        super.setCustomInsertionActionModeCallback(actionModeCallback);
    }

    @Override
    public ActionMode.Callback getCustomInsertionActionModeCallback() {
        return super.getCustomInsertionActionModeCallback();
    }

    @Override
    public void setTextClassifier(@Nullable TextClassifier textClassifier) {
        super.setTextClassifier(textClassifier);
    }

    @NonNull
    @Override
    public TextClassifier getTextClassifier() {
        return super.getTextClassifier();
    }

    @Override
    public int getOffsetForPosition(float x, float y) {
        return super.getOffsetForPosition(x, y);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        return super.onDragEvent(event);
    }

    @Override
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
    }
}
