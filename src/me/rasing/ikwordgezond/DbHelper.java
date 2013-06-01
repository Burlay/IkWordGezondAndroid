package me.rasing.ikwordgezond;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "IkWordGezond.db";
    
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = " ,";
	
	private static final String SQL_CREATE_METINGEN =
			"CREATE TABLE " + Metingen.TABLE_NAME + " (" +
			Metingen._ID + " INTEGER PRIMARY KEY," +
			Metingen.COLUMN_NAME_GEWICHT + TEXT_TYPE + COMMA_SEP +
			Metingen.COLUMN_NAME_DATUM + TEXT_TYPE +
			" )";
	
	private static final String SQL_DELETE_METINGEN = "DROP TABLE IF EXISTS " + Metingen.TABLE_NAME;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	
	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_METINGEN);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_METINGEN);
        onCreate(db);
	}

}
