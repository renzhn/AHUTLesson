package com.ahutlesson.android.model;


import android.content.Context;

public class Lesson {
	
	public String name,alias,place,teacher;
	public String homework = "";
	public int lid,week,time,startweek,endweek;
	public boolean hasHomework = false;

	public Lesson(int lid0,String name0, String alias0, String place0, String teacher0,int startweek0, int endweek0, String homework0, int week0, int time0) {
		lid = lid0;
		name = name0;
		alias = alias0;
		place = place0;
		teacher = teacher0;
		startweek = startweek0;
		endweek = endweek0;
		homework = homework0;
		if(homework != null && !homework.contentEquals("")) hasHomework = true;
		week = week0;
		time = time0;
	}
	
	public boolean beforeStart(Context context) {
		return (Timetable.getInstance(context).numOfWeek < startweek);
	}
	
	public boolean afterEnd(Context context) {
		return (Timetable.getInstance(context).numOfWeek > endweek);
	}
	
	public boolean isInRange(Context context) {
		return !beforeStart(context) && !afterEnd(context);
	}

}
