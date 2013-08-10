package me.rasing.mijngewicht;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYValueSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

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
//	    XYMultipleSeriesDataset series = new XYMultipleSeriesDataset();
//	    XYValueSeries newTicketSeries = new XYValueSeries("New Tickets");
//	    newTicketSeries.add(1, 2, 14);
//	    newTicketSeries.add(2, 2, 12);
//	    newTicketSeries.add(3, 2, 18);
//	    newTicketSeries.add(4, 2, 5);
//	    newTicketSeries.add(5, 2, 1);
//	    series.addSeries(newTicketSeries);
//	    XYValueSeries fixedTicketSeries = new XYValueSeries("Fixed Tickets");
//	    fixedTicketSeries.add(1, 1, 7);
//	    fixedTicketSeries.add(2, 1, 4);
//	    fixedTicketSeries.add(3, 1, 18);
//	    fixedTicketSeries.add(4, 1, 3);
//	    fixedTicketSeries.add(5, 1, 1);
//	    series.addSeries(fixedTicketSeries);
//
//	    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
//	    renderer.setAxisTitleTextSize(16);
//	    renderer.setChartTitleTextSize(20);
//	    renderer.setLabelsTextSize(15);
//	    renderer.setRange(new double[]{0, 6, 0, 6});
//
//	    //renderer.setMargins(new int[] { 20, 30, 15, 0 });
//	    XYSeriesRenderer newTicketRenderer = new XYSeriesRenderer();
//	    newTicketRenderer.setColor(Color.BLUE);
//	    renderer.addSeriesRenderer(newTicketRenderer);
//	    XYSeriesRenderer fixedTicketRenderer = new XYSeriesRenderer();
//	    fixedTicketRenderer.setColor(Color.GREEN);
//	    renderer.addSeriesRenderer(fixedTicketRenderer);
//
//	    renderer.setXLabels(0);
//	    renderer.setYLabels(0);
//	    renderer.setDisplayChartValues(false);
//	    renderer.setShowGrid(false);
//	    renderer.setShowLegend(false);
//	    renderer.setShowLabels(true);


    	int[] x= { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
    	int[] y = { 30, 34, 35, 57, 77, 89, 100, 111, 123, 145 };
    	
    	TimeSeries series = new TimeSeries("Line1");
    	for (int i = 0; i < x.length; i++) {
    		series.add(x[i], y[i]);
    	}
    	
    	XYMultipleSeriesDataset dataset= new XYMultipleSeriesDataset();
    	dataset.addSeries(series);
    	
    	XYSeriesRenderer renderer = new XYSeriesRenderer();
    	renderer.setLineWidth(1);
    	renderer.setColor(Color.GREEN); // TODO use theming
    	renderer.setFillBelowLine(true);
    	renderer.setFillBelowLineColor(Color.GREEN); // TODO use theming
    	//renderer.setDisplayChartValues(true);
    	//renderer.setChartValuesTextSize(25);
    	//renderer.setChartValuesSpacing(25);
    	
    	XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    	mRenderer.addSeriesRenderer(renderer);

	    mRenderer.setZoomEnabled(false, false);
	    mRenderer.setPanEnabled(false, false);
	    
	    mRenderer.setShowLegend(false);
	    mRenderer.setShowAxes(false);
	    //mRenderer.setShowLabels(false);
	    
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
			chartView = ChartFactory.getLineChartView(getActivity(), dataset, mRenderer);
		//}


	    LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.dashboard_chart_layout);	    
	    //layout.removeAllViews();
	    layout.addView(chartView, new LayoutParams(960,
	              LayoutParams.FILL_PARENT));
    }
}
