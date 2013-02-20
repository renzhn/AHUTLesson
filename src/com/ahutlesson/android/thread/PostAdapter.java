package com.ahutlesson.android.thread;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahutlesson.android.R;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.Post;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PostAdapter extends ArrayAdapter<Post> {
	
	private int resourceId;
	
	public PostAdapter(Context context, int textViewResourceId, ArrayList<Post> postList) {
		super(context, textViewResourceId, postList);
        this.resourceId = textViewResourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Post post = getItem(position);
        LinearLayout postItem = new LinearLayout(getContext());  
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(resourceId, postItem, true);
        TextView uname = (TextView) postItem.findViewById(R.id.tvPostItemUname);
        TextView content = (TextView) postItem.findViewById(R.id.tvPostItemContent);
        TextView floor = (TextView) postItem.findViewById(R.id.tvPostItemFloor);
        TextView time = (TextView) postItem.findViewById(R.id.tvPostItemTime);
        ImageView avatar = (ImageView) postItem.findViewById(R.id.imagePostItemAvatar);
        ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(post.uxh), avatar);
        uname.setText(String.valueOf(post.uname));
        content.setText(String.valueOf(post.content));
        floor.setText(post.floor + "¥");
        time.setText(post.getPostTime());
		return postItem;
	}
	
}
