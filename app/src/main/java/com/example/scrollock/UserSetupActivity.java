package com.example.scrollock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class UserSetupActivity extends AppCompatActivity {

    EditText nameInput;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setup);

        nameInput = findViewById(R.id.nameInput);
        saveBtn = findViewById(R.id.saveBtn);

        // Check if name already exists → skip setup
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String existingName = prefs.getString("userName", null);
        if (existingName != null && !existingName.trim().isEmpty()) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }

        saveBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();

            if (name.isEmpty()) {
                nameInput.setError("Please enter your name");
                nameInput.requestFocus();
                return;
            }

            // Save name + mark setup complete
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("userName", name);
            editor.putBoolean("isSetupDone", true);
            editor.apply();

            // Move to Dashboard
            Intent intent = new Intent(UserSetupActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
