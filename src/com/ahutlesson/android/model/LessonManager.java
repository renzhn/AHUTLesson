package com.ahutlesson.android.model;

import java.util.ArrayList;
import java.util.Iterator;

import com.ahutlesson.android.utils.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class LessonManager {
	
	private static LessonManager lessonManager;
	
	private Context context;
	private DatabaseHelper DBHelper;
	private Lesson lessons[][];
	
	public LessonManager(Context context0){
		context = context0;
		DBHelper = new DatabaseHelper(context, "ahutlesson");
		getAllLessons();
	}
	
	public static LessonManager getInstance(Context context) {
		if(lessonManager == null) {
			lessonManager = new LessonManager(context);
		}
		return lessonManager;
	}
	
	public Lesson[][] getLessons() {
		if (lessons == null)
			getAllLessons();
		return lessons;
	}
	
	public void getAllLessons(){
		lessons = new Lesson[7][5];
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		
		String name,alias,place,teacher,homework;
		int lid,week,time,startweek,endweek;
		
		String[] cols = {"lid","lessonname","lessonalias","lessonplace","teachername","startweek","endweek","homework","week","time"};
		Cursor lessoninfo = db.query("lesson", cols, null, null, null, null, null);
		if(lessoninfo.getCount()==0){
			lessoninfo.close();
			db.close();
			return;
		}
		lessoninfo.moveToFirst();
		do{
			lid = lessoninfo.getInt(0);
			name =  lessoninfo.getString(1);
			alias = lessoninfo.getString(2);
			place = lessoninfo.getString(3);
			teacher = lessoninfo.getString(4);
			startweek = lessoninfo.getInt(5);
			endweek = lessoninfo.getInt(6);
			homework = lessoninfo.getString(7);
			week = lessoninfo.getInt(8);
			time = lessoninfo.getInt(9);
			lessons[week][time] = new Lesson(lid, name, alias, place, teacher, startweek, endweek, homework, week, time);
		}while(lessoninfo.moveToNext());
		lessoninfo.close();
		db.close();
		return;
	}
	
	public Lesson getLessonAt(int week0, int time0){
		if(!Timetable.isValidWeek(week0)||!Timetable.isValidTime(time0))return null;
		return lessons[week0][time0];
	}
	
	public void deleteLessonAt(int week0, int time0){
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		db.delete("lesson", "week=" + week0 + " and time="
				+ time0, null);
		db.close();
		lessons[week0][time0] = null;
	}
	
	public void deleteLesson(Lesson lesson) {
		deleteLessonAt(lesson.week, lesson.time);
	}
	
	public void EditLessonAt(String lessonName,String lessonAlias,String lessonPlace,String teacherName,int startWeek,int endWeek,int week,int time){
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		LessonManager lessonManager = LessonManager.getInstance(context);
		Lesson lesson = lessonManager.getLessonAt(week, time);
		if(lesson == null) return;
		if(lessonName.contentEquals(""))
			return;
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
			cv.put("startweek", startWeek);
			cv.put("endweek", endWeek);
			cv.put("teachername", teacherName);
			db.insert("lesson", null, cv);
		}else{
			ContentValues cv = new ContentValues();
			cv.put("lessonname", lessonName);
			cv.put("lessonalias", lessonAlias);
			cv.put("lessonplace", lessonPlace);
			cv.put("startweek", startWeek);
			cv.put("endweek", endWeek);
			cv.put("teachername", teacherName);
			db.update("lesson", cv, "week=" + String.valueOf(week) + " AND time=" + String.valueOf(time), null);
		}
		result.close();
		db.close();
		String homework = (lesson != null && lesson.homework != null) ? lesson.homework : "";
		Lesson tmpLesson = new Lesson(lesson.lid, lessonName, lessonAlias, lessonPlace, teacherName, startWeek, endWeek, homework, week, time);
		lessonManager.lessons[week][time] = tmpLesson;
	}
	
	public void editHomework(int week, int time, String input) {
		if(input == null || input.contentEquals("")) return;
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("homework", input);
		db.update("lesson", cv, "week=" + String.valueOf(week) + " AND time=" + String.valueOf(time), null);
		if(lessons[week][time] != null){
			lessons[week][time].homework = input;
			lessons[week][time].hasHomework = true;
		}
	}
	
	public void deleteHomework(int week, int time){
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("homework", "");
		db.update("lesson", cv, "week=" + String.valueOf(week) + " AND time=" + String.valueOf(time), null);
		if(lessons[week][time] != null){
			lessons[week][time].homework = null;
			lessons[week][time].hasHomework = false;
		}
	}
	
	public void deleteAllHomework(){
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("homework", "");
		db.update("lesson", cv, null, null);
		for(int i = 0; i < 7; i++){
			for(int j = 0; j < 5; j++){
				if(lessons[i][j] == null) continue;
				lessons[i][j].homework = null;
				lessons[i][j].hasHomework = false;
			}
		}
	}
	
	public void deleteDB() {
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		db.delete("lesson", null, null);
		db.close();
		lessons = new Lesson[7][5];
	}

	public void lessonlistToDB(ArrayList<Lesson> lessonlist) {
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		db.delete("lesson", null, null);
		Iterator<Lesson> i = lessonlist.iterator();
		while(i.hasNext()) {
			Lesson lesson = i.next();
			ContentValues cv = new ContentValues();
			cv.put("lid", lesson.lid);
			cv.put("lessonname", lesson.name);
			cv.put("lessonalias", lesson.alias);
			cv.put("teachername", lesson.teacher);
			cv.put("lessonplace", lesson.place);
			cv.put("startweek",lesson.startweek);
			cv.put("endweek", lesson.endweek);
			cv.put("week", lesson.week);
			cv.put("time", lesson.time);
			db.insert("lesson", null, cv);
		}
		db.close();
		lessonManager = new LessonManager(context);
	}
	
	public void setLessondbVersion(String version) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString("lessondbver", version);
		editor.commit();
	}
	
	public String getLessondbVersion() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("lessondbver", "0");
	}
}
