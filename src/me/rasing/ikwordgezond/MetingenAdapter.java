package me.rasing.ikwordgezond;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MetingenAdapter extends SimpleCursorAdapter {

	private Cursor c;
	private Context context;
	private int layout;
	private ArrayList<Integer> selected_positions = new ArrayList<Integer>();

	public MetingenAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	    this.c = c;
	    this.context = context;
	    this.layout = layout;
	}

	public void toggleSelection(int position) {
		if (selected_positions.contains(position)) {
			this.selected_positions.remove(Integer.valueOf(position));
		} else {
			this.selected_positions.add(Integer.valueOf(position));
		}
	}
	
	public View getView(final int position, View inView, ViewGroup parent) {
		MetingenHolder holder = null;
		
		if (inView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inView = inflater.inflate(layout, null);
			
			holder = new MetingenHolder();
			
			holder.gewicht = (TextView) inView.findViewById(R.id.gewicht);
			holder.datum = (TextView) inView.findViewById(R.id.datum);
			
			inView.setTag(holder);
		} else {
			holder = (MetingenHolder) inView.getTag();
		}
		
		c.moveToPosition(position);
		
		holder.gewicht.setText(c.getString(c.getColumnIndexOrThrow(Metingen.COLUMN_NAME_GEWICHT)));
		holder.datum.setText(c.getString(c.getColumnIndexOrThrow(Metingen.COLUMN_NAME_DATUM)));
		
		if (selected_positions.contains(position)) {
			inView.setBackgroundColor(Color.rgb(238, 233, 233));
		} else {
			inView.setBackgroundColor(Color.rgb(255, 255, 255));
		}
		
		//final CheckBox cBox = (CheckBox) inView.findViewById(R.id.bcheck); // your
		// CheckBox
		//cBox.setOnClickListener(new OnClickListener() {

//			public void onClick(View v) {
//
//				CheckBox cb = (CheckBox) v.findViewById(R.id.your_checkbox_id);
//
//				if (cb.isChecked()) {
//					itemChecked.set(pos, true);
//					// do some operations here
//				} else if (!cb.isChecked()) {
//					itemChecked.set(pos, false);
//					// do some operations here
//				}
//			}
//		});
		//cBox.setChecked(itemChecked.get(pos)); // this will Check or Uncheck the
		// CheckBox in ListView
		// according to their original
		// position and CheckBox never
		// loss his State when you
		// Scroll the List Items.
		return inView;
	}
	
	public ArrayList<Integer> getSelectedPositions() {
		return this.selected_positions;
	}
	
	private class MetingenHolder {

		public TextView datum;
		public TextView gewicht;
	}
}
