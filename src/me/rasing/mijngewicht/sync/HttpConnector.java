package me.rasing.mijngewicht.sync;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import me.rasing.mijngewicht.DbHelper;
import me.rasing.mijngewicht.Metingen;
import me.rasing.mijngewicht.authentication.AccountGeneral;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountsException;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HttpConnector {
	private final Context context;

	public HttpConnector(Context context) {
		this.context = context;
	}

	public SyncResponse getMeasurements(Account account)
			throws AccountsException, IOException, GeneralSecurityException {
		AccountManager accountManager = AccountManager.get(this.context);
		String authToken = accountManager.blockingGetAuthToken(account,
				AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, true);

		URL url = this.getURL("measurements");

		if (url != null) {
			// Load CAs from an InputStream
			// (could be from a resource or ByteArrayInputStream or ...)
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			AssetManager assets = this.context.getAssets();
			// From
			// https://www.washington.edu/itconnect/security/ca/load-der.crt
			InputStream caInput = new BufferedInputStream(
					assets.open("IkWordGezondApp.cer"));
			Certificate ca;

			try {
				ca = cf.generateCertificate(caInput);
				System.out.println("ca="
						+ ((X509Certificate) ca).getSubjectDN());
			} finally {
				caInput.close();
			}

			// Create a KeyStore containing our trusted CAs
			String keyStoreType = KeyStore.getDefaultType();
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);

			// Create a TrustManager that trusts the CAs in our KeyStore
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance(tmfAlgorithm);
			tmf.init(keyStore);

			// Create an SSLContext that uses our TrustManager
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, tmf.getTrustManagers(), null);

			HttpsURLConnection httpUrlConnection = (HttpsURLConnection) url
					.openConnection();
			httpUrlConnection.setSSLSocketFactory(context.getSocketFactory());
			httpUrlConnection.setRequestProperty("Cookie", "session="
					+ authToken);
			httpUrlConnection.setRequestMethod("GET");
			httpUrlConnection.setRequestProperty("User-Agent",
					"MijnGewichtAgentAndroid");
			httpUrlConnection.setUseCaches(true);

			return new SyncResponse(httpUrlConnection);
		}

		return null;
	}

	public URL getURL(String path) throws IOException {
		Lookup lookup = new Lookup("_sync._tcp.ikwordgezond.nl", Type.SRV);

		SimpleResolver resolver = new SimpleResolver();
		resolver.setTimeout(10);
		lookup.setResolver(resolver);

		Record[] records = lookup.run();

		if (records != null) {
			SRVRecord record = (SRVRecord) records[0];

			String hostname = record.getTarget().toString()
					.replaceFirst("\\.$", "");
			int port = record.getPort();

			return new URL("https://" + hostname + ":" + port + "/" + path);
			// for (Record record : records) {
			// SRVRecord srv = (SRVRecord) record;
			//
			// String hostname = srv.getTarget().toString()
			// .replaceFirst("\\.$", "");
			// int port = srv.getPort();
			//
			// Log.d("!!!DATA!!!", hostname + ":" + port);
			// }
		} else {
			Log.d("!!!DATA!!!", "DNS Error: " + lookup.getErrorString());
		}

		return null;
	}

	public SyncResponse pushMeasurements(Account account)
			throws AccountsException, IOException, GeneralSecurityException {
		SyncResponse response = null;

		/*
		 * Push new data to server.
		 */
		DbHelper mDbHelper = new DbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = { Metingen._ID, Metingen.COLUMN_NAME_GEWICHT,
				Metingen.COLUMN_NAME_DATUM, Metingen.COLUMN_NAME_GUID };

		Cursor cursor = db.query(Metingen.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null, null);
		
		String body = "[";

		while (cursor.moveToNext()) {

			Float weight = cursor.getFloat(cursor
					.getColumnIndex(Metingen.COLUMN_NAME_GEWICHT));
			String date = cursor.getString(cursor
					.getColumnIndex(Metingen.COLUMN_NAME_DATUM));
			String guid = cursor.getString(cursor
					.getColumnIndex(Metingen.COLUMN_NAME_GUID));

			body += "{\"guid\":\"" + guid + "\", \"weight\":"
					+ weight + ", \"date_taken\":\"" + date + "\"}";
			
			body += (cursor.isLast()) ? "]" : ",";

		}

		db.close();
		
		Log.i("BODY", body);
		
		URL url = this.getURL("measurements");

		HttpsURLConnection conn = getConnection(url, "POST", account,
				body);
		
		OutputStream out = conn.getOutputStream();
		out.write(body.getBytes());
		out.close();

		int responseCode = conn.getResponseCode();

		//if (responseCode == 201 || responseCode == 200) {
		//	DateFormat df = new SimpleDateFormat("yyy-MM-dd'T'HH:mmZ",
		//			Locale.getDefault());
		//	df.setTimeZone(TimeZone.getTimeZone("UTC"));

		//	ContentValues values = new ContentValues();
		//	values.put(Metingen.COLUMN_NAME_LAST_SYNCED,
		//			df.format(new Date()));

		//	String[] whereArgs = { guid };

		//	db.update(Metingen.TABLE_NAME, values,
		//			Metingen.COLUMN_NAME_GUID + " = ?", whereArgs);
		//}

		response = new SyncResponse(conn);

		Log.d("!!!DATA!!!", ">>> " + responseCode);
		
		return response;
	}

	private HttpsURLConnection getConnection(URL url, String string,
			Account account, String body) throws OperationCanceledException,
			AuthenticatorException, IOException, CertificateException,
			KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		

		byte[] bodyBytes = null;
		bodyBytes = body.getBytes();
		
		AccountManager accountManager = AccountManager.get(this.context);

		String authToken = accountManager.blockingGetAuthToken(account,
				AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, true);

		// Load CAs from an InputStream
		// (could be from a resource or ByteArrayInputStream or ...)
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		AssetManager assets = this.context.getAssets();
		// From https://www.washington.edu/itconnect/security/ca/load-der.crt
		InputStream caInput = new BufferedInputStream(
				assets.open("IkWordGezondApp.cer"));
		Certificate ca;

		try {
			ca = cf.generateCertificate(caInput);
			System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
		} finally {
			caInput.close();
		}

		// Create a KeyStore containing our trusted CAs
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(null, null);
		keyStore.setCertificateEntry("ca", ca);

		// Create a TrustManager that trusts the CAs in our KeyStore
		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
		tmf.init(keyStore);

		// Create an SSLContext that uses our TrustManager
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, tmf.getTrustManagers(), null);

		HttpsURLConnection httpUrlConnection = (HttpsURLConnection) url
				.openConnection();
		httpUrlConnection.setSSLSocketFactory(context.getSocketFactory());
		httpUrlConnection.setRequestMethod("POST");
		httpUrlConnection.setRequestProperty("User-Agent",
				"MijnGewichtAgentAndroid");
		httpUrlConnection
				.setRequestProperty("Content-Type", "application/json");
		httpUrlConnection.setRequestProperty("Content-Length",
				Integer.toString(bodyBytes.length));
		httpUrlConnection.setRequestProperty("Cookie", "session=" + authToken);
		httpUrlConnection.setUseCaches(false);

		return httpUrlConnection;
	}

}
