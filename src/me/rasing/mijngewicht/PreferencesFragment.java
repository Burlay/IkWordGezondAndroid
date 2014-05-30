package me.rasing.mijngewicht;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class PreferencesFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.unit_preferences);// show the current value in the settings screen
		
	    for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
	        Preference pref = getPreferenceScreen().getPreference(i);
	        
	        if (pref instanceof ListPreference) {
	        	ListPreference listPref = (ListPreference) pref;
	        	listPref.setSummary(listPref.getEntry());
	        }
	      }
	}
}
