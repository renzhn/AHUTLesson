package com.ahutpt.lesson;

import java.text.SimpleDateFormat;
import com.mobclick.android.MobclickAgent;
import com.mobclick.android.UmengUpdateListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.ahutpt.lesson.R;
import com.ahutpt.lesson.helper.ChangeLog;
import com.ahutpt.lesson.time.Alert;
import com.ahutpt.lesson.time.Timetable;
import com.ahutpt.lesson.view.Grid;
import com.ahutpt.lesson.view.ScheduleView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends SherlockActivity{
	
	private static ScheduleView scheduleView;
	private Alert alert;
	private boolean noticeUpdate;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		
		alert = new Alert(MainActivity.this);

		ChangeLog cl = new ChangeLog(this);
		if (cl.firstRun()){
			new AlertDialog.Builder(this).setTitle("在线下载课表")
			.setMessage("是否进入课表下载页面？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(MainActivity.this, ManageDBActivity.class);
					startActivity(i);
				}
			}).setNegativeButton("取消", null).show();
			
			cl.getLogDialog().show();
		}
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		noticeUpdate = preferences.getBoolean("notice_update", true);
		
		//MobclickAgent.setDebugMode(true);
		MobclickAgent.setUpdateOnlyWifi(false);
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.onError(this);
		if(noticeUpdate){
			MobclickAgent.update(this);
			MobclickAgent.updateAutoPopup = true;
			MobclickAgent.setUpdateListener(new UmengUpdateListener(){
				public void onUpdateReturned(int arg) {
					
				}
			});
		}
		
		//主要布局
		LinearLayout mainLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.main, null);
		setContentView(mainLayout);
		
		//绘制课表
		scheduleView = new ScheduleView(MainActivity.this);
		mainLayout.addView(scheduleView);
		scheduleView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openLessonDetail();
			}
		});
	}

	public void openLessonDetail() {
		// 打开课程详情
		Intent i = new Intent(this, LessonActivity.class);
		i.putExtra("week", Grid.markWeek);
		i.putExtra("time", Grid.markTime);
		this.startActivity(i);
	}
	@Override
	protected void onResume() {
		super.onResume();
		
		//日期信息
		getSupportActionBar().setSubtitle(dateInfo());
		
		alert.setAlarm();
		MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	public static void refresh(){
		if(scheduleView!=null)
			scheduleView.invalidate();
	}
    
	public String dateInfo(){
		SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("M月d日"); 
		String  date  =  sDateFormat.format(new java.util.Date()); 
		int numOfWeek = Timetable.getNumOfWeekSincePeriod();
		return date + " " + "第" + String.valueOf(numOfWeek) + "周" + " " + Timetable.weekname[Timetable.getCurrentWeekDay()];
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_setting:
			Intent i = new Intent(this, SettingActivity.class);
			startActivity(i);
			return true;
		case R.id.menu_help:
			openHelpDialog();
			return true;
		case R.id.menu_exit:
			super.onDestroy();
			this.finish();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	
	private void openHelpDialog() {
		new AlertDialog.Builder(this)
				.setTitle("使用说明")
				.setMessage(R.string.help_message)
				.setPositiveButton("确定",null).show();
	}

	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			System.out.println("VersionInfo" + "Exception" + e);
		}
		return versionName;
	}
}