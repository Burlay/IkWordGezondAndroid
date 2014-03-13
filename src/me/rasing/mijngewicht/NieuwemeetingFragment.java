package me.rasing.mijngewicht;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import me.rasing.mijngewicht.fragments.DatePickerFragment;
import me.rasing.mijngewicht.fragments.TimePickerFragment;
import me.rasing.mijngewicht.providers.GewichtProvider;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class NieuwemeetingFragment extends Fragment implements OnClickListener {
	private int id = 0;
	protected boolean ttsIsInit;
	private OnDoneOrDiscardSelectedListener mListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_nieuwemeeting,
				container, false);

		// initTextToSpeech();

		TextView editDate = (TextView) rootView.findViewById(R.id.editDate);
		editDate.setOnClickListener(this);
		TextView editTime = (TextView) rootView.findViewById(R.id.editTime);
		editTime.setOnClickListener(this);

		final Intent intent = getActivity().getIntent();
		id = intent.getIntExtra(MeetingInvoerenActivity.ID, 0);

		if (id != 0) {
			final DbHelper mDbHelper = new DbHelper(getActivity()
					.getBaseContext());
			final SQLiteDatabase db = mDbHelper.getWritableDatabase();

			// Define a projection that specifies which columns from the
			// database
			// you will actually use after this query.
			final String[] projection = { Metingen._ID,
					Metingen.COLUMN_NAME_GEWICHT, Metingen.COLUMN_NAME_DATUM };

			final Cursor c = db.query(Metingen.TABLE_NAME, // The table to query
					projection, // The columns to return
					Metingen._ID + "=" + Integer.toString(id), // The columns
																// for the WHERE
																// clause
					null, // The values for the WHERE clause
					null, // don't group the rows
					null, // don't filter by row groups
					Metingen.COLUMN_NAME_DATUM + " DESC");

			c.moveToFirst();
			final double gewicht = c.getDouble(c
					.getColumnIndexOrThrow(Metingen.COLUMN_NAME_GEWICHT));
			final String datum = c.getString(c
					.getColumnIndexOrThrow(Metingen.COLUMN_NAME_DATUM));

			db.close();

			TextView mWeight = (TextView) rootView.findViewById(R.id.gewicht);
			mWeight.setText(NumberFormat.getInstance().format(gewicht));

			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mmZ", Locale.getDefault());
			try {
				// txtDatum.setText(format.parse(datum).toString());
				editDate.setText(DateFormat.getDateInstance(DateFormat.LONG)
						.format(format.parse(datum)));
				// txtTijd.setText(format.parse(datum).toString());
				editTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
						.format(format.parse(datum)));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			final Date today = c.getTime();

			editDate.setText(DateFormat.getDateInstance(DateFormat.LONG)
					.format(today));
			editTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
					.format(today));

			getActivity().setTitle("Nieuwe meeting");
		}

		// Inflate a "Done/Discard" custom action bar view.
		LayoutInflater actionBarInflater = (LayoutInflater) getActivity()
				.getActionBar().getThemedContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View customActionBarView = actionBarInflater.inflate(
				R.layout.actionbar_custom_view_done_discard, null);
		customActionBarView.findViewById(R.id.actionbar_done)
				.setOnClickListener(this);
		customActionBarView.findViewById(R.id.actionbar_discard)
				.setOnClickListener(this);

		// Show the custom action bar view and hide the normal Home icon and
		// title.
		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(customActionBarView,
				new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));

		return rootView;
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnDoneOrDiscardSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnDoneOrDiscardSelectedListener.");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editDate:
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(getFragmentManager(), "datePicker");
			break;
		case R.id.editTime:
			DialogFragment timePicker = new TimePickerFragment();
			timePicker.show(getFragmentManager(), "timePicker");
			break;
		case R.id.actionbar_done:
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getActivity().getCurrentFocus()
					.getWindowToken(), 0);

			this.saveMeasurement();
			
			break;
		case R.id.actionbar_discard:
			mListener.onDiscardSelected();
			break;
		}
	}

	private void saveMeasurement() {

		EditText editText = (EditText) getActivity().findViewById(R.id.gewicht);
		final String gewicht = editText.getText().toString();

		TextView editDate = (TextView) getActivity()
				.findViewById(R.id.editDate);
		String mDatum = editDate.getText().toString();

		TextView editTime = (TextView) getActivity()
				.findViewById(R.id.editTime);
		String mTijdstip = editTime.getText().toString();

		if (gewicht.length() == 0) {
			editText.setError("Vul je gewicht in.");
			return;
		}

		Date datum;
		try {
			datum = DateFormat.getDateTimeInstance(DateFormat.LONG,
					DateFormat.SHORT).parse(mDatum + " " + mTijdstip);
			final DateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mmZ", Locale.getDefault());

			// Create a new map of values, where column names are the keys
			ContentValues values = new ContentValues();
			values.put(Metingen.COLUMN_NAME_GUID, UUID.randomUUID().toString());
			values.put(Metingen.COLUMN_NAME_GEWICHT, gewicht);
			values.put(Metingen.COLUMN_NAME_DATUM, formatter.format(datum));

			ContentResolver mContentResolver = this.getActivity()
					.getContentResolver();

			Uri mUri = new Uri.Builder().scheme("content")
					.authority(GewichtProvider.AUTHORITY)
					.path(Metingen.TABLE_NAME).build();
			if (id != 0) {
				String selection = Metingen._ID + " = ?";
				String[] selectionArgs = { String.valueOf(id) };

				mContentResolver.update(mUri, values, selection, selectionArgs);
			} else {
				mContentResolver.insert(mUri, values);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mListener.onDiscardSelected();
	}
	
	public interface OnDoneOrDiscardSelectedListener {
		public void onDoneSelected();
		public void onDiscardSelected();
	}
}
