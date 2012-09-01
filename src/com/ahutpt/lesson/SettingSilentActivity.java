package com.ahutpt.lesson;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class SettingSilentActivity extends SherlockPreferenceActivity {

	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {

		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting_silent);
	}
}
