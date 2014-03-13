package me.rasing.mijngewicht.authentication;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import me.rasing.mijngewicht.R;
import me.rasing.mijngewicht.sync.HttpConnector;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    public final static String ARG_ACCOUNT_TYPE = "me.rasing.mijngewicht.auth";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private final int REQ_SIGNUP = 1;

    private AccountManager mAccountManager;
    private String mAuthTokenType;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("!!!DATA!!!", "onCreate");
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        mAccountManager = AccountManager.get(getBaseContext());

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

        if (accountName != null) {
        	TextView txtAccountName = (TextView) findViewById(R.id.accountName);
            txtAccountName.setText(accountName);
        }

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                Intent signup = new Intent(getBaseContext(), SignUpActivity.class);
                signup.putExtras(getIntent().getExtras());
                startActivityForResult(signup, REQ_SIGNUP);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.d("!!!DATA!!!", "AuthenticatorActivity.onActivityResult");

        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
        	Log.d("!!!DATA!!!", data.toString());
            finishLogin(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void submit() {

        //final String userName = ((TextView) findViewById(R.id.accountName)).getText().toString();
        //final String userPass = ((TextView) findViewById(R.id.accountPassword)).getText().toString();

        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        Log.d("!!!DATA!!!", ">>> accountType: " + accountType);

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {
                String accountName = ((TextView) findViewById(R.id.accountName)).getText().toString().trim();
                String accountPassword = ((TextView) findViewById(R.id.accountPassword)).getText().toString().trim();
                
                String authtoken = null;
                Bundle data = new Bundle();
                try {
    				String postDataBuilder = "{\"username\":\"" + accountName
    						+ "\", \"password\":\"" + accountPassword + "\"}";
    				byte[] postData = null;
    				postData = postDataBuilder.toString().getBytes();
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

    				HttpConnector connector = new HttpConnector(getApplicationContext());
    				
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
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                    data.putString(PARAM_USER_PASS, accountPassword);

                } catch (Exception e) {
                	Log.d("!!!DATA!!!", KEY_ERROR_MESSAGE + e.getMessage());
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    finishLogin(intent);
                }
            }
        }.execute();
    }

    private void finishLogin(Intent intent) {
    	Log.d("!!!DATA!!!", "AuthenticatorActivity.finishLogin");
    	
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
    	Log.d("!!!DATA!!!", ">>> accountName: " + accountName);
    	Log.d("!!!DATA!!!", ">>> accountPassword: " + accountPassword);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

    	Log.d("!!!DATA!!!", ">>> ARG_IS_ADDING_NEW_ACCOUNT: " + getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false));
        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }


}
