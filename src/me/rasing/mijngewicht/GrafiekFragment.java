package me.rasing.mijngewicht;

import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    	int[] y = { 30, 34, 35, 57, 77, 89, 100, 111, 123, 145 };
    	
    	long value = new Date().getTime() - 3 * TimeChart.DAY;
    	TimeSeries series = new TimeSeries("Line1");
    	for (int i = 0; i < y.length; i++) {
    		series.add(new Date(value + i * TimeChart.DAY / 4), y[i]);
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
	    mRenderer.setXLabelsPadding(-35);
	    
	    mRenderer.setYLabelsColor(0, Color.BLACK);
	    mRenderer.setYLabelsAlign(Align.LEFT);
	    mRenderer.setLabelsTextSize(20);
	    
	    int[] margins = { 0, 0, -38, 0 };
	    mRenderer.setMargins(margins);
	    
	    mRenderer.setShowGrid(true);
	    
    	mRenderer.setApplyBackgroundColor(true);
    	mRenderer.setBackgroundColor(Color.WHITE); // TODO use color from theme
    	mRenderer.setMarginsColor(Color.WHITE); // TODO use color from theme
    	
	    GraphicalView chartView;
		/*if (chartView != null) {
			chartView.repaint();
		}
		else { */
	    	//Log.d("datum", new Date(now - (HOURS - j) * HOUR).toString());
			//chartView = ChartFactory.getLineChartView(getActivity(), dataset, mRenderer);
			chartView = ChartFactory.getTimeChartView(getActivity(), dataset, mRenderer, "H:mm:ss");
		//}


	    LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.dashboard_chart_layout);	    
	    //layout.removeAllViews();
	    layout.addView(chartView, new LayoutParams(960,
	              LayoutParams.MATCH_PARENT));
    }
}
