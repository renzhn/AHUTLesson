package com.ahutlesson.android;

import java.util.ArrayList;

import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.Lessonmate;
import com.ahutlesson.android.ui.LessonmateAdapter;
import com.ahutlesson.android.utils.GlobalContext;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class LessonmateActivity extends BaseActivity {

	private int lid;

	private LinearLayout layoutLoading, layoutList, layoutEmpty;
	private ListView lvList;
	private LessonmateAdapter lvLessonmateAdapter;
	private ArrayList<Lessonmate> lessonmateList = new ArrayList<Lessonmate>();
	private int lessonmatePage = 1;
	public static int lessonmatesPerPage = 1;
	private View footerView;
	private TextView tvNextPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		enableHomeButton();
		GlobalContext.initImageLoader();
		
		lid = getIntent().getExtras().getInt("lid");
		if (lid == 0) {
			this.finish();
			return;
		}

		String title = getIntent().getExtras().getString("title");
		if (title != null) {
			actionBar.setTitle(title);
		}

		setContentView(R.layout.list);

		layoutLoading = (LinearLayout) findViewById(R.id.layoutLoading);
		layoutList = (LinearLayout) findViewById(R.id.layoutList);
		layoutEmpty = (LinearLayout) findViewById(R.id.layoutEmpty);
		
		lvList = (ListView) layoutList.findViewById(R.id.lvList);
		
		footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.listview_footer, null, false);
		tvNextPage = (TextView) footerView.findViewById(R.id.tvNextPage);

		footerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new LoadMoreLessonmates().execute();
			}
		});
		lvList.addFooterView(footerView);
		if (lvLessonmateAdapter == null) {
			lvLessonmateAdapter = new LessonmateAdapter(LessonmateActivity.this,
					R.layout.lessonmate_item, lessonmateList);
		}
		lvList.setAdapter(lvLessonmateAdapter);
		lvList.setOnItemClickListener(null);
		new LoadLessonmates().execute();
	}

	private class LoadLessonmates extends
			AsyncTask<Integer, Integer, ArrayList<Lessonmate>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			layoutLoading.setVisibility(View.VISIBLE);
			layoutList.setVisibility(View.GONE);
			layoutEmpty.setVisibility(View.GONE);
			lessonmateList.clear();
		}

		@Override
		protected ArrayList<Lessonmate> doInBackground(Integer... param) {
			lessonmatePage = 1;
			try {
				return AHUTAccessor.getInstance(LessonmateActivity.this)
						.getLessonmateList(lid, lessonmatePage);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Lessonmate> ret) {
			layoutLoading.setVisibility(View.GONE);
			if (ret == null)
				return;

			if (ret.size() == lessonmatesPerPage) {
				tvNextPage.setText("加载更多");
			} else {
				tvNextPage.setText("没有更多课友了");
				footerView.setOnClickListener(null);
			}

			layoutEmpty.setVisibility(View.GONE);
			layoutList.setVisibility(View.VISIBLE);
			lessonmateList.addAll(ret);
			lvLessonmateAdapter.notifyDataSetChanged();
		}
	}

	private class LoadMoreLessonmates extends
			AsyncTask<Integer, Integer, ArrayList<Lessonmate>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			tvNextPage.setText("加载中,请稍候...");
		}

		@Override
		protected ArrayList<Lessonmate> doInBackground(Integer... param) {
			lessonmatePage++;
			try {
				return AHUTAccessor.getInstance(LessonmateActivity.this)
						.getLessonmateList(lid, lessonmatePage);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Lessonmate> ret) {
			if (ret == null)
				return;

			if (ret.size() == lessonmatesPerPage) {
				tvNextPage.setText("加载更多");
			} else {
				tvNextPage.setText("没有更多课友了");
				footerView.setOnClickListener(null);
			}

			lessonmateList.addAll(ret);
			lvLessonmateAdapter.notifyDataSetChanged();
		}
	}
}
