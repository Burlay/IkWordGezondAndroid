package me.rasing.mijngewicht;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
	
	@Override
	public Cursor swapCursor(Cursor c) {
		super.swapCursor(c);
	    this.c = c;
		return this.c;
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
		
		SimpleDateFormat format = 
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.getDefault());
		
		final String datum = c.getString(c.getColumnIndexOrThrow(Metingen.COLUMN_NAME_DATUM));
		
		try {
			final Date d = format.parse(datum);
			
			final NumberFormat numberFormatter = NumberFormat.getInstance();
			numberFormatter.setMinimumFractionDigits(2);
			
			final String number = numberFormatter.format(c.getFloat(c.getColumnIndexOrThrow(Metingen.COLUMN_NAME_GEWICHT)));
			
			holder.gewicht.setText(number + " kg");
			holder.datum.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(d));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (selected_positions.contains(position)) {
			inView.setBackgroundColor(Color.rgb(138, 213, 240));
		} else {
			inView.setBackgroundColor(Color.rgb(255, 255, 255));
		}
		
		return inView;
	}
	
	public ArrayList<Integer> getSelectedPositions() {
		return this.selected_positions;
	}
	
	private class MetingenHolder {

		public TextView datum;
		public TextView gewicht;
	}

	public void clearSelection() {
		this.selected_positions.clear();
	}
	
	public int count() {
		return this.selected_positions.size();
	}
}
