package com.ahutpt.lesson.lesson;

import com.ahutpt.lesson.time.Timetable;

import android.content.Context;

public class Lesson {

	private static Timetable timetable;
	private Context context;
	public String name;
	public String alias;
	public String place;
	public String teacher;
	public int week;
	public int time;

	public boolean exist = false;

	public Lesson(String name0, String alias0, String place0, String teacher0,
			int week0, int time0, Context context0) {
		context = context0;
		name = name0;
		alias = alias0;
		if (alias.contentEquals(""))
			alias = name;
		place = place0;
		teacher = teacher0;
		week = week0;
		time = time0;
		exist = true;
	}

	public void loadTime(){
		if(timetable==null){
			timetable = new Timetable(context);
		}
		if(!Timetable.loaded)
			new Timetable(context);
	}
	
	public long getCurrentLessonEndTime(int advanceMode) {
		loadTime();
		if(canAppend()){
			return timetable.getCurrentLessonEndTime(time + 1, advanceMode);
		}else{
			return timetable.getCurrentLessonEndTime(time, advanceMode);	
		}
	}
	
	public long getNextTime(int advanceMode) {
		// 下一次上此课的时间（毫秒）
		loadTime();
		return Timetable.getNextLessonBeginTime(week, time, advanceMode);
	}

	public long getNextEndTime(int advanceMode) {
		loadTime();
		return Timetable.getNextLessonEndTime(week, time, advanceMode);
	}

	public int isNowHaving() {
		// -1还没上，0正在上，1上过了, 2不存在
		if (!exist)
			return 2;
		return Timetable.isNowHavingLesson(week, time);
	}

	public void delete() {
		LessonManager.deleteLessonAt(week, time);
		exist = false;
	}

	public String toString() {
		return "week: " + String.valueOf(week) + ", time: "
				+ String.valueOf(time) + ", name: " + name;
	}

	public boolean canAppend() {
		// 后两节有课
		if (time == 0 || time == 2) {
			Lesson appendLesson =  LessonManager.getLessonAt(week, time + 1, context);
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
			Lesson appendLesson = LessonManager.getLessonAt(week, time - 1, context);
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
