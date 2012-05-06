package com.ahutpt.lesson;

import java.util.HashMap;
import java.util.Map;

import com.ahutpt.lesson.lesson.Lesson;
import com.ahutpt.lesson.lesson.LessonManager;
import com.ahutpt.lesson.time.Timetable;
import com.mobclick.android.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditLessonActivity extends Activity {
	
	Lesson lesson,prevLesson;
	int week,time;
	String lessonName,lessonAlias,lessonPlace,teacherName;
	EditText etLessonName,etLessonAlias,etLessonPlace,etTeacherName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(!LessonManager.loaded)
			new LessonManager(this);
		
		week = getIntent().getExtras().getInt("week");
		time = getIntent().getExtras().getInt("time");
		
		setContentView(R.layout.edit_lesson);
		
		if(time==1||time==3){
			//是否延续前两节
			prevLesson = LessonManager.getLessonAt(week, time-1, this);
			if(prevLesson!=null){
				new AlertDialog.Builder(this).setTitle("是否延续前两节课")
				.setMessage("这节课的前两节有课，\n是否延续前两节课？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						LessonManager.addOrEdit(prevLesson.name, "", prevLesson.place, prevLesson.teacher, week, time);
						EditLessonActivity.this.finish();
					}
				}).setNegativeButton("取消", null).show();
			}
		}
		
		TextView tvCurrentTime = (TextView)findViewById(R.id.tvCurrentTime);
		tvCurrentTime.setText(Timetable.weekname[week] + Timetable.lessontime_name[time]);
		
		etLessonName = (EditText)findViewById(R.id.etLessonName);
		etLessonAlias = (EditText)findViewById(R.id.etLessonAlias);
		etLessonPlace = (EditText)findViewById(R.id.etLessonPlace);
		etTeacherName = (EditText)findViewById(R.id.etTeacherName);
		
		lesson = LessonManager.getLessonAt(week, time, this);
		if(lesson != null){
			etLessonName.setText(lesson.name);
			etLessonAlias.setText(lesson.alias);
			etLessonPlace.setText(lesson.place);
			etTeacherName.setText(lesson.teacher);	
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
		LessonManager.addOrEdit(lessonName, lessonAlias, lessonPlace, teacherName, week, time);
		
		Map<String, String> loglesson= new HashMap<String, String>();
		loglesson.put("name", lessonName);
		loglesson.put("alias", lessonAlias);
		loglesson.put("place", lessonPlace);
		loglesson.put("teacher", teacherName);
		MobclickAgent.onEvent(this, "delete_lesson", loglesson);
	}

}
