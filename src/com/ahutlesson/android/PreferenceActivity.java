package com.ahutlesson.android;

import com.ahutlesson.android.model.LessonManager;
import com.ahutlesson.android.model.Timetable;
import com.ahutlesson.android.model.TimetableSetting;
import com.ahutlesson.android.model.UserManager;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.DatePicker;

public class PreferenceActivity extends SherlockPreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.drawable.ic_action_settings);

		addPreferencesFromResource(R.xml.preferences);

		Preference setBeginDate = (Preference) findPreference("set_begin_date");
		setBeginDate
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						final Timetable timetable = Timetable
								.getInstance(PreferenceActivity.this);
						final TimetableSetting timetableSetting = timetable
								.getTimetableSetting();
						DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker datePicker,
									int year, int month, int dayOfMonth) {
								timetableSetting.year = year;
								timetableSetting.month = month + 1;
								timetableSetting.day = dayOfMonth;
								timetable.setTimetableSetting(timetableSetting);
							}
						};
						DatePickerDialog dialog = new DatePickerDialog(
								PreferenceActivity.this, dateListener,
								timetableSetting.year,
								timetableSetting.month - 1,
								timetableSetting.day);
						dialog.show();
						return false;
					}

				});

		CheckBoxPreference seasonWinter = (CheckBoxPreference) findPreference("season_winter");
		seasonWinter
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference arg0,
							Object arg1) {
						Timetable.getInstance(PreferenceActivity.this)
								.toggleSeason();
						return true;
					}
				});

		Preference downDB = (Preference) findPreference("down_lesson");
		downDB.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				confirm("下载课表会清空现有的课表，继续吗？", new Runnable() {
					@Override
					public void run() {
						new DownLesson().execute();
					}
				});
				return true;
			}

		});

		Preference downUserInfo = (Preference) findPreference("user_downinfo");
		downUserInfo
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						new DownUserInfo().execute();
						return true;
					}

				});

		Preference userLogout = (Preference) findPreference("user_logout");
		userLogout
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						confirm("确定要注销账户吗？", new Runnable() {
							@Override
							public void run() {
								clearImageCache();
								SharedPreferences preferences = PreferenceManager
										.getDefaultSharedPreferences(PreferenceActivity.this);
								preferences.edit().clear().commit();
								Intent i = getBaseContext().getPackageManager()
										.getLaunchIntentForPackage(
												getBaseContext()
														.getPackageName());
								i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(i);
							}
						});
						return true;
					}
				});

		Preference delDB = (Preference) findPreference("delete_db");
		delDB.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				confirm("确定要清空课表吗？", new Runnable() {
					@Override
					public void run() {
						LessonManager.getInstance(PreferenceActivity.this)
								.deleteDB();
					}
				});
				return true;
			}
		});

		Preference clearImageCache = (Preference) findPreference("clear_image_cache");
		clearImageCache
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						clearImageCache();
						alert("图片缓存已清除");
						return true;
					}
				});

	}

	private class DownUserInfo extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... para) {
			clearImageCache();
			try {
				UserManager.getInstance(PreferenceActivity.this)
						.updateUserInfo();
				return null;
			} catch (Exception e) {
				return e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String ret) {
			if (ret != null) {
				alert(ret);
			} else {
				alert("下载成功！");
			}
		}
	}

	ProgressDialog progressDialog;

	class DownLesson extends AsyncTask<Integer, Integer, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(PreferenceActivity.this,
					"请稍等", "下载课表中...", true);
		}

		@Override
		protected String doInBackground(Integer... para) {
			try {
				UserManager.getInstance(PreferenceActivity.this)
						.updateLessonDB();
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
			} catch (Exception e) {
			}
			if (ret != null) {
				alert(ret);
			} else {
				alert("下载成功！");
			}
		}
	}

	@Override
	protected void onPause() {
		try {
			progressDialog.dismiss();
			progressDialog = null;
		} catch (Exception e) {
		}
		super.onPause();
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

	public void confirm(String message, final Runnable r) {
		new AlertDialog.Builder(PreferenceActivity.this)
				.setMessage(message)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								r.run();
							}
						}).setNegativeButton(R.string.cancel, null).show();
	}

	public void alert(String message) {
		new AlertDialog.Builder(this).setMessage(message)
				.setPositiveButton(R.string.ok, null).show();
	}

	public void clearImageCache() {
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().clearDiscCache();
	}
}
