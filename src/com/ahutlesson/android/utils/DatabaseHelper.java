package com.ahutlesson.android.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	DatabaseHelper(Context context, String name, CursorFactory cursorFactory,
			int version) {
		super(context, name, cursorFactory, version);
	}
	
	public static final int dbVersion = 1;
	
	public DatabaseHelper(Context baseContext, String name) {
		super(baseContext,name,null,dbVersion);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE lesson (_id INTEGER PRIMARY KEY AUTOINCREMENT, lid INTEGER, lessonname TEXT, lessonalias TEXT, teachername TEXT, lessonplace TEXT, startweek INTEGER, endweek INTEGER, homework TEXT, week INTEGER ,time INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}