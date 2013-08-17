package me.rasing.mijngewicht.providers;

import me.rasing.mijngewicht.DbHelper;
import me.rasing.mijngewicht.Metingen;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class GewichtProvider extends ContentProvider {
	private static final String AUTHORITY = "me.rasing.mijngewicht.providers.GewichtProvider";
	public static final Uri METINGEN_URI = Uri.parse("content://" + AUTHORITY + "/" + Metingen.TABLE_NAME);
	private static DbHelper mDbHelper;
	private static final UriMatcher sUriMatcher;
	private static final int METINGEN = 0;
	private static final int METINGEN_ID = 1;
	
	@Override
	public boolean onCreate() {
		mDbHelper = new DbHelper(getContext());
		
		return true;
	}
	
	@Override
	public int delete(Uri uri, String where, String[] selectionArgs) {
		SQLiteDatabase db;
		int delCount = 0;
		
		switch (sUriMatcher.match(uri)) {
			case METINGEN:
				break;
			case METINGEN_ID:
				String id = uri.getLastPathSegment();
				db = mDbHelper.getWritableDatabase();
				delCount = db.delete(Metingen.TABLE_NAME, Metingen._ID + "="+id, null);
				break;
		}
	   // notify all listeners of changes:
	   if (delCount > 0) {
	      getContext().getContentResolver().notifyChange(uri, null);
	   }
	   return delCount;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(Metingen.TABLE_NAME);
		
		switch (sUriMatcher.match(uri)) {
			case METINGEN:
				break;
			case METINGEN_ID:
				Log.d("case", "Metingen_id");
				break;
			default:
				Log.d("case", "not found");
		}
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}
	static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	    sUriMatcher.addURI(AUTHORITY, Metingen.TABLE_NAME, METINGEN);
	    sUriMatcher.addURI(AUTHORITY, Metingen.TABLE_NAME + "/#", METINGEN_ID);
	}
}
