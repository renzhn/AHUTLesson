package com.ahutpt.lesson;

import java.util.HashMap;
import java.util.Map;

import com.ahutpt.lesson.lesson.Lesson;
import com.ahutpt.lesson.lesson.LessonManager;
import com.ahutpt.lesson.time.Timetable;
import com.mobclick.android.MobclickAgent;

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
		if(!((week >= 0 && week <= 6)&&(time >=0 && time <= 4))){
			this.finish();
		}
		
		setContentView(R.layout.lesson);
		
		tvLessonName = (TextView)findViewById(R.id.tvLessonName);
		tvLessonPlace = (TextView)findViewById(R.id.tvLessonPlace);
		tvTeacherName = (TextView)findViewById(R.id.tvTeacherName);
		tvLessonTime = (TextView)findViewById(R.id.tvLessonTime);


		tvCurrentTime = (TextView)findViewById(R.id.tvCurrentTime);
		tvCurrentTime.setText(Timetable.weekname[week] + Timetable.lessontime_name[time]);

		lesson = LessonManager.getLessonAt(week, time, this);
		if(lesson != null){
			tvLessonName.setText(lesson.name);
			tvLessonPlace.setText(lesson.place);
			tvTeacherName.setText(lesson.teacher);	
		}
		
		tvLessonTime.setText(Timetable.begintime[time] + " ~ " +Timetable.endtime[time]);
	}
	
	@Override
	protected void onResume() {
		// 删除或修改后重新载入
		super.onResume();

		MobclickAgent.onResume(this);
		
		tvLessonName = (TextView)findViewById(R.id.tvLessonName);
		tvLessonPlace = (TextView)findViewById(R.id.tvLessonPlace);
		tvTeacherName = (TextView)findViewById(R.id.tvTeacherName);
		tvLessonTime = (TextView)findViewById(R.id.tvLessonTime);
		
		lesson = LessonManager.getLessonAt(week, time, this);
		if(lesson != null){
			tvLessonName.setText(lesson.name);
			tvLessonPlace.setText(lesson.place);
			tvTeacherName.setText(lesson.teacher);	
		}
	}
	
	@Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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
			Map<String, String> loglesson= new HashMap<String, String>();
			loglesson.put("name", lesson.name);
			loglesson.put("alias", lesson.alias);
			loglesson.put("place", lesson.place);
			loglesson.put("teacher", lesson.teacher);
			MobclickAgent.onEvent(this, "delete_lesson", loglesson);
			lesson.delete();
			LessonActivity.this.finish();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

}
