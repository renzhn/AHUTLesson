package com.ahutlesson.android;

import java.util.ArrayList;

import com.actionbarsherlock.view.Menu;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.ui.notice.Notice;
import com.ahutlesson.android.ui.notice.NoticeAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class NoticeActivity extends BaseActivity {

	private static final int MENU_REFRESH = 0;

	
	private LinearLayout layoutLoading, layoutList, layoutEmpty;
	private ListView lvList;
	private NoticeAdapter lvNoticeAdapter;
	private ArrayList<Notice> list = new ArrayList<Notice>();
	private int page = 1;
	private View footerView;
	private TextView tvNextPage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		actionBar.setLogo(R.drawable.forum);
		
		setContentView(R.layout.list);
		
		layoutLoading = (LinearLayout) findViewById(R.id.layoutLoading);
		layoutList = (LinearLayout) findViewById(R.id.layoutList);
		layoutEmpty = (LinearLayout) findViewById(R.id.layoutEmpty);
		
		TextView tvEmpty = (TextView) layoutEmpty.findViewById(R.id.tvEmpty);
		tvEmpty.setText("暂无提醒");

		footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.listview_footer, null, false);
		tvNextPage = (TextView) footerView.findViewById(R.id.tvNextPage);
		footerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new LoadNextPage().execute();
			}
		});

		lvList = (ListView) layoutList.findViewById(R.id.lvList);
		lvNoticeAdapter = new NoticeAdapter(NoticeActivity.this,
				R.layout.notice_item, list);
		lvList.addFooterView(footerView);
		lvList.setAdapter(lvNoticeAdapter);
		
		lvList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
				Notice n = list.get(position);
				Intent i = new Intent(NoticeActivity.this, ThreadActivity.class);
				i.putExtra("tid", n.tid);
				startActivity(i);
			}
		});
		
		new LoadData().execute();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
			menu.add(0, MENU_REFRESH, Menu.NONE, R.string.refresh)
			.setIcon(R.drawable.refresh)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_REFRESH:
			new LoadData().execute();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
	
	public void showData() {
		layoutLoading.setVisibility(View.GONE);
		layoutList.setVisibility(View.VISIBLE);
		lvNoticeAdapter.notifyDataSetChanged();
	}

	private class LoadData extends AsyncTask<Integer, Integer, ArrayList<Notice>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			layoutLoading.setVisibility(View.VISIBLE);
			layoutList.setVisibility(View.GONE);
			list.clear();
		}

		@Override
		protected ArrayList<Notice> doInBackground(Integer... param) {
			page = 1;
			return AHUTAccessor.getInstance(NoticeActivity.this).getNoticeList(page);
		}

		@Override
		protected void onPostExecute(ArrayList<Notice> ret) {
			layoutLoading.setVisibility(View.GONE);
			if(ret == null) {
				alert("获取数据失败，请检查手机网络设置");
				return;
			}

			if(ret.size() == 0) {
				layoutEmpty.setVisibility(View.VISIBLE);
			}else if(ret.size() > 0) {
				layoutEmpty.setVisibility(View.GONE);
				tvNextPage.setText("加载更多");
				list.addAll(ret);
				showData();
			}
		}
	}
	
	private class LoadNextPage extends AsyncTask<Integer, Integer, ArrayList<Notice>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			tvNextPage.setText("加载中,请稍候...");
		}

		@Override
		protected ArrayList<Notice> doInBackground(Integer... params) {
			page++;
			return AHUTAccessor.getInstance(NoticeActivity.this).getNoticeList(page);
		}

		@Override
		protected void onPostExecute(ArrayList<Notice> ret) {
			if(ret == null) {
				alert("获取数据失败，请检查手机网络设置");
				tvNextPage.setText("加载更多");
				return;
			}

			if(ret.size() == 0) {
				page--;
				tvNextPage.setText("没有更多的提醒了");
			}else{
				tvNextPage.setText("加载更多");
				list.addAll(ret);
				lvNoticeAdapter.notifyDataSetChanged();
			}
		}
	}

}
