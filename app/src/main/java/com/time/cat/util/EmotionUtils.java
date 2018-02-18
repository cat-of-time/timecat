
package com.time.cat.util;

import android.support.v4.util.ArrayMap;

import com.time.cat.R;
import com.time.cat.util.LogUtil;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/17
 * @discription 表情加载类,可自己添加多种表情，分别建立不同的map存放和不同的标志符即可
 * @usage null
 */
public class EmotionUtils {

	/**
	 * 表情类型标志符
	 */
	public static final int EMOTION_CLASSIC_TYPE=0x0001;//经典表情

	/**
	 * key-表情文字;
	 * value-表情图片资源
	 */
	public static ArrayMap<String, Integer> EMPTY_MAP;
	public static ArrayMap<String, Integer> EMOTION_CLASSIC_MAP;


	static {
		EMPTY_MAP = new ArrayMap<>();
		EMOTION_CLASSIC_MAP = new ArrayMap<>();

		EMOTION_CLASSIC_MAP.put("[呵呵]", R.drawable.ic_emotion);
	}

	/**
	 * 根据名称获取当前表情图标R值
	 * @param EmotionType 表情类型标志符
	 * @param imgName 名称
	 * @return
	 */
	public static int getImgByName(int EmotionType,String imgName) {
		Integer integer=null;
		switch (EmotionType){
			case EMOTION_CLASSIC_TYPE:
				integer = EMOTION_CLASSIC_MAP.get(imgName);
				break;
			default:
				LogUtil.e("EmotionUtils","the emojiMap is null!! Handle Yourself ");
				break;
		}
		return integer == null ? -1 : integer;
	}

	/**
	 * 根据类型获取表情数据
	 * @param EmotionType
	 * @return
	 */
	public static ArrayMap<String, Integer> getEmojiMap(int EmotionType){
		ArrayMap EmojiMap=null;
		switch (EmotionType){
			case EMOTION_CLASSIC_TYPE:
				EmojiMap=EMOTION_CLASSIC_MAP;
				break;
			default:
				EmojiMap=EMPTY_MAP;
				break;
		}
		return EmojiMap;
	}
}
