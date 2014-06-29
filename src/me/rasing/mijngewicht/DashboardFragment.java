package me.rasing.mijngewicht;

import java.text.DecimalFormat;

import me.rasing.mijngewicht.models.MeasurementsModel;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
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

public class DashboardFragment extends Fragment implements MeasurementsModel.Callback {

	private WebView webView;
	private TextView txtWeight;
	private TextView mTotaal;
	private TextView txtTotalLost;
	private MeasurementsModel measurements;

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
		
		txtWeight = (TextView) rootView.findViewById(R.id.fragmentDashboardWeight);
		mTotaal = (TextView) rootView.findViewById(R.id.fragmentDashboardTotalLostText);
		txtTotalLost = (TextView) rootView.findViewById(R.id.fragmentDashboardTotalLost);
		
		measurements = new MeasurementsModel(this.getActivity());
		measurements.registerCallback(this);

		return rootView;
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void createGraph() {
		DbHelper mDbHelper = new DbHelper(this.getActivity());
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		String[] projection = {
				"COUNT(*)",
				};

		Cursor cursor = db.query(Metingen.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null,
				null);
		
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		db.close();
		
		if (count == 1) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				webView.loadUrl("file:///android_asset/empty_graph.html");
			} else {
				webView.loadUrl("file://localhost/android_asset/empty_graph.html");
			}
		} else {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				webView.loadUrl("file:///android_asset/new_graph.html");
			} else {
				webView.loadUrl("file://localhost/android_asset/new_graph.html");
			}
		}
	}

	@Override
	public void MeasurementsModelCallback() {
		this.onDataChanged();
	}

	private void onDataChanged() {
		DecimalFormat formatter = new DecimalFormat("#.##");
		String weightUnit = "kg";
		
		Float currentWeight = measurements.getCurrentWeight(weightUnit);
		if (currentWeight != null)
			txtWeight.setText(formatter.format(currentWeight) + " " + weightUnit);
		
		Float weightDifference = measurements.getTotalWeightDifference(weightUnit);
		if (weightDifference != null) {
			if (weightDifference <= 0 ) {
				mTotaal.setText(R.string.weight_lost);
			} else if (weightDifference > 0 ) {
				mTotaal.setText(R.string.weight_gained);
			}
			txtTotalLost.setText(formatter.format(Math.abs(weightDifference)) + " " + weightUnit);
		}
		createGraph();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, measurements);
	}
}
