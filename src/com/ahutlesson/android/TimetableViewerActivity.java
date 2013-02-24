package com.ahutlesson.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.ui.main.ScheduleView;
import com.ahutlesson.android.utils.ValidateHelper;

public class TimetableViewerActivity extends BaseActivity {

	private static final int MENU_RETYPE = 0;
	
	private ScheduleView scheduleView;
	private Lesson lessons[][] = new Lesson[7][5];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String uxh = getIntent().getExtras().getString("uxh");
		actionBar.setTitle(uxh + "的课表");
		if(ValidateHelper.isXH(uxh)){
			new GetLessons().execute(uxh);
		}else{
			alert("不是有效的学号");
			finish();
		}
	}

	class GetLessons extends AsyncTask<String, String, Lesson[][]> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(TimetableViewerActivity.this, "",
					"数据下载中...", true);
		}

		@Override
		protected Lesson[][] doInBackground(String... para) {
			try {
				return AHUTAccessor.getInstance(TimetableViewerActivity.this).getLessons(para[0]);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(Lesson[][] result) {
			dialog.dismiss();
			if(result == null) {
				return;
			}else{
				lessons = result;
				showLessons();
			}
		}
	}

	private void showLessons() {
		scheduleView = new ScheduleView(TimetableViewerActivity.this, lessons, false);
		setContentView(scheduleView);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_RETYPE, Menu.NONE, "重新输入")
		.setIcon(android.R.drawable.ic_menu_search)
		.setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_RETYPE:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("课表浏览器");
			alert.setMessage("请输入学号：");

			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			alert.setView(input);

			alert.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String xh = input.getText().toString();
							if(ValidateHelper.isXH(xh)){
								actionBar.setTitle(xh + "的课表");
								lessons = new Lesson[7][5];
								new GetLessons().execute(xh);
							}else{
								alert("不是有效的学号");
							}
						}
					});

			alert.setNegativeButton(R.string.cancel, null);
			alert.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
