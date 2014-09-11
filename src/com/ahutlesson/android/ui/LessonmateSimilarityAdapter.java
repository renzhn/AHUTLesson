package com.ahutlesson.android.ui;

import java.util.ArrayList;

import com.ahutlesson.android.R;
import com.ahutlesson.android.model.LessonmateSimilarity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LessonmateSimilarityAdapter extends
		ArrayAdapter<LessonmateSimilarity> {

	private LayoutInflater inflater;

	public LessonmateSimilarityAdapter(Context context0,
			int textViewResourceId, ArrayList<LessonmateSimilarity> list) {
		super(context0, textViewResourceId, list);
		inflater = LayoutInflater.from(context0);
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		final LessonmateSimilarity row = getItem(position);
		ViewHolder holder;
		if (v == null || !(v.getTag() instanceof ViewHolder)) {

			holder = new ViewHolder();
			v = inflater.inflate(R.layout.lessonmate_similarity, parent, false);
			holder.xh = (TextView) v.findViewById(R.id.tvXH);
			holder.similarity = (TextView) v.findViewById(R.id.tvSimilarity);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		holder.xh.setText(row.xh);
		holder.similarity.setText(row.getSimilarity());
		return v;
	}

	static class ViewHolder {
		TextView xh, similarity;
	}
}
