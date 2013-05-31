package com.ahutlesson.android;

import java.util.ArrayList;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.Notice;
import com.ahutlesson.android.ui.NoticeAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class NoticeActivity extends BaseActivity {

	private static final int MENU_REFRESH = 0;
	private static final int MENU_VIEWTHREAD = 1;
	private static final int MENU_VIEWREPLY = 2;
	
	private LinearLayout layoutLoading, layoutList, layoutEmpty;
	private ListView lvList;
	private NoticeAdapter lvNoticeAdapter;
	private ArrayList<Notice> list = new ArrayList<Notice>();
	private int page = 1;
	public static int noticesPerPage = 1;
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
		
		lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				openContextMenu(view);
			}
		});
		registerForContextMenu(lvList);
		
		new LoadData().execute();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(Menu.NONE, MENU_VIEWREPLY, Menu.NONE, "查看回复");  
		menu.add(Menu.NONE, MENU_VIEWTHREAD, Menu.NONE, "查看原帖");  
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	public boolean onContextItemSelected(android.view.MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final Notice n = list.get(info.position);	
		switch (item.getItemId()) {
		case MENU_VIEWTHREAD:
			Intent i = new Intent(NoticeActivity.this, ThreadActivity.class);
			i.putExtra("tid", n.tid);
			i.putExtra("subject", n.subject);
			startActivity(i);
			return true;
		case MENU_VIEWREPLY:
			Intent i1 = new Intent(NoticeActivity.this, PostActivity.class);
			i1.putExtra("pid", n.pid);
			startActivity(i1);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_REFRESH, Menu.NONE, R.string.refresh)
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
			layoutEmpty.setVisibility(View.GONE);
			list.clear();
		}

		@Override
		protected ArrayList<Notice> doInBackground(Integer... param) {
			page = 1;
			try {
				return AHUTAccessor.getInstance(NoticeActivity.this).getNoticeList(page);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Notice> ret) {
			layoutLoading.setVisibility(View.GONE);
			if(ret == null) return;

			if(ret.size() == 0) {
				layoutEmpty.setVisibility(View.VISIBLE);
				return;
			}
			
			if(ret.size() == noticesPerPage) {
				tvNextPage.setText("加载更多");
				footerView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new LoadNextPage().execute();
					}
				});
			}else if(ret.size() < noticesPerPage){
				tvNextPage.setText("没有更多的消息了");
				footerView.setOnClickListener(null);
			}
			
			layoutEmpty.setVisibility(View.GONE);
			list.addAll(ret);
			showData();
			
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
			try {
				return AHUTAccessor.getInstance(NoticeActivity.this).getNoticeList(page);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Notice> ret) {
			if(ret == null) {
				tvNextPage.setText("下一页");
				return;
			}

			if(ret.size() == 0) {
				page--;
				tvNextPage.setText("没有更多的提醒了");
				footerView.setOnClickListener(null);
				return;
			}
			
			if(ret.size() == noticesPerPage) {
				tvNextPage.setText("加载更多");
				footerView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new LoadNextPage().execute();
					}
				});
			}else if(ret.size() < noticesPerPage) {
				tvNextPage.setText("没有更多的消息了");
				footerView.setOnClickListener(null);
			}

			list.addAll(ret);
			lvNoticeAdapter.notifyDataSetChanged();
		}
	}

}
