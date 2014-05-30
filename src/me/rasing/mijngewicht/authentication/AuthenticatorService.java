package me.rasing.mijngewicht.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {

	public IBinder onBind(Intent intent) {
		Authenticator authenticator = new Authenticator(this);
		return authenticator.getIBinder();
	}
}
