package com.ahutpt.lesson.lesson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.ahutpt.lesson.helper.DatabaseHelper;
import com.ahutpt.lesson.time.Timetable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LessonManager {

	private static Context context;
	private static DatabaseHelper DBHelper;
	public static Lesson lessons[][] = new Lesson[7][5];
	public static boolean loaded = false;
	
	public LessonManager(Context context0){
		context = context0;
		DBHelper = new DatabaseHelper(context, "ahutlesson");
		getAllLessons();
		loaded = true;
	}
	
	public void getAllLessons(){
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		
		String name,alias,place,teacher;
		int week,time;
		
		String[] cols = {"lessonname","lessonalias","lessonplace","teachername","week","time"};
		Cursor lessoninfo = db.query("lesson", cols, null, null, null, null, null);
		if(lessoninfo.getCount()==0){
			lessoninfo.close();
			db.close();
			return;
		}
		lessoninfo.moveToFirst();
		do{
			name =  lessoninfo.getString(0);
			alias = lessoninfo.getString(1);
			place = lessoninfo.getString(2);
			teacher = lessoninfo.getString(3);
			week = lessoninfo.getInt(4);
			time = lessoninfo.getInt(5);
			lessons[week][time] = new Lesson(name,alias,place,teacher,week,time,context);
		}while(lessoninfo.moveToNext());
		lessoninfo.close();
		db.close();
		return;
	}
	
	public static Lesson getLessonAt(int week0, int time0, Context context0){
		if(!Timetable.isValidWeek(week0)||!Timetable.isValidTime(time0))return null;
		return lessons[week0][time0];
	}
	
	public static void deleteLessonAt(int week0, int time0){
		if(DBHelper==null)return;
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		db.delete("lesson", "week=" + week0 + " and time="
				+ time0, null);
		db.close();
		lessons[week0][time0] = null;
		new LessonManager(context);
	}
	
	public static boolean updateDB(String JSONDATA) {
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		db.delete("lesson", null, null);
		try {
			JSONTokener jsonParser = new JSONTokener(JSONDATA);
			JSONObject lesson;
			JSONArray lessons;
			lessons = (JSONArray)jsonParser.nextValue();
			if(lessons==null)return false;
			for(int i = 0;i < lessons.length(); i++){
				lesson = lessons.getJSONObject(i);
				String lessonName = lesson.getString("lessonname");
				String lessonAlias = lesson.getString("lessonalias");
				String teacherName = lesson.getString("teachername");
				int week = lesson.getInt("week");
				int time = lesson.getInt("time");
				String lessonPlace = lesson.getString("place");
				ContentValues cv = new ContentValues();
				cv.put("lessonname", lessonName);
				cv.put("lessonalias", lessonAlias);
				cv.put("teachername", teacherName);
				cv.put("lessonplace", lessonPlace);
				cv.put("week", week);
				cv.put("time", time);
				db.insert("lesson", null, cv);
			}
			db.close();
			clean();
			new LessonManager(context);
			return true;
		} catch (JSONException ex) {
			// 异常处理代码
			return false;
		} catch(ClassCastException ex){
			return false;
		}
	}
	
	public static void addOrEdit(String lessonName,String lessonAlias,String lessonPlace,String teacherName,int week,int time){
		if(DBHelper==null)return;
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
		lessons[week][time] = new Lesson(lessonName,lessonAlias,lessonPlace,teacherName,week,time,context);
		new LessonManager(context);
	}

	public static void deleteDB() {
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		db.delete("lesson", null, null);
		db.close();
		clean();
		new LessonManager(context);
	}
	
	public static void clean(){
		lessons = new Lesson[7][5];
	}
}
