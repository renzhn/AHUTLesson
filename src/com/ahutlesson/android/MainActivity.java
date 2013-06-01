package com.ahutlesson.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.alarm.Alarm;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.LessonManager;
import com.ahutlesson.android.model.Timetable;
import com.ahutlesson.android.model.TimetableSetting;
import com.ahutlesson.android.model.UserManager;
import com.ahutlesson.android.service.CheckUnreadService;
import com.ahutlesson.android.ui.HomeworkFragment;
import com.ahutlesson.android.ui.LessonListFragmentAdapter;
import com.ahutlesson.android.ui.timetable.ScheduleView;
import com.ahutlesson.android.utils.ChangeLog;
import com.ahutlesson.android.utils.GlobalContext;
import com.ahutlesson.android.utils.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class MainActivity extends BaseFragmentActivity implements
		OnNavigationListener {

	private static final String[] TITLES = { "当日课程", "课程总表", "课后作业" };
	private static int viewMode = 0;

	FragmentManager fragmentManager;
	
	// VIEW
	private static final int TODAY_VIEW = 0;
	private static final int GRID_VIEW = 1;
	private static final int HOMEWORK_VIEW = 2;

	private View dateInfoView;
	private TextView tvDateInfo;

	// TODAY_VIEW
	private View todayView;
	private LessonListFragmentAdapter mLessonListFragmentAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	public static List<Integer> unreadLessonForum = new ArrayList<Integer>();

	// GRID_VIEW
	private static ScheduleView scheduleView;

	// MENU
	private static final int MENU_SETTING = 0;
	private static final int MENU_CLEAR_HOMEWORK = 1;
	private static final int MENU_TIMETABLEVIEWER = 2;
	private static final int MENU_PROFILE = 3;
	private static final int MENU_MESSAGE = 4;
	private static final int MENU_NOTICE = 5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GlobalContext.mainActivity = this;
		disableHomeButton();
		actionBar.setDisplayShowTitleEnabled(false);
		
		//if not login
		UserManager userManager = UserManager.getInstance(this);
		if(!userManager.hasLocalUser()) {
			openActivity(RegisterActivity.class);
			finish();
			return;
		}
		
		// list navigation
		Context context = actionBar.getThemedContext();
		ArrayAdapter<CharSequence> list = new ArrayAdapter<CharSequence>(context, R.layout.sherlock_spinner_item);
		list.add(TITLES[0]);
		list.add(TITLES[1]);
		list.add(TITLES[2]);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(list, this);
		
		//dateInfo
		dateInfoView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.actionbar_customview, null, false);
		tvDateInfo = (TextView) dateInfoView.findViewById(R.id.tvCumstomView);
		tvDateInfo.setText(dateInfo());
		actionBar.setCustomView(dateInfoView);
		actionBar.setDisplayShowCustomEnabled(true);

		//Main View
		fragmentManager = getSupportFragmentManager();
				
		//Today View
		todayView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.today, null, false);
		mLessonListFragmentAdapter = new LessonListFragmentAdapter(getSupportFragmentManager());

		mPager = (ViewPager) todayView.findViewById(R.id.pager);
		mPager.setAdapter(mLessonListFragmentAdapter);

		mIndicator = (TitlePageIndicator) todayView.findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		mIndicator.setCurrentItem(Timetable.getCurrentWeekDay());
		
		// Changelog
		ChangeLog cl = new ChangeLog(MainActivity.this);
		if (cl.firstRun()) {
			cl.getLogDialog().show();
		}
		
		//ImageLoader
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
	        .cacheInMemory()
	        .cacheOnDisc()
	        .build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
	        .defaultDisplayImageOptions(defaultOptions)
	        .build();
		ImageLoader.getInstance().init(config); 
        
		//CheckUnread
		startService(new Intent(MainActivity.this, CheckUnreadService.class));
		
		// Update
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean autoUpdate = prefs.getBoolean("auto_update", true);
		if(autoUpdate)
			new UpdateTask().execute();
		
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		
		MobclickAgent.onError(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(new Intent(this, CheckUnreadService.class));
	}

	// 显示视图
	public void showView() {
		switch (viewMode) {
		case TODAY_VIEW:
			setContentView(todayView);
			break;
		case GRID_VIEW:
			// 绘制课表
			scheduleView = new ScheduleView(this,
					LessonManager.getInstance(this).lessons, true);
			setContentView(scheduleView);
			break;
		case HOMEWORK_VIEW:
			setContentView(R.layout.homework);

			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			transaction.replace(R.id.frameLayoutFragment,
					new HomeworkFragment());
			transaction.commit();
			break;
		}
	}

	public static boolean needRefresh = false;
	
	public void refreshTodayView() {
		if(mLessonListFragmentAdapter != null) 
			mLessonListFragmentAdapter.notifyDataSetChanged();
		invalidateOptionsMenu();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (needRefresh) {
			if(mLessonListFragmentAdapter != null) 
				mLessonListFragmentAdapter.notifyDataSetChanged();
			if (scheduleView != null)
				scheduleView.invalidate();
			tvDateInfo.setText(dateInfo());
		}
		
		Alarm.setAlarm(this);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
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
			menu.add(viewMode, MENU_PROFILE, Menu.NONE, "个人中心")
					.setIcon(R.drawable.account)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM
									| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			menu.add(viewMode, MENU_MESSAGE, Menu.NONE, "我的消息")
					.setIcon(R.drawable.message)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM
									| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			menu.add(viewMode, MENU_NOTICE, Menu.NONE, "我的提醒")
					.setIcon(R.drawable.forum)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM
									| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			menu.add(viewMode, MENU_SETTING, Menu.NONE, "设置")
					.setIcon(R.drawable.preference)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM
									| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			break;
		case HOMEWORK_VIEW:
			menu.add(viewMode, MENU_CLEAR_HOMEWORK, Menu.NONE, "清空所有作业")
					.setIcon(R.drawable.delete)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM
									| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			break;

		}
		menu.add(0, MENU_TIMETABLEVIEWER, Menu.NONE, "课表浏览器");
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_PROFILE:
			openActivity(ProfileActivity.class);
			return true;
		case MENU_MESSAGE:
			openActivity(MessageActivity.class);
			return true;
		case MENU_NOTICE:
			openActivity(NoticeActivity.class);
			return true;
		case MENU_SETTING:
			openActivity(PreferenceActivity.class);
			return true;
		case MENU_CLEAR_HOMEWORK:
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("清空作业")
					.setMessage("确定清空所有课程作业？")
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									LessonManager
											.getInstance(MainActivity.this)
											.deleteAllHomework();
									showView();
								}
							}).setNegativeButton(R.string.cancel, null).show();
			return true;
		case MENU_TIMETABLEVIEWER:
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
							Intent i = new Intent(MainActivity.this,
									TimetableViewerActivity.class);
							i.putExtra("uxh", value);
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
		if (numOfWeek > 0) {
			return "第" + String.valueOf(numOfWeek) + "周" + " "
					+ timetable.weekName[timetable.weekDay];
		} else {
			return "未开学 " + timetable.weekName[timetable.weekDay];
		}
	}
	
	public class UpdateTask extends AsyncTask<Integer, Integer, Void> {

		@Override
		protected Void doInBackground(Integer... arg0) {
			try {
				JSONObject ret = AHUTAccessor.getInstance(MainActivity.this).checkUpdate();
				if(ret.has("upToDate")) Util.log("upToDate");
				if(ret.has("hasNewLessondbVer")) {
					Util.log("found New Lessondb Version");
					UserManager.getInstance(MainActivity.this).updateLessonDB();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							refreshTodayView();
						}
					});
					makeToast("已更新课表数据");
				}
				if(ret.has("hasNewTimetableSetting")) {
					Util.log("found New Timetable Setting");
					TimetableSetting timetableSetting = new TimetableSetting();
					JSONObject retSetting = ret.getJSONObject("newTimetableSetting");
					timetableSetting.year = retSetting.getInt("year");
					timetableSetting.month = retSetting.getInt("month");
					timetableSetting.day = retSetting.getInt("day");
					timetableSetting.setSeason(retSetting.getInt("season"));
					Timetable.getInstance(MainActivity.this).setTimetableSetting(timetableSetting);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							tvDateInfo.setText(dateInfo());
						}
					});
					makeToast("已更新时间表设置");
				}
			} catch (Exception ex) {
				Util.log(ex.getMessage());
			}
			return null;
		}
		
	}
}
