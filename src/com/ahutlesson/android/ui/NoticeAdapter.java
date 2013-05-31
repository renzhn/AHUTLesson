package com.ahutlesson.android.ui;

import java.util.ArrayList;

import com.ahutlesson.android.R;
import com.ahutlesson.android.UserActivity;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.Notice;
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

public class NoticeAdapter extends ArrayAdapter<Notice> {

	private Context context;
	private int resourceId;
	private LayoutInflater inflater;
	
	public NoticeAdapter(Context context0, int textViewResourceId, ArrayList<Notice> list) {
		super(context0, textViewResourceId, list);
        resourceId = textViewResourceId;
        context = context0;
		inflater = LayoutInflater.from(context0);
	}
	
	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder;
		if(v == null) {
			v = inflater.inflate(resourceId, parent, false);
			holder = new ViewHolder();
			holder.uname = (TextView) v.findViewById(R.id.tvItemUname);
			holder.content = (TextView) v.findViewById(R.id.tvItemContent);
			holder.time = (TextView) v.findViewById(R.id.tvItemTime);
			holder.avatar = (ImageView) v.findViewById(R.id.ivAvatar);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		
		final Notice notice = getItem(position);
		if (notice == null)		return v;
		
        if(notice.hasAvatar) {
            ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(notice.fromUxh), holder.avatar);
        }else{
        	holder.avatar.setImageResource(R.drawable.noavatar);
        }
		holder.uname.setText(notice.uname);
		holder.content.setText("我回复了你的帖子“" + notice.subject + "”，快去看看吧");
		if(!notice.read) holder.content.setTypeface(null, Typeface.BOLD);
		holder.time.setText(notice.getPostTime());
        View.OnClickListener clickUserListener = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(context, UserActivity.class);
				i.putExtra("uxh", notice.fromUxh);
				context.startActivity(i);
			}
		};
		holder.avatar.setOnClickListener(clickUserListener);
		holder.uname.setOnClickListener(clickUserListener);
		return v;
	}
	
	static class ViewHolder {
		TextView uname, content, time;
		ImageView avatar;
	}

}
