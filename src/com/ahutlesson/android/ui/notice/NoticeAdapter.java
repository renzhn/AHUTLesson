package com.ahutlesson.android.ui.notice;

import java.util.ArrayList;

import com.ahutlesson.android.R;
import com.ahutlesson.android.api.AHUTAccessor;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NoticeAdapter extends ArrayAdapter<Notice> {
	
	private int resourceId;
	
	public NoticeAdapter(Context context, int textViewResourceId, ArrayList<Notice> list) {
		super(context, textViewResourceId, list);
        this.resourceId = textViewResourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Notice notice = getItem(position);
        LinearLayout item = new LinearLayout(getContext());  
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(resourceId, item, true);
        TextView uname = (TextView) item.findViewById(R.id.tvItemUname);
        TextView content = (TextView) item.findViewById(R.id.tvItemContent);
        TextView time = (TextView) item.findViewById(R.id.tvItemTime);
        ImageView avatar = (ImageView) item.findViewById(R.id.ivAvatar);
        if(notice.hasAvatar) {
            ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(notice.fromUxh), avatar);
        }else{
        	avatar.setImageResource(R.drawable.noavatar);
        }
        uname.setText(notice.uname);
        content.setText("我回复了你的帖子“" + notice.subject + "”，快去看看吧");
        if(!notice.read) content.setTypeface(null, Typeface.BOLD);
        time.setText(notice.getPostTime());
		return item;
	}

}
