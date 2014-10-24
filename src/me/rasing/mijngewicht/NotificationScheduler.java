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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri.Builder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationScheduler extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean notificationPreference = sharedPref.getBoolean("remind_weighing", false);
		if (notificationPreference == false) {
			return;
		}
		
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
		if (cursor.getCount() < 1) {
			return;
		}
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
			Drawable blankDrawable = context.getResources().getDrawable(R.drawable.notification_large_icon);
		    Bitmap blankBitmap=((BitmapDrawable)blankDrawable).getBitmap();
		    
			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_stat_mg)
			.setLargeIcon(blankBitmap)
			.setContentTitle("Vandaag wegen")
			.setContentText("Weeg jezelf wekelijks voor het beste resultaat.");

			Intent resultIntent = new Intent(context, MeetingInvoerenActivity.class);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			stackBuilder.addParentStack(MeetingInvoerenActivity.class);
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
					stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
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
