package com.ahutlesson.android.ui.timetable;

import com.ahutlesson.android.R;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

/**
 * 绘制周名称
 * 
 * */
public class Week extends ScheduleParent {
	private String[] weekNames;

	public Week(Activity activity, View view) {
		super(activity, view);
		weekNames = activity.getResources().getStringArray(R.array.week_name);
		// 设置周名称文字的大小
		paint.setTextSize(weekNameSize);
		paint.setAntiAlias(true);
	}

	@Override
	public void draw(Canvas canvas) {

		float left = borderMargin;
		float top = borderMargin;
		paint.setColor(Color.LTGRAY);
		canvas.drawLine(0, top, canvas.getWidth(), top, paint);
		
		float everyWeekWidth = (view.getMeasuredWidth() - borderMargin * 2) / 7;
		for (int i = 0; i < 7; i++) {
			if (i == 5 || i == 6)
				// 用于周六,日的颜色在其它地方用到
				paint.setColor(Color.parseColor("#D2691E"));
			else
				paint.setColor(Color.BLACK);

			left = borderMargin + everyWeekWidth * i
					+ (everyWeekWidth - paint.measureText(weekNames[i])) / 2;
			// 开始绘制周期名称
			canvas.drawText(weekNames[i], left, top + paint.getTextSize()
					+ weekNameMargin, paint);
		}

	}

}
