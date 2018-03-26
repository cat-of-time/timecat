package com.time.cat.util.source;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.time.cat.R;
import com.time.cat.TimeCatApp;
import com.time.cat.data.model.entity.Achievement;
import com.time.cat.data.model.entity.CollectionTopic;
import com.time.cat.data.model.entity.EmojiBean;
import com.time.cat.data.model.entity.TopicBean;
import com.time.cat.util.override.LogUtil;

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
public class AssetManager {
    private static final String TAG = AssetManager.class.getSimpleName().trim();

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
//
//    protected com.ticktick.tomato.f.d a = TomatoApplication.a().b();
//    private int[] achievement_serial;
//    private String[] achievement_name;
//    private String[] achievement_describe;
//    private String[] achievement_big_icon;
//    private Context context;
//    private SparseArray g = getAchievementImgAssets();
//    private List h = e();
//
//    public d(Context context) {
//        this.context = context;
//        this.achievement_serial = context.getResources().getIntArray(R.array.achievement_serial);
//        this.achievement_name = context.getResources().getStringArray(R.array.achievement_name);
//        this.achievement_describe = context.getResources().getStringArray(R.array.achievement_describe);
//        this.achievement_big_icon = context.getResources().getStringArray(R.array.achievement_big_icon);
//    }
//
//    private void a(int i) {
//        a aVar = new a();
//        aVar.c = i;
//        a.a(aVar, this.a);
//    }

