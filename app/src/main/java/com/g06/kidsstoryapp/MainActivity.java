package com.g06.kidsstoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView timerTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;
    private boolean isChildAccount;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        timerTextView = findViewById(R.id.timerTextView);

        isChildAccount = getIntent().hasExtra("childId");
        if (isChildAccount) {
            userId = getIntent().getStringExtra("childId");
        }



        setupBottomNavigation();
        loadUserData();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

        if (item.getItemId() == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (item.getItemId() == R.id.nav_settings) {
            if (isChildAccount) {
                selectedFragment = new ChildSettingsFragment();
            } else {
                selectedFragment = new ParentSettingsFragment();
            }
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment)
                    .commit();
        }

        return true;
        });

        // Set default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new HomeFragment())
                .commit();
    }

    private void loadUserData() {
        DocumentReference userRef;
        if (isChildAccount) {
            userRef = db.collection("children").document(userId);
        } else {
            userRef = db.collection("parents").document(userId);
        }

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (isChildAccount) {
                    Long timeLimit = documentSnapshot.getLong("timeLimit");
                    if (timeLimit != null) {
                        timeLeftInMillis = timeLimit * 60 * 1000; // Convert minutes to milliseconds
                        startTimer();
                    }
                } else {
                    // Handle parent account data if needed
                }
            }
        });
    }

    private boolean oneMinuteWarningShown = false;

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;

                int minutes = (int) (timeLeftInMillis / 1000) / 60;
                int seconds = (int) (timeLeftInMillis / 1000) % 60;

                if (minutes == 0 && !oneMinuteWarningShown) {
                    Toast.makeText(MainActivity.this, "You have 1 minute left!", Toast.LENGTH_SHORT).show();
                    oneMinuteWarningShown = true;
                }
                updateTimerText();
                updateFirebaseTimeLeft();
            }

            @Override
            public void onFinish() {
                startActivity(new Intent(MainActivity.this, AccountSelectionActivity.class));
                logoutUser();
            }
        }.start();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText("Time left: " + timeLeftFormatted);
    }

    private void updateFirebaseTimeLeft() {
        if (isChildAccount) {
            db.collection("children").document(userId)
                    .update("timeLimit", timeLeftInMillis / 1000 / 60); // Update time left in minutes
        }
    }

    void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, AccountSelectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (isChildAccount && timeLeftInMillis > 0) {
//            long currentTimestamp = System.currentTimeMillis();
//            db.collection("children").document(userId)
//                    .update("lastPausedTime", currentTimestamp, "timeLimit", timeLeftInMillis / 1000 / 60); // Lưu phút còn lại
//            if (countDownTimer != null) {
//                countDownTimer.cancel();
//            }
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}