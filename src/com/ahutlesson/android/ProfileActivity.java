package com.ahutlesson.android;

import android.os.Bundle;

public class ProfileActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		actionBar.setIcon(R.drawable.account);
	}
}
