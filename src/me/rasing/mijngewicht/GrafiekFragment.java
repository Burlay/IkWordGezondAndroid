package me.rasing.mijngewicht;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.rasing.mijngewicht.providers.GewichtProvider;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class GrafiekFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_grafiek, container, false);

    	return rootView;
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	this.drawChart();
    }
    
    private void drawChart() {
    	// Define a projection that specifies which columns from the database
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
    			Metingen.COLUMN_NAME_DATUM + " ASC"
    			);

    	TimeSeries series = new TimeSeries("Line1");
    	
    	while (cursor.moveToNext()) {
    		float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(Metingen.COLUMN_NAME_GEWICHT));
    		String datum = cursor.getString(cursor.getColumnIndexOrThrow(Metingen.COLUMN_NAME_DATUM));
    		
			SimpleDateFormat format = 
					new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
			
    		try {
				Date date = format.parse(datum);
				series.add(date, weight);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	XYMultipleSeriesDataset dataset= new XYMultipleSeriesDataset();
    	dataset.addSeries(series);
    	
    	XYSeriesRenderer renderer = new XYSeriesRenderer();
    	renderer.setLineWidth(1);
    	//renderer.setColor(Color.GREEN); // TODO use theming
    	
    	FillOutsideLine outsideFill = new FillOutsideLine(FillOutsideLine.Type.BOUNDS_ALL);
    	//outsideFill.setColor(Color.GREEN);
    	renderer.addFillOutsideLine(outsideFill);
    	
    	XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    	mRenderer.addSeriesRenderer(renderer);

	    mRenderer.setZoomEnabled(false, false);
	    mRenderer.setPanEnabled(false, false);
	    
	    mRenderer.setShowLegend(false);
	    mRenderer.setShowAxes(false);
	    
	    mRenderer.setAntialiasing(true);

	    mRenderer.setAxesColor(Color.GRAY);
	    mRenderer.setXLabelsColor(Color.BLACK);
	    mRenderer.setXLabelsAlign(Align.LEFT);
	    mRenderer.setXLabelsPadding(-35);
	    
	    mRenderer.setYLabelsColor(0, Color.BLACK);
	    mRenderer.setYLabelsAlign(Align.LEFT);
	    mRenderer.setYLabelsPadding(-8);
	    mRenderer.setYLabelsVerticalPadding(8);
	    mRenderer.setLabelsTextSize(20);
	    
	    int[] margins = { 16, -1, -38, 0 };
	    mRenderer.setMargins(margins);
	    
	    mRenderer.setShowGrid(true);
	    
    	mRenderer.setApplyBackgroundColor(true);
    	mRenderer.setBackgroundColor(Color.WHITE); // TODO use color from theme
    	mRenderer.setMarginsColor(Color.WHITE); // TODO use color from theme
    	
	    GraphicalView chartView = ChartFactory.getTimeChartView(getActivity(), dataset, mRenderer, "dd MMM");

	    LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.dashboard_chart_layout);	    
	    //layout.removeAllViews();
	    layout.addView(chartView, new LayoutParams(LayoutParams.MATCH_PARENT,
	              LayoutParams.MATCH_PARENT));
    }
}
