package me.rasing.mijngewicht;

import java.text.NumberFormat;

import me.rasing.mijngewicht.providers.GewichtProvider;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class DashboardFragment extends Fragment {

	private WebView webView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
		
		webView = (WebView) rootView.findViewById(R.id.webView1);
		WebSettings settings= webView.getSettings();
		settings.setJavaScriptEnabled(true);
		// settings.setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
		// settings.setAllowUniversalAccessFromFileURLs(true);

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
    		
    		final String datum = cursor.getString( cursor.getColumnIndexOrThrow( Metingen.COLUMN_NAME_DATUM ) );
    		
    		data += "{" + "'date': '" + datum + "', 'close':'" + weight + "'},";
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

    	if ( cursor.getCount() > 0 ) {
    		cursor.moveToFirst();

    		float weight = cursor.getFloat( cursor.getColumnIndexOrThrow( Metingen.COLUMN_NAME_GEWICHT ) );

    		float difference; 
    		if ( cursor.getCount() == 1 ) {
    			difference = 0;
    		} else {
    			// Save the difference between the 2 most recent weights.
    			cursor.moveToNext();
    			difference = weight - cursor.getFloat(cursor.getColumnIndex(Metingen.COLUMN_NAME_GEWICHT));
    		}

    		cursor.moveToLast();
    		float startWeight = cursor.getFloat(
    				cursor.getColumnIndexOrThrow(Metingen.COLUMN_NAME_GEWICHT)
    				);

    		cursor.close();

    		// Calculate the total weight lost or gained and display it.
    		float totalLost = weight - startWeight;

    		TextView txtTotalLost = (TextView) getActivity().findViewById(R.id.fragmentDashboardTotalLost);
    		txtTotalLost.setText(NumberFormat.getInstance().format(Math.abs(totalLost)) + " kg");

    		TextView mTotaal = (TextView) getActivity().findViewById(R.id.fragmentDashboardTotalLostText);
    		if (difference <= 0 ) {
    			mTotaal.setText(R.string.totaal_tekst);
    		} else if (difference > 0 ) {
    			mTotaal.setText(R.string.totaal_tekst_aangekomen);
    		}

    		// Display the current weight.
    		TextView txtWeight = (TextView) getActivity().findViewById(R.id.fragmentDashboardWeight);
    		txtWeight.setText(NumberFormat.getInstance().format(weight) + " kg");
    	}

		super.onResume();
    }

	@Override
	public void onPause() {
		webView.loadUrl("javascript:d3.select('#grafiek').remove();");
		super.onPause();
	}
}
