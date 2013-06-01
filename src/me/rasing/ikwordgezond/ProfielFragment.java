package me.rasing.ikwordgezond;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfielFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
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
    	    Metingen.COLUMN_NAME_DATUM + " DESC",
    	    "1"
    	    );
    	
    	cursor.moveToFirst();
    	String itemId = cursor.getString(
    	    cursor.getColumnIndexOrThrow(Metingen.COLUMN_NAME_GEWICHT)
    	);
    	while(cursor.moveToNext()) {
    		Log.d("DB", cursor.getString(
    				cursor.getColumnIndexOrThrow(Metingen.COLUMN_NAME_GEWICHT)
    				));
    	}
    	
    	db.close();
    	
    	View rootView = inflater.inflate(R.layout.fragment_profiel, container, false);

        TextView editText = (TextView) rootView.findViewById(R.id.gewicht2);
    	editText.setText(itemId);
        
        getActivity().setTitle("Profiel");
        return rootView;
    }
}
