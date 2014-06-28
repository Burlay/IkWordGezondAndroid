package me.rasing.mijngewicht.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;

import me.rasing.mijngewicht.DbHelper;
import me.rasing.mijngewicht.Metingen;
import me.rasing.mijngewicht.providers.GewichtProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri.Builder;
import android.preference.PreferenceManager;
import android.webkit.JavascriptInterface;

public class MeasurementsModel {

	private final Context context;
	private final ContentResolver mContentResolver;

	public MeasurementsModel(Context context) {
		this.context = context;
		this.mContentResolver = context.getContentResolver();
	}

	private float getCurrentWeight() {
		return getCurrentWeight("kg");
	}
	
	public Float getCurrentWeight(String weightUnit) {
		String[] projection = { Metingen.COLUMN_NAME_GEWICHT };
		Builder b = GewichtProvider.METINGEN_URI.buildUpon();
		b.appendQueryParameter("LIMIT", "1");
		Cursor c = mContentResolver.query(b.build(), projection,
				null, null, Metingen.COLUMN_NAME_DATUM + " DESC");
		
		if (c.getCount() == 0) {
			c.close();
			return null;
		}
		
		c.moveToFirst();
		Float weight = c.getFloat(c.getColumnIndexOrThrow( Metingen.COLUMN_NAME_GEWICHT ));
		c.close();
		
		return localizeWeight(weight, weightUnit);
	}

	public Float getTotalWeightDifference(String weightUnit) {
		Float currentWeight = this.getCurrentWeight(weightUnit);
		Float startingWeight = this.getStartingWeight(weightUnit);
		
		if (currentWeight != null && startingWeight != null) {
			return currentWeight - startingWeight;
		} else {
			return null;
		}
	}

	public Float getStartingWeight(String weightUnit) {
		String[] projection = { Metingen.COLUMN_NAME_GEWICHT };
		Builder b = GewichtProvider.METINGEN_URI.buildUpon();
		b.appendQueryParameter("LIMIT", "1");
		Cursor c = mContentResolver.query(b.build(), projection,
				null, null, Metingen.COLUMN_NAME_DATUM + " ASC");
		
		if (c.getCount() == 0) {
			c.close();
			return null;
		}
		
		c.moveToFirst();
		Float weight = c.getFloat( c.getColumnIndexOrThrow( Metingen.COLUMN_NAME_GEWICHT ) );
		c.close();
		return localizeWeight(weight, weightUnit);
	}

	public float getBMI(float length) {
		return getCurrentWeight() / ( length * length );
	}

	private float localizeWeight(float weight, String weightUnit) {
		
		float result = 0;
		if ( "lb".equals(weightUnit) ) {
			result = (float) (weight * 2.2);
		} else {
			result = weight;
		}
		
		return result;
	}

	public boolean isEmpty() {
		DbHelper mDbHelper = new DbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		String[] projection = {
				"COUNT(*)",
				};

		Cursor cursor = db.query(Metingen.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null,
				null);
		
		cursor.moveToFirst();
		int count =  cursor.getInt(0);
		cursor.close();
		db.close();
		
		return (count < 1);
	}
	
	@JavascriptInterface
	public String getWeightUnit() {
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    	String weightUnit = sharedPref.getString("weightUnit", "kg");
    	
		return weightUnit;
	}
	
	public Integer getDaysSinceLastWeighing() {
		String[] projection = { Metingen.COLUMN_NAME_DATUM };
		Builder b = GewichtProvider.METINGEN_URI.buildUpon();
		b.appendQueryParameter("LIMIT", "1");
		Cursor c = context.getContentResolver().query(b.build(), projection,
				null, null, Metingen.COLUMN_NAME_DATUM + " DESC");

		if (c.getCount() != 0) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ",
					Locale.getDefault());
			DateTime now = DateTime.now();
			c.moveToFirst();
			final String datum = c.getString(c
					.getColumnIndexOrThrow(Metingen.COLUMN_NAME_DATUM));
			Date d;
			try {
				d = format.parse(datum);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			DateTime dateTime = d == null ? null : new DateTime(d);
			Period p = Days.daysBetween(dateTime.withTimeAtStartOfDay(),
					now.withTimeAtStartOfDay()).toPeriod();
			c.close();
			return p.getDays();
		}
		c.close();
		return null;
	}

	@JavascriptInterface
	public String getMeasurementsAsJSON() {
		DbHelper mDbHelper = new DbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		String[] projection = {
				Metingen.COLUMN_NAME_GEWICHT,
				Metingen.COLUMN_NAME_DATUM
				};

		Cursor cursor = db.query(Metingen.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				Metingen.COLUMN_NAME_DATUM + " ASC",
				null);
		
    	String data = "[";

    	while (cursor.moveToNext()) {
    		final float weight = cursor.getFloat( cursor.getColumnIndexOrThrow( Metingen.COLUMN_NAME_GEWICHT ) );
    		float localWeight = this.localizeWeight(weight, this.getWeightUnit());

    		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.getDefault());
    		DateFormat df2 = new SimpleDateFormat("yyy-MM-dd HH:mm", Locale.getDefault());
    		
    		String datumString = cursor.getString( cursor.getColumnIndexOrThrow( Metingen.COLUMN_NAME_DATUM ) );
    		
    		Date date = null;
			try {
				date = df1.parse(datumString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		data += "{" + "\"date\": \"" + df2.format(date) + "\", \"close\":\"" + localWeight + "\"},";
    	}
    	
    	data = data.replaceAll(",$", "");
    	data += "]";
    	
    	cursor.close();
    	db.close();
    	
		return data;
	}
}