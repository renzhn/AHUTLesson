package com.ahutpt.lesson;

import com.ahutpt.lesson.network.NetworkHelper;
import com.ahutpt.lesson.utils.ValidateHelper;
import com.ahutpt.lesson.lesson.LessonManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;

public class ManageDBActivity extends SherlockPreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.setting_db);
		
		if(!LessonManager.loaded){
			new LessonManager(this);
		}
		
		Preference downDB = (Preference)findPreference("down_db");
		downDB.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			public boolean onPreferenceClick(Preference preference) {
				new AlertDialog.Builder(ManageDBActivity.this).setTitle("清空确认")
				.setMessage("下载课表前会清空以前的课表\n是否继续？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						beginUpdate();
					}

				}).setNegativeButton("取消", null).show();
				return true;
			}
			
		});
		Preference delDB = (Preference)findPreference("delete_db");
		delDB.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			public boolean onPreferenceClick(Preference preference) {
				new AlertDialog.Builder(ManageDBActivity.this).setTitle("清空确认")
				.setMessage("确定要清空课表吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						LessonManager.deleteDB();
					}

				}).setNegativeButton("取消", null).show();
				return true;
			}
			
		});
	}

	private void beginUpdate() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String xh = preferences.getString("down_xh", "");
		if(xh.contentEquals("")){
			alert("请先设置学号");
			return;
		}		
		if(!ValidateHelper.isXH(xh)){
			alert("学号无效");
			return;
		}
		new UpdateAsync().execute(xh);
	}
	
	
	class UpdateAsync extends AsyncTask<String, String, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(ManageDBActivity.this, "",
					"数据下载中...", true);
		}

		@Override
		protected String doInBackground(String... para) {
			return NetworkHelper
					.readURL("http://ahut2011.sinaapp.com/lesson/getdata.php?xh=" + para[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			switch(LessonManager.updateDB(result)){
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
		}
	}
	
	public void alert(String notice) {
		Toast.makeText(this, notice, Toast.LENGTH_LONG).show();
	}
}
