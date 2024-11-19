package com.g06.kidsstoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 2000; // 2 seconds
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(this::checkLoginStatus, SPLASH_TIMEOUT);
    }

    private void checkLoginStatus() {
        if (mAuth.getCurrentUser() != null) {
            // User is signed in
            startActivity(new Intent(SplashActivity.this, AccountSelectionActivity.class));
        } else {
            // No user is signed in
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish();
    }
}