package com.ahutpt.lesson;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditLessonActivity extends Activity {
	
	Lesson lesson;
	int week,time;
	String lessonName,lessonAlias,lessonPlace,teacherName;
	
	EditText etLessonName,etLessonAlias,etLessonPlace,etTeacherName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		week = getIntent().getExtras().getInt("week");
		time = getIntent().getExtras().getInt("time");
		
		setContentView(R.layout.edit_lesson);
		
		TextView tvCurrentTime = (TextView)findViewById(R.id.tvCurrentTime);
		tvCurrentTime.setText(Timetable.weekname[week] + Timetable.lessontime_name[time]);

		etLessonName = (EditText)findViewById(R.id.etLessonName);
		etLessonAlias = (EditText)findViewById(R.id.etLessonAlias);
		etLessonPlace = (EditText)findViewById(R.id.etLessonPlace);
		etTeacherName = (EditText)findViewById(R.id.etTeacherName);
		
		lesson = new Lesson(week,time,this);
		if(lesson.exist){
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

	protected void addOrEditLesson() {
		lessonName = etLessonName.getText().toString();
		lessonAlias = etLessonAlias.getText().toString();
		lessonPlace = etLessonPlace.getText().toString();
		teacherName = etTeacherName.getText().toString();
		lesson.week = this.week;
		lesson.time = this.time;
		lesson.addOrEdit(lessonName, lessonAlias, lessonPlace, teacherName);
	}

}
