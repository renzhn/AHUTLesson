package com.ahutlesson.android;

import com.ahutlesson.android.utils.ChangeLog;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

public class SettingActivity extends SherlockPreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
		
		Preference dbManage = (Preference)findPreference("setting_dbmanage");
		dbManage.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(SettingActivity.this, ManageDBActivity.class);
				startActivity(i);
				return true;
			}
			
		});

		Preference wizard = (Preference)findPreference("wizard");
		wizard.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(SettingActivity.this, WizardActivity.class);
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
		intent.putExtra(Intent.EXTRA_TEXT, "我在用安工大课程助手，挺不错的，下载地址：http://ahut2011.sinaapp.com/app/ahutlesson.apk");
		startActivity(Intent.createChooser(intent, "分享到"));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return (true);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
