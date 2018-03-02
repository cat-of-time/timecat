package com.time.cat.mvp.view.emotion.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/17
 * @discription EmotiomComplateFragment界面Viewpager数据适配器
 * @usage null
 */
public class EmotionPagerAdapter extends PagerAdapter {

	private List<GridView> gvs;

	public EmotionPagerAdapter(List<GridView> gvs) {
		this.gvs = gvs;
	}

	@Override
	public int getCount() {
		return gvs.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView(gvs.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		((ViewPager) container).addView(gvs.get(position));
		return gvs.get(position);
	}

}
