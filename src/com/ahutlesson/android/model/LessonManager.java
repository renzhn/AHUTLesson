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

	public LessonManager(Context context0) {
		context = context0;
		DBHelper = new DatabaseHelper(context, "ahutlesson");
		getAllLessons();
	}

	public static LessonManager getInstance(Context context) {
		if (lessonManager == null) {
			lessonManager = new LessonManager(context);
		}
		return lessonManager;
	}

	public Lesson[][] getLessons() {
		if (lessons == null)
			getAllLessons();
		return lessons;
	}

	public void getAllLessons() {
		lessons = new Lesson[7][5];
		SQLiteDatabase db = DBHelper.getWritableDatabase();

		String name, place, teacher;
		int lid, week, time, startweek, endweek;

		String[] cols = { "lid", "lessonname", "lessonplace", "teachername",
				"startweek", "endweek", "week", "time" };
		Cursor lessoninfo = db.query("lesson", cols, null, null, null, null,
				null);
		if (lessoninfo.getCount() == 0) {
			lessoninfo.close();
			db.close();
			return;
		}
		lessoninfo.moveToFirst();
		do {
			lid = lessoninfo.getInt(0);
			name = lessoninfo.getString(1);
			place = lessoninfo.getString(2);
			teacher = lessoninfo.getString(3);
			startweek = lessoninfo.getInt(4);
			endweek = lessoninfo.getInt(5);
			week = lessoninfo.getInt(6);
			time = lessoninfo.getInt(7);
			lessons[week][time] = new Lesson(lid, name, place, teacher,
					startweek, endweek, week, time);
		} while (lessoninfo.moveToNext());
		lessoninfo.close();
		db.close();
		return;
	}

	public Lesson getLessonAt(int week0, int time0) {
		if (!Timetable.isValidWeek(week0) || !Timetable.isValidTime(time0))
			return null;
		return lessons[week0][time0];
	}

	public void deleteLessonAt(int week0, int time0) {
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		db.delete("lesson", "week=" + week0 + " and time=" + time0, null);
		db.close();
		lessons[week0][time0] = null;
	}

	public void deleteLesson(Lesson lesson) {
		deleteLessonAt(lesson.week, lesson.time);
	}

	public boolean hasLessonAt(int week, int time) {
		return (getLessonAt(week, time) != null);
	}

	public void addLessonAt(String lessonName, String lessonPlace,
			String teacherName, int startWeek, int endWeek, int week, int time) {
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("lid", 0);
		cv.put("week", week);
		cv.put("time", time);
		cv.put("lessonname", lessonName);
		cv.put("lessonplace", lessonPlace);
		cv.put("startweek", startWeek);
		cv.put("endweek", endWeek);
		cv.put("teachername", teacherName);
		db.insert("lesson", null, cv);
		db.close();
		lessonManager.lessons[week][time] = new Lesson(0, lessonName,
				lessonPlace, teacherName, startWeek, endWeek, week, time);
		;
	}

	public void editLessonAt(String lessonName, String lessonPlace,
			String teacherName, int startWeek, int endWeek, int week, int time) {
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		LessonManager lessonManager = LessonManager.getInstance(context);
		Lesson lesson = getLessonAt(week, time);
		if (lesson == null)
			return;
		Cursor result;
		String[] selection = { "lessonname" };
		result = db.query("lesson", selection, "week=" + String.valueOf(week)
				+ " and time=" + String.valueOf(time), null, null, null, null);
		ContentValues cv = new ContentValues();
		cv.put("lessonname", lessonName);
		cv.put("lessonplace", lessonPlace);
		cv.put("startweek", startWeek);
		cv.put("endweek", endWeek);
		cv.put("teachername", teacherName);
		db.update("lesson", cv, "week=" + String.valueOf(week) + " AND time="
				+ String.valueOf(time), null);
		result.close();
		db.close();
		lessonManager.lessons[week][time] = new Lesson(lesson.lid, lessonName,
				lessonPlace, teacherName, startWeek, endWeek, week, time);
		;
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
		while (i.hasNext()) {
			Lesson lesson = i.next();
			ContentValues cv = new ContentValues();
			cv.put("lid", lesson.lid);
			cv.put("lessonname", lesson.name);
			cv.put("teachername", lesson.teacher);
			cv.put("lessonplace", lesson.place);
			cv.put("startweek", lesson.startweek);
			cv.put("endweek", lesson.endweek);
			cv.put("week", lesson.week);
			cv.put("time", lesson.time);
			db.insert("lesson", null, cv);
		}
		db.close();
		lessonManager = new LessonManager(context);
	}

	public void setLessondbVersion(String version) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString("lessondbver", version);
		editor.commit();
	}

	public String getLessondbVersion() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getString("lessondbver", "0");
	}
}
