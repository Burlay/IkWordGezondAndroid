package me.rasing.mijngewicht.sync;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import me.rasing.mijngewicht.DbHelper;
import me.rasing.mijngewicht.Metingen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountsException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
	private Context context;
    
    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        this.context = context;
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        this.context = context;

    }
    
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		
		//SyncStats syncStats = syncResult.stats;
		
		HttpConnector connector = new HttpConnector(this.context);
		SyncResponse pullResponse = null;
		try {
			pullResponse = connector.getMeasurements(account);
		} catch (AccountsException e) {
			syncResult.stats.numAuthExceptions++;
			
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionDetails = sw.toString();
			
			Log.d("Sync Adapter", exceptionDetails);
		} catch (IOException e) {
			syncResult.stats.numIoExceptions++;
			
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionDetails = sw.toString();
			
			Log.d("Sync Adapter", exceptionDetails);
		} catch (GeneralSecurityException e) {
			syncResult.stats.numIoExceptions++;
			
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionDetails = sw.toString();
			
			Log.d("Sync Adapter", exceptionDetails);
		}
		
		if ( pullResponse != null ) {
			if ( pullResponse.getResponseCode() == 200 ) {
				Log.d("!!!DATA!!!", ">>> " + pullResponse.getResponseCode());
				String response = pullResponse.getResponse();
				SQLiteDatabase db = null;
				
	            try {
	            	if ( response.toString().length() > 0 ){
						JSONArray jArray = new JSONArray(response.toString());
						
						for (int i=0; i < jArray.length(); i++)
						{
					        JSONObject oneObject = jArray.getJSONObject(i);
					        
							ContentValues values = new ContentValues();
							values.put(Metingen.COLUMN_NAME_GUID, oneObject.getString("measurement_guid"));
							values.put(Metingen.COLUMN_NAME_GEWICHT, oneObject.getDouble("weight"));
							values.put(Metingen.COLUMN_NAME_DATUM, oneObject.getString("date_taken"));
			    			DateFormat df = new SimpleDateFormat("yyy-MM-dd'T'HH:mmZ", Locale.getDefault());
			    			df.setTimeZone( TimeZone.getTimeZone("UTC") );
			            	
			    			values.put( Metingen.COLUMN_NAME_LAST_SYNCED, df.format(new Date()) );
			    			
			    			/*
			    			 * Push new data to server.
			    			 */
			    			DbHelper mDbHelper = new DbHelper(context);
			    			db = mDbHelper.getWritableDatabase();
			    			
			    			db.insert(Metingen.TABLE_NAME, null, values);
					        
					        Log.d("!!!DATA!!!", oneObject.toString());
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch ( SQLiteConstraintException e ) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String exceptionDetails = sw.toString();
					
					Log.d("SQLiteConstraintException", exceptionDetails);
				} finally {
					if ( db != null ) {
						db.close();
					}
				}
			} else {
				syncResult.stats.numIoExceptions++;
				Log.d("Sync Adapter", "Wrong Response code expected 200 received " + pullResponse.getResponseCode());
			}
		} else {
			syncResult.stats.numIoExceptions++;
			Log.d("Sync Adapter", "No pullResponse received");
		}
		
		SyncResponse pushResponse = null;
		try {
			pushResponse = connector.pushMeasurements(account);
		} catch (AccountsException e) {
			syncResult.stats.numAuthExceptions++;
			
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionDetails = sw.toString();
			
			Log.d("Sync Adapter", exceptionDetails);
		} catch (IOException e) {
			syncResult.stats.numIoExceptions++;
			
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionDetails = sw.toString();
			
			Log.d("Sync Adapter", exceptionDetails);
		} catch (GeneralSecurityException e) {
			syncResult.stats.numIoExceptions++;
			
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionDetails = sw.toString();
			
			Log.d("Sync Adapter", exceptionDetails);
		}
		
		if ( pushResponse != null ) {
			Log.d("!!!DATA!!!", ">>> pushResponse: " + pushResponse.getResponseCode());
		} else {
			syncResult.stats.numIoExceptions++;
			Log.d("Sync Adapter", "No pushResponse received");
		}
	}
}