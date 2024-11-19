package com.g06.kidsstoryapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccountSelectionActivity extends AppCompatActivity {

    private ListView accountListView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<String> accountList;
    private List<String> accountIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_selection);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        accountListView = findViewById(R.id.accountListView);
        accountList = new ArrayList<>();
        accountIds = new ArrayList<>();

        loadAccounts();

        accountListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedAccountId = accountIds.get(position);
            if (position == 0) { // Parent account
                // Log in as parent
                Intent intent = new Intent(AccountSelectionActivity.this, LoginActivity.class);
                intent.putExtra("parentId", selectedAccountId);
                startActivity(intent);
            } else {
                // Log in as child
//                Intent intent = new Intent(AccountSelectionActivity.this, MainActivity.class);
//                intent.putExtra("childId", selectedAccountId);
//                startActivity(intent);
                  checkTimeLimit(selectedAccountId);
            }
            finish();
        });
    }

    private void checkTimeLimit(String childId) {
        db.collection("children")
                .document(childId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Get timeLimit as Long instead of String
                        Long timeLimit = task.getResult().getLong("timeLimit");
                        String lastLoginTime = task.getResult().getString("lastLoginTime");

                        if (timeLimit == null) {
                            // If timeLimit is not set, allow access
                            proceedToMainActivity(childId);
                            return;
                        }

                        if (isTimeLimitExceeded(timeLimit, lastLoginTime)) {
                            // Show time limit exceeded notification
                            Toast.makeText(this, "Time limit exceeded for today!", Toast.LENGTH_LONG).show();
                            // Close the app
                            startActivity(new Intent(this, AccountSelectionActivity.class));
                            Toast.makeText(this, "Time limit exceeded for today!", Toast.LENGTH_LONG).show();
                        } else {
                            proceedToMainActivity(childId);
                        }
                    } else {
                        Toast.makeText(this, "Error checking time limit", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void proceedToMainActivity(String childId) {
        updateLastLoginTime(childId);
        Intent intent = new Intent(AccountSelectionActivity.this, MainActivity.class);
        intent.putExtra("childId", childId);
        startActivity(intent);
    }

    private boolean isTimeLimitExceeded(Long timeLimit, String lastLoginTime) {
        if (lastLoginTime == null) {
            return false;
        }

        try {
            LocalDateTime now = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                now = LocalDateTime.now();
            }
            LocalDateTime lastLogin = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                lastLogin = LocalDateTime.parse(lastLoginTime,
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            // If last login was on a different day, reset the counter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (lastLogin.toLocalDate().isBefore(now.toLocalDate())) {
                    return false;
                }
            }

            // Compare minutes since last login with timeLimit
            long minutesSinceLastLogin = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                minutesSinceLastLogin = Duration.between(lastLogin, now).toMinutes();
            }
            return minutesSinceLastLogin >= timeLimit;
        } catch (Exception e) {
            // If there's any error parsing the date, allow access
            return false;
        }
    }

    private void updateLastLoginTime(String childId) {
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        db.collection("children")
                .document(childId)
                .update("lastLoginTime", currentTime);
    }

    private void loadAccounts() {
        String parentId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        accountList.add("Parent Account"); // Add parent account
        accountIds.add(parentId);

        db.collection("children")
                .whereEqualTo("parentId", parentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String childName = document.getString("name");
                            String childId = document.getId();
                            if (childName != null && childId != null) {
                                accountList.add(childName);
                                accountIds.add(childId);
                            }
                        }
                        if (!isFinishing() && !isDestroyed()) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_list_item_1, accountList);
                            accountListView.setAdapter(adapter);
                        }
                    } else {
                        Toast.makeText(this, "Error loading accounts", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}