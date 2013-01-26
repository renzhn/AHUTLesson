package com.ahutpt.lesson;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.ahutpt.lesson.lesson.LessonManager;
import com.ahutpt.lesson.network.NetworkHelper;
import com.ahutpt.lesson.time.Timetable;
import com.ahutpt.lesson.utils.ValidateHelper;

public class WizardActivity extends SherlockActivity {

	private TextView tvCurrentWeek;
	private EditText etWizardXH;
	private EditText etBeginDate_year, etBeginDate_month, etBeginDate_day;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setTitle("首次使用向导");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wizard);

		new Timetable(this);

		etBeginDate_year = (EditText) findViewById(R.id.etBeginDate_year);
		etBeginDate_month = (EditText) findViewById(R.id.etBeginDate_month);
		etBeginDate_day = (EditText) findViewById(R.id.etBeginDate_day);

		etBeginDate_year.setText(Integer.toString(Timetable.getBeginDate_year()));
		etBeginDate_month.setText(Integer.toString(Timetable.getBeginDate_month()));
		etBeginDate_day.setText(Integer.toString(Timetable.getBeginDate_day()));

		etBeginDate_year.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				int v = parseInt(etBeginDate_year.getText().toString());
				if(v != -1){
					Timetable.setBeginDate_year(v);
					updateCurrentWeek();
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
		
		etBeginDate_month.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				int v = parseInt(etBeginDate_month.getText().toString());
				if(v != -1){
					Timetable.setBeginDate_month(v);
					updateCurrentWeek();
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
		
		etBeginDate_day.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				int v = parseInt(etBeginDate_day.getText().toString());
				if(v != -1){
					Timetable.setBeginDate_day(v);
					updateCurrentWeek();
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
		
		tvCurrentWeek = (TextView) findViewById(R.id.tvCurrentWeek);
		etWizardXH = (EditText) findViewById(R.id.etWizardXH);
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		etWizardXH.setText(preferences.getString("down_xh", ""));
		updateCurrentWeek();

		Button btnFinishWizard = (Button) findViewById(R.id.btnFinishWizard);
		btnFinishWizard.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String xh = etWizardXH.getText().toString();
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(WizardActivity.this);

				Editor edit = preferences.edit();
				edit.putString("down_xh", xh);
				edit.commit();
				if (!ValidateHelper.isXH(xh)) {
					alert("请检查输入的学号是否有误");
				} else {
					new UpdateAsync().execute(xh);
				}
			}
		});

	}
	private int parseInt(String value){
		int v = -1;
		try{
			v = Integer.valueOf(value);
		}catch(Exception ex){
			
		}
		return v;
	}
	private void updateCurrentWeek() {
		int weeks = Timetable.getNumOfWeekSincePeriod();
		if (weeks == 0) {
			tvCurrentWeek.setText("当前不在上课周");
		} else {
			tvCurrentWeek.setText("当前是第" + Timetable.getNumOfWeekSincePeriod()
					+ "周");
		}
	}

	class UpdateAsync extends AsyncTask<String, String, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(WizardActivity.this, "", "数据下载中...",
					true);
		}

		@Override
		protected String doInBackground(String... para) {
			return NetworkHelper
					.readURL("http://ahut2011.sinaapp.com/lesson/getdata.php?xh="
							+ para[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			switch (LessonManager.updateDB(result)) {
			case LessonManager.EMPTY_RESPONSE:
				alert("服务器未返回数据");
				break;
			case LessonManager.EMPTY_DATA:
				alert("未找到课表信息");
				break;
			case LessonManager.PARSE_ERROR:
				alert("解析数据失败");
				break;
			case LessonManager.UPDATE_OK:
				alert("数据下载成功");
				break;
			}
			dialog.dismiss();
			Intent i = getBaseContext().getPackageManager()
					.getLaunchIntentForPackage(
							getBaseContext().getPackageName());
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
	}

	public void alert(String notice) {
		Toast.makeText(this, notice, Toast.LENGTH_LONG).show();
	}
}
