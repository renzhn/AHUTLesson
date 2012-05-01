package com.ahutpt.lesson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class LessonActivity extends Activity {
	private int week,time;
	private Lesson lesson;
	private TextView tvLessonName,tvLessonPlace,tvTeacherName,tvLessonTime,tvCurrentTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		week = getIntent().getExtras().getInt("week");
		time = getIntent().getExtras().getInt("time");
		setContentView(R.layout.lesson);

		Timetable timetable = new Timetable(this);
		
		tvLessonName = (TextView)findViewById(R.id.tvLessonName);
		tvLessonPlace = (TextView)findViewById(R.id.tvLessonPlace);
		tvTeacherName = (TextView)findViewById(R.id.tvTeacherName);
		tvLessonTime = (TextView)findViewById(R.id.tvLessonTime);


		tvCurrentTime = (TextView)findViewById(R.id.tvCurrentTime);
		tvCurrentTime.setText(Timetable.weekname[week] + Timetable.lessontime_name[time]);

		lesson = new Lesson(week,time,this);
		if(lesson.exist){
			tvLessonName.setText(lesson.name);
			tvLessonPlace.setText(lesson.place);
			tvTeacherName.setText(lesson.teacher);	
		}
		
		tvLessonTime.setText(timetable.getBeginTime(time) + " ~ " + timetable.getEndTime(time));
	}
	
	@Override
	protected void onResume() {
		// 删除或修改后重新载入
		super.onResume();
		
		tvLessonName = (TextView)findViewById(R.id.tvLessonName);
		tvLessonPlace = (TextView)findViewById(R.id.tvLessonPlace);
		tvTeacherName = (TextView)findViewById(R.id.tvTeacherName);
		tvLessonTime = (TextView)findViewById(R.id.tvLessonTime);
		
		lesson = new Lesson(week,time,this);
		if(lesson.exist){
			tvLessonName.setText(lesson.name);
			tvLessonPlace.setText(lesson.place);
			tvTeacherName.setText(lesson.teacher);	
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.lesson, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_edit_lesson:
			Intent i = new Intent(this,EditLessonActivity.class);
			i.putExtra("week", week);
			i.putExtra("time", time);
			startActivity(i);
			return true;
		case R.id.menu_delete_lesson:
			lesson.delete();
			LessonActivity.this.finish();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

}
