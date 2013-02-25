package com.ahutlesson.android;

import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.LessonManager;
import com.ahutlesson.android.model.Timetable;
import com.umeng.analytics.MobclickAgent;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class EditLessonActivity extends BaseActivity {

	private Lesson lesson;
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

		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(timetable.weekName[week] + timetable.lessontimeName[time]);

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
		case R.id.menu_edit_ok:
			EditLesson();
			finish();
			return true;
		case R.id.menu_edit_cancel:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void EditLesson() {
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

		lessonManager.EditLessonAt(lessonName, lessonAlias, lessonPlace,
				teacherName, startWeek, endWeek, week, time);
	}

}
