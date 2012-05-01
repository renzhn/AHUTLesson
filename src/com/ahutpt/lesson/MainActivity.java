package com.ahutpt.lesson;

import java.text.SimpleDateFormat;
import com.ahutpt.lesson.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private ScheduleView scheduleView;
	private Timetable timetable;
	private Alert alert;
	private AsyncTask<Integer, Integer, Integer> loading;
	private boolean noticeUpdate;
	private boolean loaded = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		noticeUpdate = preferences.getBoolean("notice_update", true);
	}
	
	@Override
	protected void onResume() {
		// 刷新显示
		setContentView(R.layout.loading);
		loaded = false;
		super.onResume();
		loading = new LoadingSchedule();
		loading.execute();
		ChangeLog cl = new ChangeLog(this);
		if (cl.firstRun())
			cl.getLogDialog().show();
	}
	
	class LoadingSchedule extends AsyncTask<Integer, Integer, Integer>{

		@Override
		protected Integer doInBackground(Integer... arg0) {
			timetable = new Timetable(MainActivity.this);
			alert = new Alert(MainActivity.this);
			alert.setAlarm();
			return 0;
		}
		@Override  
        protected void onPostExecute(Integer result) {  

			LinearLayout mainLayout = (LinearLayout) getLayoutInflater().inflate(
					R.layout.main, null);
			setContentView(mainLayout);
			
			TextView tvDate = (TextView)findViewById(R.id.tvDate);
			tvDate.setText(dateInfo());
			scheduleView = new ScheduleView(MainActivity.this);
			mainLayout.addView(scheduleView);
			loaded = true;
        } 
	}
	
	class checkUpdate extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... para) {
			//return NetworkHelper.readURL("http://ahut2011.sinaapp.com/app/lesson/check.php?ver=" + getAppVersionName(getApplicationContext()));
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if(!noticeUpdate)return;
			/*
			if (result.contentEquals("FoundNewVersion")) {
	    		Intent intent = new Intent(getBaseContext(), FoundUpdateActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			*/
		}
	}
	
	public String dateInfo(){
		SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("MM月d日"); 
		String  date  =  sDateFormat.format(new java.util.Date()); 
		if(date.startsWith("0"))
			date = date.substring(1);
		int numOfWeek = timetable.getNumOfWeekSincePeriod();
		return date + " " + Timetable.weekname[timetable.getCurrentWeekDay()] + " " + "第" + String.valueOf(numOfWeek) + "周";
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
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
		case R.id.menu_exit:
			super.onDestroy();
			this.finish();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(!loaded) return false;
		scheduleView.onKeyDown(keyCode, event);
		return super.onKeyDown(keyCode, event);
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