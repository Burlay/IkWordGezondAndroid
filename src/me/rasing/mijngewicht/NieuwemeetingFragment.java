package me.rasing.mijngewicht;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class NieuwemeetingFragment extends Fragment implements OnClickListener {
	//private static final int TTS_DATA_CHECK = 0;
	private int id = 0;
	private TextToSpeech tts;
	protected boolean ttsIsInit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_nieuwemeeting, container, false);

		//initTextToSpeech();
		
		TextView editDate = (TextView) rootView.findViewById(R.id.editDate);
		editDate.setOnClickListener(this);
		TextView editTime = (TextView) rootView.findViewById(R.id.editTime);
		editTime.setOnClickListener(this);

		final Intent intent = getActivity().getIntent();
		id = intent.getIntExtra(MeetingInvoerenActivity.ID, 0);

		if (id != 0) {
			final DbHelper mDbHelper = new DbHelper(getActivity().getBaseContext());
			final SQLiteDatabase db = mDbHelper.getWritableDatabase();

			// Define a projection that specifies which columns from the database
			// you will actually use after this query.
			final String[] projection = {
					Metingen._ID,
					Metingen.COLUMN_NAME_GEWICHT,
					Metingen.COLUMN_NAME_DATUM
			};

			//final String[] values = {Integer.toString(id)};

			// How you want the results sorted in the resulting Cursor
			//String sortOrder =
			//    FeedReaderContract.FeedEntry.COLUMN_NAME_UPDATED + " DESC";

			final Cursor c = db.query(
					Metingen.TABLE_NAME,  // The table to query
					projection,           // The columns to return
					Metingen._ID + "=" + Integer.toString(id),         // The columns for the WHERE clause
					null,               // The values for the WHERE clause
					null,                 // don't group the rows
					null,                 // don't filter by row groups
					Metingen.COLUMN_NAME_DATUM + " DESC"
					);

			c.moveToFirst();
			final double gewicht = c.getDouble(
					c.getColumnIndexOrThrow(Metingen.COLUMN_NAME_GEWICHT)
					);
			final String datum = c.getString(
					c.getColumnIndexOrThrow(Metingen.COLUMN_NAME_DATUM)
					);

			db.close();

			TextView mWeight = (TextView) rootView.findViewById(R.id.gewicht);
			mWeight.setText(NumberFormat.getInstance().format(gewicht));

			SimpleDateFormat format = 
					new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
			try {
				//txtDatum.setText(format.parse(datum).toString());
				editDate.setText(DateFormat.getDateInstance(DateFormat.LONG).format(format.parse(datum)));
				//txtTijd.setText(format.parse(datum).toString());
				editTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(format.parse(datum)));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			final Date today = c.getTime();

			editDate.setText(DateFormat.getDateInstance(DateFormat.LONG).format(today));
			editTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(today));

			getActivity().setTitle("Nieuwe meeting");
		}




		// Inflate a "Done/Discard" custom action bar view.
		LayoutInflater actionBarInflater = (LayoutInflater) getActivity().getActionBar().getThemedContext()
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View customActionBarView = actionBarInflater.inflate(
				R.layout.actionbar_custom_view_done_discard, null);
		customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (tts != null && ttsIsInit){
							tts.speak("150 kilograms", TextToSpeech.QUEUE_ADD, null);
						}
						
						EditText editText = (EditText) getActivity().findViewById(R.id.gewicht);
						String gewicht = editText.getText().toString();

						TextView editDate = (TextView) getActivity().findViewById(R.id.editDate);
						String mDatum = editDate.getText().toString();

						TextView editTime = (TextView) getActivity().findViewById(R.id.editTime);
						String mTijdstip = editTime.getText().toString();

						
						if (gewicht.length() == 0) {
							editText.setError("Vul je gewicht in.");
							return;
						}
						
						// Gets the data repository in write mode
						DbHelper mDbHelper = new DbHelper(getActivity().getBaseContext());
						SQLiteDatabase db = mDbHelper.getWritableDatabase();

						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
						try {
							Date datum = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).parse(mDatum + " " + mTijdstip);
							DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

							// Create a new map of values, where column names are the keys
							ContentValues values = new ContentValues();
							values.put(Metingen.COLUMN_NAME_GEWICHT, gewicht);
							values.put(Metingen.COLUMN_NAME_DATUM, formatter.format(datum));

							if (id != 0) {
								// Which row to update, based on the ID
								String selection = Metingen._ID + "=?";
								String[] selectionArgs = { String.valueOf(id) };

								db.update(
										Metingen.TABLE_NAME,
										values,
										selection,
										selectionArgs);
							} else {
								// Insert the new row, returning the primary key value of the new row
								db.insert(
										Metingen.TABLE_NAME,
										null,
										values);

							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						db.close();

						Intent intent = new Intent(getActivity(), MainActivity.class);
						startActivity(intent);
					}
				});
		customActionBarView.findViewById(R.id.actionbar_discard).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(), MainActivity.class);
						startActivity(intent);
					}
				});

		// Show the custom action bar view and hide the normal Home icon and title.
		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
				| ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(customActionBarView, new ActionBar.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));



		return rootView;
	}

//	private void initTextToSpeech() {
//		Intent intent = new Intent(Engine.ACTION_CHECK_TTS_DATA);
//		startActivityForResult(intent, TTS_DATA_CHECK);
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		
//		if ( requestCode == TTS_DATA_CHECK &&
//				resultCode == Engine.CHECK_VOICE_DATA_PASS) {
//			tts = new TextToSpeech(getActivity(), new OnInitListener() {
//				@Override
//				public void onInit(int status) {
//					if (status == TextToSpeech.SUCCESS) {
//						ttsIsInit = true;
//					}
//				}
//			});
//		} else {
//			startActivity(new Intent(Engine.ACTION_INSTALL_TTS_DATA));
//		}
//	}
	
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
		}
	}

	public static class DatePickerFragment extends DialogFragment
	implements DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			TextView editDate = (TextView) getActivity().findViewById(R.id.editDate);
			String mDatum = editDate.getText().toString();

			// Use the current date as the default date in the picker
			Calendar c = Calendar.getInstance();

			try {
				Date datum = DateFormat.getDateInstance(DateFormat.LONG).parse(mDatum);
				c.setTime(datum);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Use the current date as the default date in the picker
			Calendar c = Calendar.getInstance();
			c.set(year, month, day);

			TextView editDate = (TextView) getActivity().findViewById(R.id.editDate);
			editDate.setText(DateFormat.getDateInstance(DateFormat.LONG).format(c.getTime()));
		}
	}

	public static class TimePickerFragment extends DialogFragment
	implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			TextView editTime = (TextView) getActivity().findViewById(R.id.editTime);
			String mTijdstip = editTime.getText().toString();

			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();

			try {
				Date tijdstip = DateFormat.getTimeInstance(DateFormat.SHORT).parse(mTijdstip);
				c.setTime(tijdstip);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					android.text.format.DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Use the current date as the default date in the picker
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, hourOfDay);
			c.set(Calendar.MINUTE, minute);

			TextView editTime = (TextView) getActivity().findViewById(R.id.editTime);
			editTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime()));
		}
	}
}
