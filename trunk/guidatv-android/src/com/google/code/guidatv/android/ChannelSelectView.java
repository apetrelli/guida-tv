package com.google.code.guidatv.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.restlet.resource.ResourceException;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;

import com.google.code.guidatv.android.SelectableExpandableListAdapter.ParentChildIndex;
import com.google.code.guidatv.android.db.ChannelDbAdapter;
import com.google.code.guidatv.android.rest.GuidaTvService;
import com.google.code.guidatv.model.Channel;

public class ChannelSelectView extends ExpandableListActivity
{
    private static final int SCHEDULE_VIEW=1;

    private GuidaTvService mGuidaTvService;

    private Map<Integer, Map<Integer, String>> position2code;

    private ProgressDialog dialog;
    
    private ChannelDbAdapter mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_list);
        mDb = new ChannelDbAdapter(this);
        mDb.open();
        mGuidaTvService = new GuidaTvService();
        fillData();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	mDb.close();
    }
    
    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id)
    {
        boolean retValue = super.onChildClick(parent, v, groupPosition, childPosition, id);
        CheckBox childView = (CheckBox) v;
		Map<Integer, String> childPos2code = position2code.get(groupPosition);
		if (childPos2code != null) {
			final String code = childPos2code.get(childPosition);
			final String name = childView.getText().toString();
			if (code != null) {
				boolean isChecked = childView.isChecked();
				if (isChecked) {
					mDb.addChannel(code, name);
				} else {
					mDb.deleteChannel(code);
				}
			}
		}
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
        dialog = ProgressDialog.show(ChannelSelectView.this, "",
                "Loading. Please wait...", true);
        new LoadChannelsTask().execute();
    }

    private class LoadChannelsTask extends AsyncTask<Void, Void, List<Channel>> {

        @Override
        protected List<Channel> doInBackground(Void... params)
        {
			try {
				return mGuidaTvService.getChannels();
			} catch (IOException e) {
				Log.d("ScheduleView", "Cannot retrieve channels", e);
				return null;
			} catch (ResourceException e) {
				Log.d("ScheduleView", "Cannot retrieve channels", e);
				return null;
			}
        }

        @Override
        protected void onPostExecute(List<Channel> channels)
        {
        	Set<String> codes = new HashSet<String>();
        	Cursor cursor = mDb.fetchAllChannels();
        	startManagingCursor(cursor);
        	boolean goOn = cursor.moveToFirst();
        	while (goOn) {
        		codes.add(cursor.getString(cursor.getColumnIndexOrThrow(ChannelDbAdapter.KEY_CODE)));
        		goOn = cursor.moveToNext();
        	}
        	Set<SelectableExpandableListAdapter.ParentChildIndex> positions = new HashSet<SelectableExpandableListAdapter.ParentChildIndex>();
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
                if (codes.contains(channel.getCode())) {
                	positions.add(new ParentChildIndex(networkPos, channelPos));
                }
                channelPos++;
            }
            SelectableExpandableListAdapter adapter = new SelectableExpandableListAdapter(
                    ChannelSelectView.this, networks, R.layout.network_item,
                    new String[]
                    { "NAME" }, new int[]
                    { R.id.network_text }, channelsList, R.layout.channel_item,
                    new String[]
                    { "NAME" }, new int[]
                    { R.id.channel_text }, positions, position2code, mDb);
            setListAdapter(adapter);
            getExpandableListView().setOnChildClickListener(ChannelSelectView.this);
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }
    }
}