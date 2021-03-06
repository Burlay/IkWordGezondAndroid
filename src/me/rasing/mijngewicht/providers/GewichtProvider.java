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
	public static final String AUTHORITY = "me.rasing.mijngewicht.providers.GewichtProvider";
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
	public Uri insert(Uri uri, ContentValues mValues) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		long rowId = db.insert(Metingen.TABLE_NAME, null, mValues);
		
		if ( rowId > 0 ) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c = null;
		
		switch (sUriMatcher.match(uri)) {
			case METINGEN:
				String limit = uri.getQueryParameter("LIMIT");
				if (limit == null) {
					qb.setTables(Metingen.TABLE_NAME);
					c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				} else {
					String query = "SELECT ";
					for (int i = 0; i < projection.length; i++) {
						query += projection[i];
						query += i!=projection.length-1?", ":" ";
					}
					query += "FROM " + Metingen.TABLE_NAME + " ";
					query += sortOrder==null?"":"ORDER BY " + sortOrder + " ";
					query += "LIMIT " + limit;
					c = db.rawQuery(query, selectionArgs);
				}
				break;
			case METINGEN_ID:
				Log.d("case", "Metingen_id");
				break;
			default:
				throw new RuntimeException();
		}
		
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int count = db.update(Metingen.TABLE_NAME, values, selection, selectionArgs);
		
		if ( count > 0 )
			getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}
	static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	    sUriMatcher.addURI(AUTHORITY, Metingen.TABLE_NAME, METINGEN);
	    sUriMatcher.addURI(AUTHORITY, Metingen.TABLE_NAME + "/#", METINGEN_ID);
	}
}
