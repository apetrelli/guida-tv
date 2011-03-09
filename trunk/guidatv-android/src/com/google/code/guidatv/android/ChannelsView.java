package com.google.code.guidatv.android;

import java.util.List;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.google.code.guidatv.android.rest.GuidaTvService;
import com.google.code.guidatv.model.Channel;

public class ChannelsView extends ListActivity
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
            int i = 0;
            for (Channel channel : channels) {
                channelArray[i] = channel.getName();
                i++;
            }
            ListAdapter adapter = new ArrayAdapter<String>(ChannelsView.this,
                    R.layout.channel_item, R.id.channel_text, channelArray);
            setListAdapter(adapter);
        }
    }
}
