package com.g06.kidsstoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

public class ChildSettingsFragment extends Fragment {

    public ChildSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_settings, container, false);

        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());

        return view;
    }

    private void logout() {
        Intent intent = new Intent(requireActivity(), AccountSelectionActivity.class);
        startActivity(intent);
    }
}