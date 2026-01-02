package com.example.scrollock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtUserName, txtUserEmail;
    private ImageView backArrow;
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        txtUserName.setText(prefs.getString("userName", "User"));
        txtUserEmail.setText(prefs.getString("userEmail", "hello@reallygreatsite.com"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        backArrow = findViewById(R.id.backArrow);
        txtUserName = findViewById(R.id.txtUserName);
        txtUserEmail = findViewById(R.id.txtUserEmail);

        // Load saved name from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String name = prefs.getString("userName", "User");
        String email = prefs.getString("userEmail", "hello@reallygreatsite.com"); // optional default
        txtUserName.setText(name);
        txtUserEmail.setText(email);

        // Back button
        backArrow.setOnClickListener(v -> finish());

        // Menu actions
        findViewById(R.id.editProfile).setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class))
        );

        findViewById(R.id.changePassword).setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class))
        );

        findViewById(R.id.connectedApps).setOnClickListener(v ->
                startActivity(new Intent(this, ConnectedAppsActivity.class))
        );

        findViewById(R.id.logout).setOnClickListener(v -> {
            // Clear saved user session
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });
    }
}
