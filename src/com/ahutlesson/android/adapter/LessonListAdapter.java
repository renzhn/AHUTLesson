package com.ahutlesson.android.adapter;

import java.util.List;

import com.ahutlesson.android.R;
import com.ahutlesson.android.lesson.Lesson;
import com.ahutlesson.android.time.Timetable;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LessonListAdapter extends ArrayAdapter<Lesson> {
	
    private int resource;
    private LayoutInflater inflater;

    public LessonListAdapter (Context c, int resourceId, List<Lesson> objects) {

          super(c, resourceId, objects);
          resource = resourceId;
          inflater = LayoutInflater.from(c);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent ) {

          convertView = (RelativeLayout) inflater.inflate(resource, null);

          Lesson lesson = getItem(position);

          TextView tvName = (TextView) convertView.findViewById(R.id.lessonItemName);
          TextView tvPlace = (TextView) convertView.findViewById(R.id.lessonItemPlace);
          TextView tvTime = (TextView) convertView.findViewById(R.id.lessonItemTime);
          
          if(!lesson.isAppended()){
              tvTime.setText(Timetable.begintime[lesson.time] + " ~ "
        				+ Timetable.endtime[lesson.time]);
          }else{
              tvTime.setText(Timetable.begintime[lesson.time] + " ~ "
        				+ Timetable.endtime[lesson.time + 1]);
          }
          tvPlace.setText(lesson.place);
          
          if(lesson.beforeStart){
              tvName.setText(lesson.name + " (未开始)");
              tvName.setTextColor(Color.parseColor("#666666"));
          }else if(lesson.hasHomework){
        	  tvName.setText(lesson.name + " (有作业)");
              tvName.setTextColor(Color.parseColor("#CE5600"));
          }else if(lesson.afterEnd){
              tvName.setText(lesson.name + " (已结课)");
              tvName.setTextColor(Color.parseColor("#666666"));
          }else{
              tvName.setText(lesson.name);
          }

          return convertView;
    }
}
