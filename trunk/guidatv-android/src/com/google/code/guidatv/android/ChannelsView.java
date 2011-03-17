package com.google.code.guidatv.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.google.code.guidatv.android.rest.GuidaTvService;
import com.google.code.guidatv.model.Channel;

public class ChannelsView extends ExpandableListActivity
{
    private static final int SCHEDULE_VIEW=1;

    private GuidaTvService mGuidaTvService;

    private Map<Integer, Map<Integer, String>> position2code;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_list);
        mGuidaTvService = new GuidaTvService();
        fillData();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id)
    {
        boolean retValue = super.onChildClick(parent, v, groupPosition, childPosition, id);
        Intent i = new Intent(this, ChannelScheduleView.class);
        i.putExtra("CHANNEL_CODE", position2code.get(groupPosition).get(childPosition));
        startActivityForResult(i, SCHEDULE_VIEW);
        return retValue;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        fillData();
    }

    private void fillData()
    {
        dialog = ProgressDialog.show(ChannelsView.this, "",
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
                channelPos++;
            }
            SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                    ChannelsView.this, networks, R.layout.network_item,
                    new String[]
                    { "NAME" }, new int[]
                    { R.id.network_text }, channelsList, R.layout.channel_item,
                    new String[]
                    { "NAME" }, new int[]
                    { R.id.channel_text });
            setListAdapter(adapter);
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }
    }
}
