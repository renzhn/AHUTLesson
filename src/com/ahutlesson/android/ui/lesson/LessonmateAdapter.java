package com.ahutlesson.android.ui.lesson;

import java.util.ArrayList;

import com.ahutlesson.android.R;
import com.ahutlesson.android.api.AHUTAccessor;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LessonmateAdapter extends ArrayAdapter<Lessonmate> {

	private int resourceId;
	View item;
	 
	public LessonmateAdapter(Context context, int textViewResourceId, ArrayList<Lessonmate> list) {
		super(context, textViewResourceId, list);
        this.resourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Lessonmate lessonmate = getItem(position);
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        item = inflater.inflate(resourceId, parent, false);
        TextView xm = (TextView) item.findViewById(R.id.tvXM);
        TextView bj = (TextView) item.findViewById(R.id.tvBJ);
        TextView zy = (TextView) item.findViewById(R.id.tvZY);
        xm.setText(lessonmate.xm);
        bj.setText(lessonmate.bj);
        zy.setText(lessonmate.zy);
        if(lessonmate.registered) {
            ImageView avatar = (ImageView) item.findViewById(R.id.ivAvatar);
            if(lessonmate.hasAvatar) {
                ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(lessonmate.xh), avatar);
            }else{
            	avatar.setImageResource(R.drawable.noavatar);
            }
            avatar.setVisibility(View.VISIBLE);
        }else{
        	
        }
        return item;
	}
}
