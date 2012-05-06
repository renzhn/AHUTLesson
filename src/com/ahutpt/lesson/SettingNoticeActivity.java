package com.ahutpt.lesson;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingNoticeActivity extends PreferenceActivity {
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting_notice);
	}
}
