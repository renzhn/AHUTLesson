package com.ahutlesson.android.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahutlesson.android.R;
import com.ahutlesson.android.UserActivity;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.Post;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PostAdapter extends ArrayAdapter<Post> {

	private Context context;
	private int resourceId;
	private LayoutInflater inflater;
	
	public PostAdapter(Context context0, int textViewResourceId, ArrayList<Post> postList) {
		super(context0, textViewResourceId, postList);
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
			holder.uname = (TextView) v.findViewById(R.id.tvPostItemUname);
			holder.content = (TextView) v.findViewById(R.id.tvPostItemContent);
			holder.floor = (TextView) v.findViewById(R.id.tvPostItemFloor);
			holder.time = (TextView) v.findViewById(R.id.tvPostItemTime);
			holder.avatar = (ImageView) v.findViewById(R.id.ivAvatar);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		
		final Post post = getItem(position);
		if (post == null)	return v;
		
		if (post.hasAvatar) {
			ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(post.uxh), holder.avatar);
		} else {
			holder.avatar.setImageResource(R.drawable.noavatar);
		}
        
        holder.uname.setText(String.valueOf(post.uname));
        holder.content.setText(String.valueOf(post.content));
        holder.floor.setText(post.floor + "¥");
        holder.time.setText(post.getPostTime());
        View.OnClickListener clickUserListener = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(context, UserActivity.class);
				i.putExtra("uxh", post.uxh);
				context.startActivity(i);
			}
		};
		holder.avatar.setOnClickListener(clickUserListener);
		holder.uname.setOnClickListener(clickUserListener);
		return v;
	}
	
	static class ViewHolder {
		TextView uname, content, floor, time;
		ImageView avatar;
	}
	
}
