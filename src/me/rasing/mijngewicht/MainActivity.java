package me.rasing.mijngewicht;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import me.rasing.mijngewicht.authentication.AuthenticatorActivity;
import me.rasing.mijngewicht.fragments.BlankstateFragment;
import me.rasing.mijngewicht.fragments.DashboardFragment;
import me.rasing.mijngewicht.fragments.HistoryFragment;
import me.rasing.mijngewicht.models.MeasurementsModel;
import me.rasing.mijngewicht.providers.GewichtProvider;
import me.rasing.mijngewicht.sync.MetingenObserver;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private static final String PROPERTY_REG_ID = "registration_id";

	private static final String PROPERTY_APP_VERSION = "app_version";

	protected static final String SENDER_ID = "393625117753";
    
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private CharSequence mTitle;
	private String[] mDrawerItems;
	private CharSequence mDrawerTitle;
	private ActionBarDrawerToggle mDrawerToggle;// Constants

	private GoogleCloudMessaging gcm;

	private String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

    	this.registerMeasurementsObserver();
        
		setContentView(R.layout.activity_main);

		this.addNavigationDrawer();
		
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        if ( checkPlayServices() ) {
        	gcm = GoogleCloudMessaging.getInstance(this);
        	regid = getRegistrationId(getApplicationContext());
        	
        	Log.i("RegistrationID", "RegistrationId="+regid);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i("TAG", "No valid Google Play Services APK found.");
        }
	}

	private void addNavigationDrawer() {		
		mDrawerItems = getResources().getStringArray(R.array.drawer_items);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(getActionBarThemedContextCompat(),
        		android.R.layout.simple_list_item_1,
        		android.R.id.text1, mDrawerItems));
        
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
        		this,					/* host Activity */
        		mDrawerLayout,			/* DrawerLayout object */
                R.drawable.ic_drawer,	/* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,	/* "open drawer" description */
                R.string.drawer_close	/* "close drawer" description */
                ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void registerMeasurementsObserver() {
    	ContentResolver mContentResolver = this.getContentResolver();

		AccountManager accountManager = AccountManager.get(this.getApplicationContext());
		Account[] accounts = accountManager.getAccountsByType(AuthenticatorActivity.ARG_ACCOUNT_TYPE);
		
		for ( Account account : accounts ) {
	        // Turn on automatic syncing for the default account and authority
	        ContentResolver.setSyncAutomatically(account, GewichtProvider.AUTHORITY, true);
		}
        
    	Uri mUri = new Uri.Builder()
    	                  .scheme("content")
    	                  .authority(GewichtProvider.AUTHORITY)
    	                  .path(Metingen.TABLE_NAME)
    	                  .build();
    	MetingenObserver observer = new MetingenObserver(null, this.getApplicationContext());
    	mContentResolver.registerContentObserver(mUri, true, observer);
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			if ( this.isBlankState() ) {
					this.show(BlankstateFragment.class);
			} else {
					this.show(DashboardFragment.class);
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private void show(Class<?> cls) throws InstantiationException, IllegalAccessException {
		Fragment fragment = (Fragment) cls.newInstance();

		FragmentManager fragmentManager= getFragmentManager();
		fragmentManager
		.beginTransaction()
		.replace(R.id.container, fragment, cls.getSimpleName())
		.commit();
		setTitle(mDrawerItems[0]);
	}

	private boolean isBlankState() {
		MeasurementsModel measurements = new MeasurementsModel(this.getApplicationContext());
		return measurements.isEmpty();
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				
				try {
					if ( gcm == null ) {
						gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;
					Log.i("Registration ID", regid);
					
					storeRegistrationId(getApplicationContext(), regid);
				} catch ( IOException e ) {			
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String exceptionDetails = sw.toString();
					
					msg = "Error :" +exceptionDetails;
				}
				// TODO Auto-generated method stub
				return msg;
			}

			@Override
			protected void onPostExecute(String result) {
				Log.i("Result", "Registration ID=" + result);
			}
		}.execute(null, null, null);
	}

	protected void storeRegistrationId(Context baseContext, String regid2) {
		final SharedPreferences prefs = getGCMPreferences(getApplicationContext());
		int appVersion = getAppVersion(getApplicationContext());
		Log.i("TAG", "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regid);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		
		if (registrationId.isEmpty()) {
			Log.i("getRegistrationId", "Registration not found.");
			return "";
		}
		
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i("Rawr", "Registration not found.");
			return "";
		}

		return registrationId;
	}

	private int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException("could not get package name: " + e);
		}
	}

	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(this.getClass().getSimpleName(), Context.MODE_PRIVATE);
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if ( resultCode != ConnectionResult.SUCCESS) {
			if ( GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i("checkPlayServices", "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
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
    	// Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        
        // Handle your other action bar items...
    	// handle item selection
    	switch (item.getItemId()) {
    	case R.id.actie_nieuw:
			Intent intent = new Intent(this, MeetingInvoerenActivity.class);
			startActivity(intent);
			
    		return true;
    	case R.id.action_settings:
    		Log.i("Action Bar", "Settings option choosen");
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// Save the selected item in the dropdown navigation.
		int i = getActionBar().getSelectedNavigationIndex();
	    outState.putInt("index", i);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int pos,
				long id) {
			String tag = "";

			FragmentManager fragmentManager= getFragmentManager();
			Fragment fragment = fragmentManager.findFragmentById(R.id.container);

			switch (pos) {
			case 0:
				if(!(fragment instanceof DashboardFragment))  {
					fragment = new DashboardFragment();
					tag = "Profiel";

					fragmentManager
					.beginTransaction()
					.replace(R.id.container, fragment, tag)
					.commit();
				}
				break;
			case 1:
				if(!(fragment instanceof GrafiekFragment)) {
					fragment = new GrafiekFragment();
					tag = "Grafiek";

					fragmentManager
						.beginTransaction()
						.replace(R.id.container, fragment, tag)
						.commit();
				}
				break;
			case 2:
				if(!(fragment instanceof HistoryFragment)) {
					fragment = new HistoryFragment();
					tag = "Geschiedenis";

					fragmentManager
						.beginTransaction()
						.replace(R.id.container, fragment, tag)
						.commit();
				}
				break;
			case 3:
				tag = "ProfilePreferences";

				fragmentManager
					.beginTransaction()
					.replace(R.id.container, new ProfilePreferencesFragment(), tag)
					.commit();
				break;
			case 4:
				tag = "Preferences";

				fragmentManager
					.beginTransaction()
					.replace(R.id.container, new PreferencesFragment(), tag)
					.commit();
				break;
			}
			// Highlight the selected item, update the title, and close the drawer
		    mDrawerList.setItemChecked(pos, true);
		    setTitle(mDrawerItems[pos]);
			mDrawerLayout.closeDrawer(mDrawerList);
		}

	}	

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.actie_nieuw).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
}
