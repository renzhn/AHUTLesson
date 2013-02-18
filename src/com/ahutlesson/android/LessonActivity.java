package com.ahutlesson.android;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.model.User;
import com.ahutlesson.android.model.UserManager;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class LessonActivity extends BaseFragmentActivity implements OnNavigationListener {

	private static final String[] TITLES = {"课程讨论", "课友列表"};
	private static int viewMode = 0;
	
	private UserManager userManager;
	private User user;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar.setDisplayShowTitleEnabled(false);

		// List Navigation
		Context context = actionBar.getThemedContext();
		ArrayAdapter<CharSequence> list = new ArrayAdapter<CharSequence>(context,
				R.layout.sherlock_spinner_item);
		list.add(TITLES[0]);
		list.add(TITLES[1]);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(list, this);
		
		showView();
	}

	@Override
	protected void onResume() {
		// 删除或修改后重新载入
		super.onResume();
		userManager = UserManager.getInstance(this);
		user = userManager.getUser();
	}
	
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// 选择导航菜单
		viewMode = itemPosition;
		showView();
		invalidateOptionsMenu();
		return false;
	}

	public void showView() {
		switch (viewMode) {
		case 0:
			loadLessonForum();
			break;
		case 1:
			
			break;
		}
	}
	
	private void loadLessonForum() {
		
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		switch (viewMode) {
		case 0:
			menu.add(R.string.edit)
				.setIcon(android.R.drawable.ic_menu_edit)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add(user.uname)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			menu.add(R.string.delete)
				.setIcon(android.R.drawable.ic_menu_delete)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			break;
		}
		return true;
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
	
}
