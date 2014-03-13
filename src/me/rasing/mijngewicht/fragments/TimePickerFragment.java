package me.rasing.mijngewicht.fragments;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import me.rasing.mijngewicht.R;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		TextView editTime = (TextView) getActivity()
				.findViewById(R.id.editTime);
		String mTijdstip = editTime.getText().toString();

		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();

		try {
			Date tijdstip = DateFormat.getTimeInstance(DateFormat.SHORT).parse(
					mTijdstip);
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

		TextView editTime = (TextView) getActivity()
				.findViewById(R.id.editTime);
		editTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(
				c.getTime()));
	}
}