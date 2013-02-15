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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LessonActivity extends BaseActivity {
	private int week, time;
	private Lesson lesson;
	private TextView tvLessonName, tvLessonPlace, tvTeacherName, tvLessonWeek,
			tvHomework, tvLessonTime, tvCurrentTime;
	private Button btnEditHomework, btnDeleteHomework;

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
			return;
		}

		setContentView(R.layout.lesson);

		tvLessonName = (TextView) findViewById(R.id.tvLessonName);
		tvLessonPlace = (TextView) findViewById(R.id.tvLessonPlace);
		tvTeacherName = (TextView) findViewById(R.id.tvTeacherName);
		tvLessonWeek = (TextView) findViewById(R.id.tvLessonWeek);
		tvHomework = (TextView) findViewById(R.id.tvHomework);
		tvLessonTime = (TextView) findViewById(R.id.tvLessonTime);

		tvCurrentTime = (TextView) findViewById(R.id.tvCurrentTime);
		tvCurrentTime.setText(timetable.weekname[week]
				+ timetable.lessontime_name[time]);

		btnEditHomework = (Button) findViewById(R.id.btnEditHomework);
		btnDeleteHomework = (Button) findViewById(R.id.btnDeleteHomework);

		btnEditHomework.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				editHomework();
			}
		});
		btnDeleteHomework.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				deleteHomework();
			}
		});

		tvLessonTime.setText(timetable.begintime[time] + " ~ "
				+ timetable.endtime[time]);
	}

	protected void editHomework() {
		// ������ҵ
		if (lesson == null)
			return;
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("�༭��ҵ");
		alert.setMessage("�����뱾�γ̵���ҵ");

		final EditText input = new EditText(this);
		alert.setView(input);
		if (lesson.hasHomework && lesson.homework != null
				&& !lesson.homework.contentEquals("")) {
			input.setText(lesson.homework);
		}
		alert.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				if (!value.contentEquals("")) {
					lesson.homework = value;
					lesson.hasHomework = true;
					lessonManager.editHomework(week, time, lesson.homework);
					tvHomework.setText(lesson.homework);
				}
			}
		});

		alert.setNegativeButton("ȡ��", null);
		alert.show();
	}

	protected void deleteHomework() {
		// �����ҵ
		if (lesson == null)
			return;
		if (!lesson.hasHomework)
			return;
		new AlertDialog.Builder(LessonActivity.this).setTitle("�����ҵ")
				.setMessage("ȷ����ձ��γ���ҵ��")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						lesson.homework = null;
						lesson.hasHomework = false;
						lessonManager.deleteHomework(week, time);
						tvHomework.setText("��");
					}

				}).setNegativeButton("ȡ��", null).show();
	}

	@Override
	protected void onResume() {
		// ɾ�����޸ĺ���������
		super.onResume();
		lesson = lessonManager.getLessonAt(week, time);
		if (lesson != null) {
			tvLessonName.setText(lesson.name);
			tvLessonPlace.setText(lesson.place);
			tvLessonWeek.setText("��" + lesson.startweek + "~" + lesson.endweek
					+ "��");
			tvTeacherName.setText(lesson.teacher);
			if (lesson.homework != null && !lesson.homework.contentEquals("")) {
				tvHomework.setText(lesson.homework);
			}
			btnEditHomework.setVisibility(View.VISIBLE);
			btnDeleteHomework.setVisibility(View.VISIBLE);
		} else {
			btnEditHomework.setVisibility(View.GONE);
			btnDeleteHomework.setVisibility(View.GONE);
		}
		
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.lesson, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_edit_lesson:
			Intent i = new Intent(this, EditLessonActivity.class);
			i.putExtra("week", week);
			i.putExtra("time", time);
			startActivity(i);
			return true;
		case R.id.menu_delete_lesson:
			if(lesson == null) break;
			new AlertDialog.Builder(LessonActivity.this)
					.setTitle("ɾ���γ�")
					.setMessage("ȷ��ɾ�����γ̣�")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									if (lesson != null) {
										MobclickAgent.onEvent(
												LessonActivity.this,
												"delete_lesson", lesson.name
														+ " : " + lesson.place
														+ " : "
														+ lesson.teacher);
										lesson.delete();
									}
									LessonActivity.this.finish();
								}

							}).setNegativeButton("ȡ��", null).show();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
		return false;
	}

}