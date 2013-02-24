package com.ahutlesson.android.ui.message;

import java.util.ArrayList;

import com.ahutlesson.android.R;
import com.ahutlesson.android.UserActivity;
import com.ahutlesson.android.api.AHUTAccessor;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<Message> {

	private Context context;
	private int resourceId;
	
	public MessageAdapter(Context context0, int textViewResourceId, ArrayList<Message> list) {
		super(context0, textViewResourceId, list);
        resourceId = textViewResourceId;
        context = context0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Message message = getItem(position);
        LinearLayout item = new LinearLayout(getContext());  
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(resourceId, item, true);
        TextView uname = (TextView) item.findViewById(R.id.tvItemUname);
        TextView title = (TextView) item.findViewById(R.id.tvItemTitle);
        TextView content = (TextView) item.findViewById(R.id.tvItemContent);
        TextView time = (TextView) item.findViewById(R.id.tvItemTime);
        ImageView avatar = (ImageView) item.findViewById(R.id.ivAvatar);
        if(message.hasAvatar) {
            ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(message.fromUxh), avatar);
        }else{
        	avatar.setImageResource(R.drawable.noavatar);
        }
        uname.setText(message.uname);
        title.setText(message.title);
        content.setText(message.content);
        if(!message.read) title.setTypeface(null, Typeface.BOLD);
        time.setText(message.getPostTime());
        View.OnClickListener clickUserListener = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(context, UserActivity.class);
				i.putExtra("uxh", message.fromUxh);
				context.startActivity(i);
			}
		};
        avatar.setOnClickListener(clickUserListener);
        uname.setOnClickListener(clickUserListener);
		return item;
	}
}
