package me.rasing.mijngewicht;

import java.util.ArrayList;
import java.util.Iterator;

import me.rasing.mijngewicht.R;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class GeschiedenisFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_geschiedenig, container, false);
		final MetingenAdapter dataAdapter;

		DbHelper mDbHelper = new DbHelper(getActivity().getBaseContext());
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				Metingen._ID,
				Metingen.COLUMN_NAME_GEWICHT,
				Metingen.COLUMN_NAME_DATUM
		};

		// How you want the results sorted in the resulting Cursor
		//String sortOrder =
		//    FeedReaderContract.FeedEntry.COLUMN_NAME_UPDATED + " DESC";

		Cursor cursor = db.query(
				Metingen.TABLE_NAME,  // The table to query
				projection,           // The columns to return
				null,                 // The columns for the WHERE clause
				null,                 // The values for the WHERE clause
				null,                 // don't group the rows
				null,                 // don't filter by row groups
				Metingen.COLUMN_NAME_DATUM + " DESC"
				);

		// The desired columns to be bound
		String[] columns = new String[] {
				Metingen.COLUMN_NAME_GEWICHT,
				Metingen.COLUMN_NAME_DATUM
		};

		int[] to = new int[] {
				R.id.gewicht,
				R.id.datum,
		};

		// create the adapter using the cursor pointing to the desired data
		//as well as the layout information
		dataAdapter = new MetingenAdapter(
				getActivity(), R.layout.geschiedenis_list_item,
				cursor,
				columns,
				to,
				0);

		final ListView listView = (ListView) rootView.findViewById(R.id.geschiedenis);
		listView.setAdapter(dataAdapter);

		db.close();
		
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

		    @Override
		    public void onItemCheckedStateChanged(ActionMode mode, int position,
		                                          long id, boolean checked) {
		    	dataAdapter.toggleSelection(position);
		    	dataAdapter.notifyDataSetChanged();
		    }

		    @Override
		    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		        // Respond to clicks on the actions in the CAB
		        switch (item.getItemId()) {
		            case R.id.actie_delete:
		        		DbHelper mDbHelper = new DbHelper(getActivity().getBaseContext());
		            	SQLiteDatabase db = mDbHelper.getWritableDatabase();

		            	// Define 'where' part of query.
		            	//String selection = Metingen._ID + "=";
		            	// Specify arguments in placeholder order.
		            	
		            	ArrayList<Integer> selected_positions = dataAdapter.getSelectedPositions();
		            	
		            	//String[] selectionArgs = new String[selected_positions.size()];
		            	ArrayList<String> i = new ArrayList<String>();
		            	
		            	Iterator<Integer> iterator = selected_positions.iterator();
		            	while (iterator.hasNext()) {
		            		Integer pos = iterator.next();
			            	Cursor c = (Cursor) listView.getItemAtPosition(pos);
			            	int id = c.getInt(c.getColumnIndex("_id"));
			            	i.add(pos.toString());
			            	// Issue SQL statement.
			            	db.delete(Metingen.TABLE_NAME, Metingen._ID + "="+Integer.toString(id), null);
		            	}

		            	// Issue SQL statement.
		            	//db.delete(Metingen.TABLE_NAME, selection, i.toArray(selectionArgs));
		            	
		                //deleteSelectedItems();
		                mode.finish(); // Action picked, so close the CAB
		                
		                dataAdapter.notifyDataSetChanged();
		                return true;
		            default:
		                return false;
		        }
		    }

		    @Override
		    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		        // Inflate the menu for the CAB
		        MenuInflater inflater = mode.getMenuInflater();
		        inflater.inflate(R.menu.cab_geschiedenis, menu);
		        return true;
		    }

		    @Override
		    public void onDestroyActionMode(ActionMode mode) {
		        // Here you can make any necessary updates to the activity when
		        // the CAB is removed. By default, selected items are deselected/unchecked.
		    }

		    @Override
		    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		        // Here you can perform updates to the CAB due to
		        // an invalidate() request
		        return false;
		    }
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Cursor c = (Cursor) parent.getItemAtPosition(pos);
				
				Intent intent = new Intent(getActivity(), MeetingInvoerenActivity.class);
				intent.putExtra(MeetingInvoerenActivity.ID, c.getInt(c.getColumnIndex(Metingen._ID)));
				startActivity(intent);
			}

		});

		return rootView;
	}
}
