package com.g06.kidsstoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateChildAccountActivity extends AppCompatActivity {

    private EditText nameEditText, ageEditText;
    private Button createAccountButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_child_account);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        nameEditText = findViewById(R.id.nameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        createAccountButton = findViewById(R.id.createAccountButton);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChildAccount();
            }
        });
    }

    private void createChildAccount() {
        String name = nameEditText.getText().toString().trim();
        String ageString = ageEditText.getText().toString().trim();

        if (name.isEmpty() || ageString.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
            return;
        }

        String parentId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        String childId = db.collection("children").document().getId();

        Map<String, Object> child = new HashMap<>();
        child.put("childId", childId);
        child.put("parentId", parentId);
        child.put("name", name);
        child.put("age", age);
        child.put("timeLimit", 120); // Default time limit: 120 minutes
        child.put("dailyUsage", new HashMap<String, Integer>());

        db.collection("children").document(childId)
                .set(child)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateParentDocument(parentId, childId);
                        } else {
                            Toast.makeText(CreateChildAccountActivity.this, "Failed to create child account", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateParentDocument(String parentId, String childId) {
        db.collection("parents").document(parentId)
                .update("children", com.google.firebase.firestore.FieldValue.arrayUnion(childId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateChildAccountActivity.this, "Child account created successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateChildAccountActivity.this, MainActivity.class);
                            finish();
                        } else {
                            Toast.makeText(CreateChildAccountActivity.this, "Failed to update parent document", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}