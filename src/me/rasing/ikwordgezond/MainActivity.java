package me.rasing.ikwordgezond;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private String[] mMenuItems;
	private ListView mDrawerList;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mMenuItems = getResources().getStringArray(R.array.menuitems_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
         mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMenuItems));
        
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        if (savedInstanceState == null) {
            selectItem(0);
        }
        
        mDrawerLayout.openDrawer(Gravity.START);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
    	
    	@Override
    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		selectItem(position);
    	}
    }

    public static class DatePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {
		String DATE_SEP = "-";

    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		// Use the current date as the default date in the picker
    		final Calendar c = Calendar.getInstance();
    		int year = c.get(Calendar.YEAR);
    		int month = c.get(Calendar.MONTH);
    		int day = c.get(Calendar.DAY_OF_MONTH);

    		// Create a new instance of DatePickerDialog and return it
    		return new DatePickerDialog(getActivity(), this, year, month, day);
    	}

    	public void onDateSet(DatePicker view, int year, int month, int day) {
            TextView editDate = (TextView) getActivity().findViewById(R.id.editDate);
            editDate.setText(Integer.toString(year) + DATE_SEP + String.format("%02d", month) + DATE_SEP + String.format("%02d", day));
    	}
    }
    
    public void showDatePickerDialog(View v) {
    	DialogFragment newFragment = new DatePickerFragment();
    	newFragment.show(getFragmentManager(), "datePicker");
    }
    
    @SuppressLint("NewApi")
	private void selectItem(int position) {
    	
		try {
	    	// Create a new fragment based on the chosen menu item
	    	String className = "me.rasing.ikwordgezond." + getResources().getStringArray(R.array.menuitems_array)[position].replaceAll("\\s","") + "Fragment";
	    	
			Class<?> cl = Class.forName(className);
			Constructor<?> con;
			con = cl.getConstructor();
			Fragment fragment = (Fragment) con.newInstance();
	    	
	    	// Insert the fragment by replacing any existing fragment
	    	FragmentManager fragmentManager = getFragmentManager();
	    	fragmentManager.beginTransaction()
	    				   .replace(R.id.content_frame,  fragment)
	    	               .commit();
	    	
	    	// Highlight the selected item, update the title, and close the drawer
	    	mDrawerList.setItemChecked(position, true);
	    	setTitle(mMenuItems[position]);
	    	mDrawerLayout.closeDrawer(mDrawerList);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
