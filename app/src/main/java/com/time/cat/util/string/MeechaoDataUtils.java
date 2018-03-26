package com.time.cat.util.string;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/17
 * @discription 数据文字处理，包括文章标签变色 / 评论内容格式化 / 提问问题内容加标识 / 文章标题处理 ...
 * @usage null
 */
public class MeechaoDataUtils {
    /**
     * 文字格式化，适用但不局限于心得内容，用于处理给定内容 str 中的标签
     *
     * @param str 给定内容
     *
     * @return s
     */
    public static SpannableStringBuilder covArticleContent(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        List<String> tagList = RegexUtil.getMathcherStr(str, RegexUtil.topicValReg);
        if (null != tagList && tagList.size() != 0) {
            int fromIndex = 0;
            for (String tagVal : tagList) {
                int _start = str.indexOf(tagVal, fromIndex);
                builder.setSpan(new ForegroundColorSpan(Color.parseColor("#ffaa31")),
                        _start, _start + tagVal.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                builder.setSpan(new AbsoluteSizeSpan(0, true), _start + tagVal.indexOf("["), _start + tagVal.indexOf("]") + 1,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                fromIndex = _start + tagVal.length();
            }
        }
        return builder;
    }

    /**
     * 心得标签格式化 ，「旅行[1|标签]」
     *
     * @param labelVal 标签内容
     * @param labelId 标签id
     *
     * @return String
     */
    public static String formatLabel(String labelVal, int labelId) {
        if (TextUtils.isEmpty(labelVal)) {
            return null;
        }
        return "「" + labelVal + "[" + labelId + "|标签]」";
    }

    /**
     * 标签格式化
     *
     * @param labelTopicValue 标签 内容
     *
     * @return s
     */
    public static SpannableStringBuilder covTopicVal(String labelTopicValue) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder(labelTopicValue);
        if (TextUtils.isEmpty(labelTopicValue)) {
            return spannableString;
        }
        spannableString.setSpan(new AbsoluteSizeSpan(0, true), labelTopicValue.indexOf("["),
                labelTopicValue.indexOf("]") + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static String regexTag(String s) {
        String DEFAULT_REGEX = "\\s#([^#]*?)\\[.*?\\|话题]#\\s";
        Pattern p = Pattern.compile(DEFAULT_REGEX);
        Matcher m = p.matcher(s);
//        String sub = m.
        return m.find() ? m.group(1) : "";
    }
}
