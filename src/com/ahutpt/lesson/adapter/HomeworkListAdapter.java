package com.ahutpt.lesson.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahutpt.lesson.R;
import com.ahutpt.lesson.lesson.Lesson;
import com.ahutpt.lesson.time.Timetable;

public class HomeworkListAdapter  extends ArrayAdapter<Lesson>{
	
    private int resource;
    private LayoutInflater inflater;

    public HomeworkListAdapter (Context c, int resourceId, List<Lesson> objects) {

          super(c, resourceId, objects);
          resource = resourceId;
          inflater = LayoutInflater.from(c);
    }
    
    @Override
    public View getView (int position, View convertView, ViewGroup parent ) {

          convertView = (RelativeLayout) inflater.inflate(resource, null);

          Lesson lesson = getItem(position);

          TextView tvName = (TextView) convertView.findViewById(R.id.homeworkLessonItemName);
          TextView tvHomework = (TextView) convertView.findViewById(R.id.homeworkLessonItemHomework);
          TextView tvTime = (TextView) convertView.findViewById(R.id.homeworkLessonItemTime);
          
          tvName.setText(lesson.name);
          if(lesson.homework != null)
        	  tvHomework.setText(lesson.homework);
          tvTime.setText(Timetable.weekname[lesson.week]
  				+ Timetable.lessontime_name[lesson.time]);
          

          return convertView;
    }
}
