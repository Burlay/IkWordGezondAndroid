package me.rasing.mijngewicht;

import java.text.DateFormat;
import java.util.Date;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class ProfilePreferencesFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.profile_preferences);

		// show the current value in the settings screen
		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
			setSummary(getPreferenceScreen().getPreference(i));
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		setSummary(findPreference(key));
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}
	
	private void setSummary(Preference pref) {
		if (pref instanceof ListPreference) {
			ListPreference listPref = (ListPreference) pref;
			listPref.setSummary(listPref.getEntry());
		} else if (pref instanceof DatePickerPreference) {
			DatePickerPreference datePref = (DatePickerPreference) pref;
			
			Date hmm = datePref.getDate();
			
			if (hmm != null) {
			String date = DateFormat.getDateInstance(DateFormat.LONG).format(hmm);
			
			datePref.setSummary(date);
			}
        } else if (pref instanceof NumberPickerPreference) {
        	NumberPickerPreference numberPref = (NumberPickerPreference) pref;
        	
        	if (numberPref.getLength() != null) {
        		numberPref.setSummary(numberPref.getLength().toString());
        	}
        }
	}
}
