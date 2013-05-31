package com.ahutlesson.android.ui;

import java.util.ArrayList;

import com.ahutlesson.android.R;
import com.ahutlesson.android.model.ForumThread;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ForumThreadAdapter extends ArrayAdapter<ForumThread> {
	
	private int resourceId;
	private LayoutInflater inflater;
	
	public ForumThreadAdapter(Context context, int textViewResourceId, ArrayList<ForumThread> threadList) {
		super(context, textViewResourceId, threadList);
        this.resourceId = textViewResourceId;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder;
		
		if (v == null) {
			v = inflater.inflate(resourceId, parent, false);
			holder = new ViewHolder();
			holder.subject = (TextView) v.findViewById(R.id.tvForumThreadItemSubject);
			holder.uname = (TextView) v.findViewById(R.id.tvForumThreadItemUname);
			holder.time = (TextView) v.findViewById(R.id.tvForumThreadItemTime);
			holder.reply = (TextView) v.findViewById(R.id.tvForumThreadItemReply);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		
		ForumThread thread = getItem(position);
		if (thread == null) return v;
		
		holder.subject.setText(String.valueOf(thread.subject));
        if(thread.top) {
        	ImageView ivTopThread = (ImageView) v.findViewById(R.id.ivTopThread);
        	ivTopThread.setVisibility(View.VISIBLE);
        }
        holder.uname.setText(String.valueOf(thread.uname));
        holder.time.setText(thread.getReplyTime());
        holder.reply.setText(String.valueOf(thread.reply));
		return v;
	}
	
	static class ViewHolder {
		TextView subject, uname, time, reply;
	}
}
