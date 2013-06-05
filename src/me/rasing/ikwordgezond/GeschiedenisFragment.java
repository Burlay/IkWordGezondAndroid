package me.rasing.ikwordgezond;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class GeschiedenisFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_geschiedenig, container, false);

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
		SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(
				getActivity(), R.layout.geschiedenis_list_item,
				cursor,
				columns,
				to,
				0);

		final ListView listView = (ListView) rootView.findViewById(R.id.geschiedenis);
		listView.setAdapter(dataAdapter);

		db.close();
		
		return rootView;
	}
}
