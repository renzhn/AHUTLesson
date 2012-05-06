package com.ahutpt.lesson.view;

import java.util.ArrayList;

import com.ahutpt.lesson.interfaces.ScheduleElement;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.View;

/**
 * ªÊ÷∆»’¿˙
 * */
public class Schedule extends ScheduleParent {
	
	private ArrayList<ScheduleElement> elements = new ArrayList<ScheduleElement>();
	public Grid grid;

	public Schedule(Activity activity, View view) {
		super(activity, view);
		elements.add(new Week(activity, view));
		grid = new Grid(activity, view);
		elements.add(grid);
	}

	@Override
	public void draw(Canvas canvas) {
		for (ScheduleElement ce : elements)
			ce.draw(canvas);
	}

}
