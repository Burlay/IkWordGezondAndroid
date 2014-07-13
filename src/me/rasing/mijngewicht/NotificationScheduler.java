package me.rasing.mijngewicht;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;

import me.rasing.mijngewicht.providers.GewichtProvider;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri.Builder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationScheduler extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String[] projection = {Metingen.COLUMN_NAME_DATUM};

		Builder b = GewichtProvider.METINGEN_URI.buildUpon();
		b.appendQueryParameter("LIMIT", "1");
		ContentResolver mResolver = context.getContentResolver();
		Cursor cursor = mResolver.query(
				b.build(),
				projection,
				null,
				null,
				Metingen.COLUMN_NAME_DATUM + " DESC");

		cursor.moveToFirst();
		String dateString = cursor.getString(cursor.getColumnIndexOrThrow(Metingen.COLUMN_NAME_DATUM));
		SimpleDateFormat format = 
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.getDefault());
		Date lastWeighing = null;
		try {
			lastWeighing = format.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		
		DateTime dateTime = lastWeighing==null?null:new DateTime(lastWeighing);
		Period p = Days.daysBetween(dateTime.withTimeAtStartOfDay(), DateTime.now().withTimeAtStartOfDay()).toPeriod();
		
		if (p.getDays() >= 7) {
			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_stat_mg)
			.setContentTitle("My notification")
			.setContentText("Hello World!");
			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(context, MeetingInvoerenActivity.class);

			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
					stackBuilder.getPendingIntent(
							0,
							PendingIntent.FLAG_UPDATE_CURRENT
							);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager =
					(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			int mId = 0;
			mNotificationManager.notify(mId, mBuilder.build());
		}
		

	}
    
    public void ScheduleRepeatingNotification(Context context) {
    	Intent intent = new Intent("me.rasing.mijngewicht.SCHEDULE_NOTIFICATION");
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);

    	AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    	alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
