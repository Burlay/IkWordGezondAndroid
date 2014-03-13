package me.rasing.mijngewicht.fragments;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import me.rasing.mijngewicht.R;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		TextView editDate = (TextView) getActivity()
				.findViewById(R.id.editDate);
		String mDatum = editDate.getText().toString();

		// Use the current date as the default date in the picker
		Calendar c = Calendar.getInstance();

		try {
			Date datum = DateFormat.getDateInstance(DateFormat.LONG).parse(
					mDatum);
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

		TextView editDate = (TextView) getActivity()
				.findViewById(R.id.editDate);
		editDate.setText(DateFormat.getDateInstance(DateFormat.LONG).format(
				c.getTime()));
	}
}