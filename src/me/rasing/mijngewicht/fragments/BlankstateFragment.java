package me.rasing.mijngewicht.fragments;

import me.rasing.mijngewicht.R;
import me.rasing.mijngewicht.R.layout;
import android.os.Bundle;
import android.app.Fragment;
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
