package com.ahutlesson.android.ui;

import java.util.ArrayList;

import com.ahutlesson.android.R;
import com.ahutlesson.android.UserActivity;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.Message;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<Message> {

	private Context context;
	private int resourceId;
	private LayoutInflater inflater;
	
	public MessageAdapter(Context context0, int textViewResourceId, ArrayList<Message> list) {
		super(context0, textViewResourceId, list);
        resourceId = textViewResourceId;
        context = context0;
		inflater = LayoutInflater.from(context0);
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder;
		if (v == null) {
			v = inflater.inflate(resourceId, parent, false);
			holder = new ViewHolder();
			holder.uname = (TextView) v.findViewById(R.id.tvItemUname);
			holder.title = (TextView) v.findViewById(R.id.tvItemTitle);
			holder.content = (TextView) v.findViewById(R.id.tvItemContent);
			holder.time = (TextView) v.findViewById(R.id.tvItemTime);
			holder.avatar = (ImageView) v.findViewById(R.id.ivAvatar);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		
		final Message message = getItem(position);
		if(message == null) return v;
		
        if(message.hasAvatar) {
            ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(message.fromUxh), holder.avatar);
        }else{
        	holder.avatar.setImageResource(R.drawable.noavatar);
        }
        holder.uname.setText(message.uname);
        holder.title.setText(message.title);
        holder.content.setText(message.content);
        if(!message.read) holder.title.setTypeface(null, Typeface.BOLD);
        holder.time.setText(message.getPostTime());
        View.OnClickListener clickUserListener = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(context, UserActivity.class);
				i.putExtra("uxh", message.fromUxh);
				context.startActivity(i);
			}
		};
		holder.avatar.setOnClickListener(clickUserListener);
		holder.uname.setOnClickListener(clickUserListener);
		return v;
	}
	
	static class ViewHolder {
		TextView uname, title, content, time;
		ImageView avatar;
	}
}
