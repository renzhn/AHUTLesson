package com.ahutlesson.android.ui.main;

import java.util.List;

import com.ahutlesson.android.R;
import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.Timetable;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LessonListAdapter extends ArrayAdapter<Lesson> {
	
	private Context context;
    private int resource;
    private LayoutInflater inflater;

    public LessonListAdapter (Context context0, int resourceId, List<Lesson> objects) {
          super(context0, resourceId, objects);
          context = context0;
          resource = resourceId;
          inflater = LayoutInflater.from(context0);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent ) {

          convertView = (LinearLayout) inflater.inflate(resource, null);

          Lesson lesson = getItem(position);

          TextView tvName = (TextView) convertView.findViewById(R.id.lessonItemName);
          TextView tvPlace = (TextView) convertView.findViewById(R.id.lessonItemPlace);
          TextView tvTime = (TextView) convertView.findViewById(R.id.lessonItemTime);
          TextView tvTeacher = (TextView) convertView.findViewById(R.id.lessonItemTeacher);

          Timetable timetable = Timetable.getInstance(this.getContext());
          if(!timetable.isAppended(lesson)){
              tvTime.setText(timetable.begintime[lesson.time] + " ~ "
        				+ timetable.endtime[lesson.time]);
          }else{
              tvTime.setText(timetable.begintime[lesson.time] + " ~ "
        				+ timetable.endtime[lesson.time + 1]);
          }
          tvPlace.setText(lesson.place);
          tvTeacher.setText(lesson.teacher);
          
          if(lesson.beforeStart(context)){
              tvName.setText(lesson.name + " (未开始)");
              tvName.setTextColor(Color.parseColor("#666666"));
          }else if(lesson.hasHomework){
        	  tvName.setText(lesson.name + " (有作业)");
              tvName.setTextColor(Color.parseColor("#CE5600"));
          }else if(lesson.afterEnd(context)){
              tvName.setText(lesson.name + " (已结课)");
              tvName.setTextColor(Color.parseColor("#666666"));
          }else{
              tvName.setText(lesson.name);
          }

          return convertView;
    }
}
