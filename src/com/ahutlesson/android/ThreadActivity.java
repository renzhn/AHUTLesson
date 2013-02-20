package com.ahutlesson.android;

import java.util.ArrayList;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.Post;
import com.ahutlesson.android.thread.PostAdapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ThreadActivity extends BaseActivity {

	private int tid;
	private String subject;

	private static final int MENU_REFRESH = 0;
	
	private LinearLayout layoutLoading, layoutThread;
	private ListView lvPostList;
	private PostAdapter lvPostAdapter;
	private ArrayList<Post> postList = new ArrayList<Post>();
	private int threadPage = 1;
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
		
		actionBar.setLogo(R.drawable.comment);
		
		setContentView(R.layout.thread);
		
		layoutLoading = (LinearLayout) findViewById(R.id.layoutLoading);
		layoutThread = (LinearLayout) findViewById(R.id.layoutThread);
		
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
				new loadNextPage().execute();
			}
		});

		lvPostList = (ListView) layoutThread.findViewById(R.id.lvPostList);
		lvPostAdapter = new PostAdapter(ThreadActivity.this,
				R.layout.post_item, postList);
		lvPostList.addHeaderView(headerView, null, false);
		lvPostList.addFooterView(footerView);
		lvPostList.setAdapter(lvPostAdapter);
		
		ImageButton ibSend = (ImageButton) findViewById(R.id.ibSend);
		etReplyContent = (EditText) findViewById(R.id.etContent);
		ibSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				replyContent = etReplyContent.getText().toString();
				if(replyContent == null || replyContent.contentEquals("")) {
					alert("请输入回复内容!");
				}else{
					new postNewReply().execute();
				}
			}
		});
		
		new loadThread().execute();
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
			new loadThread().execute();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
	public void showPosts() {
		layoutLoading.setVisibility(View.GONE);
		layoutThread.setVisibility(View.VISIBLE);
		lvPostAdapter.notifyDataSetChanged();
	}

	private class loadThread extends AsyncTask<Integer, Integer, ArrayList<Post>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			layoutLoading.setVisibility(View.VISIBLE);
			layoutThread.setVisibility(View.GONE);
			postList.clear();
		}

		@Override
		protected ArrayList<Post> doInBackground(Integer... param) {
			threadPage = 1;
			return AHUTAccessor.getInstance(ThreadActivity.this).getPostList(tid, threadPage);
		}

		@Override
		protected void onPostExecute(ArrayList<Post> ret) {
			if(ret == null) {
				alert("获取数据失败，请检查手机网络设置");
				return;
			}

			if(ret.size() == 0) {
				layoutLoading.setVisibility(View.GONE);
			}else if(ret.size() > 0) {
				tvNextPage.setText("加载更多");
				postList.addAll(ret);
				showPosts();
			}
		}
	}
	
	private class loadNextPage extends AsyncTask<Integer, Integer, ArrayList<Post>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			tvNextPage.setText("加载中,请稍候...");
		}

		@Override
		protected ArrayList<Post> doInBackground(Integer... params) {
			threadPage++;
			return AHUTAccessor.getInstance(ThreadActivity.this).getPostList(tid, threadPage);
		}

		@Override
		protected void onPostExecute(ArrayList<Post> ret) {
			if(ret == null) {
				alert("获取数据失败，请检查手机网络设置");
				tvNextPage.setText("加载更多");
				return;
			}

			if(ret.size() == 0) {
				threadPage--;
				tvNextPage.setText("没有更多的帖子了");
			}else{
				tvNextPage.setText("加载更多");
				postList.addAll(ret);
				lvPostAdapter.notifyDataSetChanged();
			}
		}
	}

	private class postNewReply extends AsyncTask<Integer, Integer, String> {

		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ThreadActivity.this, "请稍等...", "提交中...", true);
		}
		
		@Override
		protected String doInBackground(Integer... arg0) {
			return AHUTAccessor.getInstance(ThreadActivity.this).postNewReply(tid, replyContent);
		}

		@Override
		protected void onPostExecute(String ret) {
			progressDialog.dismiss();
			if(ret.startsWith("0")) {
				etReplyContent.setText("");
				alert("发布成功!");
				new loadThread().execute();
			}else if(ret.startsWith("1")){
				alert(ret.substring(2));
			}else{
				alert("连接服务器失败，请检查手机网络设置");
			}
		}

	}
	
}
