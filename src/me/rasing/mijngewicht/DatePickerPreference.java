package me.rasing.mijngewicht;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class DatePickerPreference extends DialogPreference implements
		OnDateChangedListener {

	private static final String DEFAULT_VALUE = "";
	private DatePicker datePicker;
	private String mCurrentValue;

	public DatePickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DatePickerPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected View onCreateDialogView() {
		this.datePicker = new DatePicker(getContext());
		this.datePicker.setCalendarViewShown(false);

		Calendar calendar = Calendar.getInstance();

		if (this.mCurrentValue != null) {
			calendar.setTime(this.getDate());
		}

		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), this);

		return datePicker;
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		Calendar selected = new GregorianCalendar(year, month, day);
		this.mCurrentValue = formatter.format(selected.getTime());

	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// When the user selects "OK", persist the new value
		if (positiveResult) {
			Log.d("!!!DATA!!!", ">>> " + this.mCurrentValue);
			persistString(this.mCurrentValue);
		}
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		if (restorePersistedValue) {
			// Restore existing state
			mCurrentValue = this.getPersistedString(DEFAULT_VALUE);
		} else {
			// Set default state from the XML attribute
			mCurrentValue = (String) defaultValue;
			persistString(mCurrentValue);
		}
		Log.d("!!!DATA!!!", ">>> " + this.mCurrentValue);
	}

	/**
	 * Called when Android pauses the activity.
	 */
	@Override
	protected Parcelable onSaveInstanceState() {
		if (isPersistent())
			return super.onSaveInstanceState();
		else
			return new SavedState(super.onSaveInstanceState());
	}

	/**
	 * Called when Android restores the activity.
	 */
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state == null || !state.getClass().equals(SavedState.class)) {
			super.onRestoreInstanceState(state);
			//setTheDate(((SavedState) state).dateValue);
		} else {
			SavedState s = (SavedState) state;
			super.onRestoreInstanceState(s.getSuperState());
			//setTheDate(s.value);
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}

	private static class SavedState extends BaseSavedState {
		// Member that holds the setting's value
		// Change this data type to match the type saved by your Preference
		String value;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public SavedState(Parcel source) {
			super(source);
			// Get the current preference's value
			value = source.readString(); // Change this to read the appropriate
											// data type
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			// Write the preference's value
			dest.writeString(value); // Change this to write the appropriate
										// data type
		}

		// Standard creator object using an instance of this class
		@SuppressWarnings("unused")
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	public Date getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());

		try {
			if (this.mCurrentValue != null) {
			return format.parse(this.mCurrentValue);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
