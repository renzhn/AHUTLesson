package com.ahutlesson.android;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.LessonmateSimilarity;
import com.ahutlesson.android.ui.LessonmateSimilarityAdapter;
import com.ahutlesson.android.utils.ValidateHelper;

public class LessonmateSimilarityActivity extends BaseActivity {

	private static final int MENU_RETYPE = 0;

	private String uxh;

	private LinearLayout layoutLoading, layoutList, layoutEmpty;
	private ListView lvList;
	private LessonmateSimilarityAdapter lvAdapter;
	private ArrayList<LessonmateSimilarity> list = new ArrayList<LessonmateSimilarity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		enableHomeButton();

		uxh = getIntent().getExtras().getString("uxh");

		actionBar.setTitle(getResources().getString(
				R.string.lessonmate_similarity)
				+ "： " + uxh);
		
		setContentView(R.layout.list);

		layoutLoading = (LinearLayout) findViewById(R.id.layoutLoading);
		layoutList = (LinearLayout) findViewById(R.id.layoutList);
		layoutEmpty = (LinearLayout) findViewById(R.id.layoutEmpty);

		lvList = (ListView) layoutList.findViewById(R.id.lvList);

		if (lvAdapter == null) {
			lvAdapter = new LessonmateSimilarityAdapter(
					LessonmateSimilarityActivity.this,
					R.layout.lessonmate_similarity, list);
		}
		lvList.setAdapter(lvAdapter);
		lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LessonmateSimilarity row = list.get(position);
				Intent i = new Intent(LessonmateSimilarityActivity.this,
						TimetableViewerActivity.class);
				i.putExtra("uxh", row.xh);
				LessonmateSimilarityActivity.this.startActivity(i);
			}

		});

		if (ValidateHelper.isXH(uxh)) {
			new GetLessonmateSimilarity().execute();
		} else {
			makeToast("不是有效的学号");
			finish();
		}

	}

	String errorMsg;
	
	class GetLessonmateSimilarity extends
			AsyncTask<String, String, ArrayList<LessonmateSimilarity>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorMsg = null;
			layoutLoading.setVisibility(View.VISIBLE);
			layoutList.setVisibility(View.GONE);
			layoutEmpty.setVisibility(View.GONE);
			list.clear();
		}

		@Override
		protected ArrayList<LessonmateSimilarity> doInBackground(String... para) {
			try {
				return AHUTAccessor.getInstance(
						LessonmateSimilarityActivity.this)
						.getLessonmateSimilarity(uxh);
			} catch (Exception e) {
				errorMsg = e.getMessage();
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<LessonmateSimilarity> ret) {
			layoutLoading.setVisibility(View.GONE);
			if (ret != null) {
				layoutEmpty.setVisibility(View.GONE);
				layoutList.setVisibility(View.VISIBLE);
				list.addAll(ret);
				lvAdapter.notifyDataSetChanged();
			} 
			if (errorMsg != null) {
				layoutEmpty.setVisibility(View.VISIBLE);
				((TextView)layoutEmpty.findViewById(R.id.tvEmpty)).setText(errorMsg);
			}
		}
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

			alert.setTitle(getResources().getString(
					R.string.lessonmate_similarity));
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
							if (ValidateHelper.isXH(xh)) {
								uxh = xh;
								actionBar.setTitle(getResources().getString(
										R.string.lessonmate_similarity)
										+ "： " + uxh);
								new GetLessonmateSimilarity().execute();
							} else {
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
