/*
 *  Guida TV: una guida TV per canali italiani.
 *  Copyright (C) 2011 Antonio Petrelli
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.google.code.guidatv.android;

import com.google.code.guidatv.android.db.ChannelDbAdapter;

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

	private ChannelDbAdapter mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mDb = new ChannelDbAdapter(this);
        mDb.open();
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
        Button cleanupButton = (Button) findViewById(R.id.cleanup);
        cleanupButton.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
            	mDb.deleteAll();
            }
        });
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	mDb.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
}