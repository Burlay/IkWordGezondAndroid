package me.rasing.ikwordgezond;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Fragment fragment = new ProfielFragment();

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
			           .replace(R.id.content_frame,  fragment)
			           .commit();
		
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// handle item selection
    	switch (item.getItemId()) {
    	case R.id.action_geschiedenis:
    		Fragment fragment = new GeschiedenisFragment();

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
