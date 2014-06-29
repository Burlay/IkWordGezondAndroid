package me.rasing.mijngewicht.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.rasing.mijngewicht.DbHelper;
import me.rasing.mijngewicht.Metingen;
import me.rasing.mijngewicht.providers.GewichtProvider;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.JavascriptInterface;

public class MeasurementsModel implements LoaderManager.LoaderCallbacks<Cursor> {

	private final Context context;
	private final ContentResolver mContentResolver;
	private Callback callbacks;
	private Cursor cursor;

	public interface Callback {
		public void MeasurementsModelCallback();
	}
	
	public MeasurementsModel(Context context) {
		this.context = context;
		this.mContentResolver = context.getContentResolver();
	}

	private float getCurrentWeight() {
		return getCurrentWeight("kg");
	}
	
	public Float getCurrentWeight(String weightUnit) {
		if (cursor.getCount() == 0)
			return null;

		cursor.moveToFirst();
		Float weight = cursor.getFloat(cursor.getColumnIndexOrThrow( Metingen.COLUMN_NAME_GEWICHT ));
		
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
		if (cursor.getCount() == 0)
			return null;
		
		cursor.moveToLast();
		Float weight = cursor.getFloat( cursor.getColumnIndexOrThrow( Metingen.COLUMN_NAME_GEWICHT ) );

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
		if (cursor.getCount() == 0)
				return null;
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ",
				Locale.getDefault());
		DateTime now = DateTime.now();
		cursor.moveToFirst();
		final String datum = cursor.getString(cursor.getColumnIndexOrThrow(Metingen.COLUMN_NAME_DATUM));
		Date d;
		try {
			d = format.parse(datum);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		DateTime dateTime = d == null ? null : new DateTime(d);
		Period p = Days.daysBetween(dateTime.withTimeAtStartOfDay(),
				now.withTimeAtStartOfDay()).toPeriod();
		return p.getDays();
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

	public void registerContentObserver(
			ContentObserver observer) {
		mContentResolver.registerContentObserver(GewichtProvider.METINGEN_URI, true, observer);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { Metingen.COLUMN_NAME_GEWICHT,
				Metingen.COLUMN_NAME_DATUM };

		CursorLoader cursorLoader = new CursorLoader(this.context,
				GewichtProvider.METINGEN_URI, projection, null, null,
				Metingen.COLUMN_NAME_DATUM + " DESC");

		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		this.cursor = data;
		this.callbacks.MeasurementsModelCallback();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		this.cursor = null;
		//this.callbacks.MeasurementsModelCallback();
	}

	public void registerCallback(
			MeasurementsModel.Callback callback) {
		this.callbacks = callback;
	}
}