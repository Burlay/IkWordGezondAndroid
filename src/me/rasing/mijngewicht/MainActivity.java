package me.rasing.mijngewicht;

import me.rasing.mijngewicht.fragments.BlankstateFragment;
import me.rasing.mijngewicht.fragments.HistoryFragment;
import me.rasing.mijngewicht.providers.GewichtProvider;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity 
		implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String STATE_CURRENT_TITLE = null;

	/**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getCharSequence(STATE_CURRENT_TITLE);
        } else {
            mTitle = getTitle();
        }

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.profiel_menu, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
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
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = getFragmentManager();
		mTitle = getResources().getStringArray(R.array.drawer_items)[position];
		
    	switch (position) {
    	case 0:
    		Uri uri = GewichtProvider.METINGEN_URI;
    		String[] projection = null;
    		Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
    		if (cursor.getCount() == 0) {
        		fragmentManager.beginTransaction()
        		.replace(R.id.container, new BlankstateFragment())
        		.commit();
    		} else {
        		fragmentManager.beginTransaction()
        		.replace(R.id.container, new DashboardFragment())
        		.commit();
    		}
    		cursor.close();
    		break;
    	case 1:
    		fragmentManager.beginTransaction()
    		.replace(R.id.container, new HistoryFragment())
    		.commit();
    		break;
    	//case 2:
    	//	fragmentManager.beginTransaction()
    	//	.replace(R.id.container, new HistoryFragment())
    	//	.commit();
    	//	break;
    	case 2:
    		Intent intent = new Intent(this, PreferencesActivity.class);
    		this.startActivity(intent);
    	}
	}
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(STATE_CURRENT_TITLE, mTitle);
    }
}
