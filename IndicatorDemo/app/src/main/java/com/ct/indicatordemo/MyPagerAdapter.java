package com.ct.indicatordemo;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MyPagerAdapter extends PagerAdapter{//List<View> 指定后 指向的是地址，当关联的list改变时，它指向的是地址也相应改变

	private List<View> mListViews;
	private boolean ischange = false;
	private View mCurrentView;
	
	public MyPagerAdapter(List<View> mListViews) {
		this.mListViews = mListViews;
	}
	
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		if(ischange)
		{
			((ViewPager) arg0).removeAllViews();//刷新的时候先destroy然后instantiateItem
		}
		else {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}
	}

	@Override
	public void finishUpdate(View arg0) {
		ischange = false;//完成更新
	}

	@Override
	public int getCount() {
		return mListViews.size();
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		mCurrentView = (View)object;
	}
	public View getPrimaryItem() {
         return mCurrentView;//获得当前显示的view
    }
	
	public View getItem(int index) {
        return mListViews.get(index);//获得view
   }

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		View v = mListViews.get(arg1);
		((ViewPager) arg0).addView(mListViews.get(arg1), 0);
		return mListViews.get(arg1);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (arg1);
	}

	@Override
	public Parcelable saveState() {
	return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return POSITION_NONE;
	}

	@Override
	public void notifyDataSetChanged() {//重绘当前可见区域
		// TODO Auto-generated method stub
		ischange = true;
		super.notifyDataSetChanged();//适配器更新
	}


}
