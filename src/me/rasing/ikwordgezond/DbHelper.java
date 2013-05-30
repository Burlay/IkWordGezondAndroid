package me.rasing.ikwordgezond;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "IkWordGezond.db";
    
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = " ,";
	
	private static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE " + Metingen.TABLE_NAME + " (" +
			Metingen._ID + " INTEGER PRIMARY KEY," +
			Metingen.COLUMN_NAME_GEWICHT + TEXT_TYPE + COMMA_SEP +
			Metingen.COLUMN_NAME_SPIERMASSA + TEXT_TYPE + COMMA_SEP +
			Metingen.COLUMN_NAME_VET + TEXT_TYPE + COMMA_SEP +
			Metingen.COLUMN_NAME_WATER + TEXT_TYPE + COMMA_SEP +
			" )";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	
	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		onCreate(db);
	}

}
