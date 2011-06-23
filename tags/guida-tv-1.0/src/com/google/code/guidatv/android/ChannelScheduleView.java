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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.resource.ResourceException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.code.guidatv.android.rest.GuidaTvService;
import com.google.code.guidatv.model.Schedule;
import com.google.code.guidatv.model.Transmission;

public class ChannelScheduleView extends ListActivity
{
    private static final String CHANNEL_CODE_ID = "CHANNEL_CODE";
    
    private static final Uri GOOGLE_URI = Uri.parse("http://www.google.it");
    
    private static final Uri WIKIPEDIA_URI = Uri.parse("http://it.m.wikipedia.org/");
    
    private static final Uri IMDB_URI = Uri.parse("http://m.imdb.com");

	private GuidaTvService mGuidaTvService;
    
    private Date mCurrentDate;
    
    private String mChannelCode;
    
    private Schedule mSchedule;
    
    private static final int OPEN_GOOGLE_ID = Menu.FIRST;
    
    private static final int OPEN_WIKIPEDIA_ID = Menu.FIRST + 1;
    
    private static final int OPEN_IMDB_ID = Menu.FIRST + 2;
    
    private static final int OPEN_INFO_ID = Menu.FIRST + 3;
    
    private static final int OPEN_HOME_ID = Menu.FIRST + 4;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_channel_schedule);
        mGuidaTvService = new GuidaTvService();
        Bundle extras = getIntent().getExtras();
        mCurrentDate = null;
        mChannelCode = savedInstanceState == null ? null : savedInstanceState.getString(CHANNEL_CODE_ID);
        if (mChannelCode == null) {
        	mChannelCode = extras.getString(CHANNEL_CODE_ID);
        }
        registerForContextMenu(getListView());
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putString(CHANNEL_CODE_ID, mChannelCode);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	fillData();
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, OPEN_GOOGLE_ID, 0, R.string.menu_open_google);
        menu.add(0, OPEN_WIKIPEDIA_ID, 0, R.string.menu_open_wikipedia);
        menu.add(0, OPEN_IMDB_ID, 0, R.string.menu_open_imdb);
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        Transmission transmission = mSchedule.getTransmissions().get(info.position);
        String transmissionInfo = transmission.getDescription();
        if (transmissionInfo != null) {
        	menu.add(0, OPEN_INFO_ID, 0, R.string.menu_open_info);
        }
		String mainLink = transmission.getMainLink();
        if (mainLink != null) {
        	menu.add(0, OPEN_HOME_ID, 0, R.string.menu_open_home);
        }
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	Uri destination = null;
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Transmission transmission = mSchedule.getTransmissions().get(
				info.position);
		switch(item.getItemId()) {
    	case OPEN_GOOGLE_ID:
			destination = Uri.withAppendedPath(GOOGLE_URI,
					"search?q=" + Uri.encode(transmission.getName()));
	        break;
    	case OPEN_WIKIPEDIA_ID:
			destination = Uri.withAppendedPath(WIKIPEDIA_URI,
					"wiki?search=" + Uri.encode(transmission.getName()));
    		break;
    	case OPEN_IMDB_ID:
			destination = Uri.withAppendedPath(IMDB_URI,
					"find?q=" + Uri.encode(transmission.getName()));
    		break;
    	case OPEN_INFO_ID:
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(transmission.getDescription()).setCancelable(false).setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
    		AlertDialog dialog = builder.create();
    		dialog.show();
    		break;
    	case OPEN_HOME_ID:
    		destination = Uri.parse(transmission.getMainLink());
		}
		if (destination != null) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					destination);
			startActivity(browserIntent);
		}
    	return super.onContextItemSelected(item);
    }

    private void fillData()
    {
    	Date currentDate = getRealApplication().getCurrentDate();
		if (mCurrentDate == null || !mCurrentDate.equals(currentDate)) {
    		mCurrentDate = currentDate;
        	setListAdapter(null);
    		new LoadScheduleTask().execute(mChannelCode, mCurrentDate);
    	}
    }

    private class LoadScheduleTask extends AsyncTask<Serializable, Void, Schedule> {

        private Date requestedDate;

		@Override
        protected Schedule doInBackground(Serializable... params)
        {
			try {
				requestedDate = (Date) params[1];
				return mGuidaTvService.getSchedule((String) params[0], requestedDate);
			} catch (IOException e) {
				Log.d("ChannelScheduleView", "Cannot retrieve schedule for "
						+ params[0] + ", are you connected to Internet", e);
				return null;
			} catch (ResourceException e) {
				Log.d("ScheduleView", "Cannot retrieve channels", e);
				return null;
			}
        }

        @Override
        protected void onPostExecute(Schedule schedule)
        {
        	mSchedule = schedule;
        	if (schedule == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ChannelScheduleView.this);
				builder.setMessage(
						R.string.error_cannot_get_schedule)
						.setCancelable(false)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								});
				AlertDialog dialog = builder.create();
				dialog.show();
				TextView empty = (TextView) findViewById(android.R.id.empty);
				empty.setText("Unreadable schedule");
        		return;
        	}
        	Calendar nowCalendar = Calendar.getInstance();
			Date now = nowCalendar.getTime();
        	Calendar requestedDateCalendar = nowCalendar;
        	requestedDateCalendar.setTime(requestedDate);
        	boolean isToday = nowCalendar.get(Calendar.YEAR) == requestedDateCalendar.get(Calendar.YEAR) &&
        	nowCalendar.get(Calendar.MONTH) == requestedDateCalendar.get(Calendar.MONTH) &&
        	nowCalendar.get(Calendar.DATE) == requestedDateCalendar.get(Calendar.DATE);
        	int index = 0;
            List<Map<String, String>> transmissionList = new ArrayList<Map<String,String>>();
            for (Transmission transmission : schedule.getTransmissions()) {
                Map<String, String> transmissionMap = new HashMap<String, String>();
                transmissionMap.put("INFO", transmission.getDescription());
                transmissionMap.put("TIME_AND_NAME",
                        DateFormat.format("kk:mm", transmission.getStart())
                                .toString() + " " + transmission.getName());
                transmissionList.add(transmissionMap);
                if (now.after(transmission.getStart())) {
                	index++;
                }
            }
            SimpleAdapter adapter = new SimpleAdapter(
                    ChannelScheduleView.this, transmissionList, R.layout.transmission_item,
                    new String[]
                    { "TIME_AND_NAME" }, new int[]
                    { R.id.text });
            setListAdapter(adapter);
            if (isToday) {
            	if (index > 0) {
            		index--;
            	}
            	ListView listView = (ListView) findViewById(android.R.id.list);
            	listView.setSelection(index);
            }
        }
    }
	
	private GuidaTvApplication getRealApplication() {
		return (GuidaTvApplication) getApplication();
	}
}
