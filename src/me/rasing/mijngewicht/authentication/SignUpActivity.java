package me.rasing.mijngewicht.authentication;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import me.rasing.mijngewicht.R;
import me.rasing.mijngewicht.sync.HttpConnector;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;

public class SignUpActivity extends Activity {
    //private static final String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE"; // TODO Constant also defined in AuthenticatorActivity.java
	//protected static final String PARAM_USER_PASS = "USER_PASS"; // TODO Constant also defined in AuthenticatorActivity.java
	protected static final String KEY_ERROR_MESSAGE = "3";
	private String mAccountType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d("!!!DATA!!!", ">>> SignupActivity.onCreate");

        mAccountType = getIntent().getStringExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE);
        
        Log.d("!!!DATA!!!", ">>> SignUpActivity.Signup.mAccountType " + mAccountType);

        setContentView(R.layout.act_register);

        findViewById(R.id.alreadyMember).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
    	Log.d("!!!DATA!!!", ">>> createAccount");
        // Validation!

        new AsyncTask<String, Void, Intent>() {

            String name = ((TextView) findViewById(R.id.name)).getText().toString().trim();
            String accountName = ((TextView) findViewById(R.id.accountName)).getText().toString().trim();
            String accountPassword = ((TextView) findViewById(R.id.accountPassword)).getText().toString().trim();

            @Override
            protected Intent doInBackground(String... params) {
            	Log.d("!!!DATA!!!", ">>> doInBackground");
            	String postDataBuilder = "{\"username\":\"" + name + "\", \"password\":\"" + accountPassword + "\"}";
            	byte[] postData = null;
                postData = postDataBuilder.toString().getBytes();

				HttpConnector connector = new HttpConnector(getApplicationContext());
				
				try {
    				// Load CAs from an InputStream
    				// (could be from a resource or ByteArrayInputStream or ...)
    				CertificateFactory cf = CertificateFactory.getInstance("X.509");
    				AssetManager assets = getApplicationContext().getAssets();
    				// From https://www.washington.edu/itconnect/security/ca/load-der.crt
    				InputStream caInput = new BufferedInputStream(assets.open("IkWordGezondApp.cer"));
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
    				
					//URL url = new URL("http://192.168.2.1:8080/users");
					URL url = connector.getURL("users");
	            	HttpsURLConnection httpUrlConnection = (HttpsURLConnection) url.openConnection();
			    	httpUrlConnection.setSSLSocketFactory(context.getSocketFactory());
	            	httpUrlConnection.setRequestMethod("POST");
	            	httpUrlConnection.setRequestProperty("User-Agent", "MijnGewichtAgentAndroid");
	            	httpUrlConnection.setRequestProperty("Content-Type", "application/json");
	            	httpUrlConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));
	            	httpUrlConnection.setUseCaches(false);
	            	
	            	OutputStream out = httpUrlConnection.getOutputStream();
	                out.write(postData);
	                out.close();

	                int responseCode = httpUrlConnection.getResponseCode();
	                Log.d("!!!DATA!!!", ">>> " + responseCode);
				} catch (MalformedURLException e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String exceptionDetails = sw.toString();
					
					Log.d("Sign Up", exceptionDetails);
				} catch (IOException e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String exceptionDetails = sw.toString();
					
					Log.d("Sign Up", exceptionDetails);
				} catch (CertificateException e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String exceptionDetails = sw.toString();
					
					Log.d("Sign Up", exceptionDetails);
				} catch (KeyStoreException e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String exceptionDetails = sw.toString();
					
					Log.d("Sign Up", exceptionDetails);
				} catch (NoSuchAlgorithmException e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String exceptionDetails = sw.toString();
					
					Log.d("Sign Up", exceptionDetails);
				} catch (KeyManagementException e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String exceptionDetails = sw.toString();
					
					Log.d("Sign Up", exceptionDetails);
				}

				String authtoken = null;
				Bundle data = new Bundle();
				final Intent res = new Intent();
				postDataBuilder = "{\"username\":\"" + name
						+ "\", \"password\":\"" + accountPassword + "\"}";
				postData = null;
				postData = postDataBuilder.toString().getBytes();

				try {
    				// Load CAs from an InputStream
    				// (could be from a resource or ByteArrayInputStream or ...)
    				CertificateFactory cf = CertificateFactory.getInstance("X.509");
    				AssetManager assets = getApplicationContext().getAssets();
    				// From https://www.washington.edu/itconnect/security/ca/load-der.crt
    				InputStream caInput = new BufferedInputStream(assets.open("IkWordGezondApp.cer"));
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
    				
					//URL url = new URL("http://192.168.2.1:8080/session");
    				URL url = connector.getURL("sessions");
					HttpsURLConnection httpUrlConnection = (HttpsURLConnection) url
							.openConnection();
			    	httpUrlConnection.setSSLSocketFactory(context.getSocketFactory());
					httpUrlConnection.setRequestMethod("POST");
					httpUrlConnection.setRequestProperty("User-Agent",
							"MijnGewichtAgentAndroid");
					httpUrlConnection.setRequestProperty("Content-Type",
							"application/json");
					httpUrlConnection.setRequestProperty("Content-Length",
							Integer.toString(postData.length));
					httpUrlConnection.setRequestProperty("Accept", "*/*");
					httpUrlConnection.setRequestProperty("Accept-Language",
							"nl-NL,nl;q=0.8,en-US;q=0.6,en;q=0.4");
					httpUrlConnection.setUseCaches(false);

					OutputStream out = httpUrlConnection.getOutputStream();
					out.write(postData);
					out.close();

					int responseCode = httpUrlConnection.getResponseCode();
	                Log.d("!!!DATA!!!", ">>> " + responseCode);

					List<String> cookies = httpUrlConnection.getHeaderFields().get("set-cookie");
					for (String cookie: cookies) {
						Log.d("!!!DATA!!!", ">>> " + cookie);
						authtoken = cookie.split("; *")[0].split("=")[1];
						Log.d("!!!DATA!!!", ">>> " + authtoken);
					}

					data.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
					data.putString(AccountManager.KEY_ACCOUNT_TYPE,
							mAccountType);
					data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
					data.putString(AuthenticatorActivity.PARAM_USER_PASS, accountPassword);
				} catch (Exception e) {
					data.putString(KEY_ERROR_MESSAGE, e.getMessage());
				}

				res.putExtras(data);
				return res;
			}

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
