package me.rasing.mijngewicht.fragments;

import java.text.DecimalFormat;

import me.rasing.mijngewicht.R;
import me.rasing.mijngewicht.models.MeasurementsModel;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
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

	@SuppressLint("NewApi")
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

    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
    	float length = sharedPref.getFloat("length", 0) / 100;
    	float bmi = measurements.getBMI(length);
		TextView txtBmi = (TextView) getActivity().findViewById(R.id.bmi);
		txtBmi.setText(formatter.format(bmi));
		Log.i("AAAAAAAAAAAAH", ">>> onResume <<<");
    	
		super.onResume();
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

	@Override
	public void onInflate(Activity activity, AttributeSet attrs,
			Bundle savedInstanceState) {
		Log.i("AAAAAAAAAAAAH", ">>> onInflate <<<");
		super.onInflate(activity, attrs, savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		Log.i("AAAAAAAAAAAAH", ">>> onAttach <<<");
		super.onAttach(activity);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("AAAAAAAAAAAAH", ">>> onViewCreated <<<");
		super.onViewCreated(view, savedInstanceState);
	}

//	@Override
//	public void onViewStateRestored(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		Log.i("AAAAAAAAAAAAH", ">>> onViewStateRestored <<<");
//		super.onViewStateRestored(savedInstanceState);
//	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.i("AAAAAAAAAAAAH", ">>> onStart <<<");
		super.onStart();
	}
}
