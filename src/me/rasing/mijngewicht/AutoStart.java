package me.rasing.mijngewicht;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("DO AWESOME STUFF", "ON BOOT");
	}

}
