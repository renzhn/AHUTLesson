package com.ahutlesson.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.LessonManager;
import com.ahutlesson.android.model.Timetable;
import com.ahutlesson.android.model.User;
import com.ahutlesson.android.model.UserManager;
import com.umeng.analytics.MobclickAgent;

public class LessonDetailActivity extends BaseActivity {
	
	private int week, time;
	private Lesson lesson;
	private TextView tvLessonName, tvLessonPlace, tvTeacherName, tvLessonWeek,
			tvHomework, tvLessonTime, tvCurrentTime;
	private Button btnEditHomework, btnDeleteHomework;
	
	private LessonManager lessonManager;
	private Timetable timetable;
	private UserManager userManager;
	private User user;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		week = getIntent().getExtras().getInt("week");
		time = getIntent().getExtras().getInt("time");

		if (!((week >= 0 && week <= 6) && (time >= 0 && time <= 4))) {
			this.finish();
			return;
		}
        
		setContentView(R.layout.lessondetail);

		tvLessonName = (TextView) findViewById(R.id.tvLessonName);
		tvLessonPlace = (TextView) findViewById(R.id.tvLessonPlace);
		tvTeacherName = (TextView) findViewById(R.id.tvTeacherName);
		tvLessonWeek = (TextView) findViewById(R.id.tvLessonWeek);
		tvHomework = (TextView) findViewById(R.id.tvHomework);
		tvLessonTime = (TextView) findViewById(R.id.tvLessonTime);

		tvCurrentTime = (TextView) findViewById(R.id.tvCurrentTime);

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

	}

	@Override
	protected void onResume() {
		// 删除或修改后重新载入
		super.onResume();
		
		lessonManager = LessonManager.getInstance(this);
		timetable = Timetable.getInstance(this);
		userManager = UserManager.getInstance(this);
		user = userManager.getUser();

		tvCurrentTime.setText(timetable.weekname[week]
				+ timetable.lessontime_name[time]);
		tvLessonTime.setText(timetable.begintime[time] + " ~ "
				+ timetable.endtime[time]);
		
		
		lesson = lessonManager.getLessonAt(week, time);
		if (lesson != null) {
			tvLessonName.setText(lesson.name);
			tvLessonPlace.setText(lesson.place);
			tvLessonWeek.setText("第" + lesson.startweek + "~" + lesson.endweek
					+ "周");
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

	protected void editHomework() {
		// 添加作业
		if (lesson == null)
			return;
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("编辑作业");
		alert.setMessage("请输入本课程的作业");

		final EditText input = new EditText(this);
		alert.setView(input);
		if (lesson.hasHomework && lesson.homework != null
				&& !lesson.homework.contentEquals("")) {
			input.setText(lesson.homework);
		}
		alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
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

		alert.setNegativeButton("取消", null);
		alert.show();
	}

	protected void deleteHomework() {
		// 清空作业
		if (lesson == null)
			return;
		if (!lesson.hasHomework)
			return;
		new AlertDialog.Builder(LessonDetailActivity.this).setTitle("清空作业")
				.setMessage("确定清空本课程作业？")
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						lesson.homework = null;
						lesson.hasHomework = false;
						lessonManager.deleteHomework(week, time);
						tvHomework.setText("无");
					}

				}).setNegativeButton(R.string.cancel, null).show();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.edit)
			.setIcon(android.R.drawable.ic_menu_edit)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(user.uname)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(R.string.delete)
			.setIcon(android.R.drawable.ic_menu_delete)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
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
			if (lesson == null)
				break;
			new AlertDialog.Builder(LessonDetailActivity.this)
					.setTitle("删除课程")
					.setMessage("确定删除本课程？")
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									if (lesson != null) {
										LessonManager.getInstance(
												LessonDetailActivity.this)
												.deleteLesson(lesson);
									}
									LessonDetailActivity.this.finish();
								}

							}).setNegativeButton(R.string.cancel, null).show();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
		return false;
	}
	
	
}
