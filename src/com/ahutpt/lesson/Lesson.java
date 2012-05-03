package com.ahutpt.lesson;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Lesson {

	private static DatabaseHelper DBHelper;
	private static Timetable timetable;
	private Context context;
	public String name;
	public String alias;
	public String place;
	public String teacher;
	public int week;
	public int time;
	
	public boolean exist = false;

	public Lesson(String name0,String alias0,String place0,String teacher0,int week0,int time0,Context context0){
		context = context0;
		timetable = new Timetable(context);
		name = name0;
		alias = alias0;
		if(alias.contentEquals(""))alias = name;
		place = place0;
		teacher = teacher0;
		week = week0;
		time = time0;
		exist = true;
	}
	
	public Lesson(int week0,int time0,Context context0){
		context = context0;
		timetable = new Timetable(context);
		DBHelper = new DatabaseHelper(context,"ahutlesson");  
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		String[] cols = {"lessonname","lessonalias","lessonplace","teachername"};
		Cursor lessoninfo = db.query("lesson", cols, "week=" + String.valueOf(week0) + " and time=" + String.valueOf(time0), null, null, null, null);
		if(lessoninfo.getCount()==0){
			lessoninfo.close();
			db.close();
			return;
		}
		lessoninfo.moveToFirst();
		name = lessoninfo.getString(0);
		alias = lessoninfo.getString(1);
		if(alias.contentEquals(""))alias = name;
		place = lessoninfo.getString(2);
		teacher = lessoninfo.getString(3);
		week = week0;
		time = time0;
		exist = true;
		lessoninfo.close();
		db.close();
	}
	
	public long getNextTime(int advanceMode){
		//下一次上此课的时间（毫秒）
		return timetable.getNextLessonBeginTime(week,time,advanceMode);
	}
	
	public long getNextEndTime(int advanceMode) {
		return timetable.getNextLessonEndTime(week,time,advanceMode);
	}
	
	public int isNowHaving(){
		//-1还没上，0正在上，1上过了, 2不存在
		if(!exist)
			return 2;
		return timetable.isNowHavingLesson(week,time);
	}

	public void delete() {
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		db.delete("lesson", "week=" + String.valueOf(week) + " and time=" + String.valueOf(time), null);
		db.close();
		exist = false;
	}
	
	public String toString(){
		return "week: " + String.valueOf(week) + ", time: " + String.valueOf(time) + ", name: " + name;
	}

}
