package com.ahutlesson.android.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.actionbarsherlock.app.SherlockListFragment;
import com.ahutlesson.android.LessonActivity;
import com.ahutlesson.android.R;
import com.ahutlesson.android.adapter.HomeworkListAdapter;
import com.ahutlesson.android.lesson.Lesson;
import com.ahutlesson.android.lesson.LessonManager;

public class HomeworkFragment extends SherlockListFragment {

	private static final int MENU_DELETE = 1;
	
	private List<Lesson> homeworkLessonList;
	
	public static HomeworkFragment newInstance() {
		HomeworkFragment fragment = new HomeworkFragment();
		return fragment;
	}
	
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
		loadData();
		setListAdapter(new HomeworkListAdapter(getActivity(),
				R.layout.homework_item, homeworkLessonList));
    }  
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.homework_list, container, false);
	}
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
	    registerForContextMenu(getListView());
        
    }
	
	public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
	    menu.add(200, MENU_DELETE, Menu.NONE, R.string.delete);  
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getGroupId() != 200) return false;
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();		
		switch (item.getItemId()) {
		case MENU_DELETE:
			if (!LessonManager.loaded)
				new LessonManager(this.getActivity().getBaseContext());
			
			try{
				Lesson tmpLesson = homeworkLessonList.get(info.position);
				LessonManager.deleteHomework(tmpLesson.week, tmpLesson.time);
			}catch(IndexOutOfBoundsException ex){
				return true;
			}
			
			reload();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void loadData() {
		if (!LessonManager.loaded) {
			new LessonManager(getActivity());
		}
		homeworkLessonList = new ArrayList<Lesson>();
		for (Lesson[] lessons : LessonManager.lessons) {
			for (Lesson lesson : lessons) {
				if (lesson != null && lesson.hasHomework)
					homeworkLessonList.add(lesson);
			}
		}
	}
	
	private void reload(){
		loadData();
		FragmentManager fragmentManager = this.getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.frameLayoutFragment, HomeworkFragment.newInstance());
		transaction.commit();
	}
	
	// ¿Î³ÌÏêÇé
	public void openLessonDetail(int week,int time) {
		Intent i = new Intent(this.getActivity(), LessonActivity.class);
		i.putExtra("week", week);
		i.putExtra("time", time);
		this.startActivity(i);
	}

}
