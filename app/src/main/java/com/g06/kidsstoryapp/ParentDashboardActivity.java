package com.g06.kidsstoryapp;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Map;

public class ParentDashboardActivity extends AppCompatActivity {

    private LinearLayout dashboardContainer;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dashboard);

        dashboardContainer = findViewById(R.id.dashboardContainer);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadChildrenData();
    }

    private void loadChildrenData() {
        String parentId = mAuth.getCurrentUser().getUid();
        db.collection("children")
                .whereEqualTo("parentId", parentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        addChildView(document);
                    }
                });
    }

    private void addChildView(QueryDocumentSnapshot document) {
        String name = document.getString("name");
        Long age = document.getLong("age");
        Long timeLimit = document.getLong("timeLimit");
        Map<String, Long> dailyUsage = (Map<String, Long>) document.get("dailyUsage");

        LinearLayout childView = new LinearLayout(this);
        childView.setOrientation(LinearLayout.VERTICAL);
        childView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        childView.setPadding(0, 16, 0, 16);

        TextView nameAgeView = new TextView(this);
        nameAgeView.setText(name + ", " + age + " years old");
        nameAgeView.setTextSize(18);
        childView.addView(nameAgeView);

        TextView timeLimitView = new TextView(this);
        timeLimitView.setText("Time limit: " + timeLimit + " minutes");
        childView.addView(timeLimitView);

        TextView avgUsageView = new TextView(this);
        long totalUsage = 0;
        int days = 0;
        if (dailyUsage != null) {
            for (Long usage : dailyUsage.values()) {
                totalUsage += usage;
                days++;
            }
        }
        double avgUsage = days > 0 ? (double) totalUsage / days : 0;
        avgUsageView.setText("Average daily usage: " + String.format("%.2f", avgUsage) + " minutes");
        childView.addView(avgUsageView);

        dashboardContainer.addView(childView);
    }
}