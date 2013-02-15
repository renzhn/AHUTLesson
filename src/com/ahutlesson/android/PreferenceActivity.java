package com.ahutlesson.android;

import com.ahutlesson.android.lesson.LessonManager;
import com.ahutlesson.android.utils.ChangeLog;
import com.ahutlesson.android.utils.NetworkHelper;
import com.ahutlesson.android.utils.ValidateHelper;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;

public class PreferenceActivity extends SherlockPreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		Preference changelog = (Preference) findPreference("changelog");
		changelog.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				ChangeLog cl = new ChangeLog(PreferenceActivity.this);
				cl.getFullLogDialog().show();
				return true;
			}

		});

		Preference share = (Preference) findPreference("share");
		share.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				share();
				return true;
			}

		});

		Preference downDB = (Preference) findPreference("down_db");
		downDB.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				new AlertDialog.Builder(PreferenceActivity.this)
						.setTitle("清空确认")
						.setMessage("下载课表前会清空以前的课表\n是否继续？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										beginUpdate();
									}

								}).setNegativeButton("取消", null).show();
				return true;
			}

		});
		Preference delDB = (Preference) findPreference("delete_db");
		delDB.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				new AlertDialog.Builder(PreferenceActivity.this)
						.setTitle("清空确认")
						.setMessage("确定要清空课表吗？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										LessonManager.getInstance(PreferenceActivity.this).deleteDB();
									}

								}).setNegativeButton("取消", null).show();
				return true;
			}

		});

		Preference checkUpdate = (Preference) findPreference("check_update");
		checkUpdate.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				UmengUpdateAgent.setUpdateOnlyWifi(false);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
					@Override
					public void onUpdateReturned(int updateStatus,
							UpdateResponse updateInfo) {
						switch (updateStatus) {
						case 0: // has update
							UmengUpdateAgent.showUpdateDialog(
									PreferenceActivity.this, updateInfo);
							break;
						case 1: // has no update
							alert("您正在使用最新版");
							break;
						case 3: // time out
							alert("网络连接超时");
							break;
						}
					}
				});
				UmengUpdateAgent.update(PreferenceActivity.this);
				return true;
			}

		});

	}

	private void beginUpdate() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String xh = preferences.getString("down_xh", "");
		if (xh.contentEquals("")) {
			alert("请先设置学号");
			return;
		}
		if (!ValidateHelper.isXH(xh)) {
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
			dialog = ProgressDialog.show(PreferenceActivity.this, "",
					"数据下载中...", true);
		}

		@Override
		protected String doInBackground(String... para) {
			return NetworkHelper
					.readURL("http://ahut2011.sinaapp.com/lesson/getdata.php?xh="
							+ para[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			switch (LessonManager.getInstance(PreferenceActivity.this).updateDB(result)) {
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

	public void share() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
				"我在用安工大课程助手，挺不错的，下载地址：http://ahutapp.com/lesson");
		startActivity(Intent.createChooser(intent, "分享到"));
	}

	public void alert(String notice) {
		Toast.makeText(this, notice, Toast.LENGTH_LONG).show();
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
