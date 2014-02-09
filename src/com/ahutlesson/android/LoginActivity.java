package com.ahutlesson.android;

import com.ahutlesson.android.model.Timetable;
import com.ahutlesson.android.model.UserManager;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends BaseActivity {

	private String uxh, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		disableHomeButton();
		setContentView(R.layout.login);

		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText etLoginXH = (EditText) findViewById(R.id.etLoginXH);
				EditText etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
				uxh = etLoginXH.getText().toString();
				password = etLoginPassword.getText().toString();
				if(uxh.length() == 0 || password.length() == 0) {
					alert("用户名和密码不能为空!");
				}else{
					new LoginTask().execute();
				}
			}
		});

		Button btnToRegister = (Button) findViewById(R.id.btnToRegister);
		btnToRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity(RegisterActivity.class);
				LoginActivity.this.finish();
			}
		});
	}

	ProgressDialog progressDialog;
	private class LoginTask extends AsyncTask<Integer, Integer, Boolean> {
		
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(LoginActivity.this, "请稍等...", "获取帐号信息中...", true);
		}
		
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				UserManager.getInstance(LoginActivity.this).verifyUser(uxh,	password);
				UserManager.getInstance(LoginActivity.this).updateLessonDB();
				Timetable.getInstance(LoginActivity.this).updateTimetableSetting();
				return true;
			} catch (Exception e) {
				alert(e.getMessage());
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean ret) {
			try {
				progressDialog.dismiss();
				progressDialog = null;
		    } catch (Exception e) {}
			if(ret) {
				openActivity(MainActivity.class);
				LoginActivity.this.finish();
			}
		}
	}

	@Override
	protected void onPause() {
		try {
			progressDialog.dismiss();
			progressDialog = null;
	    } catch (Exception e) {}
		super.onPause();
	}

}
