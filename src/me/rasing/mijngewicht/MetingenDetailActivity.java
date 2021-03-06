package me.rasing.mijngewicht;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class MetingenDetailActivity extends Activity {
	public static final String ID = "id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_metingen_detail);

		final Intent intent = getIntent();
		final int id = intent.getIntExtra(ID, 0);

		final DbHelper mDbHelper = new DbHelper(getBaseContext());
		final SQLiteDatabase db = mDbHelper.getWritableDatabase();

		final String[] projection = {
				Metingen._ID,
				Metingen.COLUMN_NAME_GEWICHT,
				Metingen.COLUMN_NAME_DATUM
		};

		final Cursor c = db.query(
				Metingen.TABLE_NAME,  // The table to query
				projection,           // The columns to return
				Metingen._ID + "=" + Integer.toString(id),         // The columns for the WHERE clause
				null,               // The values for the WHERE clause
				null,                 // don't group the rows
				null,                 // don't filter by row groups
				Metingen.COLUMN_NAME_DATUM + " DESC"
				);
		
		c.moveToFirst();
    	final double gewicht = c.getDouble(
    			c.getColumnIndexOrThrow(Metingen.COLUMN_NAME_GEWICHT)
    			);
    	final String datum = c.getString(
    			c.getColumnIndexOrThrow(Metingen.COLUMN_NAME_DATUM)
    			);
    	
		TextView mWeight = (TextView) findViewById(R.id.gewicht);
		mWeight.setText(NumberFormat.getInstance().format(gewicht));
		
		SimpleDateFormat format = 
				new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
		
		TextView txtDatum = (TextView) findViewById(R.id.editDate);
		TextView txtTijd = (TextView) findViewById(R.id.editTime);
		try {
			txtDatum.setText(format.parse(datum).toString());
			txtTijd.setText(format.parse(datum).toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
