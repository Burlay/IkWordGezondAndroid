package me.rasing.ikwordgezond;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class NieuwemeetingFragment extends Fragment implements OnClickListener{
	String DATE_SEP = "-";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	setHasOptionsMenu(true);
    	
        View rootView = inflater.inflate(R.layout.fragment_nieuwemeeting, container, false);
        
        // Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
        
        TextView editDate = (TextView) rootView.findViewById(R.id.editDate);
        editDate.setText(Integer.toString(year) + DATE_SEP + String.format("%02d", month) + DATE_SEP + String.format("%02d", day));
        editDate.setOnClickListener(this);
        
        getActivity().setTitle("Nieuwe meeting");
        
        return rootView;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.nieuwe_meeting_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// handle item selection
    	switch (item.getItemId()) {
    	case R.id.action_done:


InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    		
    		EditText editText = (EditText) getActivity().findViewById(R.id.gewicht);
    		String message = editText.getText().toString();

    		TextView editDate = (TextView) getActivity().findViewById(R.id.editDate);
    		String datum = editDate.getText().toString();

    		// Gets the data repository in write mode
    		DbHelper mDbHelper = new DbHelper(getActivity().getBaseContext());
    		SQLiteDatabase db = mDbHelper.getWritableDatabase();

    		// Create a new map of values, where column names are the keys
    		ContentValues values = new ContentValues();
    		values.put(Metingen.COLUMN_NAME_GEWICHT, message);
    		values.put(Metingen.COLUMN_NAME_DATUM, datum);

    		// Insert the new row, returning the primary key value of the new row
    		db.insert(
    				Metingen.TABLE_NAME,
    				null,
    				values);

    		db.close();
    		
    		Fragment fragment = new ProfielFragment();

    		// Insert the fragment by replacing any existing fragment
    		FragmentManager fragmentManager = getFragmentManager();
    		
    		fragmentManager.popBackStack();
    		
    		fragmentManager.beginTransaction()
    			           .replace(R.id.content_frame,  fragment)
    			           .commit();

    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.editDate:
		    	DialogFragment newFragment = new DatePickerFragment();
		    	newFragment.show(getFragmentManager(), "datePicker");
				break;
		}
	}
}
