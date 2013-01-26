package com.ahutpt.lesson;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ahutpt.lesson.fragment.HomeworkFragment;
import com.ahutpt.lesson.fragment.LessonFragmentAdapter;
import com.ahutpt.lesson.lesson.LessonManager;
import com.ahutpt.lesson.time.Alert;
import com.ahutpt.lesson.time.Timetable;
import com.ahutpt.lesson.utils.ChangeLog;
import com.ahutpt.lesson.view.Grid;
import com.ahutpt.lesson.view.ScheduleView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class MainActivity extends SherlockFragmentActivity implements
		ActionBar.OnNavigationListener {

	private Alert alert;
	private boolean noticeUpdate;

	private static int viewMode = 0;

	// ACTION
	private static final int SETTING = 0;
	private static final int HELP = 1;
	private static final int CLEAR_HOMEWORK = 2;
	
	// VIEW
	private static final int TODAY_VIEW = 0;
	private static final int GRID_VIEW = 1;
	private static final int HOMEWORK_VIEW = 2;

	//TODAY_VIEW
	private LessonFragmentAdapter mAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	
	// GRID_VIEW
	private static ScheduleView scheduleView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		super.onCreate(savedInstanceState);

		// Alert
		alert = new Alert(MainActivity.this);

		// 准备UI
		// List Navigation
		ArrayAdapter<CharSequence> list = new ArrayAdapter<CharSequence>(this,
				R.layout.sherlock_spinner_item);
		list.add("当日课程");
		list.add("课程总表");
		list.add("课后作业");
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(list, this);

		// DateInfo
		LinearLayout layoutDateInfo = (LinearLayout) getLayoutInflater()
				.inflate(R.layout.dateinfo, null);
		TextView text = new TextView(this);
		text.setGravity(Gravity.CENTER);
		text.setText(this.dateInfo());
		text.setTextSize(15);
		text.setTextColor(Color.BLACK);
		text.setPadding(10, 0, 0, 0);
		text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(MainActivity.this, TimetableSettingActivity.class);
				startActivity(i);
			}
		});
		layoutDateInfo.addView(text);
		actionBar.setCustomView(layoutDateInfo);
		actionBar.setDisplayShowCustomEnabled(true);

		// ShowView
		showView();

		// Changelog
		openChangelogDialog();
		
		// Update
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		noticeUpdate = preferences.getBoolean("notice_update", true);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.onError(this);
		if (noticeUpdate) {
			UmengUpdateAgent.update(this);
		}
	}
	
	// 显示视图
	public void showView() {
		switch (viewMode) {
		case TODAY_VIEW:
			setContentView(R.layout.today);

			mAdapter = new LessonFragmentAdapter(getSupportFragmentManager());
			
			mPager = (ViewPager) findViewById(R.id.pager);
			mPager.setAdapter(mAdapter);

			mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
			mIndicator.setViewPager(mPager);
			mIndicator.setCurrentItem(Timetable.getCurrentWeekDay());
			break;
		case GRID_VIEW:
			LinearLayout mainLayout = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.main, null);
			setContentView(mainLayout);

			// 绘制课表
			if(!LessonManager.loaded)
				new LessonManager(MainActivity.this);
			scheduleView = new ScheduleView(MainActivity.this, LessonManager.lessons);
			mainLayout.addView(scheduleView);
			scheduleView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openLessonDetail();
				}
			});
			break;
		case HOMEWORK_VIEW:
			setContentView(R.layout.homework);
			
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.replace(R.id.frameLayoutFragment, HomeworkFragment.newInstance());
			transaction.commit();
			break;
		}
	}
	
	public void refreshTodayView(){
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		switch(viewMode){
		case TODAY_VIEW:
			refreshTodayView();
			break;
		}
		alert.setAlarm();
		MobclickAgent.onResume(this);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// 选择导航菜单
		viewMode = itemPosition;
		showView();
		invalidateOptionsMenu();
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		switch (viewMode) {
		case TODAY_VIEW:
			menu.add(viewMode, SETTING, Menu.NONE, "设置")
					.setIcon(android.R.drawable.ic_menu_preferences)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			break;
		case GRID_VIEW:
			menu.add(viewMode, HELP, Menu.NONE, "帮助")
					.setIcon(android.R.drawable.ic_menu_help)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

			break;
		case HOMEWORK_VIEW:
			menu.add(viewMode, CLEAR_HOMEWORK, Menu.NONE, "清空所有作业")
					.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			break;

		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case SETTING:
			Intent i = new Intent(this, SettingActivity.class);
			startActivity(i);
			return true;
		case HELP:
			openHelpDialog();
			return true;
		case CLEAR_HOMEWORK:
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("清空作业")
					.setMessage("确定清空所有课程作业？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									LessonManager.deleteAllHomework();
									showView();
								}
							}).setNegativeButton("取消", null).show();
			return true;
		case R.id.menu_timetableviewer:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("课表浏览器");
			alert.setMessage("请输入学号：");

			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			alert.setView(input);

			alert.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input.getText().toString();
							Intent i = new Intent(MainActivity.this, TimetableViewerActivity.class);
							i.putExtra("xh", value);
							startActivity(i);
						}
					});

			alert.setNegativeButton("取消", null);

			alert.show();
			return true;
		case R.id.menu_exit:
			this.finish();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	// Changelog Dialog
	private void openChangelogDialog() {
		ChangeLog cl = new ChangeLog(this);
		if (cl.firstRunEver()) {
			Intent i = new Intent(MainActivity.this,
					WizardActivity.class);
			startActivity(i);
		}else if (cl.firstRun()) {
			cl.getLogDialog().show();
		}
	}

	// 日期信息
	public String dateInfo() {
		if (!Timetable.loaded) {
			new Timetable(this);
		}
		return "第" + String.valueOf(Timetable.numOfWeek) + "周" + " " + Timetable.weekname[Timetable.weekDay];
	}

	// 课程详情
	public void openLessonDetail() {
		Intent i = new Intent(this, LessonActivity.class);
		i.putExtra("week", Grid.markWeek);
		i.putExtra("time", Grid.markTime);
		this.startActivity(i);
	}

	// 帮助对话框
	private void openHelpDialog() {
		new AlertDialog.Builder(this).setTitle("使用说明")
				.setMessage(R.string.help_message)
				.setPositiveButton("确定", null).show();
	}
	
}
