package com.time.cat.util.string;

import com.timecat.commonjar.contentProvider.SPHelper;
import com.time.cat.data.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexUtil {

    public static final String SYMBOL_REX_WITH_BLANK = "[ ,\\./:\"\\\\\\[\\]\\|`~!@#\\$%\\^&\\*\\(\\)_\\+=<\\->\\?;'，。、；：‘’“”【】《》？\\{\\}！￥…（）—=]";
    public static final String SYMBOL_REX_WITHOUT_BLANK = "[,\\./:\"\\\\\\[\\]\\|`~!@#\\$%\\^&\\*\\(\\)_\\+=<\\->\\?;'，。、；：‘’“”【】《》？\\{\\}！￥…（）—=]";

    public static String SYMBOL_REX = SYMBOL_REX_WITH_BLANK;

    public static void refreshSymbolSelection() {
        boolean b = SPHelper.getBoolean(Constants.TREAT_BLANKS_AS_SYMBOL, true);
        if (b) {
            SYMBOL_REX = SYMBOL_REX_WITH_BLANK;
        } else {
            SYMBOL_REX = SYMBOL_REX_WITHOUT_BLANK;
        }
    }

    public static boolean isEnglish(String charaString) {
        return charaString.matches("^[a-zA-Z]*-*[a-zA-Z]*");
    }

    public static boolean isPositiveInteger(String orginal) {
        return isMatch("^\\+{0,1}[1-9]\\d*", orginal);
    }

    public static boolean isNegativeInteger(String orginal) {
        return isMatch("^-[1-9]\\d*", orginal);
    }

    public static boolean isWholeNumber(String orginal) {
        return isMatch("[+-]{0,1}0", orginal) || isPositiveInteger(orginal) || isNegativeInteger(orginal);
    }

    public static boolean isPositiveDecimal(String orginal) {
        return isMatch("\\+{0,1}[0]\\.[1-9]*|\\+{0,1}[1-9]\\d*\\.\\d*", orginal);
    }

    public static boolean isNegativeDecimal(String orginal) {
        return isMatch("^-[0]\\.[1-9]*|^-[1-9]\\d*\\.\\d*", orginal);
    }

    public static boolean isDecimal(String orginal) {
        return isMatch("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+", orginal);
    }

    public static boolean isNumber(String orginal) {
        return isWholeNumber(orginal) || isDecimal(orginal) || isNegativeDecimal(orginal) || isPositiveDecimal(orginal);
    }

    private static boolean isMatch(String regex, String orginal) {
        if (orginal == null || orginal.trim().equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher isNum = pattern.matcher(orginal);
        return isNum.matches();
    }


//    // 根据Unicode编码完美的判断中文汉字和符号
//    public static boolean isChinese(char c) {
//        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
//        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
//                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
//                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
//                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
//                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
//                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
//                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 输入的字符是否是汉字
     *
     * @param a char
     *
     * @return boolean
     */
    public static boolean isChinese(char a) {
        int v = (int) a;
        return (v >= 19968 && v <= 171941);
    }

    public static boolean isSymbol(char a) {
        String s = a + "";
        return s.matches(SYMBOL_REX);
    }

    public static boolean isSymbol(String a) {
        return a.matches(SYMBOL_REX);
    }

    // s = #天气[1|话题]#
    public static final String labelIdReg = "(?<=\\[)(\\d+)(?=|)"; // 获取标签id ，如 1
    public static final String topicIdReg = "(?<=\\[)(\\d+)(?=|)"; // 获取标签id ，如 1
    public static final String topicTypeReg = "(?<=\\|)(.+)(?=])"; // 获取 标签类别 ，如 话题
    public static final String textReg = "(?<=#)(.+)(?=\\[)"; // 获取 标签内容，不包括前后# ,如 天气
    public static final String topicValReg = "\\s#[^#]*?\\[.*?\\|话题\\]#\\s"; // 获取 标签内容 ,如 #天气[1|话题]#

    /**
     * 正则：手机号（简单）
     */
    public static final String REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$";

    /**
     * 验证手机号（简单）
     *
     * @param input 待验证文本
     *
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMobileSimple(final CharSequence input) {
        return isMatch(REGEX_MOBILE_SIMPLE, input);
    }

    /**
     * 判断是否匹配正则
     *
     * @param regex 正则表达式
     * @param input 要匹配的字符串
     *
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMatch(final String regex, final CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }

    /**
     * 正则提取内容
     *
     * @param str 文本内容
     * @param reg 正则表达式
     */
    public static List<String> getMathcherStr(String str, String reg) {
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        System.out.println(result);
        return result;
    }

    /**
     * 正则提取第一条匹配到的内容
     *
     * @param str 文本内容
     * @param reg 正则表达式
     */
    public static String getFirstMathcherStr(String str, String reg) {
        String result = null;
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            result = matcher.group();
        }
        System.out.println(result);
        return result;
    }
}
