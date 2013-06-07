package me.rasing.ikwordgezond;

import android.app.Fragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfielFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	setHasOptionsMenu(true);
    	
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
    	String weight = cursor.getString(
    			cursor.getColumnIndexOrThrow(Metingen.COLUMN_NAME_GEWICHT)
    			);
    	String datum = cursor.getString(
    			cursor.getColumnIndexOrThrow(Metingen.COLUMN_NAME_DATUM)
    			);
    	while(cursor.moveToNext()) {
    		Log.d("DB", cursor.getString(
    				cursor.getColumnIndexOrThrow(Metingen.COLUMN_NAME_GEWICHT)
    				));
    	}
    	
    	db.close();
    	
    	View rootView = inflater.inflate(R.layout.fragment_profiel, container, false);

        TextView txtWeight = (TextView) rootView.findViewById(R.id.gewicht);
    	txtWeight.setText(weight + " kg");

        TextView txtDatum = (TextView) rootView.findViewById(R.id.datum);
    	txtDatum.setText(datum);
        
        getActivity().setTitle("Profiel");
        return rootView;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.profiel_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// handle item selection
    	switch (item.getItemId()) {
    	case R.id.actie_nieuw:
    		Fragment fragment = new NieuwemeetingFragment();

    		// Insert the fragment by replacing any existing fragment
    		FragmentManager fragmentManager = getFragmentManager();
    		fragmentManager.beginTransaction()
    			           .replace(R.id.content_frame,  fragment)
    			           .addToBackStack(null)
    			           .commit();
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
}
