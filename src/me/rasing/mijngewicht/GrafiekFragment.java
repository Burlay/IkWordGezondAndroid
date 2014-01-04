package me.rasing.mijngewicht;

import me.rasing.mijngewicht.providers.GewichtProvider;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class GrafiekFragment extends Fragment {

    private WebView webView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_grafiek, container, false);
    	webView = (WebView) rootView.findViewById(R.id.webview);
    	WebSettings settings= webView.getSettings();
    	settings.setJavaScriptEnabled(true);
    	// settings.setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
    	// settings.setAllowUniversalAccessFromFileURLs(true);
    	
    	webView.setWebChromeClient(new WebChromeClient() {
    		  //public boolean onConsoleMessage(ConsoleMessage cm) {
    		  //  Log.d("MyApplication", cm.message() + " -- From line "
    		  //                       + cm.lineNumber() + " of "
    		  //                       + cm.sourceId() );
    		  //  return true;
    		  //}
    		});
		
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    		webView.loadUrl("file:///android_asset/new_graph.html");
		} else {
			webView.loadUrl("file://localhost/android_asset/new_graph.html");
		}
		
    	
    	
    	// specifies which columns from the database
    	// you will actually use after this query.
    	String[] projection = {
    	    Metingen._ID,
    	    Metingen.COLUMN_NAME_GEWICHT,
    	    Metingen.COLUMN_NAME_DATUM
    	    };

    	ContentResolver mContentResolver = getActivity().getContentResolver();
    	Cursor cursor = mContentResolver.query(
    			GewichtProvider.METINGEN_URI,
    			projection,
    			null,
    			null,
    			Metingen.COLUMN_NAME_DATUM + " DESC"
    			);
    	
    	String data = "var data = [";

    	while (cursor.moveToNext()) {
    		final float weight = cursor.getFloat( cursor.getColumnIndexOrThrow( Metingen.COLUMN_NAME_GEWICHT ) );
    		
    		final String datum = cursor.getString( cursor.getColumnIndexOrThrow( Metingen.COLUMN_NAME_DATUM ) );
    		
    		data += "{" + "'date': '" + datum + "', 'close':'" + weight + "'},";

    		//Log.d( "!!!DATA!!!", datum );
    		
    		//Log.d( "!!!DATA!!!", Float.toString( weight ) );
    	}
    	
    	data = data.replaceAll(",$", "");
    	data += "];";

    	webView.loadUrl("javascript:" + data);
    	return rootView;
    }
}
