package com.ahutpt.lesson;

import com.ahutpt.lesson.helper.ChangeLog;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class SettingActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);
		Preference setTimeTable = (Preference)findPreference("setting_timetable");
		setTimeTable.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(SettingActivity.this, TimetableSettingActivity.class);
				startActivity(i);
				return true;
			}
			
		});
		Preference setNotice = (Preference)findPreference("setting_notice");
		setNotice.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(SettingActivity.this, SettingNoticeActivity.class);
				startActivity(i);
				return true;
			}
			
		});
		Preference setSilent = (Preference)findPreference("setting_silent");
		setSilent.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(SettingActivity.this, SettingSilentActivity.class);
				startActivity(i);
				return true;
			}
			
		});
		
		Preference changelog = (Preference)findPreference("changelog");
		changelog.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			public boolean onPreferenceClick(Preference preference) {
				ChangeLog cl = new ChangeLog(SettingActivity.this);
				cl.getFullLogDialog().show();
				return true;
			}
			
		});
		
		Preference share = (Preference)findPreference("share");
		share.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			public boolean onPreferenceClick(Preference preference) {
				share();
				return true;
			}
			
		});
		
		Preference about = (Preference)findPreference("about");
		about.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			public boolean onPreferenceClick(Preference preference) {

				new AlertDialog.Builder(SettingActivity.this).setTitle(R.string.about)
				.setMessage(R.string.about_msg)
				.setNegativeButton("OK", null).show();
				
				return true;
			}
			
		});
		
	}
	
	public void share() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "我在用安工大课程助手，挺不错的，下载地址：http://dev1994.com/app/lesson");
		startActivity(Intent.createChooser(intent, "分享到"));
	}

}
