package com.ahutlesson.android;

import android.os.Bundle;

public class ShareActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		actionBar.setIcon(R.drawable.ic_action_share);
		setContentView(R.layout.share);
	}
}
