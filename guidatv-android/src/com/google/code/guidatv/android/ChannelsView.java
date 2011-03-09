package com.google.code.guidatv.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ExpandableListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleExpandableListAdapter;

import com.google.code.guidatv.android.rest.GuidaTvService;
import com.google.code.guidatv.model.Channel;

public class ChannelsView extends ExpandableListActivity
{

    private GuidaTvService mGuidaTvService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_list);
        mGuidaTvService = new GuidaTvService();
        fillData();
    }

    private void fillData()
    {
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
            String[] channelArray = new String[channels.size()];
            List<Map<String, String>> networks = new ArrayList<Map<String,String>>();
            List<List<Map<String, String>>> channelsList = new ArrayList<List<Map<String,String>>>();
            String pastNetwork = null;
            List<Map<String, String>> channelList = null;
            for (Channel channel : channels) {
                if (!channel.getNetwork().equals(pastNetwork)) {
                    pastNetwork = channel.getNetwork();
                    Map<String, String> network = new HashMap<String, String>();
                    network.put("NAME", pastNetwork);
                    networks.add(network);
                    channelList = new ArrayList<Map<String,String>>();
                    channelsList.add(channelList);
                }
                Map<String, String> channelMap = new HashMap<String, String>();
                channelMap.put("NAME", channel.getName());
                channelList.add(channelMap);
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
        }
    }
}
