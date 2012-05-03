package com.ahutpt.lesson;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class ScheduleView extends View {
	public Schedule sch;

	public ScheduleView(Activity activity) {
		super(activity);

		sch = new Schedule(activity, this);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {

		sch.draw(canvas);

	}

	@Override
	public boolean onTouchEvent(MotionEvent motion) {
		sch.grid.openLessonDetail(motion.getX(), motion.getY());
		return super.onTouchEvent(motion);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return true;
	}
}
