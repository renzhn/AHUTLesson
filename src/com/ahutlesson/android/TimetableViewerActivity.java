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
import com.ahutlesson.android.model.LessonsInfo;
import com.ahutlesson.android.ui.GridView;
import com.ahutlesson.android.utils.ValidateHelper;

public class TimetableViewerActivity extends BaseActivity {

	private static final int MENU_RETYPE = 0;
	
	private String uxh;
	private GridView gridView;
	private Lesson lessons[][] = new Lesson[7][5];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		enableHomeButton();

		uxh = getIntent().getExtras().getString("uxh");
		if(ValidateHelper.isXH(uxh)){
			new GetLessons().execute(uxh);
		}else{
			makeToast("不是有效的学号");
			finish();
		}
	}

	ProgressDialog dialog;
	class GetLessons extends AsyncTask<String, String, LessonsInfo> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(TimetableViewerActivity.this, "",
					"数据下载中...", true);
		}

		@Override
		protected LessonsInfo doInBackground(String... para) {
			try {
				return AHUTAccessor.getInstance(TimetableViewerActivity.this).getLessons(uxh);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(LessonsInfo ret) {
			try {
		        dialog.dismiss();
		        dialog = null;
		    } catch (Exception e) {}
			if(ret == null) {
				return;
			}else{
				lessons = ret.lessons;
				actionBar.setTitle(ret.xm + "的课表");
				showLessons();
			}
		}
	}

	@Override
	protected void onPause() {
		try {
	        dialog.dismiss();
	        dialog = null;
	    } catch (Exception e) {}
		super.onPause();
	}

	private void showLessons() {
		if (lessons != null)
			gridView = new GridView(TimetableViewerActivity.this, lessons);
		setContentView(gridView);
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
			input.setText(uxh);
			alert.setView(input);

			alert.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String xh = input.getText().toString();
							if(ValidateHelper.isXH(xh)){
								uxh = xh;
								lessons = new Lesson[7][5];
								new GetLessons().execute();
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
