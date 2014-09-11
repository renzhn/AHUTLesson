package com.ahutlesson.android;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.alarm.Alarm;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.GridPosition;
import com.ahutlesson.android.model.LessonManager;
import com.ahutlesson.android.model.Timetable;
import com.ahutlesson.android.model.TimetableSetting;
import com.ahutlesson.android.model.UserManager;
import com.ahutlesson.android.ui.GridView;
import com.ahutlesson.android.utils.ChangeLog;
import com.ahutlesson.android.utils.GlobalContext;
import com.ahutlesson.android.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseActivity {

	private View dateInfoView;
	private TextView tvDateInfo;
	private GridView gridView;

	public static boolean needRefresh = false;

	private static final int MENU_PROFILE = 0;
	private static final int MENU_TIMETABLEVIEWER = 1;
	private static final int MENU_LESSONMATESIMILARITY = 5;
	private static final int MENU_SETTING = 2;
	private static final int MENU_ABOUT = 3;
	private static final int MENU_SHARE = 4;

	private static final int MENU_ADD = 10;
	private static final int MENU_EDIT = 11;
	private static final int MENU_DELETE = 12;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GlobalContext.mainActivity = this;
		disableHomeButton();
		// if not login
		UserManager userManager = UserManager.getInstance(this);
		if (!userManager.hasLocalUser()) {
			openActivity(RegisterActivity.class);
			finish();
			return;
		}

		// dateInfo
		dateInfoView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.actionbar_customview, null, false);
		tvDateInfo = (TextView) dateInfoView.findViewById(R.id.tvCumstomView);
		tvDateInfo.setText(dateInfo());
		actionBar.setCustomView(dateInfoView);
		actionBar.setDisplayShowCustomEnabled(true);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		gridView = new GridView(this, null);
		setContentView(gridView);
		registerForContextMenu(gridView);

		// Changelog
		ChangeLog cl = new ChangeLog(MainActivity.this);
		if (cl.firstRun()) {
			cl.getLogDialog().show();
		}

		// Update
		boolean autoUpdate = prefs.getBoolean("auto_update", true);
		if (autoUpdate)
			new UpdateTask().execute();

		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void refresh() {
		if (tvDateInfo != null) tvDateInfo.setText(dateInfo());
		if (gridView != null) gridView.refreshView();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (needRefresh) {
			refresh();
		}

		Alarm.setAlarm(this);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		needRefresh = true;
		MobclickAgent.onPause(this);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_SHARE, Menu.NONE, R.string.share)
			.setIcon(R.drawable.ic_action_share)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(Menu.NONE, MENU_PROFILE, Menu.NONE, R.string.profile);
		menu.add(Menu.NONE, MENU_TIMETABLEVIEWER, Menu.NONE, R.string.timetable_viewer);
		menu.add(Menu.NONE, MENU_LESSONMATESIMILARITY, Menu.NONE, R.string.lessonmate_similarity);
		menu.add(Menu.NONE, MENU_SETTING, Menu.NONE, R.string.setting);
		menu.add(Menu.NONE, MENU_ABOUT, Menu.NONE, R.string.about);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SHARE:
			openActivity(ShareActivity.class);
			return true;
		case MENU_PROFILE:
			openActivity(ProfileActivity.class);
			return true;
		case MENU_ABOUT:
			openActivity(AboutActivity.class);
			return true;
		case MENU_TIMETABLEVIEWER:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle(R.string.timetable_viewer);
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
		case MENU_LESSONMATESIMILARITY:
			AlertDialog.Builder alert1 = new AlertDialog.Builder(this);

			alert1.setTitle(R.string.lessonmate_similarity);
			alert1.setMessage("“课友度”越高，你与TA上同一节课的课数越多\n" + 
					"请输入学号：");

			final EditText input1 = new EditText(this);
			input1.setInputType(InputType.TYPE_CLASS_NUMBER);
			alert1.setView(input1);
			alert1.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input1.getText().toString();
							Intent i = new Intent(MainActivity.this,
									LessonmateSimilarityActivity.class);
							i.putExtra("uxh", value);
							startActivity(i);
						}
					});

			alert1.setNegativeButton(R.string.cancel, null);

			alert1.show();
			return true;
		case MENU_SETTING:
			openActivity(PreferenceActivity.class);
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (gridView == null) return;
		GridPosition position = gridView.getCurrentGridPosition();
		if (position == null) return;
		if (position.hasLesson) {
			gridView.updateLessonPosition(false);
			menu.add(0, MENU_EDIT, Menu.NONE, R.string.edit);
			menu.add(0, MENU_DELETE, Menu.NONE, R.string.delete);
		} else {
			gridView.updateLessonPosition(true);
			menu.add(0, MENU_ADD, Menu.NONE, R.string.add);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		if (gridView == null) return false;
		GridPosition position = gridView.getCurrentGridPosition();
		if (position == null) return false;
		switch (item.getItemId()) {
		case MENU_ADD:
			Intent intent = new Intent(this, EditLessonActivity.class);
			intent.putExtra("week", position.week);
			intent.putExtra("time", position.time);
			startActivity(intent);
			break;
		case MENU_EDIT:
			Intent intent1 = new Intent(this, EditLessonActivity.class);
			intent1.putExtra("week", position.week);
			intent1.putExtra("time", position.time);
			startActivity(intent1);
			break;
		case MENU_DELETE:
			LessonManager.getInstance(this).deleteLessonAt(position.week,
					position.time);
			refresh();
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onContextMenuClosed(android.view.Menu menu) {
		refresh();
		super.onContextMenuClosed(menu);
	}
	
	public String dateInfo() {
		Timetable timetable = Timetable.getInstance(this);
		timetable.refreshNumOfWeek();
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
				JSONObject ret = AHUTAccessor.getInstance(MainActivity.this)
						.checkUpdate();
				if (ret.has("upToDate"))
					Util.log("upToDate");
				if (ret.has("hasNewLessondbVer")) {
					Util.log("found New Lessondb Version");
					UserManager.getInstance(MainActivity.this).updateLessonDB();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							refresh();
						}
					});
					String latestLessondbVer = ret
							.getString("latestLessondbVer");
					makeToast("已更新课表数据 (" + latestLessondbVer + ")");
				}
				if (ret.has("hasNewTimetableSetting")) {
					Util.log("found New Timetable Setting");
					TimetableSetting timetableSetting = new TimetableSetting();
					JSONObject retSetting = ret
							.getJSONObject("newTimetableSetting");
					timetableSetting.year = retSetting.getInt("year");
					timetableSetting.month = retSetting.getInt("month");
					timetableSetting.day = retSetting.getInt("day");
					timetableSetting.setSeason(retSetting.getInt("season"));
					Timetable.getInstance(MainActivity.this)
							.setTimetableSetting(timetableSetting);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							refresh();
						}
					});
					makeToast("已更新开学时间：" + timetableSetting.getBeginDate());
				}
			} catch (Exception ex) {
				Util.log(ex.getMessage());
			}
			return null;
		}

	}

}
