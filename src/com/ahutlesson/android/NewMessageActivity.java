package com.ahutlesson.android;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.utils.ValidateHelper;

public class NewMessageActivity extends BaseActivity {
	
	private String uxh;
	private String title, content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uxh = getIntent().getExtras().getString("uxh");
		if(!ValidateHelper.isXH(uxh)) {
			this.finish();
			return;
		}
		setContentView(R.layout.new_message);
		
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
		EditText etTitle = (EditText) findViewById(R.id.etTitle);
		EditText etContent = (EditText) findViewById(R.id.etContent);
		title = etTitle.getText().toString();
		content = etContent.getText().toString();
		if(title.contentEquals("") || content.contentEquals("")) {
			alert("标题或内容为空！");
		}else{
			new SendMessage().execute();
		}
	}

	private class SendMessage extends AsyncTask<Integer, Integer, String> {
		
		ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(NewMessageActivity.this, "请稍等...", "提交中...", true);
		}
		
		@Override
		protected String doInBackground(Integer... arg0) {
			try {
				AHUTAccessor.getInstance(NewMessageActivity.this).sendMessage(uxh, title, content);
			} catch (Exception e) {
				makeToast(e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String ret) {
			progressDialog.dismiss();
			NewMessageActivity.this.finish();
		}
	}
	
}
