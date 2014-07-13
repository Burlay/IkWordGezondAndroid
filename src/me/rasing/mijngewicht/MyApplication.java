package me.rasing.mijngewicht;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		NotificationScheduler notificationScheduler = new NotificationScheduler();
		notificationScheduler.ScheduleRepeatingNotification(this.getApplicationContext());
		
		Log.e("HELLO", "DO AWESOME STUFF HERE");
	}
}
