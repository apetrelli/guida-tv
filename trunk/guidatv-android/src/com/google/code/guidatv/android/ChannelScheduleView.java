package com.google.code.guidatv.android;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
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
    private GuidaTvService mGuidaTvService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_channel_schedule);
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
}
