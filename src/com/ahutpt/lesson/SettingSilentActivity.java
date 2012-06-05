package com.ahutpt.lesson;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingSilentActivity extends PreferenceActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting_silent);
	}
}
