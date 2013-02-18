package com.ahutlesson.android.api;

import java.util.ArrayList;
import java.util.Iterator;

import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.utils.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class LocalAccessor {

	private static LocalAccessor accessor;
	
	private DatabaseHelper DBHelper;
	
	public LocalAccessor(Context context) {
		DBHelper = new DatabaseHelper(context, "ahutlesson");
	}

	public static LocalAccessor getInstance(Context context) {
		if(accessor == null){
			accessor = new LocalAccessor(context);
		}
		return accessor;
	}
	
}
