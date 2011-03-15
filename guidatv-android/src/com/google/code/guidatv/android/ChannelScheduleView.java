package com.google.code.guidatv.android;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.SimpleAdapter;

import com.google.code.guidatv.android.rest.GuidaTvService;
import com.google.code.guidatv.model.Schedule;
import com.google.code.guidatv.model.Transmission;

public class ChannelScheduleView extends ListActivity
{

    private Date currentDate = new Date();

    private GuidaTvService mGuidaTvService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_channel_schedule);
        mGuidaTvService = new GuidaTvService();
        fillData();
    }

    private void fillData()
    {
        Bundle extras = getIntent().getExtras();
        new LoadChannelsTask().execute(extras.getString("CHANNEL_CODE"));

    }

    private class LoadChannelsTask extends AsyncTask<String, Void, Schedule> {

        @Override
        protected Schedule doInBackground(String... params)
        {
            return mGuidaTvService.getSchedule(params[0], currentDate);
        }

        @Override
        protected void onPostExecute(Schedule schedule)
        {
            List<Map<String, String>> channelList = new ArrayList<Map<String,String>>();
            for (Transmission transmission : schedule.getTransmissions()) {
                Map<String, String> channelMap = new HashMap<String, String>();
                channelMap.put("NAME", transmission.getName());
                channelMap.put("TIME", DateFormat.format("kk:mm", transmission.getStart()).toString());
                channelList.add(channelMap);
            }
            SimpleAdapter adapter = new SimpleAdapter(
                    ChannelScheduleView.this, channelList, R.layout.transmission_item,
                    new String[]
                    { "TIME", "NAME" }, new int[]
                    { R.id.transmissionTime, R.id.transmissionName });
            setListAdapter(adapter);
        }
    }
}
