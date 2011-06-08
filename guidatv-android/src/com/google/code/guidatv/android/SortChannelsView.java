package com.google.code.guidatv.android;

import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.commonsware.cwac.tlv.TouchListView;
import com.google.code.guidatv.android.db.ChannelDbAdapter;

public class SortChannelsView extends ListActivity {
	private IconicAdapter adapter = null;
	private ArrayList<OrderedChannel> array = new ArrayList<OrderedChannel>();

	private ChannelDbAdapter mDb;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.channels_to_sort);
		mDb = new ChannelDbAdapter(this);
		mDb.open();

		adapter = new IconicAdapter();
		setListAdapter(adapter);
		fillData();

		TouchListView tlv = (TouchListView) getListView();

		tlv.setDropListener(onDrop);
		tlv.setRemoveListener(onRemove);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDb.close();
	}

	private void fillData() {
		Cursor cursor = mDb.fetchAllChannels();
		startManagingCursor(cursor);
		boolean goOn = cursor.moveToFirst();
		while (goOn) {
			array.add(new OrderedChannel(cursor.getString(cursor
					.getColumnIndexOrThrow(ChannelDbAdapter.KEY_CODE)), cursor
					.getString(cursor
							.getColumnIndexOrThrow(ChannelDbAdapter.KEY_NAME)),
					cursor.getInt(cursor
							.getColumnIndexOrThrow(ChannelDbAdapter.KEY_ORDER))));
			goOn = cursor.moveToNext();
		}
	}

	private TouchListView.DropListener onDrop = new TouchListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			OrderedChannel item = adapter.getItem(from);

			adapter.remove(item);
			adapter.insert(item, to);
			for (int i = 0; i < adapter.getCount(); i++) {
				item = adapter.getItem(i);
				mDb.updateChannel(item.getCode(), i+1);
			}
		}
	};

	private TouchListView.RemoveListener onRemove = new TouchListView.RemoveListener() {
		@Override
		public void remove(int which) {
			adapter.remove(adapter.getItem(which));
		}
	};

	class IconicAdapter extends ArrayAdapter<OrderedChannel> {
		IconicAdapter() {
			super(SortChannelsView.this, R.layout.row2, array);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();

				row = inflater.inflate(R.layout.row2, parent, false);
			}

			TextView label = (TextView) row.findViewById(R.id.label);

			label.setText(array.get(position).getName());

			return (row);
		}
	}
}
