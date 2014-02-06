package com.ahutlesson.android.ui;

import java.util.ArrayList;

import com.ahutlesson.android.R;
import com.ahutlesson.android.UserActivity;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.Lessonmate;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LessonmateAdapter extends ArrayAdapter<Lessonmate> {

	private Context context;
	private LayoutInflater inflater;

	public LessonmateAdapter(Context context0, int textViewResourceId,
			ArrayList<Lessonmate> list) {
		super(context0, textViewResourceId, list);
		context = context0;
		inflater = LayoutInflater.from(context0);
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		final Lessonmate lessonmate = getItem(position);

		if (lessonmate.registered) {
			ViewHolderRegistered holder;
			if (v == null || !(v.getTag() instanceof ViewHolderRegistered)) {
				holder = new ViewHolderRegistered();
				if (lessonmate.registered) {
					v = inflater.inflate(R.layout.lessonmate_registered_item,
							parent, false);
					holder.xm = (TextView) v.findViewById(R.id.tvXM);
					holder.bj = (TextView) v.findViewById(R.id.tvBJ);
					holder.zy = (TextView) v.findViewById(R.id.tvZY);
					holder.signature = (TextView) v
							.findViewById(R.id.tvSignature);
					holder.avatar = (ImageView) v.findViewById(R.id.ivAvatar);
				} else {
					v = inflater.inflate(R.layout.lessonmate_item, parent,
							false);
					holder.xm = (TextView) v.findViewById(R.id.tvXM);
					holder.bj = (TextView) v.findViewById(R.id.tvBJ);
					holder.zy = (TextView) v.findViewById(R.id.tvZY);
				}

				v.setTag(holder);
			} else {
				holder = (ViewHolderRegistered) v.getTag();
			}
			holder.xm.setText(lessonmate.xm);
			holder.bj.setText(lessonmate.bj);
			holder.zy.setText(lessonmate.zy);
			holder.signature.setText(lessonmate.signature);
			if (lessonmate.hasAvatar) {
				ImageLoader.getInstance()
						.displayImage(AHUTAccessor.getAvatarURI(lessonmate.xh),
								holder.avatar);
			} else {
				holder.avatar.setImageResource(R.drawable.noavatar);
			}
			View.OnClickListener clickUserListener = new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(context, UserActivity.class);
					i.putExtra("uxh", lessonmate.xh);
					context.startActivity(i);
				}
			};
			holder.avatar.setOnClickListener(clickUserListener);
			v.setOnClickListener(clickUserListener);
		} else {
			ViewHolder holder;
			if (v == null || !(v.getTag() instanceof ViewHolder)) {

				holder = new ViewHolder();
				if (lessonmate.registered) {
					v = inflater.inflate(R.layout.lessonmate_registered_item,
							parent, false);
					holder.xm = (TextView) v.findViewById(R.id.tvXM);
					holder.bj = (TextView) v.findViewById(R.id.tvBJ);
					holder.zy = (TextView) v.findViewById(R.id.tvZY);
				} else {
					v = inflater.inflate(R.layout.lessonmate_item, parent,
							false);
					holder.xm = (TextView) v.findViewById(R.id.tvXM);
					holder.bj = (TextView) v.findViewById(R.id.tvBJ);
					holder.zy = (TextView) v.findViewById(R.id.tvZY);
				}

				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			holder.xm.setText(lessonmate.xm);
			holder.bj.setText(lessonmate.bj);
			holder.zy.setText(lessonmate.zy);
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "TA»¹Ã»ÓÐ×¢²á¿ÎÓÑ£¬¿ìÑûÇëTA°É£¡",
							Toast.LENGTH_SHORT).show();
				}
			});
		}
		return v;
	}

	static class ViewHolderRegistered {
		TextView xm, bj, zy, signature;
		ImageView avatar;
	}

	static class ViewHolder {
		TextView xm, bj, zy;
	}
}
