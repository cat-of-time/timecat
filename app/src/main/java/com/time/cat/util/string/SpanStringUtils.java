/**
 * 
 */
package com.time.cat.util.string;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.time.cat.util.view.EmotionUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/17
 * @discription 文本中的emoji字符处理为表情图片
 * @usage null
 */
public class SpanStringUtils {
	
	public static SpannableString getEmotionContent(int emotion_map_type, final Context context, final TextView tv, String source) {
		SpannableString spannableString = new SpannableString(source);
		Resources res = context.getResources();

		String regexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]";
		Pattern patternEmotion = Pattern.compile(regexEmotion);
		Matcher matcherEmotion = patternEmotion.matcher(spannableString);

		while (matcherEmotion.find()) {
			// 获取匹配到的具体字符
			String key = matcherEmotion.group();
			// 匹配字符串的开始位置
			int start = matcherEmotion.start();
			// 利用表情名字获取到对应的图片
			Integer imgRes = EmotionUtil.getImgByName(emotion_map_type,key);
			if (imgRes != null) {
				// 压缩表情图片
				int size = (int) tv.getTextSize()*13/10;
				Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
				Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);

				ImageSpan span = new ImageSpan(context, scaleBitmap);
				spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return spannableString;
	}
}
