package com.google.code.guidatv.android;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleExpandableListAdapter;

import com.google.code.guidatv.android.db.ChannelDbAdapter;

public class SelectableExpandableListAdapter extends
		SimpleExpandableListAdapter {

	public static class ParentChildIndex {
		private int parentIndex;

		private int childIndex;

		public ParentChildIndex(int parentIndex, int childIndex) {
			this.parentIndex = parentIndex;
			this.childIndex = childIndex;
		}

		public int getParentIndex() {
			return parentIndex;
		}

		public int getChildIndex() {
			return childIndex;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + childIndex;
			result = prime * result + parentIndex;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ParentChildIndex other = (ParentChildIndex) obj;
			if (childIndex != other.childIndex)
				return false;
			if (parentIndex != other.parentIndex)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "[" + parentIndex
					+ ", " + childIndex + "]";
		}

		
	}

	private Set<ParentChildIndex> selectedIndexes;

    private Map<Integer, Map<Integer, String>> position2code;
    
    private ChannelDbAdapter mDb;
	
	public SelectableExpandableListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int expandedGroupLayout,
			int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
			int childLayout, int lastChildLayout, String[] childFrom,
			int[] childTo, Set<ParentChildIndex> selectedChildIndexes, 
		    Map<Integer, Map<Integer, String>> position2code, ChannelDbAdapter mDb) {
		super(context, groupData, expandedGroupLayout, collapsedGroupLayout,
				groupFrom, groupTo, childData, childLayout, lastChildLayout,
				childFrom, childTo);
		this.selectedIndexes = selectedChildIndexes;
		this.position2code = position2code;
		this.mDb = mDb;
	}

	public SelectableExpandableListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int expandedGroupLayout,
			int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
			int childLayout, String[] childFrom, int[] childTo, Set<ParentChildIndex> selectedChildIndexes, Map<Integer, Map<Integer, String>> position2code, ChannelDbAdapter mDb) {
		super(context, groupData, expandedGroupLayout, collapsedGroupLayout,
				groupFrom, groupTo, childData, childLayout, childFrom, childTo);
		this.selectedIndexes = selectedChildIndexes;
		this.position2code = position2code;
		this.mDb = mDb;
	}

	public SelectableExpandableListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int groupLayout,
			String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
			int childLayout, String[] childFrom, int[] childTo, Set<ParentChildIndex> selectedChildIndexes, Map<Integer, Map<Integer, String>> position2code, ChannelDbAdapter mDb) {
		super(context, groupData, groupLayout, groupFrom, groupTo, childData,
				childLayout, childFrom, childTo);
		this.selectedIndexes = selectedChildIndexes;
		this.position2code = position2code;
		this.mDb = mDb;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		CheckBox childView = (CheckBox) super.getChildView(groupPosition, childPosition, isLastChild,
						convertView, parent);
		childView.setChecked(selectedIndexes.contains(new ParentChildIndex(groupPosition, childPosition)));
		Map<Integer, String> childPos2code = position2code.get(groupPosition);
		if (childPos2code != null) {
			final String code = childPos2code.get(childPosition);
			final String name = childView.getText().toString();
			if (code != null) {
				childView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						CheckBox checkBox = (CheckBox) v;
						boolean isChecked = checkBox.isChecked();
						ParentChildIndex parentChildIndex = new ParentChildIndex(groupPosition, childPosition);
						if (isChecked) {
							mDb.addChannel(code, name);
							selectedIndexes.add(parentChildIndex);
						} else {
							mDb.deleteChannel(code);
							selectedIndexes.remove(parentChildIndex);
						}
						checkBox.setChecked(isChecked);
 					}
				}
				);
			}
		}
		return childView;
	}
}
