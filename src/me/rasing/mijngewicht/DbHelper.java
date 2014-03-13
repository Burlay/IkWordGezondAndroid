package me.rasing.mijngewicht;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	// If you change the database schema, you must increment the database
	// version.
	public static final int DATABASE_VERSION = 6;
	public static final String DATABASE_NAME = "IkWordGezond.db";

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = " ,";
	private static final String UNIQUE = " UNIQUE";

	private static final String SQL_CREATE_METINGEN = "CREATE TABLE "
			+ Metingen.TABLE_NAME + " (" + Metingen._ID
			+ " INTEGER PRIMARY KEY" + COMMA_SEP + Metingen.COLUMN_NAME_GEWICHT
			+ TEXT_TYPE + COMMA_SEP + Metingen.COLUMN_NAME_DATUM + TEXT_TYPE
			+ COMMA_SEP + Metingen.COLUMN_NAME_LAST_SYNCED + TEXT_TYPE
			+ COMMA_SEP + Metingen.COLUMN_NAME_GUID + TEXT_TYPE + UNIQUE + " )";

	private static final String SQL_UPGRADE_METINGEN_2 = "ALTER TABLE "
			+ Metingen.TABLE_NAME + " ADD COLUMN "
			+ Metingen.COLUMN_NAME_LAST_SYNCED + TEXT_TYPE;
	private static final String SQL_UPGRADE_METINGEN_3 = "ALTER TABLE "
			+ Metingen.TABLE_NAME + " ADD COLUMN " + Metingen.COLUMN_NAME_GUID
			+ TEXT_TYPE;
	private static final String SQL_UPGRADE_METINGEN_4 = "CREATE UNIQUE INDEX "
			+ Metingen.TABLE_NAME + Metingen.COLUMN_NAME_GUID + " ON "
			+ Metingen.TABLE_NAME + "(" + Metingen.COLUMN_NAME_GUID + ");";

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_METINGEN);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion <= 2) {
			db.execSQL(SQL_UPGRADE_METINGEN_2);
		}

		if (oldVersion <= 3) {
			db.execSQL(SQL_UPGRADE_METINGEN_3);
			this.generateUUIDs(db);
		}

		if (oldVersion <= 4) {
			this.updateTimestamps(db);
		}

		if (oldVersion <= 5) {
			db.execSQL(SQL_UPGRADE_METINGEN_4);
		}
	}

	private void updateTimestamps(SQLiteDatabase db) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.getDefault());

		DateFormat df = new SimpleDateFormat("yyy-MM-dd'T'HH:mmZ",
				Locale.getDefault());
		df.setTimeZone(TimeZone.getTimeZone("UTC"));

		String[] columns = { Metingen._ID, Metingen.COLUMN_NAME_DATUM };
		Cursor cursor = db.query(Metingen.TABLE_NAME, columns, null, null,
				null, null, null);

		while (cursor.moveToNext()) {
			String dateString = cursor.getString(cursor
					.getColumnIndex(Metingen.COLUMN_NAME_DATUM));

			try {
				Date date = format.parse(dateString);

				ContentValues values = new ContentValues();
				values.put(Metingen.COLUMN_NAME_DATUM, df.format(date));

				String[] whereArgs = { cursor.getString(cursor
						.getColumnIndex(Metingen._ID)) };

				db.update(Metingen.TABLE_NAME, values, Metingen._ID + " = ?",
						whereArgs);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void generateUUIDs(SQLiteDatabase db) {
		String[] columns = { Metingen._ID };
		Cursor cursor = db.query(Metingen.TABLE_NAME, columns, null, null,
				null, null, null);

		while (cursor.moveToNext()) {
			ContentValues values = new ContentValues();
			values.put(Metingen.COLUMN_NAME_GUID, UUID.randomUUID().toString());

			String[] whereArgs = { cursor.getString(cursor
					.getColumnIndex(Metingen._ID)) };

			db.update(Metingen.TABLE_NAME, values, Metingen._ID + " = ?",
					whereArgs);
		}
	}
}
