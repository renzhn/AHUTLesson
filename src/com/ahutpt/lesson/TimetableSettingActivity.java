package com.ahutpt.lesson;

import com.ahutpt.lesson.lesson.LessonManager;
import com.ahutpt.lesson.time.Timetable;

import com.actionbarsherlock.app.SherlockActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class TimetableSettingActivity extends SherlockActivity {
	private static final String[] mode = {"当前", "夏季时间" , "冬季时间" };
	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	private EditText etBeginDate_year,etBeginDate_month,etBeginDate_day;
	private EditText etBegin0,etBegin1,etBegin2,etBegin3,etBegin4;
	private EditText etEnd0,etEnd1,etEnd2,etEnd3,etEnd4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable);
		getSupportActionBar().setHomeButtonEnabled(false);
		if(!Timetable.loaded)
			new Timetable(this);
		if(!LessonManager.loaded)
			new LessonManager(this);
		
		initView();
		spinner = (Spinner) findViewById(R.id.spinnerSetMode);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mode);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener()); 
		Button btnSubmit = (Button)findViewById(R.id.btnSubmitTimeTable);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				submitTimeTable();
				TimetableSettingActivity.this.finish();
			}
		});
        if(etBegin0.getText().toString().contentEquals(""))
        		formatTimeTable(1);

		getSupportActionBar().setTitle("时间表设置");
	}
	
	protected void submitTimeTable() {
		// 更新时间表
		Timetable.setBeginDate_year(Integer.valueOf(etBeginDate_year.getText().toString()));
		Timetable.setBeginDate_month(Integer.valueOf(etBeginDate_month.getText().toString()));
		Timetable.setBeginDate_day(Integer.valueOf(etBeginDate_day.getText().toString()));
		Timetable.setBeginTime(0, etBegin0.getText().toString());
		Timetable.setBeginTime(1, etBegin1.getText().toString());
		Timetable.setBeginTime(2, etBegin2.getText().toString());
		Timetable.setBeginTime(3, etBegin3.getText().toString());
		Timetable.setBeginTime(4, etBegin4.getText().toString());
		Timetable.setEndTime(0, etEnd0.getText().toString());
		Timetable.setEndTime(1, etEnd1.getText().toString());
		Timetable.setEndTime(2, etEnd2.getText().toString());
		Timetable.setEndTime(3, etEnd3.getText().toString());
		Timetable.setEndTime(4, etEnd4.getText().toString());
		Timetable.loadData();
		Timetable.initTime();
		new LessonManager(this);
	}

	private void initView() {
		// 初始化控件
		etBeginDate_year = (EditText)findViewById(R.id.etBeginDate_year);
		etBeginDate_month = (EditText)findViewById(R.id.etBeginDate_month);
		etBeginDate_day = (EditText)findViewById(R.id.etBeginDate_day);
		
		etBegin0 = (EditText)findViewById(R.id.time_begin0);
		etBegin1 = (EditText)findViewById(R.id.time_begin1);
		etBegin2 = (EditText)findViewById(R.id.time_begin2);
		etBegin3 = (EditText)findViewById(R.id.time_begin3);
		etBegin4 = (EditText)findViewById(R.id.time_begin4);

		etEnd0 = (EditText)findViewById(R.id.time_end0);
		etEnd1 = (EditText)findViewById(R.id.time_end1);
		etEnd2 = (EditText)findViewById(R.id.time_end2);
		etEnd3 = (EditText)findViewById(R.id.time_end3);
		etEnd4 = (EditText)findViewById(R.id.time_end4);

		etBeginDate_year.setText(Integer.toString(Timetable.getBeginDate_year()));
		etBeginDate_month.setText(Integer.toString(Timetable.getBeginDate_month()));
		etBeginDate_day.setText(Integer.toString(Timetable.getBeginDate_day())); 
		
		etBegin0.setText(Timetable.begintime[0]);
		etBegin1.setText(Timetable.begintime[1]);
		etBegin2.setText(Timetable.begintime[2]);
		etBegin3.setText(Timetable.begintime[3]);
		etBegin4.setText(Timetable.begintime[4]);
	
		etEnd0.setText(Timetable.endtime[0]);
		etEnd1.setText(Timetable.endtime[1]);
		etEnd2.setText(Timetable.endtime[2]);
		etEnd3.setText(Timetable.endtime[3]);
		etEnd4.setText(Timetable.endtime[4]);
	
	}

	class SpinnerSelectedListener implements OnItemSelectedListener{  
		  
        public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,  
                long arg3) {  
        	formatTimeTable(pos);
        }  
  
        public void onNothingSelected(AdapterView<?> arg0) {
        }
  
    }

	public void formatTimeTable(int mode) {
		//重置时间表
		switch(mode){
		case 1:
			//winter
			etBegin0.setText("08:00");
			etBegin1.setText("10:00");
			etBegin2.setText("14:30");
			etBegin3.setText("16:30");
			etBegin4.setText("19:00");
			
			etEnd0.setText("09:35");
			etEnd1.setText("11:35");
			etEnd2.setText("16:05");
			etEnd3.setText("18:05");
			etEnd4.setText("21:30");
			break;
		case 2:
			//summer
			etBegin0.setText("08:00");
			etBegin1.setText("10:00");
			etBegin2.setText("14:00");
			etBegin3.setText("16:00");
			etBegin4.setText("18:30");
			
			etEnd0.setText("09:35");
			etEnd1.setText("11:35");
			etEnd2.setText("15:35");
			etEnd3.setText("17:35");
			etEnd4.setText("21:00");
			break;
		}
	}  

}
