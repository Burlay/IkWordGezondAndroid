package me.rasing.mijngewicht;

import android.app.Application;
import android.preference.PreferenceManager;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		NotificationScheduler notificationScheduler = new NotificationScheduler();
		notificationScheduler.ScheduleRepeatingNotification(this.getApplicationContext());
		
		PreferenceManager.setDefaultValues(this.getApplicationContext(), R.xml.profile_preferences, false);
	}
}
