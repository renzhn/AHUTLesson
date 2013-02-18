package com.ahutlesson.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.alarm.Alert;
import com.ahutlesson.android.model.LessonManager;
import com.ahutlesson.android.model.Timetable;
import com.ahutlesson.android.model.UserManager;
import com.ahutlesson.android.ui.main.Grid;
import com.ahutlesson.android.ui.main.HomeworkFragment;
import com.ahutlesson.android.ui.main.LessonListFragmentAdapter;
import com.ahutlesson.android.ui.main.ScheduleView;
import com.ahutlesson.android.utils.ChangeLog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class MainActivity extends BaseFragmentActivity implements OnNavigationListener {
	
	private static final String[] TITLES = {"当日课程", "课程总表", "课后作业"};
	private static int viewMode = 0;

	// VIEW
	private static final int TODAY_VIEW = 0;
	private static final int GRID_VIEW = 1;
	private static final int HOMEWORK_VIEW = 2;

	//TODAY_VIEW
	private LessonListFragmentAdapter mLessonListFragmentAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	
	// GRID_VIEW
	private static ScheduleView scheduleView;

	// MENU
	private static final int SETTING = 0;
	private static final int HELP = 1;
	private static final int CLEAR_HOMEWORK = 2;
	private static final int TIMETABLEVIEWER = 3;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		disableHomeButton();
		actionBar.setDisplayShowTitleEnabled(false);
		
		//if Not Login
		UserManager userManager = UserManager.getInstance(this);
		if(!userManager.hasLocalUser()) {
			Intent i = new Intent(this,RegisterActivity.class);
			startActivity(i);
			finish();
			return;
		}
		
		// List Navigation
		Context context = actionBar.getThemedContext();
		ArrayAdapter<CharSequence> list = new ArrayAdapter<CharSequence>(context, R.layout.sherlock_spinner_item);
		list.add(TITLES[0]);
		list.add(TITLES[1]);
		list.add(TITLES[2]);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(list, this);

		// ShowView
		showView();

		// Changelog
		ChangeLog cl = new ChangeLog(this);
		if (cl.firstRun()) {
			cl.getLogDialog().show();
		}
		
		// Update
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.onError(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(true);
		UmengUpdateAgent.setUpdateListener(null);
		UmengUpdateAgent.update(this);
	}
	
	// 显示视图
	public void showView() {
		switch (viewMode) {
		case TODAY_VIEW:
			setContentView(R.layout.today);

			mLessonListFragmentAdapter = new LessonListFragmentAdapter(getSupportFragmentManager());
			
			mPager = (ViewPager) findViewById(R.id.pager);
			mPager.setAdapter(mLessonListFragmentAdapter);

			mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
			mIndicator.setViewPager(mPager);
			mIndicator.setCurrentItem(Timetable.getCurrentWeekDay());
			break;
		case GRID_VIEW:
			// 绘制课表
			scheduleView = new ScheduleView(this, LessonManager.getInstance(this).lessons);
			setContentView(scheduleView);
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
	
	@Override
	protected void onResume() {
		super.onResume();
		
		switch(viewMode){
		case TODAY_VIEW:
			refreshTodayView();
			break;
		case GRID_VIEW:
			scheduleView.invalidate();
			break;
		}
		
		setDateInfo();
		
		Alert.setAlarm(this);
		MobclickAgent.onResume(this);
	}
	
	private void setDateInfo() {
		LinearLayout layoutDateInfo = (LinearLayout) getLayoutInflater().inflate(R.layout.dateinfo, null);
		TextView text = new TextView(this);
		text.setGravity(Gravity.CENTER);
		text.setText(dateInfo());
		text.setTextSize(15);
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
		menu.add(0, TIMETABLEVIEWER, Menu.NONE, "课表浏览器");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case SETTING:
			Intent i = new Intent(this, PreferenceActivity.class);
			startActivity(i);
			return true;
		case HELP:
			openHelpDialog();
			return true;
		case CLEAR_HOMEWORK:
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("清空作业")
					.setMessage("确定清空所有课程作业？")
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									LessonManager.getInstance(MainActivity.this).deleteAllHomework();
									showView();
								}
							}).setNegativeButton(R.string.cancel, null).show();
			return true;
		case TIMETABLEVIEWER:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("课表浏览器");
			alert.setMessage("请输入学号：");

			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			alert.setView(input);

			alert.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input.getText().toString();
							Intent i = new Intent(MainActivity.this, TimetableViewerActivity.class);
							i.putExtra("xh", value);
							startActivity(i);
						}
					});

			alert.setNegativeButton(R.string.cancel, null);

			alert.show();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	// 日期信息
	public String dateInfo() {
		Timetable timetable = Timetable.getInstance(this);
		int numOfWeek = timetable.numOfWeek;
		if(numOfWeek > 0) {
			return "第" + String.valueOf(numOfWeek) + "周" + " " + timetable.weekname[timetable.weekDay];
		}else{
			return "未开学 " + timetable.weekname[timetable.weekDay];
		}
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

	public void refreshTodayView() {
		mLessonListFragmentAdapter.notifyDataSetChanged();
	}
	
}
