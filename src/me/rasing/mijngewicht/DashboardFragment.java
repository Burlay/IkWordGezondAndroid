package me.rasing.mijngewicht;

import java.text.DecimalFormat;

import me.rasing.mijngewicht.models.MeasurementsModel;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class DashboardFragment extends Fragment {

	private WebView webView;

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

		webView = (WebView) rootView.findViewById(R.id.webView1);// disable scroll on touch
		webView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return (event.getAction() == MotionEvent.ACTION_MOVE);
			}
		});
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WebView.setWebContentsDebuggingEnabled(true);
		}
		this.createGraph(webView);

		return rootView;
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void createGraph(WebView webView2) {
		WebSettings settings= webView.getSettings();
		settings.setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new MeasurementsModel(this.getActivity().getApplicationContext()), "MeasurementsModel");

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
	}
	
    // TODO Move database access of the UI thread
    @Override
	public void onResume() {
        DecimalFormat formatter = new DecimalFormat("#.##");
    	
    	MeasurementsModel measurements = new MeasurementsModel(this.getActivity().getApplicationContext());
    	//Context context = this.getActivity().getApplicationContext();
    	
    	String weightUnit = measurements.getWeightUnit();
    	
    	float currentWeight = measurements.getCurrentWeight(weightUnit);
		TextView txtWeight = (TextView) getActivity().findViewById(R.id.fragmentDashboardWeight);
		txtWeight.setText(formatter.format(currentWeight) + " " + weightUnit);

		float weightDifference = measurements.getTotalWeightDifference(weightUnit);
		TextView mTotaal = (TextView) getActivity().findViewById(R.id.fragmentDashboardTotalLostText);
		if (weightDifference <= 0 ) {
			mTotaal.setText(R.string.weight_lost);
		} else if (weightDifference > 0 ) {
			mTotaal.setText(R.string.weight_gained);
		}
		TextView txtTotalLost = (TextView) getActivity().findViewById(R.id.fragmentDashboardTotalLost);
    	txtTotalLost.setText(formatter.format(Math.abs(weightDifference)) + " " + weightUnit);
    	
		super.onResume();
    }
}
