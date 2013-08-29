package com.ahutlesson.android;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.ForumThread;
import com.ahutlesson.android.model.Lessonmate;
import com.ahutlesson.android.ui.ForumThreadAdapter;
import com.ahutlesson.android.ui.LessonmateAdapter;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class LessonActivity extends BaseFragmentActivity implements OnNavigationListener {

	private int lid, week = -1, time = -1;
	private String title;
	private boolean isLocal;

	private static final String[] TITLES = { "课程讨论", "课友列表" };

	private static int viewMode = 0;

	private static final int MENU_COMPOSE = 0;
	private static final int MENU_DETAIL = 1;
	private static final int MENU_REFRESH = 2;

	private LinearLayout layoutLoading, layoutList, layoutEmpty;
	private ListView lvList;
	private ForumThreadAdapter lvForumThreadAdapter;
	private ArrayList<ForumThread> forumThreadList = new ArrayList<ForumThread>();
	private LessonmateAdapter lvLessonmateAdapter;
	private ArrayList<Lessonmate> lessonmateList = new ArrayList<Lessonmate>();
	private int threadPage = 1;
	public static int threadsPerPage = 1;
	public static int totalThreads = 1;
	public static int totalThreadPages = 1;
	private int lessonmatePage = 1;
	public static int lessonmatesPerPage = 1;
	private View headerView, footerView;
	private TextView tvPreviousPage, tvNextPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar.setDisplayShowTitleEnabled(false);

		lid = getIntent().getExtras().getInt("lid");
		week = getIntent().getExtras().getInt("week");
		time = getIntent().getExtras().getInt("time");
		title = getIntent().getExtras().getString("title");
		isLocal = getIntent().getExtras().getBoolean("local");
		
		if(lid == 0) {
			this.finish();
			return;
		}

		// List Navigation
		Context context = actionBar.getThemedContext();
		ArrayAdapter<CharSequence> list = new ArrayAdapter<CharSequence>(
				context, R.layout.sherlock_spinner_item);
		list.add(TITLES[0]);
		list.add(TITLES[1]);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(list, this);
		
		setLessonTitle();
		
		setContentView(R.layout.list);
		
		layoutLoading = (LinearLayout) findViewById(R.id.layoutLoading);
		layoutList = (LinearLayout) findViewById(R.id.layoutList);
		layoutEmpty = (LinearLayout) findViewById(R.id.layoutEmpty);

		TextView tvEmpty = (TextView) layoutEmpty.findViewById(R.id.tvEmpty);
		tvEmpty.setText("暂无帖子，触摸屏幕左下角来发表第一帖吧！");

		footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.listview_footer, null, false);
		tvNextPage = (TextView) footerView.findViewById(R.id.tvNextPage);

		headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.listview_header, null, false);
		tvPreviousPage = (TextView) headerView.findViewById(R.id.tvPreviousPage);
		headerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new LoadPreviousPage().execute();
			}
		});
		
		lvList = (ListView) layoutList.findViewById(R.id.lvList);
	}

	public static boolean needRefresh = false;
	
	@Override
	protected void onResume() {
		// 删除或修改后重新载入
		super.onResume();
		if(needRefresh) {
			showView();
			needRefresh = false;
		}
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// 选择导航菜单
		viewMode = itemPosition;
		showView();
		invalidateOptionsMenu();
		return false;
	}

	public void showView() {
		switch (viewMode) {
		case 0:
			lvForumThreadAdapter = new ForumThreadAdapter(LessonActivity.this,
					R.layout.forumthread_item, forumThreadList);
			lvList.removeFooterView(footerView);
			footerView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new LoadNextPage().execute();
				}
			});
			lvList.addFooterView(footerView);
			lvList.setAdapter(lvForumThreadAdapter);
			lvList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ForumThread thread = (ForumThread) lvList.getItemAtPosition(position);
					Intent i = new Intent(LessonActivity.this, ThreadActivity.class);
					i.putExtra("tid", thread.tid);
					i.putExtra("subject", thread.subject);
					i.putExtra("uname", thread.uname);
					startActivity(i);
				}
			});
			new LoadLessonForum().execute();
			break;
		case 1:
			lvList.removeHeaderView(headerView);
			lvList.removeFooterView(footerView);
			footerView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new LoadMoreLessonmates().execute();
				}
			});
			lvList.addFooterView(footerView);
			if(lvLessonmateAdapter == null) {
				lvLessonmateAdapter = new LessonmateAdapter(LessonActivity.this,
						R.layout.lessonmate_item, lessonmateList);
			}
			lvList.setAdapter(lvLessonmateAdapter);
			lvList.setOnItemClickListener(null);
			new LoadLessonmates().execute();
			break;
		}
	}


	public boolean onCreateOptionsMenu(Menu menu) {
		switch (viewMode) {
		case 0:
			menu.add(viewMode, MENU_COMPOSE, Menu.NONE, R.string.compose)
					.setIcon(R.drawable.edit)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			if (isLocal) {

				menu.add(viewMode, MENU_DETAIL, Menu.NONE, "课程详情")
						.setShowAsAction(
								MenuItem.SHOW_AS_ACTION_IF_ROOM
										| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			}
			menu.add(viewMode, MENU_REFRESH, Menu.NONE, R.string.refresh)
					.setIcon(R.drawable.refresh)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			
			actionBar.setLogo(R.drawable.forum);
			break;
		case 1:
			actionBar.setLogo(R.drawable.lessonmate);
			break;
		}
		return true;
	}
	
	private void setLessonTitle() {
		View custumView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.actionbar_customview, null, false);
		TextView tvCustomView = (TextView) custumView.findViewById(R.id.tvCumstomView);
		tvCustomView.setText(title);
		actionBar.setCustomView(custumView);
		actionBar.setDisplayShowCustomEnabled(true);
	}

	public void showForumThreads() {
		layoutLoading.setVisibility(View.GONE);
		layoutEmpty.setVisibility(View.GONE);
		layoutList.setVisibility(View.VISIBLE);
		lvForumThreadAdapter.notifyDataSetChanged();
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_REFRESH:
			new LoadLessonForum().execute();
			return true;
		case MENU_DETAIL:
			Intent i0 = new Intent(LessonActivity.this, LessonDetailActivity.class);
			i0.putExtra("week", week);
			i0.putExtra("time", time);
			startActivity(i0);
			return true;
		case MENU_COMPOSE:
			Intent i = new Intent(LessonActivity.this, NewThreadActivity.class);
			i.putExtra("lid", lid);
			startActivity(i);
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	private class LoadLessonForum extends AsyncTask<Integer, Integer, ArrayList<ForumThread>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			layoutLoading.setVisibility(View.VISIBLE);
			layoutList.setVisibility(View.GONE);
			layoutEmpty.setVisibility(View.GONE);
			forumThreadList.clear();
		}

		@Override
		protected ArrayList<ForumThread> doInBackground(Integer... params) {
			threadPage = 1;
			try {
				return AHUTAccessor.getInstance(LessonActivity.this).getForumThreadList(lid, threadPage);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<ForumThread> ret) {
			layoutLoading.setVisibility(View.GONE);
			if(ret == null) return;

			if(ret.size() == 0) {
				layoutLoading.setVisibility(View.GONE);
				layoutEmpty.setVisibility(View.VISIBLE);
				return;
			}
			
			if(totalThreadPages > threadPage) {
				tvNextPage.setText("下一页");
				footerView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new LoadNextPage().execute();
					}
				});
			}else{
				lvList.removeFooterView(footerView);
			}

			forumThreadList.addAll(ret);
			showForumThreads();
		}
	}

	private class LoadNextPage extends AsyncTask<Integer, Integer, ArrayList<ForumThread>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			tvNextPage.setText("加载中,请稍候...");
		}

		@Override
		protected ArrayList<ForumThread> doInBackground(Integer... params) {
			threadPage++;
			try {
				return AHUTAccessor.getInstance(LessonActivity.this).getForumThreadList(lid, threadPage);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<ForumThread> ret) {
			if(ret == null)  return;
			if(ret.size() == 0) {
				threadPage--;
				tvNextPage.setText("没有更多的帖子了");
				footerView.setOnClickListener(null);
				return;
			}
			
			if(threadPage == 2) {
				lvList.addHeaderView(headerView);
			}
			if(totalThreadPages > threadPage) {
				tvNextPage.setText("下一页");
				footerView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new LoadNextPage().execute();
					}
				});
			}else{
				tvNextPage.setText("没有更多的帖子了");
				footerView.setOnClickListener(null);
			}
			forumThreadList.clear();
			forumThreadList.addAll(ret);
			lvForumThreadAdapter.notifyDataSetChanged();
		}
	}

	private class LoadPreviousPage extends
			AsyncTask<Integer, Integer, ArrayList<ForumThread>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			tvPreviousPage.setText("加载中,请稍候...");
		}

		@Override
		protected ArrayList<ForumThread> doInBackground(Integer... params) {
			threadPage--;
			if(threadPage < 1)
				threadPage = 1;
			try {
				return AHUTAccessor.getInstance(LessonActivity.this).getForumThreadList(lid, threadPage);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<ForumThread> ret) {
			tvPreviousPage.setText("上一页");
			if(ret == null) return;
			
			if(threadPage == 1) {
				lvList.removeHeaderView(headerView);
			}
			tvNextPage.setText("下一页");
			footerView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new LoadNextPage().execute();
				}
			});
			forumThreadList.clear();
			forumThreadList.addAll(ret);
			lvForumThreadAdapter.notifyDataSetChanged();
		}
	}
	

	private class LoadLessonmates extends AsyncTask<Integer, Integer, ArrayList<Lessonmate>> {

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
				return AHUTAccessor.getInstance(LessonActivity.this).getLessonmateList(lid, lessonmatePage);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Lessonmate> ret) {
			layoutLoading.setVisibility(View.GONE);
			if(ret == null) return;
			
			if(ret.size() == lessonmatesPerPage) {
				tvNextPage.setText("加载更多");
			}else{
				tvNextPage.setText("没有更多课友了");
				footerView.setOnClickListener(null);
			}
			
			layoutEmpty.setVisibility(View.GONE);
			layoutList.setVisibility(View.VISIBLE);
			lessonmateList.addAll(ret);
			lvLessonmateAdapter.notifyDataSetChanged();
		}
	}


	private class LoadMoreLessonmates extends AsyncTask<Integer, Integer, ArrayList<Lessonmate>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			tvNextPage.setText("加载中,请稍候...");
		}

		@Override
		protected ArrayList<Lessonmate> doInBackground(Integer... param) {
			lessonmatePage++;
			try {
				return AHUTAccessor.getInstance(LessonActivity.this).getLessonmateList(lid, lessonmatePage);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Lessonmate> ret) {
			if(ret == null)  return;
			
			if(ret.size() == lessonmatesPerPage) {
				tvNextPage.setText("加载更多");
			}else{
				tvNextPage.setText("没有更多课友了");
				footerView.setOnClickListener(null);
			}

			lessonmateList.addAll(ret);
			lvLessonmateAdapter.notifyDataSetChanged();
		}
	}
	
}
