package me.rasing.ikwordgezond;

import java.util.Calendar;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NieuwemeetingFragment extends Fragment{
	String DATE_SEP = "-";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nieuwemeeting, container, false);
        
        // Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
        
        TextView editDate = (TextView) rootView.findViewById(R.id.editDate);
        editDate.setText(Integer.toString(year) + DATE_SEP + String.format("%02d", month) + DATE_SEP + String.format("%02d", day));
        getActivity().setTitle("Nieuwe meeting");
        
        return rootView;
    }
}
