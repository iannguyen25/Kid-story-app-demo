package com.g06.kidsstoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ParentSettingsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public ParentSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button logoutButton = view.findViewById(R.id.logoutButton);
        Button adjustTimeLimitButton = view.findViewById(R.id.adjustTimeLimitButton);
        Button parentDashboardButton = view.findViewById(R.id.parentDashboardButton);
        Button changeAccountButton = view.findViewById(R.id.AccountChange);

        logoutButton.setOnClickListener(v -> logout());
        adjustTimeLimitButton.setOnClickListener(v -> showAdjustTimeLimitDialog());
        parentDashboardButton.setOnClickListener(v -> openParentDashboard());
        changeAccountButton.setOnClickListener(v -> showChangeDialog());

        return view;
    }

    private void showChangeDialog() {
        Intent intent = new Intent(getActivity(), AccountSelectionActivity.class);
        startActivity(intent);

    }

    private void logout() {
        ((MainActivity) requireActivity()).logoutUser();
    }

    private void showAdjustTimeLimitDialog() {
        String parentId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        db.collection("children")
                .whereEqualTo("parentId", parentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() == 1) {
                        // Only one child, show Adjust Time Limit Fragment directly
                        String childId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        AdjustTimeLimitDialogFragment dialogFragment = AdjustTimeLimitDialogFragment.newInstance(childId);
                        dialogFragment.show(getChildFragmentManager(), "AdjustTimeLimit");
                    } else if (queryDocumentSnapshots.size() > 1) {
                        // Multiple children, show dialog to select child
                        SelectChildDialogFragment dialogFragment = new SelectChildDialogFragment();
                        dialogFragment.show(getChildFragmentManager(), "SelectChild");
                    }
                });
    }

    private void openParentDashboard() {
        Intent intent = new Intent(getActivity(), ParentDashboardActivity.class);
        startActivity(intent);
    }
}