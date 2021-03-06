package com.ahutlesson.android;

import com.ahutlesson.android.model.Timetable;
import com.ahutlesson.android.model.UserManager;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends BaseActivity {

	private String uxh, password, confirmPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		disableHomeButton();
		setContentView(R.layout.register);
		
		Button btnRegister = (Button) findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText etRegisterXH = (EditText) findViewById(R.id.etRegisterXH);
				EditText etRegisterPassword = (EditText) findViewById(R.id.etRegisterPassword);
				EditText etRegisterConfirmPassword = (EditText) findViewById(R.id.etRegisterConfirmPassword);
				uxh = etRegisterXH.getText().toString();
				password = etRegisterPassword.getText().toString();
				confirmPassword = etRegisterConfirmPassword.getText().toString();
				if(uxh.length() == 0 || password.length() == 0) {
					alert("用户名和密码不能为空!");
				}else if(!confirmPassword.contentEquals(password)) {
					alert("两次输入的密码不一致!");
				}else{
					new RegisterTask().execute();
				}
					
			}
		});
		
		Button btnToLogin = (Button) findViewById(R.id.btnToLogin);
		btnToLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity(LoginActivity.class);
				RegisterActivity.this.finish();
			}
		});
	}

	ProgressDialog progressDialog;
	private class RegisterTask extends AsyncTask<Integer, Integer, String> {
		
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(RegisterActivity.this, "请稍等...", "提交中...", true);
		}
		

		@Override
		protected String doInBackground(Integer... params) {
			try {
    			UserManager.getInstance(RegisterActivity.this).registerUser(uxh, password);
				UserManager.getInstance(RegisterActivity.this).updateLessonDB();
				Timetable.getInstance(RegisterActivity.this).getTimetableSetting();
				return null;
			} catch (Exception e) {
				return e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String ret) {
			try {
				progressDialog.dismiss();
				progressDialog = null;
		    } catch (Exception e) {}
			if(ret == null) {
				openActivity(MainActivity.class);
				RegisterActivity.this.finish();
			}else{
				alert(ret);
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
