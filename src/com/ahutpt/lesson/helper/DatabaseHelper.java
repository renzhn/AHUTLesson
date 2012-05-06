package com.ahutpt.lesson.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	DatabaseHelper(Context context, String name, CursorFactory cursorFactory,
			int version) {
		super(context, name, cursorFactory, version);
	}
	

	public DatabaseHelper(Context baseContext, String name) {
		super(baseContext,name,null,1);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE lesson (_id INTEGER PRIMARY KEY AUTOINCREMENT, lessonname TEXT, lessonalias TEXT, teachername TEXT, lessonplace TEXT, week INTEGER ,time INTEGER);");
		db.execSQL("CREATE TABLE note (_id INTEGER PRIMARY KEY AUTOINCREMENT, lessonid INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}

}