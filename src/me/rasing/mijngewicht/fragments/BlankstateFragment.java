package me.rasing.mijngewicht.fragments;

import me.rasing.mijngewicht.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class BlankstateFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	//setHasOptionsMenu(true);
    	
    	View rootView = inflater.inflate(R.layout.fragment_blank_state, container, false);
    	
    	return rootView;
    }
}
