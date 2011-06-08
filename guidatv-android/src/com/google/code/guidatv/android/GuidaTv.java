package com.google.code.guidatv.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GuidaTv extends Activity {

    private static final int ACTIVITY_SCHEDULE = 1;

    private static final int ACTIVITY_CHANNELS = 2;
    
    private static final int ACTIVITY_SORT_CHANNELS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button channelsButton = (Button) findViewById(R.id.channelsButton);
        channelsButton.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(GuidaTv.this, ChannelSelectView.class);
                startActivityForResult(i, ACTIVITY_CHANNELS);
            }
        });
        Button sortChannelsButton = (Button) findViewById(R.id.sortChannelsButton);
        sortChannelsButton.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(GuidaTv.this, SortChannelsView.class);
                startActivityForResult(i, ACTIVITY_SORT_CHANNELS);
            }
        });
        Button scheduleButton = (Button) findViewById(R.id.scheduleButton);
        scheduleButton.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(GuidaTv.this, ScheduleView.class);
                startActivityForResult(i, ACTIVITY_SCHEDULE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
}