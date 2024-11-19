package com.g06.kidsstoryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private TextView welcomeTextView;
    private TextView timeLimitTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        welcomeTextView = view.findViewById(R.id.welcomeTextView);
        timeLimitTextView = view.findViewById(R.id.timeLimitTextView);

        loadUserData();
    }

    private void loadUserData() {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        db.collection("parents").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String email = document.getString("email");
                            welcomeTextView.setText("Welcome, " + email);
                            timeLimitTextView.setVisibility(View.GONE);
                        }
                    }
                });

        db.collection("children").whereEqualTo("parentId", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot childDoc = task.getResult().getDocuments().get(0);
                        String name = childDoc.getString("name");
                        Long timeLimit = childDoc.getLong("timeLimit");
                        welcomeTextView.setText("Welcome, " + name);
                        timeLimitTextView.setText("Time limit: " + timeLimit + " minutes");
                        timeLimitTextView.setVisibility(View.VISIBLE);
                    }
                });
    }
}