    public static SparseArray<Achievement> getAchievementImgAssets(Context context) {
        int[] achievement_serial = context.getResources().getIntArray(R.array.achievement_serial);
        String[] achievement_name = context.getResources().getStringArray(R.array.achievement_name);
        String[] achievement_describe = context.getResources().getStringArray(R.array.achievement_describe);
        String[] achievement_big_icon = context.getResources().getStringArray(R.array.achievement_big_icon);
        SparseArray<Achievement> sparseArray = new SparseArray<>();
        for (int i = 0; i < achievement_name.length; i++) {
            Achievement achievement = new Achievement();
            achievement.set_id(achievement_serial[i]);
            achievement.setName(achievement_name[i]);
            achievement.setDescribe(achievement_describe[i]);
            achievement.setDefaultImgRes(context.getResources().getIdentifier(achievement_big_icon[i], "mipmap", context.getPackageName()));
            achievement.setCompleteImgRes(context.getResources().getIdentifier(achievement_big_icon[i] + "_complete", "mipmap", context.getPackageName()));
            achievement.setShareDefaultImgRes(context.getResources().getIdentifier(achievement_big_icon[i] + "_share", "mipmap", context.getPackageName()));
            achievement.setShareCompleteImgRes(context.getResources().getIdentifier(achievement_big_icon[i] + "_share_no", "mipmap", context.getPackageName()));
            achievement.setComplete(0);
            sparseArray.put(achievement_serial[i], achievement);
        }
        return sparseArray;
    }
//
//    private List e() {
//        return a.a(null, null, null, this.a);
//    }
//
//    public List a() {
//        return this.h;
//    }
//
//    public SparseArray b() {
//        SparseArray sparseArray = this.g;
//        for (int i = 0; i < this.h.size(); i++) {
//            a aVar = (a) this.h.get(i);
//            com.ticktick.tomato.d.a aVar2 = (com.ticktick.tomato.d.a) sparseArray.get(aVar.c);
//            if (aVar2 != null) {
//                aVar2.a(true);
//                aVar2.b(this.context.getResources().getIdentifier(this.achievement_big_icon[aVar2.a()] + "_complete", "mipmap", this.context.getPackageName()));
//                sparseArray.put(aVar.c, aVar2);
//            }
//        }
//        return sparseArray;
//    }
//
//    public SparseArray c() {
//        SparseArray b = b();
//        SparseArray sparseArray = new SparseArray();
//        for (int i = 0; i < b.size(); i++) {
//            com.ticktick.tomato.d.a aVar = (com.ticktick.tomato.d.a) b.valueAt(i);
//            if (!aVar.e()) {
//                switch (aVar.a()) {
//                    case 0:
//                        if (!b.a(this.a)) {
//                            break;
//                        }
//                        a(0);
//                        aVar.a(true);
//                        sparseArray.put(0, aVar);
//                        break;
//                    case 1:
//                        if (((com.ticktick.tomato.d.a) b.get(0)).e() && b.b(this.a)) {
//                            a(1);
//                            aVar.a(true);
//                            sparseArray.put(1, aVar);
//                            break;
//                        }
//                    case 2:
//                        if (((com.ticktick.tomato.d.a) b.get(1)).e() && b.d(this.a)) {
//                            a(2);
//                            aVar.a(true);
//                            sparseArray.put(2, aVar);
//                            break;
//                        }
//                    case 3:
//                        if (((com.ticktick.tomato.d.a) b.get(2)).e() && b.c(this.a)) {
//                            a(3);
//                            aVar.a(true);
//                            sparseArray.put(3, aVar);
//                            break;
//                        }
//                    case 4:
//                        if (!b.e(this.a)) {
//                            break;
//                        }
//                        a(4);
//                        aVar.a(true);
//                        sparseArray.put(4, aVar);
//                        break;
//                    case 5:
//                        if (((com.ticktick.tomato.d.a) b.get(4)).e() && b.f(this.a)) {
//                            a(5);
//                            aVar.a(true);
//                            sparseArray.put(5, aVar);
//                            break;
//                        }
//                    case 6:
//                        if (!b.g(this.a)) {
//                            break;
//                        }
//                        a(6);
//                        aVar.a(true);
//                        sparseArray.put(6, aVar);
//                        break;
//                    case 7:
//                        if (((com.ticktick.tomato.d.a) b.get(6)).e() && b.h(this.a)) {
//                            a(7);
//                            aVar.a(true);
//                            sparseArray.put(7, aVar);
//                            break;
//                        }
//                    case 8:
//                        if (!b.i(this.a)) {
//                            break;
//                        }
//                        a(8);
//                        aVar.a(true);
//                        sparseArray.put(8, aVar);
//                        break;
//                    case 9:
//                        if (!b.j(this.a)) {
//                            break;
//                        }
//                        a(9);
//                        aVar.a(true);
//                        sparseArray.put(9, aVar);
//                        break;
//                    case 10:
//                        if (!b.k(this.a)) {
//                            break;
//                        }
//                        a(10);
//                        aVar.a(true);
//                        sparseArray.put(10, aVar);
//                        break;
//                    case 11:
//                        if (!b.l(this.a)) {
//                            break;
//                        }
//                        a(11);
//                        aVar.a(true);
//                        sparseArray.put(11, aVar);
//                        break;
//                    case 12:
//                        if (!b.m(this.a)) {
//                            break;
//                        }
//                        a(12);
//                        aVar.a(true);
//                        sparseArray.put(12, aVar);
//                        break;
//                    case 13:
//                        if (!b.a(this.a, this.context)) {
//                            break;
//                        }
//                        a(13);
//                        aVar.a(true);
//                        sparseArray.put(13, aVar);
//                        break;
//                    case 14:
//                        if (!b.n(this.a)) {
//                            break;
//                        }
//                        a(14);
//                        aVar.a(true);
//                        sparseArray.put(14, aVar);
//                        break;
//                    case 15:
//                        if (!b.b(this.a, this.context)) {
//                            break;
//                        }
//                        a(15);
//                        aVar.a(true);
//                        sparseArray.put(15, aVar);
//                        break;
//                    case 16:
//                        if (!b.c(this.a, this.context)) {
//                            break;
//                        }
//                        a(16);
//                        aVar.a(true);
//                        sparseArray.put(16, aVar);
//                        break;
//                    case 17:
//                        if (((com.ticktick.tomato.d.a) b.get(16)).e() && b.d(this.a, this.context)) {
//                            a(17);
//                            aVar.a(true);
//                            sparseArray.put(17, aVar);
//                            break;
//                        }
//                    default:
//                        break;
//                }
//            }
//        }
//        return sparseArray;
//    }
}