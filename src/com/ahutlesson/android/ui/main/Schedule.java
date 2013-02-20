package com.ahutlesson.android.ui.main;

import java.util.ArrayList;

import com.ahutlesson.android.model.Lesson;


import android.app.Activity;
import android.graphics.Canvas;
import android.view.View;

/**
 * ªÊ÷∆»’¿˙
 * */
public class Schedule extends ScheduleParent {
	
	private ArrayList<ScheduleElement> elements = new ArrayList<ScheduleElement>();
	public GridView grid;

	public Schedule(Activity activity, View view, Lesson[][] lessons) {
		super(activity, view);
		elements.add(new Week(activity, view));
		grid = new GridView(activity, view, lessons);
		elements.add(grid);
	}

	@Override
	public void draw(Canvas canvas) {
		for (ScheduleElement ce : elements)
			ce.draw(canvas);
	}

}
