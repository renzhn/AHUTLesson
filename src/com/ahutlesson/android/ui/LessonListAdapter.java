package com.ahutlesson.android.ui;

import java.util.List;

import com.ahutlesson.android.MainActivity;
import com.ahutlesson.android.R;
import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.Timetable;
import com.readystatesoftware.viewbadger.BadgeView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LessonListAdapter extends ArrayAdapter<Lesson> {

	private Context context;
	private int resource;
	private LayoutInflater inflater;

	public LessonListAdapter(Context context0, int resourceId,
			List<Lesson> objects) {
		super(context0, resourceId, objects);
		context = context0;
		resource = resourceId;
		inflater = LayoutInflater.from(context0);
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder;
		if(v == null) {
			v = inflater.inflate(resource, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) v.findViewById(R.id.lessonItemName);
			holder.place = (TextView) v.findViewById(R.id.lessonItemPlace);
			holder.badgeHolder = (TextView) v.findViewById(R.id.lessonItemBadgeHolder);
			holder.time = (TextView) v.findViewById(R.id.lessonItemTime);
			holder.teacher = (TextView) v.findViewById(R.id.lessonItemTeacher);
            v.setTag(holder);
		}else{
			holder = (ViewHolder) v.getTag();
		}

		Lesson lesson = getItem(position);
		if(lesson == null) return v;
		
		boolean hasNew = MainActivity.unreadLessonForum.contains(lesson.lid);

		Timetable timetable = Timetable.getInstance(this.getContext());
		if(!timetable.isAppended(lesson)) {
			holder.time.setText(timetable.begintime[lesson.time] + " ~ "
					+ timetable.endtime[lesson.time]);
		}else{
			holder.time.setText(timetable.begintime[lesson.time] + " ~ "
					+ timetable.endtime[lesson.time + 1]);
		}
		holder.place.setText(lesson.place);
		holder.teacher.setText(lesson.teacher);

		if(lesson.beforeStart(context)) {
			holder.name.setText(lesson.name + " (未开始)");
			holder.name.setTextColor(Color.parseColor("#666666"));
		}else if(lesson.hasHomework) {
			holder.name.setText(lesson.name + " (有作业)");
			holder.name.setTextColor(Color.parseColor("#CE5600"));
		}else if(lesson.afterEnd(context)) {
			holder.name.setText(lesson.name + " (已结课)");
			holder.name.setTextColor(Color.parseColor("#666666"));
		}else{
			holder.name.setText(lesson.name);
		}
		if(hasNew) {
			holder.badge = new BadgeView(context, holder.badgeHolder);
			holder.badge.setText("新帖");
			holder.badge.show();
		}
		return v;
	}

	static class ViewHolder {
		TextView name, place, time, teacher, badgeHolder;
		BadgeView badge;
	}
}
