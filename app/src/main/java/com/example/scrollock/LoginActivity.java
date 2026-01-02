package com.example.scrollock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvSignUp;

    // Local dummy users
    HashMap<String, String> users = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        // Dummy credentials
        users.put("scrollock.com", "pass");
        users.put("admin@example.com", "adminpass");
        users.put("test@example.com", "test123");

        btnLogin.setOnClickListener(v -> loginUser());

        tvSignUp.setOnClickListener(v ->
                Toast.makeText(this, "Sign up disabled in local mode", Toast.LENGTH_SHORT).show());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email required");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password required");
            return;
        }

        // Validate credentials
        if (users.containsKey(email) && users.get(email).equals(password)) {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String name = prefs.getString("userName", null);

            // ⚡ Save login email for reference
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("lastLoginEmail", email);
            editor.apply();

            Intent nextScreen;
            if (name == null || name.trim().isEmpty()) {
                // First-time user: mark prefs & go to setup screen
                editor.putString("userName", ""); // placeholder so dashboard doesn't loop
                editor.apply();

                nextScreen = new Intent(LoginActivity.this, UserSetupActivity.class);
            } else {
                // Returning user → straight to dashboard
                nextScreen = new Intent(LoginActivity.this, DashboardActivity.class);
            }

            startActivity(nextScreen);
            finish();
        } else {
            Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
        }
    }
}
