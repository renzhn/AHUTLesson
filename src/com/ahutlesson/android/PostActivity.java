package com.ahutlesson.android;

import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.Post;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PostActivity extends BaseActivity {

	private int pid;
	private Post post;
	private LinearLayout layoutLoading, layoutContent;
	private EditText etReplyContent;
	private String replyContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pid = getIntent().getExtras().getInt("pid");
		if(pid == 0) {
			this.finish();
			return;
		}

		setContentView(R.layout.post);
		
		layoutLoading = (LinearLayout) findViewById(R.id.layoutLoading);
		layoutContent = (LinearLayout) findViewById(R.id.layoutContent);
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
		
		new LoadPost().execute();
	}

	private class LoadPost extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... params) {
			try {
				post = AHUTAccessor.getInstance(PostActivity.this).getPost(pid);
				return null;
			} catch (Exception e) {
				return e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String ret) {
			if(ret == null) {
				if(post == null) return;
				layoutLoading.setVisibility(View.GONE);
				layoutContent.setVisibility(View.VISIBLE);
		        TextView uname = (TextView) findViewById(R.id.tvPostItemUname);
		        TextView content = (TextView) findViewById(R.id.tvPostItemContent);
		        TextView floor = (TextView) findViewById(R.id.tvPostItemFloor);
		        TextView time = (TextView) findViewById(R.id.tvPostItemTime);
		        ImageView avatar = (ImageView) findViewById(R.id.ivAvatar);
		        if(post.hasAvatar) {
		        	ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(post.uxh), avatar);
		        }else{
		        	avatar.setImageResource(R.drawable.noavatar);
		        }
		        uname.setText(String.valueOf(post.uname));
		        content.setText(String.valueOf(post.content));
		        floor.setText(post.floor + "楼");
		        time.setText(post.getPostTime());
		        View.OnClickListener clickUserListener = new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent i = new Intent(PostActivity.this, UserActivity.class);
						i.putExtra("uxh", post.uxh);
						PostActivity.this.startActivity(i);
					}
				};
		        avatar.setOnClickListener(clickUserListener);
		        uname.setOnClickListener(clickUserListener);
			}else{
				makeToast(ret);
			}

		}
	}
	
	private class PostNewReply extends AsyncTask<Integer, Integer, String> {

		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(PostActivity.this, "请稍等...", "提交中...", true);
		}
		
		@Override
		protected String doInBackground(Integer... arg0) {
			try {
				if(post != null) {
					AHUTAccessor.getInstance(PostActivity.this).postReply(post.tid, "回复" + post.floor + "楼: " + replyContent);
					return null;
				}
				return "post is null";
			} catch (Exception e) {
				return e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String ret) {
			progressDialog.dismiss();
			if(ret == null) {
				etReplyContent.setText("");
				makeToast("回复成功!");
				PostActivity.this.finish();
			}else{
				makeToast(ret);
			}
		}
	}
}
