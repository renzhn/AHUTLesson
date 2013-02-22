package com.ahutlesson.android.ui.lesson;

import java.util.ArrayList;

import com.ahutlesson.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ForumThreadAdapter extends ArrayAdapter<ForumThread> {
	
	private int resourceId;
	
	public ForumThreadAdapter(Context context, int textViewResourceId, ArrayList<ForumThread> threadList) {
		super(context, textViewResourceId, threadList);
        this.resourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ForumThread thread = getItem(position);
        LinearLayout forumThreadItem = new LinearLayout(getContext());  
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(resourceId, forumThreadItem, true);
        TextView subject = (TextView) forumThreadItem.findViewById(R.id.tvForumThreadItemSubject);
        TextView uname = (TextView) forumThreadItem.findViewById(R.id.tvForumThreadItemUname);
        TextView time = (TextView) forumThreadItem.findViewById(R.id.tvForumThreadItemTime);
        TextView reply = (TextView) forumThreadItem.findViewById(R.id.tvForumThreadItemReply);
        subject.setText(String.valueOf(thread.subject));
        if(thread.top) {
        	ImageView ivTopThread = (ImageView)forumThreadItem.findViewById(R.id.ivTopThread);
        	ivTopThread.setVisibility(View.VISIBLE);
        }
        uname.setText(String.valueOf(thread.uname));
        time.setText(thread.getReplyTime());
        reply.setText(String.valueOf(thread.reply));
		return forumThreadItem;
	}
	
	
}
