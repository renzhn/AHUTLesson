package com.ahutpt.lesson.view;

import com.ahutpt.lesson.interfaces.ScheduleElement;

import com.ahutpt.lesson.R;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
/**
 * 日历内容的基类
 * 
 * */
public class ScheduleParent implements ScheduleElement
{
	protected Activity context;
	protected View view;
	protected Paint paint = new Paint();
	protected float borderMargin;		
	protected float weekNameMargin;
	protected float weekNameSize,lessonNameSize,lessonPlaceSize;	

    public ScheduleParent(Activity activity, View view)
    {    	
    	this.context = activity;
    	this.view = view;

    	borderMargin = activity.getResources().getDimension(R.dimen.calendar_border_margin);
		
		weekNameMargin = activity.getResources().getDimension(R.dimen.weekname_margin);
        weekNameSize = activity.getResources().getDimension(R.dimen.weekname_size);
        lessonNameSize = activity.getResources().getDimension(R.dimen.lessonname_size);
        lessonPlaceSize = activity.getResources().getDimension(R.dimen.lessonplace_size);

    }
	public void draw(Canvas canvas)
	{		
		
	}

}
