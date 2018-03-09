package com.time.cat.ui.widgets.emotion.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/17
 * @discription 产生fragment的工厂类
 * @usage null
 */
public class FragmentFactory {

    public static final String EMOTION_MAP_TYPE="EMOTION_MAP_TYPE";
    private static FragmentFactory factory;

    private FragmentFactory(){

    }

    /**
     * 双重检查锁定，获取工厂单例对象
     * @return
     */
    public static FragmentFactory getSingleFactoryInstance(){
        if(factory==null){
            synchronized (FragmentFactory.class){
                if(factory==null){
                    factory = new FragmentFactory();
                }
            }
        }
        return factory;
    }

    /**
     * 获取fragment的方法
     * @param emotionType 表情类型，用于判断使用哪个map集合的表情
     */
    public Fragment getFragment(int emotionType){
        Bundle bundle = new Bundle();

        bundle.putInt(FragmentFactory.EMOTION_MAP_TYPE,emotionType);

        EmotiomComplateFragment fragment= EmotiomComplateFragment.newInstance(EmotiomComplateFragment.class,bundle);

        return fragment;
    }

}
