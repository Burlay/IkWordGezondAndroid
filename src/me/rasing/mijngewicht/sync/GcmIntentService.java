package me.rasing.mijngewicht.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }
    
	public GcmIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("GCM", "Received a message.");
		
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		
		String messageType = gcm.getMessageType(intent);
		
		if ( !extras.isEmpty() ) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				//AccountManager accountManager = AccountManager.get(this.getApplicationContext());
				//Account[] accounts = accountManager.getAccountsByType(AuthenticatorActivity.ARG_ACCOUNT_TYPE);
				
//				for ( Account account : accounts ) {
//			        //ContentResolver.requestSync(account, GewichtProvider.AUTHORITY, new Bundle());
//				}
			}
		}
		
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

}
