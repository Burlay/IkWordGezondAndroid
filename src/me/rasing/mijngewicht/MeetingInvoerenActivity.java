package me.rasing.mijngewicht;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import me.rasing.mijngewicht.providers.GewichtProvider;
import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class MeetingInvoerenActivity extends FragmentActivity implements OnClickListener {
	public static final String ID = "id";
	private NieuwemeetingFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_invoeren);
		
		fragment = new NieuwemeetingFragment();

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
			           .replace(R.id.container,  fragment)
			           .commit();

        // Inflate a "Done/Discard" custom action bar view.
        LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(
                R.layout.actionbar_custom_view_done_discard, null);
        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(this);
        customActionBarView.findViewById(R.id.actionbar_discard).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        // Show the custom action bar view and hide the normal Home icon and title.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
	}

	@Override
	public void onClick(View v) {
		EditText editText = (EditText) findViewById(R.id.gewicht);
		String gewicht = editText.getText().toString();

		TextView editDate = (TextView) findViewById(R.id.editDate);
		String mDatum = editDate.getText().toString();

		TextView editTime = (TextView) findViewById(R.id.editTime);
		String mTijdstip = editTime.getText().toString();

		
		if (gewicht.length() == 0) {
			editText.setError("Vul je gewicht in.");
			return;
		}

		ContentResolver mResolver = this.getContentResolver();
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		try {
			Date datum = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).parse(mDatum + " " + mTijdstip);
			DateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mmZ", Locale.getDefault());

			// Create a new map of values, where column names are the keys
			ContentValues values = new ContentValues();
			values.put(Metingen.COLUMN_NAME_GEWICHT, gewicht);
			values.put(Metingen.COLUMN_NAME_DATUM, formatter.format(datum));
			
			int id = fragment.getMeetingId();

			if (id != 0) {
				// Which row to update, based on the ID
				String selection = Metingen._ID + "=?";
				String[] selectionArgs = { String.valueOf(id) };

				mResolver.update(
						GewichtProvider.METINGEN_URI,
						values,
						selection,
						selectionArgs);
			} else {
				// Insert the new row, returning the primary key value of the new row
				values.put(Metingen.COLUMN_NAME_GUID, UUID.randomUUID().toString());
				mResolver.insert(
						GewichtProvider.METINGEN_URI,
						values);

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Schedule notification.
		NotificationScheduler notificationScheduler = new NotificationScheduler();
		notificationScheduler.ScheduleNotification(getBaseContext());
		
		finish();
	}
}
