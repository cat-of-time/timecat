package com.time.cat.mvp.view.richText;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;

import com.time.cat.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/17
 * @discription null
 * @usage null
 */
public class TTextView extends AppCompatTextView {
  private static final String TAG = TTextView.class.getSimpleName().trim();

  private ArrayList<String> mTopicList = new ArrayList<>();

  private ArrayList<ForegroundColorSpan> mColorSpans = new ArrayList<>();
  private ArrayList<AbsoluteSizeSpan> mSizeSpans = new ArrayList<>();

  // 默认的正则表达式
  private final String DEFAULT_REGEX = "\\s#[^#]*?\\[.*?\\|话题]#\\s";// 获取 标签内容 ,如 #天气[1|话题]#

  // 话题标签的文字大小
  private int textSizeTopic = 18;
  // 话题文字颜色 未选中
  private int textColorNormalTopic = Color.parseColor("#FFAA31");
  // 话题文字颜色 选中
  private int textColorSelectTopic = Color.parseColor("#50ffaa31");

  // 标签匹配的正则表达式
  private String topicRegex;

  // 是否是删除操作
  private boolean isDel = false;

  public TTextView(Context context) {
    this(context, null);
  }

  public TTextView(Context context, AttributeSet attrs) {
    super(context, attrs);

    // 获取自定义属性
    TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.TEditText);
    textSizeTopic = t.getDimensionPixelOffset(R.styleable.TEditText_text_size_topic, textSizeTopic);
    textColorNormalTopic = t.getColor(R.styleable.TEditText_text_color_normal_topic, textColorNormalTopic);
    textColorSelectTopic = t.getColor(R.styleable.TEditText_text_color_select_topic, textColorSelectTopic);
    topicRegex = TextUtils.isEmpty(t.getString(R.styleable.TEditText_topic_regex)) ? DEFAULT_REGEX
        : t.getString(R.styleable.TEditText_topic_regex);

    // 用完后回收
    t.recycle();

    //设置选中颜色
    this.setHighlightColor(textColorSelectTopic);

    initEvent();
  }

  private void initEvent() {

    /**
     * 监听输入框内容改变 <br/>
     * 注意：如果在页面中实现了该方法，则需要在{@link TextWatcher#onTextChanged(CharSequence, int, int, int)}方法中手动调用
     * {@link TEditText#refreshUI(String)}方法
     */
    this.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        refreshUI(charSequence.toString());
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
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

    SpannableStringBuilder editable = new SpannableStringBuilder(getText());

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
          editable.setSpan(_sizeSpan, findPos + topic.indexOf("["), findPos + topic.indexOf("]") + 1,
              Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

          mSizeSpans.add(_sizeSpan);

          // 修改占位文字后面的文字大小
          if (topic.length() >= topic.indexOf("]")) {
            AbsoluteSizeSpan _sizeSpan1 = new AbsoluteSizeSpan(textSizeTopic, false);
            editable.setSpan(_sizeSpan1, findPos + topic.indexOf("]") + 1, findPos + topic.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            mSizeSpans.add(_sizeSpan1);
          }
        }
        findPos = findPos + topic.length();
      }
    }
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
}
