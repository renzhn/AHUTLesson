package com.ahutpt.lesson;

import com.ahutpt.lesson.lesson.Lesson;
import com.ahutpt.lesson.lesson.LessonManager;
import com.ahutpt.lesson.time.Timetable;
import com.mobclick.android.MobclickAgent;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditLessonActivity extends SherlockActivity {
	
	Lesson lesson,prevLesson;
	int week,time,startWeek,endWeek;
	String lessonName,lessonAlias,lessonPlace,teacherName;
	EditText etLessonName,etLessonAlias,etLessonPlace,etTeacherName,etStartWeek,etEndWeek;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		
		if(!LessonManager.loaded)
			new LessonManager(this);
		
		week = getIntent().getExtras().getInt("week");
		time = getIntent().getExtras().getInt("time");

		if(!((week >= 0 && week <= 6)&&(time >=0 && time <= 4))){
			this.finish();
		}
		
		setContentView(R.layout.edit_lesson);
		
		if(time==1||time==3){
			//是否延续前两节
			prevLesson = LessonManager.getLessonAt(week, time-1, this);
			if(prevLesson!=null){
				new AlertDialog.Builder(this).setTitle("是否延续前两节课")
				.setMessage("这节课的前两节有课，\n是否延续前两节课？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						LessonManager.addOrEdit(prevLesson.name, "", prevLesson.place, prevLesson.teacher, prevLesson.startweek, prevLesson.endweek, week, time);
						EditLessonActivity.this.finish();
					}
				}).setNegativeButton("取消", null).show();
			}
		}
		
		getSupportActionBar().setTitle(Timetable.weekname[week] + Timetable.lessontime_name[time]);
		
		etLessonName = (EditText)findViewById(R.id.etLessonName);
		etLessonAlias = (EditText)findViewById(R.id.etLessonAlias);
		etLessonPlace = (EditText)findViewById(R.id.etLessonPlace);
		etTeacherName = (EditText)findViewById(R.id.etTeacherName);
		etStartWeek = (EditText)findViewById(R.id.etStartWeek);
		etEndWeek = (EditText)findViewById(R.id.etEndWeek);
		
		lesson = LessonManager.getLessonAt(week, time, this);
		if(lesson != null){
			etLessonName.setText(lesson.name);
			etLessonAlias.setText(lesson.alias);
			etLessonPlace.setText(lesson.place);
			etTeacherName.setText(lesson.teacher);	
			etStartWeek.setText(String.valueOf(lesson.startweek));
			etEndWeek.setText(String.valueOf(lesson.endweek));
		}
		
		Button submit = (Button) findViewById(R.id.btnSubmitLesson);
		submit.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				addOrEditLesson();
				EditLessonActivity.this.finish();
			}
		});	
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
	
	protected void addOrEditLesson() {
		lessonName = etLessonName.getText().toString();
		lessonAlias = etLessonAlias.getText().toString();
		lessonPlace = etLessonPlace.getText().toString();
		teacherName = etTeacherName.getText().toString();
		String startWeekText = etStartWeek.getText().toString();
		String endWeekText = etEndWeek.getText().toString();
		if(startWeekText.contentEquals("")){
			startWeek = 1;
		}else{
			startWeek = Integer.valueOf(startWeekText);
		}
		
		if(endWeekText.contentEquals("")){
			endWeek = startWeek + 1;
		}else{
			endWeek = Integer.valueOf(endWeekText);
		}
		
		LessonManager.addOrEdit(lessonName, lessonAlias, lessonPlace, teacherName, startWeek, endWeek, week, time);
		MobclickAgent.onEvent(this, "add_lesson", lessonName + " : " + lessonPlace + " : " + teacherName);
	}

}
