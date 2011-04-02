package com.google.code.guidatv.android;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.code.guidatv.android.rest.GuidaTvService;
import com.google.code.guidatv.model.Channel;

public class ScheduleView extends TabActivity {

    private GuidaTvService mGuidaTvService;

    private Map<Integer, Map<Integer, String>> position2code;

    private ProgressDialog dialog;

    private Date currentDate = new Date();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);
        mGuidaTvService = new GuidaTvService();
        fillData();
	}

    private void fillData()
    {
        TextView dateView = (TextView) findViewById(R.id.selectedDate);
        dateView.setText(DateFormat.getDateFormat(this).format(currentDate));
        dialog = ProgressDialog.show(ScheduleView.this, "",
                "Loading. Please wait...", true);
        new LoadChannelsTask().execute();
    }

    private class LoadChannelsTask extends AsyncTask<Void, Void, List<Channel>> {

        @Override
        protected List<Channel> doInBackground(Void... params)
        {
            return mGuidaTvService.getChannels();

        }

        @Override
        protected void onPostExecute(List<Channel> channels)
        {
            List<Map<String, String>> networks = new ArrayList<Map<String,String>>();
            List<List<Map<String, String>>> channelsList = new ArrayList<List<Map<String,String>>>();
            String pastNetwork = null;
            List<Map<String, String>> channelList = null;
            position2code = new HashMap<Integer, Map<Integer,String>>();
            int networkPos = -1;
            int channelPos = -1;
            for (Channel channel : channels) {
                if (!channel.getNetwork().equals(pastNetwork)) {
                    pastNetwork = channel.getNetwork();
                    Map<String, String> network = new HashMap<String, String>();
                    network.put("NAME", pastNetwork);
                    networks.add(network);
                    channelList = new ArrayList<Map<String,String>>();
                    channelsList.add(channelList);
                    networkPos ++;
                    channelPos = 0;
                }
                Map<String, String> channelMap = new HashMap<String, String>();
                channelMap.put("NAME", channel.getName());
                channelList.add(channelMap);
                Map<Integer, String> innerMap = position2code.get(networkPos);
                if (innerMap == null) {
                    innerMap = new HashMap<Integer, String>();
                    position2code.put(networkPos, innerMap);
                }
                innerMap.put(channelPos, channel.getCode());
                TabHost tabHost = getTabHost();  // The activity TabHost
                tabHost.addTab(addTab(channel, tabHost));
                channelPos++;
            }
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }

		private TabHost.TabSpec addTab(Channel channel, TabHost tabHost) {
			TabHost.TabSpec spec;  // Resusable TabSpec for each tab
			Intent intent;  // Reusable Intent for each tab

			// Create an Intent to launch an Activity for the tab (to be reused)
			intent = new Intent().setClass(ScheduleView.this, ChannelScheduleView.class);
			intent.putExtra("CHANNEL_CODE", channel.getCode());
			intent.putExtra("DATE", currentDate);

			// Initialize a TabSpec for each tab and add it to the TabHost
			View tabview = createTabView(tabHost.getContext(), channel.getName());
			spec = tabHost.newTabSpec(channel.getCode()).setIndicator(tabview)
			              .setContent(intent);
			return spec;
		}

		private View createTabView(Context context, String text) {
			View view = LayoutInflater.from(context).inflate(R.layout.channel_tab,
					null);
			TextView tv = (TextView) view.findViewById(R.id.tabText);
			tv.setText(text);
			return view;
		}
    }
}
