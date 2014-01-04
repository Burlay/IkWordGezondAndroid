package me.rasing.mijngewicht;

import java.text.NumberFormat;

import me.rasing.mijngewicht.providers.GewichtProvider;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfielFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_profiel, container, false);

    	return rootView;
    }
    
    // TODO Move database access of the UI thread
    @Override
	public void onResume() {
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
    			Metingen.COLUMN_NAME_DATUM + " DESC"
    			);

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

    		TextView txtTotalLost = (TextView) getActivity().findViewById(R.id.fragmentProfielTotal);
    		txtTotalLost.setText(NumberFormat.getInstance().format(totalLost) + " kg");

    		TextView mTotaal = (TextView) getActivity().findViewById(R.id.fragmentProfielTotaalText);
    		if (difference < 0 ) {
    			mTotaal.setText(R.string.totaal_tekst);
    		} else if (difference > 0 ) {
    			mTotaal.setText(R.string.totaal_tekst_aangekomen);
    		} else {
    			mTotaal.setText(R.string.totaal_tekst_geen_verschil);
    		}
    		
    		// Display the difference between the 2 most recent weights.

    		TextView txtDifference = (TextView) getActivity().findViewById(R.id.fragmentProfielDifference);
    		txtDifference.setText(NumberFormat.getInstance().format(difference) + " kg");
    		
    		TextView mVorigeWeging = (TextView) getActivity().findViewById(R.id.fragmentProfielPreviousDate);
    		if (difference < 0 ) {
    			mVorigeWeging.setText(R.string.sinds_vorige_afgevallen);
    		} else if (difference > 0 ) {
    			mVorigeWeging.setText(R.string.sinds_vorige_aangekomen);
    		} else {
    			mVorigeWeging.setText(R.string.sinds_vorige_geen_verschil);
    		}

    		// Display the current weight.
    		TextView txtWeight = (TextView) getActivity().findViewById(R.id.gewicht);
    		txtWeight.setText(NumberFormat.getInstance().format(weight) + " kg");
    	}
    	
		super.onResume();
	}
}
