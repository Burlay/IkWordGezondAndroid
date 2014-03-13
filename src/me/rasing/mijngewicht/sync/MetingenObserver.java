package me.rasing.mijngewicht.sync;

import me.rasing.mijngewicht.authentication.AuthenticatorActivity;
import me.rasing.mijngewicht.providers.GewichtProvider;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class MetingenObserver extends ContentObserver {
	private Context context = null;

	public MetingenObserver(Handler handler) {
		super(handler);
	}
	
	public MetingenObserver(Handler handler, Context context) {
		super(handler);
		
		this.context = context;
	}

	@Override
	public void onChange(boolean selfChange) {
		onChange(selfChange, null);
	}
	
	@Override
	public void onChange(boolean selfChange, Uri changeUri) {
		if ( context != null ) {
			AccountManager accountManager = AccountManager.get(this.context);
			Account[] accounts = accountManager.getAccountsByType(AuthenticatorActivity.ARG_ACCOUNT_TYPE);
			
			for ( Account account : accounts ) {
				ContentResolver.requestSync(account, GewichtProvider.AUTHORITY, null);
			}
		}
	}
}
