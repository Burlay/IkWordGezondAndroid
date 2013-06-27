package me.rasing.mijngewicht;

import me.rasing.mijngewicht.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class MeetingInvoerenActivity extends FragmentActivity {
	public static final String ID = "id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_invoeren);
		
		Fragment fragment = new NieuwemeetingFragment();

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
			           .replace(R.id.container,  fragment)
			           .commit();
	}
}
