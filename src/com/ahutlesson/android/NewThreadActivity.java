package com.ahutlesson.android;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.api.AHUTAccessor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

public class NewThreadActivity extends BaseActivity {

	private int lid;
	private String subject, content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lid = getIntent().getExtras().getInt("lid");
		if(lid == 0) {
			this.finish();
			return;
		}
		setContentView(R.layout.new_thread);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_edit_ok:
			newThread();
			return true;
		case R.id.menu_edit_cancel:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void newThread() {
		EditText etSubject = (EditText) findViewById(R.id.etSubject);
		EditText etContent = (EditText) findViewById(R.id.etContent);
		subject = etSubject.getText().toString();
		content = etContent.getText().toString();
		if(subject.contentEquals("") || content.contentEquals("")) {
			alert("标题或内容为空！");
		}else{
			new PostThread().execute();
		}
	}

	private class PostThread extends AsyncTask<Integer, Integer, Integer> {
		
		ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(NewThreadActivity.this, "请稍等...", "提交中...", true);
		}
		
		@Override
		protected Integer doInBackground(Integer... arg0) {
			try {
				return AHUTAccessor.getInstance(NewThreadActivity.this).postThread(lid, subject, content);
			} catch (Exception e) {
				alert(e.getMessage());
				return -1;
			}
		}

		@Override
		protected void onPostExecute(Integer ret) {
			progressDialog.dismiss();
			if(ret == -1) return;
			Intent i = new Intent(NewThreadActivity.this, ThreadActivity.class);
			i.putExtra("tid", ret);
			i.putExtra("subject", subject);
			startActivity(i);
			LessonActivity.needRefresh = true;
			NewThreadActivity.this.finish();
		}
	}
	
}
