package com.google.code.guidatv.android;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleExpandableListAdapter;

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
	
	public SelectableExpandableListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int expandedGroupLayout,
			int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
			int childLayout, int lastChildLayout, String[] childFrom,
			int[] childTo, Set<ParentChildIndex> selectChildIndexes) {
		super(context, groupData, expandedGroupLayout, collapsedGroupLayout,
				groupFrom, groupTo, childData, childLayout, lastChildLayout,
				childFrom, childTo);
		this.selectedIndexes = selectChildIndexes;
	}

	public SelectableExpandableListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int expandedGroupLayout,
			int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
			int childLayout, String[] childFrom, int[] childTo, Set<ParentChildIndex> selectChildIndexes) {
		super(context, groupData, expandedGroupLayout, collapsedGroupLayout,
				groupFrom, groupTo, childData, childLayout, childFrom, childTo);
		this.selectedIndexes = selectChildIndexes;
	}

	public SelectableExpandableListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int groupLayout,
			String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
			int childLayout, String[] childFrom, int[] childTo, Set<ParentChildIndex> selectChildIndexes) {
		super(context, groupData, groupLayout, groupFrom, groupTo, childData,
				childLayout, childFrom, childTo);
		this.selectedIndexes = selectChildIndexes;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		CheckBox childView = (CheckBox) super.getChildView(groupPosition, childPosition, isLastChild,
						convertView, parent);
		childView.setChecked(selectedIndexes.contains(new ParentChildIndex(groupPosition, childPosition)));
		return childView;
	}
}
