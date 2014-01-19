package me.rasing.mijngewicht;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class MainActivity extends FragmentActivity implements OnNavigationListener {
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setTitle("Profiel");

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
		new ArrayAdapter<String>(getActionBarThemedContextCompat(),
				android.R.layout.simple_list_item_1,
				android.R.id.text1, new String[] {
						getString(R.string.profiel),
						getString(R.string.geschiedenis)}),
				this);
		
		// Load the correct fragment on orientation change.
		if(savedInstanceState != null) {
            int index = savedInstanceState.getInt("index");
            actionBar.setSelectedNavigationItem(index);
        }
	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.profiel_menu, menu);
    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// handle item selection
    	switch (item.getItemId()) {
    	case R.id.actie_nieuw:
			Intent intent = new Intent(this, MeetingInvoerenActivity.class);
			startActivity(intent);
			
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }

    @Override
    public boolean onNavigationItemSelected(int pos, long id) {
    	String tag = "";

    	FragmentManager fragmentManager= getSupportFragmentManager();
    	Fragment fragment = fragmentManager.findFragmentById(R.id.container);

    	switch (pos) {
    	case 0:
    		if(!(fragment instanceof DashboardFragment) || !(fragment instanceof BlankstateFragment)) {
    			DbHelper mDbHelper = new DbHelper(getBaseContext());
    			SQLiteDatabase db = mDbHelper.getWritableDatabase();

    			// Define a projection that specifies which columns from the database
    			// you will actually use after this query.
    			String[] projection = {
    					Metingen._ID,
    					Metingen.COLUMN_NAME_GEWICHT,
    					Metingen.COLUMN_NAME_DATUM
    			};

    			// Get the 2 most recent weights.
    			Cursor cursor = db.query(
    					Metingen.TABLE_NAME,  // The table to query
    					projection,           // The columns to return
    					null,                 // The columns for the WHERE clause
    					null,                 // The values for the WHERE clause
    					null,                 // don't group the rows
    					null,                 // don't filter by row groups
    					null,
    					"1"
    					);

    			if ( cursor.getCount() <= 0 ) {
    				fragment = new BlankstateFragment();
    				tag = "BlankState";
    			} else {
    				fragment = new DashboardFragment();
    				tag = "Profiel";
    			}

    			db.close();

    			fragmentManager
    			.beginTransaction()
    			.replace(R.id.container, fragment, tag)
    			.commit();
    		}
    		break;
    	case 1:
    		if(!(fragment instanceof GeschiedenisFragment)) {
    			Log.d("!!!DATA!!!", "Maak niew geschiedenisfragment");
    			fragment = new GeschiedenisFragment();
    			tag = "Geschiedenis";

    			fragmentManager
    			.beginTransaction()
    			.replace(R.id.container, fragment, tag)
    			.commit();
    		}
    		break;
    	}
    	return false;
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// Save the selected item in the dropdown navigation.
		int i = getActionBar().getSelectedNavigationIndex();
	    outState.putInt("index", i);
	}
}
