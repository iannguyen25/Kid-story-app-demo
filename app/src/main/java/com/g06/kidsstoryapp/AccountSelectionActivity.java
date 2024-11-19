package com.g06.kidsstoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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

        accountListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedAccountId = accountIds.get(position);
                if (position == 0) { // Parent account
                    // Log in as parent
                    startActivity(new Intent(AccountSelectionActivity.this, MainActivity.class));
                } else {
                    // Log in as child
                    Intent intent = new Intent(AccountSelectionActivity.this, MainActivity.class);
                    intent.putExtra("childId", selectedAccountId);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    private void loadAccounts() {
        String parentId = mAuth.getCurrentUser().getUid();
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
                            accountList.add(childName);
                            accountIds.add(childId);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_list_item_1, accountList);
                        accountListView.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Error loading accounts", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}