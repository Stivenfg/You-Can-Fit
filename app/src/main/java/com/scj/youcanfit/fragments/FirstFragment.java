// FirstFragment.java
package com.scj.youcanfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.scj.youcanfit.R;

public class FirstFragment extends Fragment {

    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);

        // Find the TextView in the layout
        TextView btnSwitchFragment = rootView.findViewById(R.id.btnSwitchFragment);

        // Create FragmentSwitcher instance and attach the switcher to the TextView
        btnSwitchFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the "ejercicis" fragment
                getFragmentManager().beginTransaction()
                        .replace(R.id.secondFragment, new ejercicis()) // Use the appropriate ID for your fragment container
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }
}
