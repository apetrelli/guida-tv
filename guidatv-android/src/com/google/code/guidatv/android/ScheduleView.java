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
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.code.guidatv.android.rest.GuidaTvService;
import com.google.code.guidatv.android.util.MathUtils;
import com.google.code.guidatv.model.Channel;

public class ScheduleView extends TabActivity {

	private GuidaTvService mGuidaTvService;

	int channelCount = 0;

	private ProgressDialog dialog;

	private Date currentDate = new Date();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);
		mGuidaTvService = new GuidaTvService();
		fillData();
	}

	private void fillData() {
		TextView dateView = (TextView) findViewById(R.id.selectedDate);
		dateView.setText(DateFormat.getDateFormat(this).format(currentDate));
		dialog = ProgressDialog.show(ScheduleView.this, "",
				"Loading. Please wait...", true);
		new LoadChannelsTask().execute();
	}

	private class LoadChannelsTask extends AsyncTask<Void, Void, List<Channel>> {

		@Override
		protected List<Channel> doInBackground(Void... params) {
			return mGuidaTvService.getChannels();

		}

		@Override
		protected void onPostExecute(List<Channel> channels) {
			List<Map<String, String>> networks = new ArrayList<Map<String, String>>();
			String pastNetwork = null;
			for (Channel channel : channels) {
				if (!channel.getNetwork().equals(pastNetwork)) {
					pastNetwork = channel.getNetwork();
					Map<String, String> network = new HashMap<String, String>();
					network.put("NAME", pastNetwork);
					networks.add(network);
				}
				Map<String, String> channelMap = new HashMap<String, String>();
				channelMap.put("NAME", channel.getName());
				TabHost tabHost = getTabHost(); // The activity TabHost
				tabHost.addTab(addTab(channel, tabHost));
				channelCount++;
			}
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
		}

		private TabHost.TabSpec addTab(Channel channel, TabHost tabHost) {
			TabHost.TabSpec spec; // Resusable TabSpec for each tab
			Intent intent; // Reusable Intent for each tab

			// Create an Intent to launch an Activity for the tab (to be reused)
			intent = new Intent().setClass(ScheduleView.this,
					ChannelScheduleView.class);
			intent.putExtra("CHANNEL_CODE", channel.getCode());
			intent.putExtra("DATE", currentDate);

			// Initialize a TabSpec for each tab and add it to the TabHost
			View tabview = createTabView(tabHost.getContext(),
					channel.getName());
			spec = tabHost.newTabSpec(channel.getCode()).setIndicator(tabview)
					.setContent(intent);
			return spec;
		}

		private View createTabView(Context context, String text) {
			View view = LayoutInflater.from(context).inflate(
					R.layout.channel_tab, null);
			TextView tv = (TextView) view.findViewById(R.id.tabText);
			tv.setText(text);
			return view;
		}
	}
	public static class FlingableTabHost extends TabHost {
        GestureDetector mGestureDetector;

        Animation mRightInAnimation;
        Animation mRightOutAnimation;
        Animation mLeftInAnimation;
        Animation mLeftOutAnimation;

        public FlingableTabHost(Context context, AttributeSet attrs) {
            super(context, attrs);

            mRightInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
            mRightOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
            mLeftInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
            mLeftOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);

            final int minScaledFlingVelocity = ViewConfiguration.get(context)
                    .getScaledMinimumFlingVelocity() * 10; // 10 = fudge by experimentation

            mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                        float velocityY) {
                    if (Math.abs(velocityX) > minScaledFlingVelocity &&
                        Math.abs(velocityY) < minScaledFlingVelocity) {

                        final boolean right = velocityX < 0;
                        switchTab(right);
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (mGestureDetector.onTouchEvent(ev)) {
                return true;
            }
            return super.onInterceptTouchEvent(ev);
        }

		public void switchTab(final boolean right) {
			int tabCount = getTabWidget().getTabCount();
			int currentTab = getCurrentTab();
			final int newTab = MathUtils.constrain(currentTab + (right ? 1 : -1),
			        0, tabCount - 1);
			if (newTab != currentTab) {
			    // Somewhat hacky, depends on current implementation of TabHost:
			    // http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;
			    // f=core/java/android/widget/TabHost.java
			    View currentView = getCurrentView();
			    setCurrentTab(newTab);
			    View newView = getCurrentView();

			    newView.startAnimation(right ? mRightInAnimation : mLeftInAnimation);
			    currentView.startAnimation(
			            right ? mRightOutAnimation : mLeftOutAnimation);
			}
		}
    }
}
