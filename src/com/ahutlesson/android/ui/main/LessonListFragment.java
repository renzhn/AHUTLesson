package com.ahutlesson.android.ui.main;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.ahutlesson.android.EditLessonActivity;
import com.ahutlesson.android.LessonActivity;
import com.ahutlesson.android.MainActivity;
import com.ahutlesson.android.R;
import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.LessonManager;
import com.ahutlesson.android.model.Timetable;

public class LessonListFragment extends SherlockListFragment {

	private static final int MENU_EDIT = 0;
	private static final int MENU_DELETE = 1;
	
	private int weekDay;
	private Lesson[] todayLessons;
	private List<Lesson> lessonList;
	
	public static LessonListFragment newInstance(int weekDay0) {
		LessonListFragment fragment = new LessonListFragment();
		fragment.weekDay = weekDay0;
		return fragment;
	}
    
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
		loadData();
		setListAdapter(new LessonListAdapter(getActivity(),
				R.layout.lesson_item, lessonList));
    }
    
	@Override
	public void onActivityCreated(Bundle savedState) {
	    super.onActivityCreated(savedState);
	    registerForContextMenu(getListView());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.lesson_list, container, false);
	}
	
	public void onListItemClick(ListView parent, View v, int position, long id) {
		Lesson l = lessonList.get(position);
		openLessonDetail(l.week, l.time);
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
		menu.add(weekDay + 100, MENU_EDIT, Menu.NONE, R.string.edit);  
	    menu.add(weekDay + 100, MENU_DELETE, Menu.NONE, R.string.delete);  
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getGroupId() != weekDay + 100) return false;
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();		
		switch (item.getItemId()) {
		case MENU_EDIT:
			Intent i = new Intent(this.getActivity().getBaseContext(), EditLessonActivity.class);
			i.putExtra("week", lessonList.get(info.position).week);
			i.putExtra("time", lessonList.get(info.position).time);
			startActivity(i);
			return true;
		case MENU_DELETE:
			try{
				Lesson tmpLesson = lessonList.get(info.position);
				LessonManager lessonManager = LessonManager.getInstance(this.getActivity());
				lessonManager.deleteLessonAt(tmpLesson.week, tmpLesson.time);
				if(Timetable.getInstance(this.getActivity()).canAppend(tmpLesson))
					lessonManager.deleteLessonAt(tmpLesson.week, tmpLesson.time + 1);
			}catch(IndexOutOfBoundsException ex){
				return true;
			}
			
			reload();
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	private void loadData(){
		LessonManager lessonManager = LessonManager.getInstance(this.getActivity());
		todayLessons = lessonManager.lessons[weekDay];
		lessonList = new ArrayList<Lesson>();
		for(Lesson lesson : todayLessons){
			if(lesson != null && !Timetable.getInstance(this.getActivity()).isAppended(lesson)) 
				lessonList.add(lesson);
		}
	}
	
	private void reload(){
		loadData();
		MainActivity mainActiivty = (MainActivity)getActivity();
		mainActiivty.refreshTodayView();
	}
	
	// ¿Î³ÌÏêÇé
	public void openLessonDetail(int week,int time) {
		Intent i = new Intent(this.getActivity(), LessonActivity.class);
		i.putExtra("week", week);
		i.putExtra("time", time);
		this.startActivity(i);
	}
}
