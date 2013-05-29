package me.rasing.ikwordgezond;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NieuwemeetingFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nieuwemeeting, container, false);
        getActivity().setTitle("Nieuwe meeting");
        return rootView;
    }
}
