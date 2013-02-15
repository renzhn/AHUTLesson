package com.ahutlesson.android.lesson;

import com.ahutlesson.android.time.Timetable;

import android.content.Context;

public class Lesson {

	private Context context;
	public String name,alias,place,teacher;
	public String homework = "";
	public int week,time,startweek,endweek;
	public boolean hasHomework = false;
	public boolean beforeStart = false, afterEnd = false, isInRange = true;

	public Lesson(String name0, String alias0, String place0, String teacher0,int startweek0, int endweek0, String homework0, 
			int week0, int time0, Context context0) {
		context = context0;
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
		
		Timetable timetable = Timetable.getInstance(context);
		
		if(timetable.numOfWeek < startweek ){
			beforeStart = true;
		}else if(timetable.numOfWeek > endweek){
			afterEnd = true;
		}
		
		if(beforeStart || afterEnd)
			isInRange = false;
	}
	
	public long getCurrentLessonEndTime(int advanceMode) {
		if(canAppend()){
			return Timetable.getInstance(context).getCurrentLessonEndTime(time + 1, advanceMode);
		}else{
			return Timetable.getInstance(context).getCurrentLessonEndTime(time, advanceMode);	
		}
	}
	
	public long getNextTime(int advanceMode) {
		// 下一次上此课的时间（毫秒）
		return Timetable.getInstance(context).getNextLessonBeginTime(week, time, advanceMode);
	}

	public long getNextEndTime(int advanceMode) {
		return Timetable.getInstance(context).getNextLessonEndTime(week, time, advanceMode);
	}

	public int isNowHaving() {
		// -1还没上，0正在上，1上过了
		return Timetable.getInstance(context).isNowHavingLesson(week, time);
	}

	public void delete() {
		LessonManager.getInstance(context).deleteLessonAt(week, time);
	}

	public String toString() {
		return "week: " + String.valueOf(week) + ", time: "
				+ String.valueOf(time) + ", name: " + name;
	}

	public boolean canAppend() {
		// 后两节有课
		if (time == 0 || time == 2) {
			Lesson appendLesson =  LessonManager.getInstance(context).getLessonAt(week, time + 1);
			if (appendLesson!=null) {
				if (appendLesson.name.contentEquals(name)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isAppended() {
		// 前两节有课
		if (time == 1 || time == 3) {
			Lesson appendLesson = LessonManager.getInstance(context).getLessonAt(week, time - 1);
			if (appendLesson!=null) {
				if (appendLesson.name.contentEquals(name)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int appendMode(){
		if(canAppend())return 1;
		if(isAppended())return -1;
		return 0;
	}

}
