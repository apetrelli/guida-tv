/*
 *  Guida TV: una guida TV per canali italiani.
 *  Copyright (C) 2011 Antonio Petrelli
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.google.code.guidatv.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.google.code.guidatv.android.db.ChannelDbAdapter;
import com.google.code.guidatv.android.util.MathUtils;
import com.google.code.guidatv.model.Channel;

public class ScheduleView extends TabActivity {

	private static final int CHANGE_DATE = Menu.FIRST;
	
	private static final int SELECT_CHANNELS = CHANGE_DATE + 1;
	
	private static final int SORT_CHANNELS = SELECT_CHANNELS + 1;
	
	private static final int ABOUT = SORT_CHANNELS + 1;

    private static final int ACTIVITY_CHANNELS = 2;
    
    private static final int ACTIVITY_SORT_CHANNELS = 3;
	
	private static final int DATE_DIALOG_ID = 0;

	int channelCount = 0;

	private ChannelDbAdapter mDb;
	
	private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, 
                                  int monthOfYear, int dayOfMonth) {
            	Calendar cal = Calendar.getInstance();
				cal.set(year, monthOfYear, dayOfMonth);
				getRealApplication().setCurrentDate(cal.getTime());
				writeDateText();
				
				// Force reload.
				TabHost tabHost = getTabHost(); // The activity TabHost
				int currentTab = tabHost.getCurrentTab();
				if (currentTab == 0) {
					tabHost.setCurrentTab(1);
				} else {
					tabHost.setCurrentTab(0);
				}
				tabHost.setCurrentTab(currentTab);
            }
        };
        
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);
		mDb = new ChannelDbAdapter(this);
		mDb.open();
		fillData();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDb.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean retValue = super.onCreateOptionsMenu(menu);
		menu.add(0, CHANGE_DATE, 0, R.string.menu_change_date);
		menu.add(0, SELECT_CHANNELS, 0, R.string.menu_channels);
		menu.add(0, SORT_CHANNELS, 0, R.string.menu_sort_channels);
		menu.add(0, ABOUT, 0, R.string.menu_about);
		return retValue;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case CHANGE_DATE:
			selectDate();
			return true;
		case SELECT_CHANNELS:
            i = new Intent(this, ChannelSelectView.class);
            startActivityForResult(i, ACTIVITY_CHANNELS);
			return true;
		case SORT_CHANNELS:
            i = new Intent(this, SortChannelsView.class);
            startActivityForResult(i, ACTIVITY_SORT_CHANNELS);
			return true;
		case ABOUT:
			showAboutDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillData();
	}
	
	private GuidaTvApplication getRealApplication() {
		return (GuidaTvApplication) getApplication();
	}
	
	private void selectDate() {
		showDialog(DATE_DIALOG_ID);
	}
	
	private void showAboutDialog() {
		SpannableString string = new SpannableString(getString(R.string.about_text));
		final TextView message = new TextView(this);
		message.setPadding(5, 5, 5, 5);
		message.setText(string);
		message.setVerticalScrollBarEnabled(true);
		message.setMovementMethod(new ScrollingMovementMethod());
		Linkify.addLinks(message, Linkify.ALL);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.about_title)).setCancelable(true).setPositiveButton(android.R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).setView(message);
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			Calendar cal = Calendar.getInstance();
			cal.setTime(getRealApplication().getCurrentDate());
			return new DatePickerDialog(this,
                    mDateSetListener,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));			
		}
		return null;
	}

	private void fillData() {
		writeDateText();
		Cursor cursor = mDb.fetchAllChannels();
		startManagingCursor(cursor);
		boolean goOn = cursor.moveToFirst();
		List<Channel> channels = new ArrayList<Channel>();
		while (goOn) {
			channels.add(new Channel(cursor.getString(cursor
					.getColumnIndexOrThrow(ChannelDbAdapter.KEY_CODE)), cursor
					.getString(cursor
							.getColumnIndexOrThrow(ChannelDbAdapter.KEY_NAME)), null, null, null));
			goOn = cursor.moveToNext();
		}
		loadChannelTabs(channels);
	}

	private void writeDateText() {
		TextView dateView = (TextView) findViewById(R.id.selectedDate);
		dateView.setText(DateFormat.getDateFormat(this).format(getRealApplication().getCurrentDate()));
	}

	private void loadChannelTabs(List<Channel> channels) {
		if (channels != null) {
			TabHost tabHost = getTabHost(); // The activity TabHost
			tabHost.setCurrentTab(0);
			tabHost.clearAllTabs();
			for (Channel channel : channels) {
				Map<String, String> channelMap = new HashMap<String, String>();
				channelMap.put("NAME", channel.getName());
				tabHost.addTab(addTab(channel, tabHost));
				channelCount++;
			}
		}
	}

	private TabHost.TabSpec addTab(Channel channel, TabHost tabHost) {
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(ScheduleView.this,
				ChannelScheduleView.class);
		intent.putExtra("CHANNEL_CODE", channel.getCode());

		// Initialize a TabSpec for each tab and add it to the TabHost
		View tabview = createTabView(tabHost.getContext(),
				channel.getName());
		spec = tabHost.newTabSpec(channel.getCode()).setIndicator(tabview)
				.setContent(intent);
		return spec;
	}

	private View createTabView(Context context, String text) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.channel_tab, null);
		TextView tv = (TextView) view.findViewById(R.id.tabText);
		tv.setText(text);
		return view;
	}
	public static class FlingableTabHost extends TabHost {
        GestureDetector mGestureDetector;

        Animation mRightInAnimation;
        Animation mRightOutAnimation;
        Animation mLeftInAnimation;
        Animation mLeftOutAnimation;

        public FlingableTabHost(Context context, AttributeSet attrs) {
            super(context, attrs);

            mRightInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
            mRightOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
            mLeftInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
            mLeftOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);

            final int minScaledFlingVelocity = ViewConfiguration.get(context)
                    .getScaledMinimumFlingVelocity() * 10; // 10 = fudge by experimentation

            mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                        float velocityY) {
                    if (Math.abs(velocityX) > minScaledFlingVelocity &&
                        Math.abs(velocityY) < minScaledFlingVelocity) {

                        final boolean right = velocityX < 0;
                        switchTab(right);
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (mGestureDetector.onTouchEvent(ev)) {
                return true;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if (mGestureDetector.onTouchEvent(ev)) {
                return true;
            }
            return super.onTouchEvent(ev);
        }

		public void switchTab(final boolean right) {
			int tabCount = getTabWidget().getTabCount();
			int currentTab = getCurrentTab();
			final int newTab = MathUtils.constrain(currentTab + (right ? 1 : -1),
			        0, tabCount - 1);
			if (newTab != currentTab) {
			    // Somewhat hacky, depends on current implementation of TabHost:
			    // http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;
			    // f=core/java/android/widget/TabHost.java
			    View currentView = getCurrentView();
			    setCurrentTab(newTab);
			    HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.tabsContainer);
			    TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);
			    tabWidget.focusCurrentTab(newTab);
			    View tab = tabWidget.getChildAt(newTab);
			    scroll.scrollTo(tab.getLeft() - 10, scroll.getScrollY());
			    View newView = getCurrentView();

			    newView.startAnimation(right ? mRightInAnimation : mLeftInAnimation);
			    currentView.startAnimation(
			            right ? mRightOutAnimation : mLeftOutAnimation);
			}
		}
    }
}
