package me.rasing.mijngewicht;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.rasing.mijngewicht.providers.GewichtProvider;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.ConsoleMessage;
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
    	
    	webView.setWebChromeClient(new WebChromeClient() {
    		  public boolean onConsoleMessage(ConsoleMessage cm) {
    		    Log.d("MyApplication", cm.message() + " -- From line "
    		                         + cm.lineNumber() + " of "
    		                         + cm.sourceId() );
    		    return true;
    		  }
    		});
		
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    		webView.loadUrl("file:///android_asset/new_graph.html");
		} else {
			webView.loadUrl("file://localhost/android_asset/new_graph.html");
		}

    	return rootView;
    }

    // TODO Move database access of the UI thread
    @Override
	public void onResume() {
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

    		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
    		DateFormat df2 = new SimpleDateFormat("yyy-MM-dd HH:mm", Locale.getDefault());
    		
    		String datumString = cursor.getString( cursor.getColumnIndexOrThrow( Metingen.COLUMN_NAME_DATUM ) );
    		
    		Date date = null;
			try {
				date = df1.parse(datumString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		data += "{" + "'date': '" + df2.format(date) + "', 'close':'" + weight + "'},";
    	}
    	
    	data = data.replaceAll(",$", "");
    	data += "];";

    	webView.loadUrl("javascript:" + data);
    	
    	webView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
    		@Override
            public void onGlobalLayout() {
                webView.loadUrl("javascript:if(typeof drawGraph === \"undefined\") {" +
                                    "if (document.addEventListener) { window.addEventListener('load', function(){drawGraph()}, false); }" +
                		        "} else { " +
                                    "drawGraph();" +
                		        " }");
            }
    	});

		super.onResume();
    }

	@Override
	public void onPause() {
		webView.loadUrl("javascript:d3.select('#grafiek').remove();");
		super.onPause();
	}
}
