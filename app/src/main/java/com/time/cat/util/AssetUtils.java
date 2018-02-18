package com.time.cat.util;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.time.cat.TimeCatApp;
import com.time.cat.mvp.model.entity.CollectionTopic;
import com.time.cat.mvp.model.entity.EmojiBean;
import com.time.cat.mvp.model.entity.TopicBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/17
 * @discription null
 * @usage null
 */
public class AssetUtils {
  private static final String TAG = AssetUtils.class.getSimpleName().trim();

  public static List<String> getEmojiData() {
    String emojiJson = getAssetsData("emoji_list.json");
    if (TextUtils.isEmpty(emojiJson)) {
      return null;
    }
    EmojiBean bean = JSON.parseObject(emojiJson, EmojiBean.class);
    if (null == bean) {
      return null;
    }
    return bean.getEmoji_list();
  }

  public static List<TopicBean> getArticleTopic() {
    String topicData = getAssetsData("article_topic.json");
    if (TextUtils.isEmpty(topicData)) {
      return null;
    }
    List<TopicBean> bean = JSON.parseArray(topicData, TopicBean.class);
    if (null == bean) {
      return null;
    }
    return bean;
  }

  public static List<CollectionTopic> getCollectionTopic() {
    String topicData = getAssetsData("collection_topic.json");
    if (TextUtils.isEmpty(topicData)) {
      return null;
    }
    List<CollectionTopic> bean = JSON.parseArray(topicData, CollectionTopic.class);
    if (null == bean) {
      return null;
    }
    return bean;
  }

  public static String getAssetsData(String assetsFileName) {
    InputStream is = null;
    ByteArrayOutputStream bos = null;
    try {
      is = TimeCatApp.getInstance().getApplicationContext().getAssets().open(assetsFileName);
      bos = new ByteArrayOutputStream();
      byte[] bytes = new byte[4 * 1024];
      int len;
      while ((len = is.read(bytes)) != -1) {
        bos.write(bytes, 0, len);
      }
      return new String(bos.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
      LogUtil.i(TAG, "getAssetsData Exception: " + e);
    } finally {
      try {
        if (is != null) {
          is.close();
        }
        if (bos != null) {
          bos.close();
        }
      } catch (IOException e) {
        LogUtil.i(TAG, "getAssetsData IOException: " + e);
      }
    }
    return null;
  }
}