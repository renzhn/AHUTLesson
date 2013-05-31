package com.ahutlesson.android;

import java.util.ArrayList;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.Post;
import com.ahutlesson.android.ui.PostAdapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ThreadActivity extends BaseActivity {

	private int tid;
	private String subject;

	private static final int MENU_REFRESH = 0;
	private static final int MENU_REPLY = 1;
	
	private LinearLayout layoutLoading, layoutContent;
	private ListView lvPostList;
	private PostAdapter lvPostAdapter;
	private ArrayList<Post> postList = new ArrayList<Post>();
	public static int currentPage = 1;
	public static int totalPosts = 1;
	public static int postsPerPage = 1;
	public static int totalPages = 1;
	private View footerView;
	private TextView tvNextPage;
	private EditText etReplyContent;
	private String replyContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tid = getIntent().getExtras().getInt("tid");
		subject = getIntent().getExtras().getString("subject");
		if(tid == 0) {
			this.finish();
			return;
		}
		
		actionBar.setLogo(R.drawable.forum);
		
		setContentView(R.layout.thread);
		
		layoutLoading = (LinearLayout) findViewById(R.id.layoutLoading);
		layoutContent = (LinearLayout) findViewById(R.id.layoutContent);
		
		View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
		.inflate(R.layout.thread_title, null, false);
		
		
		TextView tvThreadTitle = (TextView) headerView.findViewById(R.id.tvThreadTitle);
		tvThreadTitle.setText(subject);
		
		footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.listview_footer, null, false);
		tvNextPage = (TextView) footerView.findViewById(R.id.tvNextPage);
		footerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new LoadNextPage().execute();
			}
		});

		lvPostList = (ListView) layoutContent.findViewById(R.id.lvPostList);
		lvPostAdapter = new PostAdapter(ThreadActivity.this,
				R.layout.post_item, postList);
		lvPostList.addHeaderView(headerView, null, false);
		lvPostList.addFooterView(footerView);
		lvPostList.setAdapter(lvPostAdapter);
		lvPostList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				openContextMenu(view);
			}
		});
		
		registerForContextMenu(lvPostList);
		
		ImageButton ibSend = (ImageButton) findViewById(R.id.ibSend);
		etReplyContent = (EditText) findViewById(R.id.etContent);
		ibSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				replyContent = etReplyContent.getText().toString();
				if(replyContent.contentEquals("")) {
					return;
				}else{
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if(imm != null) {
						imm.hideSoftInputFromWindow(etReplyContent.getWindowToken(), 0);
					}
					new PostNewReply().execute();
				}
			}
		});
		
		new LoadThread().execute();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
			menu.add(Menu.NONE, MENU_REFRESH, Menu.NONE, R.string.refresh)
			.setIcon(R.drawable.refresh)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(Menu.NONE, MENU_REPLY, Menu.NONE, "回复");  
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	public boolean onContextItemSelected(android.view.MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();		
		switch (item.getItemId()) {
		case MENU_REPLY:
			Post p = postList.get(info.position - 1);
			if(p.floor == 1) {
				etReplyContent.setText("");
				etReplyContent.setSelection(0);
			}else if(p.floor > 1) {
				etReplyContent.setText("回复" + p.floor + "楼: ");
				etReplyContent.setSelection(etReplyContent.getText().length());
			}
			etReplyContent.setFocusableInTouchMode(true);
			etReplyContent.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null) {
			     imm.showSoftInput(etReplyContent, InputMethodManager.SHOW_IMPLICIT);
			}
			return true;
		}
		return super.onContextItemSelected(item);
	}
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_REFRESH:
			new LoadThread().execute();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
	
	public void showPosts() {
		layoutLoading.setVisibility(View.GONE);
		layoutContent.setVisibility(View.VISIBLE);
		lvPostAdapter.notifyDataSetChanged();
	}

	private class LoadThread extends AsyncTask<Integer, Integer, ArrayList<Post>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			layoutLoading.setVisibility(View.VISIBLE);
			layoutContent.setVisibility(View.GONE);
			postList.clear();
		}

		@Override
		protected ArrayList<Post> doInBackground(Integer... param) {
			currentPage = 1;
			try {
				return AHUTAccessor.getInstance(ThreadActivity.this).getPostList(tid, currentPage);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Post> ret) {
			layoutLoading.setVisibility(View.GONE);
			if(ret == null || ret.size() == 0)  return;

			postList.addAll(ret);
			showPosts();
			
			if(totalPages > currentPage) {
				tvNextPage.setText("下一页");
			}else{
				tvNextPage.setText("没有更多的帖子了");
			}
		}
	}
	
	private class LoadNextPage extends AsyncTask<Integer, Integer, ArrayList<Post>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			tvNextPage.setText("加载中,请稍候...");
		}

		@Override
		protected ArrayList<Post> doInBackground(Integer... params) {
			currentPage++;
			try {
				return AHUTAccessor.getInstance(ThreadActivity.this).getPostList(tid, currentPage);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Post> ret) {
			if(ret == null) return;

			if(ret.size() == 0) {
				currentPage--;
				tvNextPage.setText("没有更多的帖子了");
				return;
			}
			
			postList.addAll(ret);
			lvPostAdapter.notifyDataSetChanged();
			
			if(totalPages > currentPage) {
				tvNextPage.setText("下一页");
			}else{
				tvNextPage.setText("没有更多的帖子了");
			}
		}
	}

	private class PostNewReply extends AsyncTask<Integer, Integer, String> {

		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ThreadActivity.this, "请稍等...", "提交中...", true);
		}
		
		@Override
		protected String doInBackground(Integer... arg0) {
			try {
				AHUTAccessor.getInstance(ThreadActivity.this).postReply(tid, replyContent);
				return null;
			} catch (Exception e) {
				return e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String ret) {
			progressDialog.dismiss();
			if(ret == null) {
				etReplyContent.setText("");
				makeToast("发布成功!");
				new LoadThread().execute();
			}else{
				makeToast(ret);
			}

		}

	}
	
}
