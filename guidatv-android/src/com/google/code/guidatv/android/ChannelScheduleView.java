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
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.google.code.guidatv.android.rest.GuidaTvService;
import com.google.code.guidatv.model.Schedule;
import com.google.code.guidatv.model.Transmission;

public class ChannelScheduleView extends ListActivity
{
    private static final String CHANNEL_CODE_ID = "CHANNEL_CODE";

	private GuidaTvService mGuidaTvService;
    
    private Date mCurrentDate;
    
    private String mChannelCode;

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
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        TwoLineListItem item = (TwoLineListItem) v;
        TextView text2 = item.getText2();
        if (text2.getVisibility() == View.GONE) {
            text2.setVisibility(View.VISIBLE);
        } else {
            text2.setVisibility(View.GONE);
        }
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
        	if (schedule == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ChannelScheduleView.this);
				builder.setMessage(
						"Cannot get schedule. Are you connected to Internet?")
						.setCancelable(false)
						.setPositiveButton("OK",
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
                    { "TIME_AND_NAME", "INFO" }, new int[]
                    { android.R.id.text1, android.R.id.text2 });
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
