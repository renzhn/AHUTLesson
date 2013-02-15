package com.ahutlesson.android;

import com.ahutlesson.android.lesson.Lesson;
import com.ahutlesson.android.lesson.LessonManager;
import com.ahutlesson.android.time.Timetable;
import com.umeng.analytics.MobclickAgent;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

public class EditLessonActivity extends BaseActivity {

	private Lesson lesson, prevLesson, curLesson;
	private int week, time, startWeek, endWeek;
	private String lessonName, lessonAlias, lessonPlace, teacherName;
	private EditText etLessonName, etLessonAlias, etLessonPlace, etTeacherName,
			etStartWeek, etEndWeek;

	private LessonManager lessonManager;
	private Timetable timetable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		lessonManager = LessonManager.getInstance(this);
		timetable = Timetable.getInstance(this);
		
		week = getIntent().getExtras().getInt("week");
		time = getIntent().getExtras().getInt("time");

		if (!((week >= 0 && week <= 6) && (time >= 0 && time <= 4))) {
			this.finish();
		}

		setContentView(R.layout.edit_lesson);

		if (time == 1 || time == 3) {
			// 是否延续前两节
			prevLesson = lessonManager.getLessonAt(week, time - 1);
			curLesson = lessonManager.getLessonAt(week, time);
			if (prevLesson != null && curLesson == null) {
				new AlertDialog.Builder(this)
						.setTitle("是否延续前两节课")
						.setMessage("这节课的前两节有课，\n是否延续前两节课？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										lessonManager.addOrEdit(
												prevLesson.name, "",
												prevLesson.place,
												prevLesson.teacher,
												prevLesson.startweek,
												prevLesson.endweek, week, time);
										EditLessonActivity.this.finish();
									}
								}).setNegativeButton("取消", null).show();
			}
		}

		actionBar.setTitle(
				timetable.weekname[week] + timetable.lessontime_name[time]);

		etLessonName = (EditText) findViewById(R.id.etLessonName);
		etLessonAlias = (EditText) findViewById(R.id.etLessonAlias);
		etLessonPlace = (EditText) findViewById(R.id.etLessonPlace);
		etTeacherName = (EditText) findViewById(R.id.etTeacherName);
		etStartWeek = (EditText) findViewById(R.id.etStartWeek);
		etEndWeek = (EditText) findViewById(R.id.etEndWeek);

		lesson = lessonManager.getLessonAt(week, time);
		if (lesson != null) {
			etLessonName.setText(lesson.name);
			etLessonAlias.setText(lesson.alias);
			etLessonPlace.setText(lesson.place);
			etTeacherName.setText(lesson.teacher);
			etStartWeek.setText(String.valueOf(lesson.startweek));
			etEndWeek.setText(String.valueOf(lesson.endweek));
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.memu_edit_ok:
			addOrEditLesson();
			finish();
			return true;
		case R.id.menu_edit_cancel:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void addOrEditLesson() {
		lessonName = etLessonName.getText().toString();
		lessonAlias = etLessonAlias.getText().toString();
		lessonPlace = etLessonPlace.getText().toString();
		teacherName = etTeacherName.getText().toString();
		String startWeekText = etStartWeek.getText().toString();
		String endWeekText = etEndWeek.getText().toString();
		if (startWeekText.contentEquals("")) {
			startWeek = timetable.numOfWeek;
		} else {
			startWeek = Integer.valueOf(startWeekText);
		}

		if (endWeekText.contentEquals("")) {
			endWeek = startWeek + 1;
		} else {
			endWeek = Integer.valueOf(endWeekText);
		}

		lessonManager.addOrEdit(lessonName, lessonAlias, lessonPlace,
				teacherName, startWeek, endWeek, week, time);
	}

}
