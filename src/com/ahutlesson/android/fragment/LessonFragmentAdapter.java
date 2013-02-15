package com.ahutlesson.android.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class LessonFragmentAdapter extends FragmentStatePagerAdapter {

	public static final String[] weekNames= new String[] {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
	
	public LessonFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int weekDay) {
		return LessonFragment.newInstance(weekDay);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return weekNames[position];
	}

	@Override
	public int getCount() {
		return 7;
	}
	
	@Override
	public int getItemPosition(Object object) {
	    return POSITION_NONE;
	}
}
