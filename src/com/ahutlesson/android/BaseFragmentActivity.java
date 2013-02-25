package com.ahutlesson.android;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class BaseFragmentActivity extends SherlockFragmentActivity {

	protected ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		actionBar = getSupportActionBar();
		enableHomeButton();
		super.onCreate(savedInstanceState);
	}

	public void enableHomeButton() {
		if (actionBar == null)
			return;
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	public void disableHomeButton() {
		if (actionBar == null)
			return;
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_setting:
			Intent i = new Intent(this, PreferenceActivity.class);
			startActivity(i);
			return true;
		case R.id.menu_exit:
			this.finish();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}

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

	public void openActivity(Class<?> cls) {
		Intent i = new Intent(this, cls);
		startActivity(i);
	}
	
	public void alert(final String message) {
		if(isFinishing()) return;
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				new AlertDialog.Builder(BaseFragmentActivity.this)
				.setMessage(message)
				.setPositiveButton(R.string.ok, null).show();
			}
		});
	}
	
	public void alert(final String title, final String message) {
		if(isFinishing()) return;
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				new AlertDialog.Builder(BaseFragmentActivity.this)
				.setIcon(R.drawable.ahutlesson)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(R.string.ok, null).show();
			}
		});
	}
	

	public void makeToast(final String message) {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				Toast.makeText(BaseFragmentActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}
}
