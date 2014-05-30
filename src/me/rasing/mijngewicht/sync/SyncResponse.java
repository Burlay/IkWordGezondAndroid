package me.rasing.mijngewicht.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import android.util.Log;

public class SyncResponse {

	private HttpURLConnection httpUrlConnection;

	public SyncResponse(HttpURLConnection httpUrlConnection) {
		this.httpUrlConnection = httpUrlConnection;
	}
	
	public int getResponseCode() {

		int responseCode = -1;
		
        try {
			responseCode = this.httpUrlConnection.getResponseCode();
		} catch (IOException e) {
			Log.d("!!!DATA!!!", ">>> IOException: " + e.getMessage());
		}
        
        return responseCode;
	}

	public String getResponse() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(this.httpUrlConnection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line+"\n");
			}
			br.close();

			return sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}

}
