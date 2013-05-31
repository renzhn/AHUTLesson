package com.ahutlesson.android.ui.timetable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.ahutlesson.android.R;

public class ScheduleParent implements ScheduleElement {
	protected Context context;
	protected View view;
	protected Paint paint = new Paint();
	protected float borderMargin;		
	protected float weekNameMargin;
	protected float weekNameSize,lessonNameSize,lessonPlaceSize;	

    public ScheduleParent(Context activity, View view) {    	
    	this.context = activity;
    	this.view = view;

    	borderMargin = activity.getResources().getDimension(R.dimen.calendar_border_margin);
		
		weekNameMargin = activity.getResources().getDimension(R.dimen.weekname_margin);
        weekNameSize = activity.getResources().getDimension(R.dimen.weekname_size);
        lessonNameSize = activity.getResources().getDimension(R.dimen.lessonname_size);
        lessonPlaceSize = activity.getResources().getDimension(R.dimen.lessonplace_size);

    }

	@Override
	public void draw(Canvas canvas) {
		
	}

}
