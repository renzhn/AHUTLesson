package com.ahutpt.lesson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Lesson {

	private static SQLiteDatabase db;
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
		db = DBHelper.getWritableDatabase();
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
	
	public long getNextTime(){
		//下一次上此课的时间（毫秒）
		return timetable.getNextLessonTime(week,time);
	}
	
	public long getNextEndTime() {
		return timetable.getNextLessonEndTime(week,time);
	}
	
	public int isNowHaving(){
		//-1还没上，0正在上，1上过了
		return timetable.isNowHavingLesson(week,time);
	}
	
	public void addOrEdit(String lessonName,String lessonAlias,String lessonPlace,String teacherName){
		db = DBHelper.getWritableDatabase();
		if(lessonName.contentEquals(""))
			return ;
		Cursor result;
		String[] selection = { "lessonname" };
		result = db.query("lesson", selection, "week=" + String.valueOf(week) + " and time=" + String.valueOf(time), null, null, null, null);
		if(result.getCount()==0){
			ContentValues cv = new ContentValues();
			cv.put("week", week);
			cv.put("time", time);
			cv.put("lessonname", lessonName);
			cv.put("lessonalias", lessonAlias);
			cv.put("lessonplace", lessonPlace);
			cv.put("teachername", teacherName);
			db.insert("lesson", null, cv);
		}else{
			ContentValues cv = new ContentValues();
			cv.put("lessonname", lessonName);
			cv.put("lessonalias", lessonAlias);
			cv.put("lessonplace", lessonPlace);
			cv.put("teachername", teacherName);
			db.update("lesson", cv, "week=" + String.valueOf(week) + " AND time=" + String.valueOf(time), null);
		}
		exist = true;
		result.close();
		db.close();
	}

	public void delete() {
		db = DBHelper.getWritableDatabase();
		db.delete("lesson", "week=" + String.valueOf(week) + " and time=" + String.valueOf(time), null);
		db.close();
		exist = false;
	}
	
	public String toString(){
		return "week: " + String.valueOf(week) + ", time: " + String.valueOf(time) + ", name: " + name;
	}

}
