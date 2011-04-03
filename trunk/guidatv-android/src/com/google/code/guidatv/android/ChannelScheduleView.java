package com.google.code.guidatv.android;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.google.code.guidatv.android.rest.GuidaTvService;
import com.google.code.guidatv.model.Schedule;
import com.google.code.guidatv.model.Transmission;

public class ChannelScheduleView extends ListActivity
{
	static final int SWIPE_MIN_DISTANCE = 120;
	static final int SWIPE_MAX_OFF_PATH = 250;
	static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

    private GuidaTvService mGuidaTvService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_channel_schedule);
//		gestureDetector = new GestureDetector(new SwipeGestureDetector());
//		gestureListener = new View.OnTouchListener() {
//			public boolean onTouch(View v, MotionEvent event) {
//				if (gestureDetector.onTouchEvent(event)) {
//					return true;
//				}
//				return false;
//			}
//		};
        mGuidaTvService = new GuidaTvService();
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
//
//    @Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if (gestureDetector.onTouchEvent(event))
//			return true;
//		else
//			return false;
//	}

    private void fillData()
    {
        Bundle extras = getIntent().getExtras();
		new LoadScheduleTask().execute(extras.getString("CHANNEL_CODE"),
				extras.getSerializable("DATE"));
    }

    private class LoadScheduleTask extends AsyncTask<Serializable, Void, Schedule> {

        @Override
        protected Schedule doInBackground(Serializable... params)
        {
			return mGuidaTvService.getSchedule((String) params[0], (Date) params[1]);
        }

        @Override
        protected void onPostExecute(Schedule schedule)
        {
            List<Map<String, String>> channelList = new ArrayList<Map<String,String>>();
            for (Transmission transmission : schedule.getTransmissions()) {
                Map<String, String> channelMap = new HashMap<String, String>();
                channelMap.put("INFO", transmission.getDescription());
                channelMap.put("TIME_AND_NAME",
                        DateFormat.format("kk:mm", transmission.getStart())
                                .toString() + " " + transmission.getName());
                channelList.add(channelMap);
            }
            SimpleAdapter adapter = new SimpleAdapter(
                    ChannelScheduleView.this, channelList, R.layout.transmission_item,
                    new String[]
                    { "TIME_AND_NAME", "INFO" }, new int[]
                    { android.R.id.text1, android.R.id.text2 });
            setListAdapter(adapter);
        }
    }

	class SwipeGestureDetector extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Intent i = new Intent();
				    i.setAction("com.google.code.guidatv.android.SWITCH_RIGHT");
				    sendBroadcast(i);
				    // Right
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Intent i = new Intent();
				    i.setAction("com.google.code.guidatv.android.SWITCH_LEFT");
				    sendBroadcast(i);
					// Left
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}
	
	class GesturedScrollView extends ScrollView {

		public GesturedScrollView(Context context, AttributeSet attrs,
				int defStyle) {
			super(context, attrs, defStyle);
			// TODO Auto-generated constructor stub
		}

		public GesturedScrollView(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
		}

		public GesturedScrollView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
	}
}
