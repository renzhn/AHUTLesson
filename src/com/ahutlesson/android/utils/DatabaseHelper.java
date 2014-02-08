package com.ahutlesson.android.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

public class DatabaseHelper extends SQLiteOpenHelper {
	DatabaseHelper(Context context, String name, CursorFactory cursorFactory,
			int version) {
		super(context, name, cursorFactory, version);
	}
	
	public static final int dbVersion = 2;
	public static final String lessonTableName = "lesson";
	public static final String createLessonTableCmd = "CREATE TABLE " + lessonTableName + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, lid INTEGER, lessonname TEXT, teachername TEXT, lessonplace TEXT, startweek INTEGER, endweek INTEGER, week INTEGER ,time INTEGER);";
	
	public DatabaseHelper(Context baseContext, String name) {
		super(baseContext,name,null,dbVersion);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createLessonTableCmd);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1 && newVersion == 2) {
			dropColumn(db, lessonTableName, createLessonTableCmd, new String[]{ "homework" , "lessonalias" });
		}
	}
	
	// http://stackoverflow.com/questions/8442147/how-to-delete-or-add-column-in-sqlite
	private void dropColumn(SQLiteDatabase db,
	        String tableName,
	        String createTableCmd,
	        String[] colsToRemove) {

	    List<String> updatedTableColumns = getTableColumns(db, tableName);
	    // Remove the columns we don't want anymore from the table's list of columns
	    updatedTableColumns.removeAll(Arrays.asList(colsToRemove));

	    String columnsSeperated = TextUtils.join(",", updatedTableColumns);

	    db.execSQL("ALTER TABLE " + tableName + " RENAME TO " + tableName + "_old;");

	    // Creating the table on its new format (no redundant columns)
	    db.execSQL(createTableCmd);

	    // Populating the table with the data
	    db.execSQL("INSERT INTO " + tableName + "(" + columnsSeperated + ") SELECT "
	            + columnsSeperated + " FROM " + tableName + "_old;");
	    db.execSQL("DROP TABLE " + tableName + "_old;");
	}
	
	public List<String> getTableColumns(SQLiteDatabase db, String tableName) {
	    ArrayList<String> columns = new ArrayList<String>();
	    String cmd = "pragma table_info(" + tableName + ");";
	    Cursor cur = db.rawQuery(cmd, null);

	    while (cur.moveToNext()) {
	        columns.add(cur.getString(cur.getColumnIndex("name")));
	    }
	    cur.close();

	    return columns;
	}
}