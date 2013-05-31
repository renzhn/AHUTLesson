package com.ahutlesson.android.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ahutlesson.android.R;
import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.Timetable;

public class HomeworkListAdapter extends ArrayAdapter<Lesson> {

	private int resource;
	private LayoutInflater inflater;

	public HomeworkListAdapter(Context context, int resourceId, List<Lesson> objects) {
		super(context, resourceId, objects);
		resource = resourceId;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder;

		if (v == null) {
			v = inflater.inflate(resource, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) v.findViewById(R.id.homeworkLessonItemName);
			holder.homework = (TextView) v.findViewById(R.id.homeworkLessonItemHomework);
			holder.time = (TextView) v.findViewById(R.id.homeworkLessonItemTime);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		Lesson lesson = getItem(position);
		if(lesson == null) return v;

		holder.name.setText(lesson.name);
		if (lesson.homework != null)
			holder.homework.setText(lesson.homework);
		Timetable timetable = Timetable.getInstance(this.getContext());
		holder.time.setText(timetable.weekName[lesson.week]
				+ timetable.lessontimeName[lesson.time]);

		return v;
	}
	
	static class ViewHolder {
		TextView name, homework, time;
	}
}
