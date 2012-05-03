package com.ahutpt.lesson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LessonManager {
	
	private static DatabaseHelper DBHelper;
	private Context context;
	
	public LessonManager(Context context0){
		context = context0;
	}
	
	public Lesson[] getAllLessons(){
		
		Lesson[] lessons = new Lesson[35];
		
		DBHelper = new DatabaseHelper(context,"ahutlesson");  
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		
		String name,alias,place,teacher;
		int week,time;
		
		String[] cols = {"lessonname","lessonalias","lessonplace","teachername","week","time"};
		Cursor lessoninfo = db.query("lesson", cols, null, null, null, null, null);
		if(lessoninfo.getCount()==0){
			lessoninfo.close();
			db.close();
			return null;
		}
		lessoninfo.moveToFirst();
		int i = 0;
		do{
			name =  lessoninfo.getString(0);
			alias = lessoninfo.getString(1);
			place = lessoninfo.getString(2);
			teacher = lessoninfo.getString(3);
			week = lessoninfo.getInt(4);
			time = lessoninfo.getInt(5);
			if(name==null||alias==null||place==null||teacher==null)continue;
			lessons[i] = new Lesson(name,alias,place,teacher,week,time,context);
			i++;
		}while(lessoninfo.moveToNext());
		lessoninfo.close();
		db.close();
		return lessons;
	}
	

	public void addOrEdit(String lessonName,String lessonAlias,String lessonPlace,String teacherName,int week,int time){
		SQLiteDatabase db = DBHelper.getWritableDatabase();
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
		result.close();
		db.close();
	}
}
