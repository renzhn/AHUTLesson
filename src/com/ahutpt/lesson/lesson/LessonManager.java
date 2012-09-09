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
		
		String name,alias,place,teacher,homework;
		int week,time,startweek,endweek;
		
		String[] cols = {"lessonname","lessonalias","lessonplace","teachername","startweek","endweek","homework","week","time"};
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
			startweek = lessoninfo.getInt(4);
			endweek = lessoninfo.getInt(5);
			homework = lessoninfo.getString(6);
			week = lessoninfo.getInt(7);
			time = lessoninfo.getInt(8);
			lessons[week][time] = new Lesson(name,alias,place,teacher,startweek,endweek,homework,week,time,context);
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
		if(JSONDATA.contentEquals(""))return false;
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		db.delete("lesson", null, null);
		try {
			JSONTokener jsonParser = new JSONTokener(JSONDATA);
			JSONObject lesson;
			JSONArray lessons;
			try{
				lessons = (JSONArray)jsonParser.nextValue();
				if(lessons==null)
				return false;
				for(int i = 0;i < lessons.length(); i++){
					lesson = lessons.getJSONObject(i);
					String lessonName = lesson.getString("lessonname");
					String lessonAlias = lesson.getString("lessonalias");
					String teacherName = lesson.getString("teachername");
					int week = lesson.getInt("week");
					int time = lesson.getInt("time");
					int startweek = lesson.getInt("startweek");
					int endweek = lesson.getInt("endweek");
					String lessonPlace = lesson.getString("place");
					ContentValues cv = new ContentValues();
					cv.put("lessonname", lessonName);
					cv.put("lessonalias", lessonAlias);
					cv.put("teachername", teacherName);
					cv.put("lessonplace", lessonPlace);
					cv.put("startweek", startweek);
					cv.put("endweek", endweek);
					cv.put("week", week);
					cv.put("time", time);
					db.insert("lesson", null, cv);
				}
			}catch(NullPointerException e){
				e.printStackTrace();
				return false;
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
	
	public static void addOrEdit(String lessonName,String lessonAlias,String lessonPlace,String teacherName,int startWeek,int endWeek,int week,int time){
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
		new LessonManager(context);
	}
	
	public static void editHomework(int week, int time, String input) {
		// 编辑作业
		if(DBHelper==null)return;
		if(input == null || input.contentEquals(""))return;
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("homework", input);
		db.update("lesson", cv, "week=" + String.valueOf(week) + " AND time=" + String.valueOf(time), null);
		if(lessons[week][time] != null){
			lessons[week][time].homework = input;
			lessons[week][time].hasHomework = true;
		}
	}
	
	public static void deleteHomework(int week, int time){
		// 删除作业
		if(DBHelper==null)return;
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("homework", "");
		db.update("lesson", cv, "week=" + String.valueOf(week) + " AND time=" + String.valueOf(time), null);
		if(lessons[week][time] != null){
			lessons[week][time].homework = null;
			lessons[week][time].hasHomework = false;
		}
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
