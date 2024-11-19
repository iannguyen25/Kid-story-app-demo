package com.g06.kidsstoryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class ChildSettingsFragment extends Fragment {

    private Button logoutButton;

    public ChildSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_settings, container, false);

        logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());

        return view;
    }

    private void logout() {
        ((MainActivity) Objects.requireNonNull(getActivity())).logoutUser();
    }
}