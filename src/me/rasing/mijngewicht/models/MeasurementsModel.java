package me.rasing.mijngewicht.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.rasing.mijngewicht.DbHelper;
import me.rasing.mijngewicht.Metingen;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.webkit.JavascriptInterface;

public class MeasurementsModel {

	private final Context context;

	public MeasurementsModel(Context context) {
		this.context = context;
	}

	private float getCurrentWeight() {
		return getCurrentWeight("kg");
	}
	
	public float getCurrentWeight(String weightUnit) {
		DbHelper mDbHelper = new DbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		String[] projection = {
				Metingen.COLUMN_NAME_GEWICHT,
				};

		Cursor cursor = db.query(Metingen.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				Metingen.COLUMN_NAME_DATUM + " DESC",
				"1");
		
		cursor.moveToFirst();
		float weight = cursor.getFloat( cursor.getColumnIndexOrThrow( Metingen.COLUMN_NAME_GEWICHT ) );
		cursor.close();
		db.close();
		
		float result = ( weightUnit.equals("kg") ) ? weight : localizeWeight(weight, weightUnit);
		
		return result;
	}

	public float getTotalWeightDifference(String weightUnit) {
		float currentWeight = this.getCurrentWeight(weightUnit);
		float startingWeight = this.getStartingWeight(weightUnit);
		
		return currentWeight - startingWeight;
	}

	public float getStartingWeight(String weightUnit) {
		DbHelper mDbHelper = new DbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		String[] projection = {
				Metingen.COLUMN_NAME_GEWICHT,
				};

		Cursor cursor = db.query(Metingen.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				Metingen.COLUMN_NAME_DATUM + " ASC",
				"1");
		
		cursor.moveToFirst();
		float weight = cursor.getFloat( cursor.getColumnIndexOrThrow( Metingen.COLUMN_NAME_GEWICHT ) );
		cursor.close();
		db.close();
		
		float result = 0;
		if ( "lb".equals(weightUnit) ) {
			result = (float) (weight * 2.2);
		} else {
			result = weight;
		}
		
		return result;
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
    	String weightUnit = sharedPref.getString("weightUnit", "");
    	
		return weightUnit;
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
				null,
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
