package com.ahutpt.lesson;

import com.ahutpt.lesson.network.NetworkHelper;
import com.ahutpt.lesson.lesson.LessonManager;

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

public class ManageDBActivity extends android.preference.PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String xh = preferences.getString("down_xh", "");
		if(xh.contentEquals("")){
			alert("请先设置学号");
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
			dialog.dismiss();
			if(LessonManager.updateDB(result)){
				alert("下载成功");
			}else{
				alert("下载失败");
			}
		}
	}
	
	public void alert(String notice) {
		Toast.makeText(this, notice, Toast.LENGTH_LONG).show();
	}
}
