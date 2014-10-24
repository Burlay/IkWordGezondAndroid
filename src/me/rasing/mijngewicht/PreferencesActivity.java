package me.rasing.mijngewicht;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class PreferencesActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_meeting_invoeren);
		
		String tag = "Preferences";

		FragmentManager fragmentManager= getFragmentManager();
		
		fragmentManager
			.beginTransaction()
			.replace(R.id.container, new PreferencesFragment(), tag)
			.commit();
	}
}
