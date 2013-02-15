package com.ahutlesson.android;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class BaseActivity extends SherlockActivity {

	protected ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		super.onCreate(savedInstanceState);
	}

	protected void disableHomeButton() {
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
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
