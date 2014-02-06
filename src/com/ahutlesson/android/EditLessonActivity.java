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

	private Timetable timetable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

		lesson = LessonManager.getInstance(this).getLessonAt(week, time);
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
			editLesson();
			finish();
			return true;
		case R.id.menu_edit_cancel:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void editLesson() {
		lessonName = etLessonName.getText().toString();
		lessonAlias = etLessonAlias.getText().toString();
		lessonPlace = etLessonPlace.getText().toString();
		teacherName = etTeacherName.getText().toString();
		String startWeekText = etStartWeek.getText().toString();
		String endWeekText = etEndWeek.getText().toString();
		try {
			startWeek = Integer.valueOf(startWeekText);
			endWeek = Integer.valueOf(endWeekText);
			if (startWeek > endWeek) throw new Exception("结束周不能小于起始周");
		} catch (NumberFormatException ex) {
			makeToast("起始周或结束周输入错误");
			startWeek = timetable.numOfWeek;
			endWeek = startWeek + 1;
		} catch (Exception ex) {
			makeToast(ex.getMessage());
			startWeek = timetable.numOfWeek;
			endWeek = startWeek + 1;
		}
		
		LessonManager lessonManager = LessonManager.getInstance(this);
		if (lessonManager.hasLessonAt(week, time)) {
			lessonManager.editLessonAt(lessonName, lessonAlias, lessonPlace,
				teacherName, startWeek, endWeek, week, time);
		} else {
			lessonManager.addLessonAt(lessonName, lessonAlias, lessonPlace,
					teacherName, startWeek, endWeek, week, time);
		}
		
		MainActivity.needRefresh = true;
	}

}
