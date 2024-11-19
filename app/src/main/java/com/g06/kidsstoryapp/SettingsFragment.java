package com.g06.kidsstoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private Button logoutButton;
    private Button adjustTimeLimitButton;
    private Button parentDashboardButton;
    private FirebaseAuth mAuth;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        logoutButton = view.findViewById(R.id.logoutButton);
        adjustTimeLimitButton = view.findViewById(R.id.adjustTimeLimitButton);
        parentDashboardButton = view.findViewById(R.id.parentDashboardButton);

        logoutButton.setOnClickListener(v -> logout());
        adjustTimeLimitButton.setOnClickListener(v -> showAdjustTimeLimitDialog());
        parentDashboardButton.setOnClickListener(v -> openParentDashboard());
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showAdjustTimeLimitDialog() {
        AdjustTimeLimitDialogFragment dialogFragment = new AdjustTimeLimitDialogFragment();
        dialogFragment.show(getChildFragmentManager(), "AdjustTimeLimit");
    }

    private void openParentDashboard() {
        Intent intent = new Intent(getActivity(), ParentDashboardActivity.class);
        startActivity(intent);
    }
}